import { axiosInstance } from "@/lib/request";
import type { PointProperties } from "@/types/properties";
import type { LocationQuery } from "@/types/requests/location";
import type { Response } from "@/types/response";
import type { Feature, FeatureCollection, Point } from "geojson";

class LocationService {
    fetchLocations = async (query: LocationQuery): Promise<Response<FeatureCollection<Point, PointProperties>>> => {
        const params = new URLSearchParams();
        if (query.start) params.set('start', query.start.toJSON());
        if (query.end) params.set('end', query.end.toJSON());
        if (query.device && query.device != 'all') params.append('device', query.device);
        if (query.bounds) params.set('bounds', query.bounds.toBBoxString());

        return await axiosInstance
            .get<Response<FeatureCollection<Point, PointProperties>>>(`v1/locations?${params.toString()}`)
            .then(res => res.data);
    }

    fetchLastLocation = async (): Promise<Response<Feature<Point, PointProperties>>> => {
        return axiosInstance.get<Response<Feature<Point, PointProperties>>>('v1/locations/last')
            .then(res => res.data);
    }

    reverseGeocode = async (): Promise<Response> => {
        return await axiosInstance.post<Response>('v1/locations/reverse')
            .then(res => res.data);
    }
}

export const locationService: LocationService = new LocationService();
