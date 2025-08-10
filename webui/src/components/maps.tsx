import type { Feature, FeatureCollection, LineString, Point } from "geojson";
import { MapContainer, ZoomControl, TileLayer, FeatureGroup, CircleMarker, Popup, Polyline, Tooltip, useMap, Marker, } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import type { LineStringProperties, PointProperties } from "@/types/properties";
import { cn } from "@/lib/utils";
import * as turf from "@turf/turf";
import type { Checked } from "@/types/checked";
import L from "leaflet";

export type MapsProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection<Point, PointProperties>,
    lastKnowLocation?: Feature<Point, PointProperties>,
    zoom?: number,
    showPoints?: Checked,
    showLines?: Checked,
    showLastKnown?: Checked
}

function Markers({ locations, showLines, showPoints, showLastKnown, lastKnowLocation }: MapsProps) {
    const map = useMap();

    if (locations.features.length > 0) {
        const coords = turf.center(locations).geometry.coordinates;
        map.setView([coords[1], coords[0]]);
    };

    const groupped: Feature[][] = [[]];
    for (let i = 1; i < locations.features.length; i++) {
        const fromFeature = locations.features[i - 1];
        const toFeature = locations.features[i];

        groupped[groupped.length - 1].push(fromFeature);

        const startAt = Date.parse(fromFeature.properties.timestamp);
        const endAt = Date.parse(toFeature.properties.timestamp);

        const duration = (startAt - endAt) / 1000;
        if (duration > 60 * 15) {
            groupped[groupped.length - 1].push(toFeature);
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

        if (i === locations.features.length - 1)
            groupped[groupped.length - 1].push(toFeature);
    }

    return (<>
        {groupped.map((group, groupIndex) =>
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
                {group.map((feature) => {
                    switch (feature.geometry.type) {
                        case 'LineString': {
                            if (!showLines) return null;
                            const props = feature.properties as LineStringProperties;
                            const position = L.GeoJSON.coordsToLatLngs(turf.getCoords(feature as Feature<LineString>));
                            return (
                                <Polyline
                                    key={JSON.stringify({ ...position, startAt: props.startAt, endAt: props.endAt })}
                                    positions={position}>
                                    <Tooltip>
                                        <div>
                                            <strong>Distance</strong>: {props.distance?.toFixed(2) ?? 0} {props.distanceUnit}<br />
                                            <strong>Speed</strong>: {props.speed?.toFixed(2) ?? 0} {props.speedUnit}<br />
                                            <strong>Start at</strong>: {(new Date(props.startAt)).toLocaleString()}<br />
                                            <strong>End at</strong>: {(new Date(props.endAt)).toLocaleString()}<br />
                                            {props.motions && <><strong>Motions</strong>: {props.motions.join(",")}<br /></>}
                                        </div>
                                    </Tooltip>
                                </Polyline>
                            );
                        }
                        case 'Point': {
                            if (!showPoints) return null;
                            const props = feature.properties as PointProperties;
                            const position = L.GeoJSON.coordsToLatLng(turf.getCoord(feature as Feature<Point>) as [number, number]);
                            return (
                                <CircleMarker
                                    key={JSON.stringify({ ...position, timestamp: props.timestamp })}
                                    center={position}
                                    radius={5}>
                                    <Popup>
                                        <div>
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
                                    </Popup>
                                </CircleMarker>);
                        }
                    }
                })}
            </FeatureGroup>
        )}
        {showLastKnown &&
            lastKnowLocation &&
            <Marker position={L.GeoJSON.coordsToLatLng(turf.getCoord(lastKnowLocation as Feature<Point>) as [number, number])}>
                <Popup>
                    <div>
                        <strong>Timestamp</strong>: {(new Date(lastKnowLocation.properties.timestamp)).toLocaleString()}<br />
                        <strong>Speed</strong>: {lastKnowLocation.properties.speed}<br />
                        <strong>Altitude</strong>: {lastKnowLocation.properties.altitude}<br />
                        <strong>Vertical accuracy</strong>: {lastKnowLocation.properties.verticalAccuracy}<br />
                        <strong>Course</strong>: {lastKnowLocation.properties.course}<br />
                        <strong>Course accuracy</strong>: {lastKnowLocation.properties.courseAccuracy}<br />
                        <strong>Battery level</strong>: {lastKnowLocation.properties.batteryLevel}<br />
                        <strong>Battery state</strong>: {lastKnowLocation.properties.batteryState}<br />
                        <strong>Device</strong>: {lastKnowLocation.properties.deviceId}<br />
                        <strong>SSID</strong>: {lastKnowLocation.properties.ssid}<br />
                        {lastKnowLocation.properties.motions && <><strong>Motions</strong>: {lastKnowLocation.properties.motions.join(",")}<br /></>}
                    </div>
                </Popup>
            </Marker>}
    </>);
}

export function Maps(props: MapsProps) {
    return (
        <MapContainer
            className={cn("size-full", props.className)}
            center={[-6.175, 106.8275]}
            zoom={props.zoom ?? 13}
            scrollWheelZoom={true}
            zoomControl={false}>
            <ZoomControl position="bottomright" />
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <Markers {...props} />
        </MapContainer>
    );
}
