import type { LatLngBounds } from "leaflet";

export type LocationQuery = {
    start?: Date,
    end?: Date,
    device?: string,
    offset?: number,
    limit?: number,
    bounds?: LatLngBounds
};
