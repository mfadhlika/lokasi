import type { Feature, FeatureCollection, LineString, Point } from "geojson";
import { FeatureGroup, CircleMarker, Popup, Polyline, Tooltip, Marker, useMap } from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import type { LineStringProperties, PointProperties } from "@/types/properties";
import * as turf from "@turf/turf";
import type { Checked } from "@/types/checked";
import L, { LatLngBounds, type LatLngBoundsExpression } from "leaflet";
import { renderToStaticMarkup } from "react-dom/server";
import { useAuth } from "@/hooks/use-auth";
import { Battery, BatteryCharging, BatteryFull, BatteryLow, Car, Clock, Compass, Gauge, TrendingUp, Wifi, Route, Smartphone, PlaneTakeoff, PlaneLanding } from "lucide-react";
import { useCallback, useEffect, useMemo } from "react";
import { formatDistanceStrict, formatDistanceToNow } from "date-fns";
import { MapControl } from "./map-control";
import { useIsMobile } from "@/hooks/use-mobile";

export type MarkersProps = React.ComponentProps<"div"> & {
    locations: FeatureCollection<Point, PointProperties>,
    lastKnowLocation?: Feature<Point, PointProperties>,
    zoom?: number,
    showPoints?: Checked,
    showLines?: Checked,
    showLastKnown?: Checked,
    showMovingPoints?: Checked,
    showVisits?: Checked,
    bounded: boolean,
    onBoundsChange: (bounds: LatLngBounds) => void
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
                {props.motions && <div className="inline-flex gap-2 items-center"><Car className="size-4" /> {props.motions.join(",")}</div>}
            </div>
        </Tooltip>
    );
}

type Visit = {
    name?: string,
    coordinates: L.LatLng,
    startAt: Date,
    endAt: Date,
    mode?: string[],
    distance?: number
    address?: string
}

type Layers = {
    groupped: { points: Feature<Point, PointProperties>[], lines: Feature<LineString, LineStringProperties>[] }[],
    visits: Visit[]
}

export function MapLayers({ locations, showLines, showPoints, showMovingPoints, showLastKnown, lastKnowLocation, showVisits, bounded, onBoundsChange }: MarkersProps) {
    const { userInfo } = useAuth();
    const isMobile = useIsMobile();
    const map = useMap();

    const onDragEnd = useCallback(() => {
        onBoundsChange(map.getBounds());
    }, [map, onBoundsChange]);

    useEffect(() => {
        map.on({
            'dragend': onDragEnd,
            'zoomend': onDragEnd
        });
        return () => {
            map.off({
                'dragend': onDragEnd,
                'zoomend': onDragEnd
            });
        }
    }, [map, onDragEnd]);

    useEffect(() => {
        try {
            const bbox = turf.bbox(locations, { recompute: true });
            const bounds: LatLngBoundsExpression = [
                [bbox[1], bbox[0]],
                [bbox[3], bbox[2]],
            ];

            if (!bounded) map.fitBounds(bounds);
        } catch {
            // noop
        }
    }, [bounded, locations, map]);

    const { groupped, visits } = useMemo(() => {
        return locations.features.reduce<Layers>((props, cur, i, features) => {
            if (i == 0) {
                props.visits.push({
                    name: cur.properties.geocode?.features.at(0)?.properties?.name,
                    coordinates: L.GeoJSON.coordsToLatLng(cur.geometry.coordinates as [number, number]),
                    startAt: new Date(cur.properties.timestamp),
                    endAt: new Date(cur.properties.timestamp)
                });
                return props
            };

            const prev = features[i - 1]
            const prevVisit = props.visits[props.visits.length - 1];

            props.groupped[props.groupped.length - 1].points.push(prev);

            const startAt = Date.parse(prev.properties.timestamp);
            const endAt = Date.parse(cur.properties.timestamp);

            if (prevVisit.name === cur.properties.geocode?.features.at(0)?.properties?.name) prevVisit.endAt = new Date(cur.properties.timestamp);
            else if ((cur.properties.speed ?? 0) === 0) {
                const geocodeProps = cur.properties.geocode?.features.at(0)?.properties;

                let address = '';
                if (geocodeProps?.street) address += geocodeProps?.street + ','
                if (geocodeProps?.district) address += geocodeProps?.district + ','
                if (geocodeProps?.city) address += geocodeProps?.city + ','
                if (geocodeProps?.country) address += geocodeProps?.country + ' '
                if (geocodeProps?.postcode) address += geocodeProps?.postcode

                props.visits.push({
                    name: geocodeProps?.name ?? geocodeProps?.locality,
                    coordinates: L.GeoJSON.coordsToLatLng(cur.geometry.coordinates as [number, number]),
                    startAt: new Date(cur.properties.timestamp),
                    endAt: new Date(cur.properties.timestamp),
                    address
                });
            }

            const duration = (endAt - startAt) / 1000;
            if (duration > 60 * 15) {
                props.groupped.push({ points: [], lines: [] });
                return props;
            };

            const from = turf.point((prev.geometry as Point).coordinates);
            const to = turf.point((cur.geometry as Point).coordinates);
            const distance = turf.distance(from, to, { units: "kilometers" });
            const speed = distance / duration / 3600;
            const motions = [...(new Set((prev.properties.motions ?? []).concat(cur.properties.motions)))];

            props.groupped[props.groupped.length - 1].lines.push(turf.lineString([
                (prev.geometry as Point).coordinates,
                cur.geometry.coordinates,
            ], { distance, distanceUnit: "km", speed, speedUnit: "km/h", startAt: new Date(startAt), endAt: new Date(endAt), motions }));

            if (prevVisit.name === 'Moving') {
                prevVisit.endAt = new Date(endAt);
                prevVisit.mode = prevVisit.mode?.concat(cur.properties.motions)
                prevVisit.distance = (prevVisit.distance ?? 0) + distance;
            } else if (((prev.properties.speed ?? 0) !== 0) || ((prev.properties.motions ?? []).includes('automotive'))) {
                props.visits.push({
                    name: 'Moving',
                    coordinates: L.GeoJSON.coordsToLatLng(cur.geometry.coordinates as [number, number]),
                    startAt: new Date(endAt),
                    endAt: new Date(endAt),
                    distance,
                    mode: motions
                });
            }

            if (i == features.length - 1) props.groupped[props.groupped.length - 1].points.push(cur);
            return props;
        }, { groupped: [{ points: [], lines: [] }], visits: [] });
    }, [locations]);

    return (<>
        {showVisits && <MapControl position={isMobile ? "bottomright" : "topleft"} disableClickPropagation={true} disableScrollPropagation={true}>
            <div className="leaflet-touch bg-sidebar rounded-xl border border-gray-300 w-[calc(100vw-20px)] md:w-[20vw] max-h-[25vh] md:max-h-[calc(90vh)] overflow-y-auto flex flex-col p-4 gap-4">
                {visits.map((cur) => (
                    <div key={`${cur.name}-${cur.startAt.getTime()}`}
                        className="flex flex-col gap-1 cursor-pointer"
                        onClick={() => map.setView(cur.coordinates)}>
                        <div className="inline-flex gap-2 items-center"><span className="font-semibold">{cur.name ?? '?'}</span>{cur.mode?.includes('automotive') && <Car className="size-4" />}</div>
                        {cur.name === 'Moving' && <div>
                            <span>{cur.distance?.toFixed(2)} km · </span>
                            <span>{formatDistanceStrict(cur.endAt, cur.startAt)}</span>
                        </div>}
                        {cur.address && <span>{cur.address}</span>}
                        <span>{cur.startAt.toLocaleTimeString()} - {cur.endAt.toLocaleTimeString()}</span>
                    </div>
                ))}
                {visits.length === 0 && <span>No location recorded</span>}
            </div>
        </MapControl>}
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
                {showLines && group.lines.map((feature) => {
                    const props = feature.properties;
                    const position = L.GeoJSON.coordsToLatLngs(turf.getCoords(feature as Feature<LineString>));
                    return (
                        <Polyline
                            key={JSON.stringify({ ...position, startAt: props.startAt, endAt: props.endAt })}
                            positions={position}>
                            <MarkerTooltip {...props} />
                        </Polyline>
                    );
                })}
                {showPoints && group.points.map((feature, i) => {
                    const props = feature.properties as PointProperties;
                    if (!showMovingPoints &&
                        props.speed &&
                        props.speed > 0 &&
                        i > 0 && i < group.points.length - 1) return null;
                    const position = L.GeoJSON.coordsToLatLng(turf.getCoord(feature as Feature<Point>) as [number, number]);
                    return (
                        <CircleMarker
                            key={JSON.stringify({ ...position, timestamp: props.timestamp })}
                            center={position}
                            radius={5}
                            fill={true}
                            fillOpacity={1}
                            fillColor="white">
                            <MarkerPopup {...props} />
                        </CircleMarker>);
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
                <Tooltip>{formatDistanceToNow(new Date(lastKnowLocation.properties.timestamp))} ago</Tooltip>
            </Marker>}
    </>);
}
