import type { LatLngTuple } from "leaflet";
import 'leaflet/dist/leaflet.css';
import { useEffect, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { Separator } from "@/components/ui/separator";
import { Maps } from "@/components/maps";
import type { FeatureCollection, LineString } from "geojson";
import { useIsMobile } from "@/hooks/use-mobile";

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

    const isMobile = useIsMobile();

    return (
        <div className={`flex gap-4 m-4 h-full ${isMobile ? "flex-col" : ""}`}>
            <div className={isMobile ? `w-full flex` : `w-1/5 h-full flex flex-col flex-none`}>
                <div className="flex flex-col gap-4">
                    <DatePicker date={date} setDate={setDate} />
                    <DeviceSelect className="shadow-none border-none" selectedDevice={device} onSelectedDevice={setDevice} />
                    <Separator />
                </div>
            </div>
            <Maps position={position} locations={locations} />
        </div>
    )

}
