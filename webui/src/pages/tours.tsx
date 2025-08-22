import { axiosInstance } from "@/lib/request";
import type { PointProperties } from "@/types/properties";
import type { FeatureCollection, Point } from "geojson";
import { useEffect, useState } from "react";
import { MapContainer, TileLayer, GeoJSON } from "react-leaflet";
import { useParams } from "react-router";
import { toast } from "sonner";
import type { Response } from "@/types/response";
import * as turf from "@turf/turf";

export default function ToursPage() {
    const { uuid } = useParams();
    const [locations, setLocations] = useState<FeatureCollection<Point, PointProperties>>(turf.featureCollection([]));

    useEffect(() => {
        axiosInstance.get<Response<FeatureCollection<Point, PointProperties>>>(`v1/tours/${uuid}`)
            .then(({ data }) => {
                setLocations(data.data);
            })
            .catch(err => toast.error(`Failed to get user's locations: ${err}`));
    }, [uuid]);

    return (<div className="flex flex-1 flex-col gap-4">
        <MapContainer
            className="h-screen"
            center={[-6.175, 106.8275]}
            zoom={13}
            scrollWheelZoom={true}
            zoomControl={false}>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <GeoJSON data={locations} />
        </MapContainer>
    </div>);
}
