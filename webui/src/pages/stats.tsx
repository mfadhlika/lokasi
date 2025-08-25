import { Header } from "@/components/header";
import { Button } from "@/components/ui/button";
import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { axiosInstance } from "@/lib/request";
import { locationService } from "@/services/location-service";
import { type Response } from "@/types/response";
import { type Stats } from "@/types/stats";
import { formatDate } from "date-fns";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function StatsPage() {
    const [stats, setStats] = useState<Stats>({
        totalPoints: 0,
        totalReverseGeocodedPoints: 0,
        totalCitiesVisited: 0,
        totalCountriesVisited: 0,
        lastPointTimestamp: undefined
    });

    useEffect(() => {
        axiosInstance.get<Response<Stats>>(`v1/stats`)
            .then(({ data }) => setStats(data.data)).catch(err => {
                toast.error("failed to fetch stats", err);
            });
    }, []);

    const handleStartJob = () => {
        locationService.reverseGeocode()
            .then(data => {
                toast.info(data.message);
            })
            .catch(err => {
                toast.error("failed to start reverse geocode job", err);
            });
    };

    return (
        <>
            <Header>
                <Button variant='outline' onClick={handleStartJob}>
                    Start reverse geocode job
                </Button>
            </Header>
            <div className="flex flex-1 flex-col">
                <div className="grid auto-rows-min gap-4 md:grid-cols-3 p-4">
                    <Card className="@container/card px-4">
                        <CardHeader>
                            <CardDescription>Points tracked</CardDescription>
                            <CardTitle>
                                <span className="text-xl">{stats.totalPoints > 0 ? stats.totalPoints : 'No points'}</span>
                            </CardTitle>
                            <CardFooter className="pl-0">
                                <span className="text-sm">{stats.lastPointTimestamp && formatDate(stats.lastPointTimestamp, 'LLL dd, y HH:mm')}</span>
                            </CardFooter>
                        </CardHeader>
                    </Card>
                    <Card className="@container/card px-4">
                        <CardHeader>
                            <CardDescription>Reverse geocoded points</CardDescription>
                            <CardTitle>
                                <span className="text-xl">{stats.totalReverseGeocodedPoints > 0 ? stats.totalReverseGeocodedPoints : 'No reverse geocoded points'}</span>
                            </CardTitle>
                        </CardHeader>
                    </Card>
                    <Card className="@container/card px-4">
                        <CardHeader>
                            <CardDescription>City visited</CardDescription>
                            <CardTitle>
                                <span className="text-xl">{stats.totalCitiesVisited > 0 ? stats.totalCitiesVisited : 'No reverse geocoded points'}</span>
                            </CardTitle>
                        </CardHeader>
                    </Card>
                    <Card className="@container/card px-4">
                        <CardHeader>
                            <CardDescription>Country visited</CardDescription>
                            <CardTitle>
                                <span className="text-xl">{stats.totalCountriesVisited > 0 ? stats.totalCountriesVisited : 'No reverse geocoded points'}</span>
                            </CardTitle>
                        </CardHeader>
                    </Card>
                </div>
            </div>
        </>
    );
}
