import type { FeatureCollection } from "geojson";
import L, { type LatLngTuple } from "leaflet";
import { MapContainer, ZoomControl, TileLayer, GeoJSON } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import React from "react";
import hash from "object-hash";
import type { LineStringProperties } from "@/types/properties";

export function Maps({ locations, position }: React.ComponentProps<"div"> & {
    locations: FeatureCollection,
    position: LatLngTuple
}) {
    const mapRef = React.useRef<L.Map | null>(null);

    React.useEffect(() => {
        mapRef.current?.setView(position);
    }, [position]);

    return (
        <MapContainer
            className="size-full"
            center={position}
            zoom={13}
            scrollWheelZoom={true}
            ref={mapRef} zoomControl={false}>
            <ZoomControl position="bottomright" />
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            <GeoJSON
                key={hash(locations, { algorithm: 'sha1' })}
                data={locations}
                pointToLayer={(_, latlng) => {
                    return L.circleMarker(latlng, {
                        radius: 5
                    });
                }}
                onEachFeature={(feature, layer) => {
                    if (feature.properties) {
                        const props = feature.properties as LineStringProperties;
                        let content = "<div>";
                        content += `<strong>Distance</strong>: ${props.distance.toFixed(2)} ${props.distanceUnit}<br/>`;
                        content += `<strong>Speed</strong>: ${props.speed.toFixed(2)} ${props.speedUnit}<br/>`;
                        content += `<strong>Start at</strong>: ${(new Date(props.startAt)).toLocaleString()}<br/>`;
                        content += `<strong>End at</strong>: ${(new Date(props.endAt)).toLocaleString()}<br/>`;
                        if (props.motions) content += `<strong>Motions</strong>: ${props.motions.join(",")}<br/>`;
                        content += "</div>";
                        layer.bindPopup(content);
                    }
                }}
            />

        </MapContainer>
    );
}
