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
import { toast } from "sonner";
import { useLocationFilter } from "@/hooks/use-location-filter";

export default function MapsPage() {
    const [locations, setLocations] = useState<FeatureCollection>({ type: 'FeatureCollection', features: [] } as FeatureCollection);
    const [position, setPosition] = useState<LatLngTuple>([-6.175, 106.8275]);
    const [{ date, device }, setFilter] = useLocationFilter();

    useEffect(() => {
        const params = new URLSearchParams();
        if (date?.from) params.set('start', date.from.toJSON());
        if (date?.to) params.set('end', date.to.toJSON());
        if (device && device != 'all') params.append('device', device);

        axiosInstance.get(`v1/locations?${params.toString()}`)
            .then((res) => {
                const data = res.data as FeatureCollection & { message: string }
                setLocations({ ...data as FeatureCollection });
                if (!data || data.features.length == 0) return;
                const last = data.features[data.features.length - 1].geometry as LineString;
                setPosition([last.coordinates[1][1], last.coordinates[1][0]]);
            })
            .catch(err => toast.error(`Failed to get user's locations: ${err}`));
    }, [date, device]);

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
        <>
            <Header>
                <DatePicker variant="outline" date={date} setDate={handleDate} />
                <DeviceSelect className="" selectedDevice={device || "all"} onSelectedDevice={handleDevice} />
            </Header>
            <div className="flex flex-1 flex-col gap-4">
                <Maps position={position} locations={locations} />
            </div>
        </>
    )

}
