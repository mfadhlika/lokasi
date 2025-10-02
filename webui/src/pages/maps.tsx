import 'leaflet/dist/leaflet.css';
import { useEffect, useState } from "react";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { MapLayers } from "@/components/map-layers";
import type { Feature, FeatureCollection, Point } from "geojson";
import { toast } from "sonner";
import { useLocationFilter } from "@/hooks/use-location-filter";
import { LayerCheckbox, useLayerState } from "@/components/layer-checkbox";
import * as turf from "@turf/turf";
import type { PointProperties } from "@/types/properties";
import { MapContainer } from 'react-leaflet/MapContainer';
import { TileLayer } from 'react-leaflet';
import { Header } from '@/components/header';
import { MapControl } from '@/components/map-control';
import { Scan } from 'lucide-react';
import { Toggle } from '@/components/ui/toggle';
import type { LatLngBounds } from 'leaflet';
import { Button } from '@/components/ui/button';
import { locationService } from '@/services/location-service';
import { useAuthStore } from '@/hooks/use-auth';

export default function MapsPage() {
    const { userInfo } = useAuthStore();
    const [locations, setLocations] = useState<FeatureCollection<Point, PointProperties>>(turf.featureCollection([]));
    const [lastKnownLocation, setLastKnownLocation] = useState<Feature<Point, PointProperties> | undefined>();
    const [{ date, device, bounds }, setFilter] = useLocationFilter();
    const [bounded, setBounded] = useState<boolean>(bounds != undefined);
    const layerSettings = useLayerState();

    useEffect(() => {
        const params = new URLSearchParams();
        if (date?.from) params.set('start', date.from.toJSON());
        if (date?.to) params.set('end', date.to.toJSON());
        if (device && device != 'all') params.append('device', device);
        if (bounded && bounds) params.set('bounds', bounds.toBBoxString());

        locationService.fetchLocations({
            start: date?.from,
            end: date?.to,
            device,
            bounds: bounded ? bounds : undefined
        })
            .then(({ data }) => {
                setLocations(data);
            })
            .catch(err => toast.error(`Failed to get user's locations: ${err}`));
    }, [date, device, bounded, bounds]);

    useEffect(() => {
        if (!layerSettings.showLastKnown) return;
        else locationService.unsubscribeLastLocation();

        locationService.fetchLastLocation()
            .then(({ data }) => {
                setLastKnownLocation(data);
            })
            .catch(err => toast.error(`Failed to get user's lsat known locations: ${err}`));

        try {
            locationService.subscribeLastLocation(userInfo!.username!, (data) => {
                setLastKnownLocation(data);
            });
        } catch (error) {
            console.error(error);
        }

        return () => {
            locationService.unsubscribeLastLocation();
        }
    }, [layerSettings.showLastKnown, userInfo]);

    const handleDate = (newDate: DateRange | undefined) => {
        setFilter({
            device,
            bounds,
            date: newDate,
        });
    }

    const handleDevice = (newDevice: string) => {
        setFilter({
            date,
            bounds,
            device: newDevice
        });
    }

    const handleBounds = (bounds: LatLngBounds) => {
        if (!bounded) return;

        setFilter({
            device,
            date,
            bounds
        });
    }

    return (
        <div className="flex flex-1 flex-col gap-4">
            <MapContainer
                className="size-full"
                center={[-6.175, 106.8275]}
                zoom={13}
                scrollWheelZoom={true}
                zoomControl={false}
                bounds={bounds}
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <MapControl position='topleft' disableClickPropagation={true} disableScrollPropagation={true}>
                    <Header className='leaflet-touch flex bg-sidebar rounded-2xl border border-gray-300 max-w-[calc(100vw-20px)]'>
                        <DatePicker className='bg-sidebar' variant="outline" date={date} setDate={handleDate} />
                        <DeviceSelect className='bg-sidebar' selectedDevice={device || "all"} onSelectedDevice={handleDevice} />
                        <Toggle pressed={bounded} onPressedChange={setBounded} asChild>
                            <Button className='[[data-state="on"]]:bg-red-100' variant='outline' >
                                <Scan />Within area
                            </Button>
                        </Toggle>
                    </Header>
                </MapControl>
                <MapControl position='bottomright'>
                    <LayerCheckbox className='bg-sidebar' {...layerSettings} />
                </MapControl>
                <MapLayers locations={locations} lastKnowLocation={lastKnownLocation} bounded={bounded} onBoundsChange={handleBounds} {...layerSettings} />
            </MapContainer>
        </div >
    )

}
