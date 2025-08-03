import type { Feature, FeatureCollection, Point } from "geojson";
import { MapContainer, ZoomControl, TileLayer, FeatureGroup, GeoJSON, } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import hash from "object-hash";
import type { LineStringProperties, PointProperties } from "@/types/properties";
import { cn } from "@/lib/utils";
import * as turf from "@turf/turf";
import type { Checked } from "@/types/checked";
import { renderToStaticMarkup } from 'react-dom/server';
import L, { type LatLngTuple } from "leaflet";

export type MapsProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection<Point, PointProperties>,
    lastKnowLocation?: Feature<Point, PointProperties>,
    zoom?: number,
    showPoints?: Checked,
    showLines?: Checked,
    showLastKnown?: Checked
}

function InnerMaps({ locations, lastKnowLocation, showLines, showPoints, showLastKnown }: MapsProps) {
    // const map = useMap();
    // map.setView([-6.175, 106.8275]);

    const groupped: Feature[][] = [[]];
    for (let i = 1; i < locations.features.length; i++) {
        const fromFeature = locations.features[i - 1];
        const toFeature = locations.features[i];

        const startAt = Date.parse(fromFeature.properties!.timestamp);
        const endAt = Date.parse(toFeature.properties!.timestamp);

        const duration = (startAt - endAt) / 1000;
        if (duration > 60 * 15) {
            groupped.push([]);
            continue;
        };

        const from = turf.point((fromFeature.geometry as Point).coordinates);
        const to = turf.point((toFeature.geometry as Point).coordinates);
        const distance = turf.distance(from, to, { units: "kilometers" });
        const speed = distance / duration / 3600;

        groupped[groupped.length - 1].push(turf.lineString([
            fromFeature.geometry.coordinates,
            toFeature.geometry.coordinates,
        ], { distance, distanceUnit: "km", speed, speedUnit: "km/h", startAt, endAt }));

        groupped[groupped.length - 1].push(fromFeature);
        groupped[groupped.length - 1].push(toFeature);
    }

    return (<>
        {groupped.map((group, groupIndex) => {
            return (
                <FeatureGroup
                    key={groupIndex}
                    eventHandlers={{
                        mouseover: (e) => {
                            e.target.setStyle({
                                color: 'red'
                            });
                        },
                        mouseout: (e) => {
                            e.target.setStyle({
                                color: '#3388ff'
                            });
                        }
                    }}>
                    {group
                        .filter(feature => (feature.geometry.type == 'LineString' && showLines) || (feature.geometry.type == 'Point' && showPoints))
                        .map((feature, featureIndex) =>
                            <GeoJSON
                                key={`${hash(feature)}-${groupIndex}-${featureIndex}`}
                                data={feature}
                                pointToLayer={(_, latlng) => {
                                    return L.circleMarker(latlng, {
                                        radius: 5
                                    });
                                }}
                                onEachFeature={(feature, layer) => {
                                    let content;
                                    switch (feature.geometry.type) {
                                        case 'LineString': {
                                            const props = feature.properties as LineStringProperties;
                                            content = <div>
                                                <strong>Distance</strong>: {props.distance?.toFixed(2) ?? 0} {props.distanceUnit}<br />
                                                <strong>Speed</strong>: {props.speed?.toFixed(2) ?? 0} {props.speedUnit}<br />
                                                <strong>Start at</strong>: {(new Date(props.startAt)).toLocaleString()}<br />
                                                <strong>End at</strong>: {(new Date(props.endAt)).toLocaleString()}<br />
                                                {props.motions && <><strong>Motions</strong>: {props.motions.join(",")}<br /></>}
                                            </div>;
                                            break;
                                        }
                                        case 'Point': {
                                            const props = feature.properties as PointProperties;
                                            content = <div>
                                                <strong>Timestamp</strong>: {(new Date(props.timestamp)).toLocaleString()}<br />
                                                <strong>Speed</strong>: {props.speed}<br />
                                                <strong>Altitude</strong>: {props.altitude}<br />
                                                <strong>Vertical accuracy</strong>: {props.verticalAccuracy}<br />
                                                <strong>Course</strong>: {props.course}<br />
                                                <strong>Course accuracy</strong>: {props.courseAccuracy}<br />
                                                <strong>Battery level</strong>: {props.batteryLevel}<br />
                                                <strong>Battery state</strong>: {props.batteryState}<br />
                                                <strong>Device</strong>: {props.deviceId}<br />
                                                <strong>SSID</strong>: {props.ssid}<br />
                                                {props.motions && <><strong>Motions</strong>: {props.motions.join(",")}<br /></>}
                                            </div>
                                            break;
                                        }
                                    }
                                    if (content) layer.bindTooltip(renderToStaticMarkup(content));
                                }} />
                        )}
                </FeatureGroup>
            );
        })}
        {showLastKnown && lastKnowLocation && <GeoJSON data={lastKnowLocation} onEachFeature={(feature, layer) => {
            const props = feature.properties as PointProperties;
            const content = <div>
                <strong>Timestamp</strong>: {(new Date(props.timestamp)).toLocaleString()}<br />
                <strong>Speed</strong>: {props.speed}<br />
                <strong>Altitude</strong>: {props.altitude}<br />
                <strong>Vertical accuracy</strong>: {props.verticalAccuracy}<br />
                <strong>Course</strong>: {props.course}<br />
                <strong>Course accuracy</strong>: {props.courseAccuracy}<br />
                <strong>Battery level</strong>: {props.batteryLevel}<br />
                <strong>Battery state</strong>: {props.batteryState}<br />
                <strong>Device</strong>: {props.deviceId}<br />
                <strong>SSID</strong>: {props.ssid}<br />
                {props.motions && <><strong>Motions</strong>: {props.motions.join(",")}<br /></>}
            </div>;
            layer.bindTooltip(renderToStaticMarkup(content))
        }} />}
    </>);
}

export function Maps({ locations, lastKnowLocation, zoom, className, showLines, showPoints, showLastKnown }: MapsProps) {
    let center = [-6.175, 106.8275];
    if (locations.features.length > 0) {
        const coords = turf.center(locations).geometry.coordinates;
        center = [coords[1], coords[0]];
    };

    return (
        <MapContainer
            key={hash(locations)}
            className={cn("size-full", className)}
            center={center as LatLngTuple}
            zoom={zoom ?? 13}
            scrollWheelZoom={true}
            zoomControl={false}>
            <ZoomControl position="bottomright" />
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <InnerMaps locations={locations} lastKnowLocation={lastKnowLocation} showLines={showLines} showPoints={showPoints} showLastKnown={showLastKnown} />
        </MapContainer>
    );
}
