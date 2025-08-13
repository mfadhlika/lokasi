import type { Feature, FeatureCollection, LineString, Point } from "geojson";
import { FeatureGroup, CircleMarker, Popup, Polyline, Tooltip, Marker, } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import type { LineStringProperties, PointProperties } from "@/types/properties";
import { relativeTime } from "@/lib/utils";
import * as turf from "@turf/turf";
import type { Checked } from "@/types/checked";
import L from "leaflet";
import { renderToStaticMarkup } from "react-dom/server";
import { useAuth } from "@/hooks/use-auth";
import { Battery, BatteryCharging, BatteryFull, BatteryLow, Car, Clock, Compass, Gauge, TrendingUp, Wifi, Route, Smartphone, PlaneTakeoff, PlaneLanding } from "lucide-react";
import { useMemo } from "react";

export type MarkersProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection<Point, PointProperties>,
    lastKnowLocation?: Feature<Point, PointProperties>,
    zoom?: number,
    showPoints?: Checked,
    showLines?: Checked,
    showLastKnown?: Checked,
    showMovingPoints?: Checked,
}

function MarkerPopup(props: PointProperties) {
    const batteryIcon = (level?: number, state?: string) => {
        if (state && state === "charging") return <BatteryCharging />
        if (!level) return <Battery className="size-4" />
        else if (level < 20) return <BatteryLow className="size-4" />
        else if (level < 89) return <BatteryLow className="size-4" />
        else return <BatteryFull className="size-4" />
    };

    return (
        <Popup>
            <div className="flex flex-col gap-2">
                <div className="inline-flex gap-2 items-center"><Clock className="size-4" /> {(new Date(props.timestamp)).toLocaleString()}</div>
                <div className="inline-flex gap-2 items-center"><Gauge className="size-4" /> {props.speed} km/h</div>
                <div className="inline-flex gap-2 items-center"><TrendingUp className="size-4" /> {props.altitude}m ± {props.verticalAccuracy}m</div>
                <div className="inline-flex gap-2 items-center"><Compass className="size-4" /> {props.course}° ± {props.courseAccuracy}°</div>
                <div className="inline-flex gap-2 items-center">{batteryIcon(props.batteryLevel, props.batteryState)} {props.batteryLevel}%</div>
                <div className="inline-flex gap-2 items-center"><Smartphone className="size-4" /> {props.deviceId}</div>
                <div className="inline-flex gap-2 items-center"><Wifi className="size-4" /> {props.ssid}</div>
                {props.motions && <div className="inline-flex gap-2 items-center"><Car className="size-4" /> {props.motions.join(",")}</div>}
            </div>
        </Popup>
    );
}

function MarkerTooltip(props: LineStringProperties) {
    return (
        <Tooltip>
            <div className="flex flex-col gap-2">
                <div className="inline-flex gap-2 items-center"><Route className="size-4" />{props.distance?.toFixed(2) ?? 0} {props.distanceUnit}</div>
                <div className="inline-flex gap-2 items-center"><Gauge className="size-4" />{props.speed?.toFixed(2) ?? 0} {props.speedUnit}</div>
                <div className="inline-flex gap-2 items-center"><PlaneTakeoff className="size-4" /> {(new Date(props.startAt)).toLocaleString()}</div>
                <div className="inline-flex gap-2 items-center"><PlaneLanding className="size-4" /> {(new Date(props.endAt)).toLocaleString()}</div>
                {props.motions && <div><Car className="size-4" /> {props.motions.join(",")}</div>}
            </div>
        </Tooltip>
    );
}

export function Markers({ locations, showLines, showPoints, showMovingPoints, showLastKnown, lastKnowLocation }: MarkersProps) {
    const { userInfo } = useAuth();

    const groupped = useMemo(() => {
        return locations.features.reduce<Feature[][]>((groupped, cur, i, features) => {
            if (i == 0) return groupped;

            const prev = features[i - 1]

            groupped[groupped.length - 1].push(prev);

            const startAt = Date.parse(prev.properties.timestamp);
            const endAt = Date.parse(cur.properties.timestamp);

            const duration = (endAt - startAt) / 1000;
            if (duration > 60 * 15) {
                groupped[groupped.length - 1].push(cur);
                groupped.push([]);
                return groupped;
            };

            const from = turf.point((prev.geometry as Point).coordinates);
            const to = turf.point((cur.geometry as Point).coordinates);
            const distance = turf.distance(from, to, { units: "kilometers" });
            const speed = distance / duration / 3600;

            groupped[groupped.length - 1].push(turf.lineString([
                (prev.geometry as Point).coordinates,
                cur.geometry.coordinates,
            ], { distance, distanceUnit: "km", speed, speedUnit: "km/h", startAt, endAt }));

            return groupped;
        }, [[]]);
    }, [locations]);

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
                                    <MarkerTooltip {...props} />
                                </Polyline>
                            );
                        }
                        case 'Point': {
                            if (!showPoints) return null;
                            const props = feature.properties as PointProperties;
                            if (!showMovingPoints && props.speed && props.speed > 0) return null;
                            const position = L.GeoJSON.coordsToLatLng(turf.getCoord(feature as Feature<Point>) as [number, number]);
                            return (
                                <CircleMarker
                                    key={JSON.stringify({ ...position, timestamp: props.timestamp })}
                                    center={position}
                                    radius={5}>
                                    <MarkerPopup {...props} />
                                </CircleMarker>);
                        }
                    }
                })}
            </FeatureGroup>
        )}
        {showLastKnown &&
            lastKnowLocation &&
            <Marker icon={L.divIcon({
                className: "bg-transparent",
                html: renderToStaticMarkup(
                    <div className="size-8 py-1 rounded-full bg-cyan-500 border-3 border-[#3388ff] text-center">
                        <span className="decoration-white">{userInfo?.username?.substring(0, 2).toUpperCase()}</span>
                    </div>),
                iconAnchor: [10, 10]
            })} position={L.GeoJSON.coordsToLatLng(turf.getCoord(lastKnowLocation as Feature<Point>) as [number, number])}>
                <MarkerPopup {...lastKnowLocation.properties} />
                <Tooltip>{relativeTime(new Date(lastKnowLocation.properties.timestamp))}</Tooltip>
            </Marker>}
    </>);
}
