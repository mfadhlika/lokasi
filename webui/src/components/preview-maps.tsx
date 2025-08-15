import type { Feature, FeatureCollection } from "geojson";
import { MapContainer, TileLayer, GeoJSON, } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import hash from "object-hash";
import { cn } from "@/lib/utils";
import * as turf from "@turf/turf";
import { type LatLngBoundsExpression, type LatLngTuple } from "leaflet";

export type PreviewMapsProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection | Feature,
    zoom?: number
}

export function PreviewMaps({ locations, className, zoom }: PreviewMapsProps) {
    let center: LatLngTuple = [-6.175, 106.8275];
    let bounds: LatLngBoundsExpression | undefined = undefined;
    try {
        const coords = turf.center(locations).geometry.coordinates;
        center = [coords[1], coords[0]];

        const bbox = turf.bbox(locations);
        bounds = [
            [bbox[1], bbox[0]],
            [bbox[3], bbox[2]],
        ]
    } catch {
        // noop
    }

    return (
        <MapContainer
            key={hash(locations)}
            className={cn("size-full", className)}
            center={center}
            zoom={zoom}
            doubleClickZoom={false}
            scrollWheelZoom={false}
            zoomControl={false}
            bounds={bounds}>
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <GeoJSON data={locations} />
        </MapContainer>
    );
}
