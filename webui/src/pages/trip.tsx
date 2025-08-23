import { axiosInstance } from "@/lib/request";
import type { PointProperties } from "@/types/properties";
import type { FeatureCollection, Point } from "geojson";
import { useEffect, useState } from "react";
import { MapContainer, TileLayer, GeoJSON } from "react-leaflet";
import { useParams } from "react-router";
import { toast } from "sonner";
import type { Response } from "@/types/response";
import * as turf from "@turf/turf";
import type { LatLngBoundsExpression, LatLngTuple } from "leaflet";

export default function TripPage() {
    const { uuid } = useParams();
    const [locations, setLocations] = useState<FeatureCollection<Point, PointProperties>>(turf.featureCollection([]));
    const [center, setCenter] = useState<LatLngTuple>([-6.175, 106.8275]);
    const [bounds, setBounds] = useState<LatLngBoundsExpression>();

    useEffect(() => {
        axiosInstance.get<Response<FeatureCollection<Point, PointProperties>>>(`v1/trips/${uuid}`)
            .then(({ data }) => {
                setLocations(data.data);
            })
            .catch(err => toast.error(`Failed to get user's locations: ${err}`));
    }, [uuid]);

    useEffect(() => {
        try {
            const coords = turf.center(locations).geometry.coordinates;
            setCenter([coords[1], coords[0]]);

            const bbox = turf.bbox(locations, { recompute: true });
            setBounds([
                [bbox[1], bbox[0]],
                [bbox[3], bbox[2]],
            ]);
        } catch {
            // noop
        }
    }, [locations, setBounds, setCenter]);

    return (<div className="flex flex-1 flex-col gap-4">
        {locations && center && bounds && <MapContainer
            className="h-screen"
            center={center}
            zoom={13}
            scrollWheelZoom={true}
            zoomControl={false}
            bounds={bounds}>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <GeoJSON data={locations} />
        </MapContainer>}
    </div>);
}
