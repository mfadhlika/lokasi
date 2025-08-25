import { LatLngBounds, type LatLngTuple } from "leaflet";
import { useMemo } from "react";
import type { DateRange } from "react-day-picker";
import { useSearchParams } from "react-router";

export type Filter = {
    date?: DateRange,
    device?: string,
    offset?: number,
    limit?: number,
    bounds?: LatLngBounds
};

export function useLocationFilter(): [Filter, (filter: Filter) => void] {
    const [searchParams, setSearchParams] = useSearchParams();
    const filter: Filter = useMemo(() => {
        let start;
        if (searchParams.get('date_from')) {
            start = new Date(searchParams.get('date_from')!);
        } else {
            start = new Date();
            start.setHours(0, 0, 0, 0);
        }

        let end;
        if (searchParams.get("date_to")) {
            end = new Date(searchParams.get("date_to")!);
        } else {
            end = new Date();
            end.setHours(23, 59, 59, 59);
        }
        const device = searchParams.get('device') ?? undefined;
        const offset = searchParams.get('offset') ? Number(searchParams.get('offset')) : undefined;
        const limit = searchParams.get('limit') ? Number(searchParams.get('limit')) : undefined;
        const bounds = searchParams.get('bounds')?.split(',').reduce((bbox, v, i) => {
            if (i % 2 == 0) bbox.push([Number(v)]);
            else bbox[bbox.length - 1].unshift(Number(v));
            return bbox;
        }, [] as number[][]) as LatLngTuple[];

        return {
            date: {
                from: start,
                to: end,
            },
            device,
            offset,
            limit,
            bounds: bounds ? new LatLngBounds(bounds) : undefined
        };
    }, [searchParams])

    const setFilter = (filter: Filter) => {
        const params = new URLSearchParams();
        if (filter.date?.from) params.set("date_from", filter.date.from.toJSON());
        if (filter.date?.to) params.set("date_to", filter.date.to.toJSON());
        if (filter.device) params.set("device", filter.device);
        if (filter.offset) params.set("offset", filter.offset.toString());
        if (filter.bounds) params.set("bounds", filter.bounds.toBBoxString());
        setSearchParams(params);
    };

    return [filter, setFilter];
}
