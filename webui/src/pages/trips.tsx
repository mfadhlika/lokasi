import { Header } from "@/components/header";
import { Maps } from "@/components/maps";
import { NewTripDialog } from "@/components/new-trip-dialog";
import { Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { axiosInstance } from "@/lib/request";
import type { TripProperties } from "@/types/properties";
import type { Response } from "@/types/response";
import type { FeatureCollection } from "geojson";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function TripsPage() {
    const [trips, setTrips] = useState<FeatureCollection>({ type: 'FeatureCollection', features: [] });

    useEffect(() => {
        axiosInstance
            .get<Response<FeatureCollection>>("v1/trips")
            .then(res => {
                setTrips(res.data.data);
            })
            .catch(err => {
                toast.error("faled to fetch trips", err);
            });
    }, []);
    return (
        <>
            <Header>
                <NewTripDialog />
            </Header>
            <div className="flex-1 grid sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 p-4">
                {
                    trips.features.map(feature => {
                        const props = feature.properties as TripProperties;
                        return (
                            <Card className="aspect-square">
                                <CardHeader>
                                    <CardTitle>{props.title}</CardTitle>
                                    <CardDescription>
                                        {(new Date(props.startAt).toLocaleDateString())} - {(new Date(props.endAt)).toLocaleDateString()}
                                    </CardDescription>
                                    <CardAction>

                                    </CardAction>
                                </CardHeader>
                                <CardContent>
                                    <Maps className="min-h-[200px]" locations={feature} />
                                </CardContent>
                                <CardFooter className="flex-col gap-2">

                                </CardFooter>
                            </Card>
                        );
                    })
                }
            </div>
        </>
    );
}
