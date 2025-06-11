import {GeoJSON, MapContainer, TileLayer, ZoomControl} from "react-leaflet";
import type {LatLngTuple} from "leaflet";
import 'leaflet/dist/leaflet.css';
import {useEffect, useRef, useState} from "react";
import {axiosInstance} from "@/lib/request.ts";
import type {FeatureCollection, Point} from "geojson";
import type {DateRange} from "react-day-picker";
import L from 'leaflet';
import {DatePicker} from "@/components/date-picker.tsx";
import {ImportDialog} from "@/components/import-dialog.tsx";
import {DeviceSelect} from "@/components/device-select.tsx";
import {AccountDropdown} from "@/components/account-dropdown.tsx";

export default function Maps() {
    const mapRef = useRef<L.Map | null>(null);
    const dataRef = useRef<L.GeoJSON | null>(null);
    const [locations, setLocations] = useState<FeatureCollection | undefined>();
    const [position, setPosition] = useState<LatLngTuple>([51.505, -0.09]);
    const [date, setDate] = useState<DateRange | undefined>(() => {
        const start = new Date();
        start.setHours(0, 0, 0, 0);

        const end = new Date();
        end.setHours(23, 59, 59, 59);

        return {
            from: start,
            to: end,
        };
    });
    const [device, setDevice] = useState<string | undefined>(undefined);

    useEffect(() => {
        setLocations(undefined);
        const params = new URLSearchParams();
        if (date?.from) {
            params.set('start', date.from.toISOString());
        }
        if (date?.to) {
            params.set('end', date.to.toISOString());
        }
        if (device) {
            params.set('device', device);
        }
        axiosInstance.get(`v1/locations?${params.toString()}`)
            .then((res) => {
                const data = res.data as FeatureCollection & { message: string }
                setLocations(data as FeatureCollection);
                if (!locations || locations.features.length == 0) return;
                const last = locations.features[locations.features.length - 1].geometry as Point;
                setPosition([last.coordinates[1], last.coordinates[0]]);
            });
    }, [date, device]);

    useEffect(() => {
        mapRef.current?.setView(position);
    }, [position]);

    return (
        <>
            <MapContainer className="absolute w-screen h-screen" center={position} zoom={13} scrollWheelZoom={true}
                          ref={mapRef} zoomControl={false}>
                <ZoomControl position="bottomright"/>
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {locations && <GeoJSON
                    ref={dataRef}
                    data={locations}
                    pointToLayer={(_, latlng) => {
                        return L.circleMarker(latlng);
                    }}
                    onEachFeature={(feature, layer) => {
                        if (feature.properties)
                            layer.bindPopup(JSON.stringify(feature.properties));
                    }}
                />}
                <div className="leaflet-top leaflet-left">
                    <div className="leaflet-control flex gap-2">
                        <DatePicker className="shadow-md" date={date} setDate={setDate}/>
                        <DeviceSelect onSelectedDevice={setDevice}/>
                        <ImportDialog/>
                    </div>
                </div>
                <div className="leaflet-top leaflet-right">
                    <div className="leaflet-control flex gap-2">
                        <AccountDropdown/>
                    </div>
                </div>
            </MapContainer>
        </>
    )

}
