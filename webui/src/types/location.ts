import type { Point } from "geojson";
import type { PointProperties } from "./properties";

type Location = PointProperties & {
    coordinates: Point,
}

export type { Location };
