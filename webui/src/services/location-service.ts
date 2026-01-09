import { axiosInstance } from "@/lib/request";
import { stompClient } from "@/lib/websocket";
import type { PointProperties } from "@/types/properties";
import type { LocationQuery } from "@/types/requests/location";
import type { Response } from "@/types/response";
import type { StompSubscription } from "@stomp/stompjs";
import type { Feature, FeatureCollection, Point } from "geojson";

class LocationService {
    lastLocationSubscription?: StompSubscription;
    lastLocationSubscriptionRetryId?: number;


    fetchLocations = async (query: LocationQuery): Promise<Response<FeatureCollection<Point, PointProperties>>> => {
        const params = new URLSearchParams();
        if (query.start) params.set('start', query.start.toJSON());
        if (query.end) params.set('end', query.end.toJSON());
        if (query.device && query.device != 'all') params.append('device', query.device);
        if (query.bounds) params.set('bounds', query.bounds.toBBoxString());
        if (query.limit) params.set('limit', query.limit.toString());
        if (query.offset) params.set('offset', query.offset.toString());

        return await axiosInstance
            .get<Response<FeatureCollection<Point, PointProperties>>>(`v1/locations?${params.toString()}`)
            .then(res => res.data);
    }

    fetchLastLocation = async (): Promise<Response<Feature<Point, PointProperties>>> => {
        return axiosInstance.get<Response<Feature<Point, PointProperties>>>('v1/locations/last')
            .then(res => res.data);
    }

    subscribeLastLocation = (username: string, callback: (feature: Feature<Point, PointProperties>) => void) => {
        this.lastLocationSubscriptionRetryId = window.setInterval(() => {
            if (!stompClient.connected) return;

            this.lastLocationSubscription = stompClient.subscribe(`/user/${username}/topic/last-known-location`, (message) => {
                callback(JSON.parse(message.body));
            });
            console.info("subscribed to last location");

            clearInterval(this.lastLocationSubscriptionRetryId);
        }, stompClient.reconnectDelay);


    }

    unsubscribeLastLocation = () => {
        this.lastLocationSubscription?.unsubscribe();
        this.lastLocationSubscription = undefined;
    }

    reverseGeocode = async (): Promise<Response> => {
        return await axiosInstance.post<Response>('v1/locations/reverse')
            .then(res => res.data);
    }
}

export const locationService: LocationService = new LocationService();
