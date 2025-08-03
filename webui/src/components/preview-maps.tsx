import type { Feature, FeatureCollection } from "geojson";
import { MapContainer, ZoomControl, TileLayer, GeoJSON, } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import hash from "object-hash";
import { cn } from "@/lib/utils";
import * as turf from "@turf/turf";
import { type LatLngTuple } from "leaflet";

export type PreviewMapsProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection | Feature,
}

export function PreviewMaps({ locations, className }: PreviewMapsProps) {
    let center = [-6.175, 106.8275];
    try {
        const coords = turf.center(locations).geometry.coordinates;
        center = [coords[1], coords[0]];
        console.log(center);
    } catch {
        console.error("error get center");
    }

    return (
        <MapContainer
            key={hash(locations)}
            className={cn("size-full", className)}
            center={center as LatLngTuple}
            zoom={13}
            scrollWheelZoom={true}
            zoomControl={false}>
            <ZoomControl position="bottomright" />
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <GeoJSON data={locations} />
        </MapContainer>
    );
}
