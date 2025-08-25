import { axiosInstance } from "@/lib/request";
import type { TripProperties } from "@/types/properties";
import type { Trip } from "@/types/requests/trip";
import { type Response } from "@/types/response";
import type { Feature, FeatureCollection, Point } from "geojson";

class TripSerice {
    fetchTrips = async (): Promise<Response<FeatureCollection<Point, TripProperties>>> => {
        return await axiosInstance
            .get<Response<FeatureCollection<Point, TripProperties>>>("v1/trips")
            .then(res => res.data);
    }

    fetchTrip = async (uuid: string): Promise<Response<Feature<Point, TripProperties>>> => {
        return await axiosInstance
            .get<Response<Feature<Point, TripProperties>>>(`v1/trips/${uuid}`)
            .then(res => res.data);
    }

    createTrip = async (trip: Trip): Promise<Response<Feature<Point, TripProperties>>> => {
        return await axiosInstance
            .post(`v1/trips`, trip)
            .then(res => res.data);
    }

    deleteTrip = async (id: number): Promise<Response> => {
        return await axiosInstance
            .delete<Response>(`v1/trips/${id}`)
            .then(res => res.data);
    }
}

export const tripService = new TripSerice();
