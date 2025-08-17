import type { FeatureCollection } from "geojson";

type PointProperties = {
    timestamp: string,
    altitude?: number,
    speed?: number,
    course?: number,
    courseAccuracy?: number,
    accuracy?: number,
    verticalAccuracy?: number,
    motions: string[],
    batteryState?: string,
    batteryLevel?: number,
    deviceId: string,
    ssid?: string,
    rawData: string,
    geocode?: FeatureCollection
};

type LineStringProperties = {
    distance?: number,
    distanceUnit: string,
    speed?: number,
    speedUnit: string,
    startAt: Date,
    endAt: Date,
    motions: string[]
};

type TripProperties = {
    title: string,
    startAt: string,
    endAt: string
}

export type { PointProperties, LineStringProperties, TripProperties };
