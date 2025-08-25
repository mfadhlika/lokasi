import { Header } from "@/components/header";
import { NewTripDialog } from "@/components/new-trip-dialog";
import { PreviewMaps } from "@/components/preview-maps";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { tripService } from "@/services/trip-service";
import type { TripProperties } from "@/types/properties";
import type { FeatureCollection } from "geojson";
import { Map, Trash } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router";
import { toast } from "sonner";

export default function TripsPage() {
    const [count, setCount] = useState<number>(0);
    const [trips, setTrips] = useState<FeatureCollection>({ type: 'FeatureCollection', features: [] });

    useEffect(() => {
        tripService.fetchTrips()
            .then(res => {
                setTrips(res.data);
            })
            .catch(err => {
                toast.error("faled to fetch trips", err);
            });
    }, [count]);

    const handleDelete = (id: number) => {
        tripService.deleteTrip(id)
            .then(() => {
                setCount((count) => count + 1);
            })
            .catch(err => {
                toast.error("faled to delete trip", err);
            });
    };

    return (
        <>
            <Header>
                <NewTripDialog onClose={() => setCount((count) => count + 1)} />
            </Header>
            <div className="flex-1 grid sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 p-4">
                {
                    trips.features.map((trip, index) => {
                        const props = trip.properties as TripProperties;
                        return (
                            <Card key={index} className="h-min">
                                <CardHeader>
                                    <CardTitle>{props.title}</CardTitle>
                                    <CardDescription>
                                        {(new Date(props.startAt).toLocaleDateString())} - {(new Date(props.endAt)).toLocaleDateString()}
                                    </CardDescription>
                                    <CardAction>

                                    </CardAction>
                                </CardHeader>
                                <CardContent>
                                    <PreviewMaps className="min-h-[200px]" locations={trip} />
                                </CardContent>
                                <CardFooter className="flex gap-2">
                                    <Button className="flex-1" variant="outline" asChild>
                                        <Link to={encodeURI(`/?date_from=${(new Date(props.startAt).toJSON())}&date_to=${(new Date(props.endAt).toJSON())}`)}>
                                            <Map />
                                            Maps
                                        </Link>
                                    </Button>
                                    <AlertDialog>
                                        <AlertDialogTrigger asChild>
                                            <Button>
                                                <Trash />
                                            </Button>
                                        </AlertDialogTrigger>
                                        <AlertDialogContent>
                                            <AlertDialogHeader>
                                                <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                                                <AlertDialogDescription>
                                                    This action cannot be undone.
                                                </AlertDialogDescription>
                                            </AlertDialogHeader>
                                            <AlertDialogFooter>
                                                <AlertDialogCancel>Cancel</AlertDialogCancel>
                                                <AlertDialogAction onClick={() => handleDelete(props.id)}>Continue</AlertDialogAction>
                                            </AlertDialogFooter>
                                        </AlertDialogContent>
                                    </AlertDialog>
                                </CardFooter>
                            </Card>
                        );
                    })
                }
            </div >
        </>
    );
}
