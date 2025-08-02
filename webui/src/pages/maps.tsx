import type { LatLngTuple } from "leaflet";
import 'leaflet/dist/leaflet.css';
import { useEffect, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import type { DateRange } from "react-day-picker";
import { DatePicker } from "@/components/date-picker.tsx";
import { DeviceSelect } from "@/components/device-select.tsx";
import { Maps } from "@/components/maps";
import type { Feature, FeatureCollection, Point } from "geojson";
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
    const [displayedLocations, setDisplayedLocations] = useState<FeatureCollection>(turf.featureCollection([]));
    const [position, setPosition] = useState<LatLngTuple>([-6.175, 106.8275]);
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

    useEffect(() => {
        if (!locations || locations.features.length == 0) return;

        console.log(locations);

        const features = locations.features;
        const lines: Feature[] = [];
        const points: Feature[] = [];
        if (showPoints) points.push(...features);
        if (showLines) {
            for (let i = 1; i < features.length; i++) {
                const fromFeature = features[i - 1];
                const toFeature = features[i];
                const startAt = Date.parse(fromFeature.properties.timestamp);
                const endAt = Date.parse(toFeature.properties.timestamp);
                const from = turf.point((fromFeature.geometry as Point).coordinates);
                const to = turf.point((toFeature.geometry as Point).coordinates);
                const distance = turf.distance(from, to, { units: "kilometers" });
                const speed = distance / ((startAt - endAt) / 3600 / 1000);

                lines.push(turf.lineString([from.geometry.coordinates, to.geometry.coordinates], { distance, distanceUnit: "KM", speed, speedUnit: "KM/H", startAt, endAt }));
            }
        }


        const last = features[features.length - 1].geometry;
        setPosition([last.coordinates[1], last.coordinates[0]]);
        setDisplayedLocations(turf.featureCollection([...lines, ...points]));
    }, [locations, showLines, showPoints]);

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
                <Maps position={position} locations={displayedLocations} />
            </div>
        </>
    )

}
