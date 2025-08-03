import 'leaflet/dist/leaflet.css';
import { useEffect, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { Maps } from "@/components/maps";
import type { FeatureCollection, Point } from "geojson";
import { Header } from "@/components/header";
import { toast } from "sonner";
import { useLocationFilter } from "@/hooks/use-location-filter";
import type { Response } from "@/types/response";
import { LayerCheckbox } from "@/components/layer-checkbox";
import type { Checked } from "@/types/checked";
import * as turf from "@turf/turf";
import type { PointProperties } from "@/types/properties";

export default function MapsPage() {
    const [locations, setLocations] = useState<FeatureCollection<Point, PointProperties>>(turf.featureCollection([]));
    const [{ date, device }, setFilter] = useLocationFilter();
    const [showLines, setShowLines] = useState<Checked>(true);
    const [showPoints, setShowPoints] = useState<Checked>(true);

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
                <LayerCheckbox showLines={showLines} setShowLines={setShowLines} showPoints={showPoints} setShowPoints={setShowPoints} />
            </Header>
            <div className="flex flex-1 flex-col gap-4">
                <Maps locations={locations} showLines={showLines} showPoints={showPoints} />
            </div>
        </>
    )

}
