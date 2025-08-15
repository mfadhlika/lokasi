import 'leaflet/dist/leaflet.css';
import { useEffect, useRef, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { Markers } from "@/components/markers";
import type { Feature, FeatureCollection, Point } from "geojson";
import { toast } from "sonner";
import { useLocationFilter } from "@/hooks/use-location-filter";
import type { Response } from "@/types/response";
import { LayerCheckbox, useLayerState } from "@/components/layer-checkbox";
import * as turf from "@turf/turf";
import type { PointProperties } from "@/types/properties";
import { MapContainer } from 'react-leaflet/MapContainer';
import { ZoomControl } from 'react-leaflet/ZoomControl';
import { TileLayer } from 'react-leaflet';
import { Header } from '@/components/header';
import { MapControl } from '@/components/map-control';

export default function MapsPage() {
    const [locations, setLocations] = useState<FeatureCollection<Point, PointProperties>>(turf.featureCollection([]));
    const [lastKnownLocation, setLastKnownLocation] = useState<Feature<Point, PointProperties> | undefined>();
    const [{ date, device }, setFilter] = useLocationFilter();
    const layerSettings = useLayerState();
    const mapRef = useRef<L.Map>(null);

    useEffect(() => {
        const params = new URLSearchParams();
        if (date?.from) params.set('start', date.from.toJSON());
        if (date?.to) params.set('end', date.to.toJSON());
        if (device && device != 'all') params.append('device', device);

        axiosInstance.get<Response<FeatureCollection<Point, PointProperties>>>(`v1/locations?${params.toString()}`)
            .then(({ data }) => {
                setLocations(data.data);
            })
            .catch(err => toast.error(`Failed to get user's locations: ${err}`));
    }, [date, device]);

    useEffect(() => {
        if (!layerSettings.showLastKnown) return

        axiosInstance.get<Response<Feature<Point, PointProperties>>>('v1/locations/last')
            .then(({ data }) => {
                setLastKnownLocation(data.data);
            })
            .catch(err => toast.error(`Failed to get user's lsat known locations: ${err}`));
    }, [layerSettings.showLastKnown]);

    useEffect(() => {
        let center;
        if (locations && locations.features?.length > 0) center = turf.flip(turf.center(locations)).geometry.coordinates as [number, number];
        else if (lastKnownLocation) center = turf.flip(lastKnownLocation).geometry.coordinates as [number, number];

        if (center) mapRef.current?.setView(center);
    }, [lastKnownLocation, locations]);


    const handleDate = (newDate: DateRange | undefined) => {
        setFilter({
            date: newDate,
            device
        });
    }

    const handleDevice = (newDevice: string) => {
        setFilter({
            date,
            device: newDevice
        });
    }

    return (
        <div className="flex flex-1 flex-col gap-4">
            <MapContainer
                ref={mapRef}
                className="size-full"
                center={[-6.175, 106.8275]}
                zoom={13}
                scrollWheelZoom={true}
                zoomControl={false}>
                <ZoomControl position="bottomleft" />
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <MapControl position='topleft' disableClickPropagation={true} disableScrollPropagation={true}>
                    <Header className='leaflet-bar leaflet-touch flex bg-white max-w-[calc(100vw-20px)]'>
                        <DatePicker variant="outline" date={date} setDate={handleDate} />
                        <DeviceSelect className='' selectedDevice={device || "all"} onSelectedDevice={handleDevice} />
                        <LayerCheckbox {...layerSettings} />
                    </Header>
                </MapControl>
                <Markers locations={locations} lastKnowLocation={lastKnownLocation} {...layerSettings} />
            </MapContainer>
        </div>
    )

}
