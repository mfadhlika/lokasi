import type { Point } from "geojson";

type Location = {
    timestamp: string,
    coordinates: Point,
    device: string,
    altitude: number,
    speed: number,
    accuracy: number,
    motions: string[],
    course: number,
    courseAccuracy: number,
    battery: number,
    batteryState: string,
    ssid: string,
    rawData: string
}

export type { Location };
