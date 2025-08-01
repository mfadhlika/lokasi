type PointProperties = {
    timestamp: string,
    altitude: number,
    speed: number,
    course: number,
    courseAccuracy: number,
    accuracy: number,
    verticalAccuracy: number,
    motions: string[],
    batteryState: string,
    batteryLevel: number,
    deviceId: string,
    ssid: string,
    rawData: string
};

type LineStringProperties = {
    distance?: number,
    distanceUnit: string,
    speed?: number,
    speedUnit: string,
    startAt: string,
    endAt: string,
    motions: string[]
};

type TripProperties = {
    title: string,
    startAt: string,
    endAt: string
}

export type { PointProperties, LineStringProperties, TripProperties };
