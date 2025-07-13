import type { LatLngTuple } from "leaflet";
import 'leaflet/dist/leaflet.css';
import { useEffect, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { Maps } from "@/components/maps";
import type { FeatureCollection, LineString } from "geojson";
import { Header } from "@/components/header";

export default function MapsPage() {
    const [locations, setLocations] = useState<FeatureCollection>({ type: 'FeatureCollection', features: [] } as FeatureCollection);
    const [position, setPosition] = useState<LatLngTuple>([-6.175, 106.8275]);
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
    const [device, setDevice] = useState<string>('all');

    useEffect(() => {
        const params = new URLSearchParams();
        if (date?.from) {
            params.set('start', date.from.toISOString());
        }
        if (date?.to) {
            params.set('end', date.to.toISOString());
        }
        if (device !== 'all') {
            params.set('device', device);
        }
        axiosInstance.get(`v1/locations?${params.toString()}`)
            .then((res) => {
                const data = res.data as FeatureCollection & { message: string }
                setLocations({ ...data as FeatureCollection });
                if (!data || data.features.length == 0) return;
                const last = data.features[data.features.length - 1].geometry as LineString;
                setPosition([last.coordinates[1][1], last.coordinates[1][0]]);
            });
    }, [date, device]);

    return (
        <>
            <Header>
                <DatePicker variant="outline" date={date} setDate={setDate} />
                <DeviceSelect className="" selectedDevice={device} onSelectedDevice={setDevice} />
            </Header>
            <div className="flex flex-1 flex-col gap-4">
                <Maps position={position} locations={locations} />
            </div>
        </>
    )

}
