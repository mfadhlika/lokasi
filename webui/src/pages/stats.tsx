import { Header } from "@/components/header";
import { Button } from "@/components/ui/button";
import { Card, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { axiosInstance } from "@/lib/request";
import { type Response } from "@/types/response";
import { type Stats } from "@/types/stats";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function StatsPage() {
    const [stats, setStats] = useState<Stats>({
        totalPoints: 0,
        totalReverseGeocodedPoints: 9
    });

    useEffect(() => {
        axiosInstance.get<Response<Stats>>(`v1/stats`)
            .then(({ data }) => setStats(data.data)).catch(err => {
                toast.error("failed to fetch stats", err);
            });
    }, []);

    const handleStartJob = () => {
        axiosInstance.post<Response>('v1/locations/reverse')
            .then(({ data }) => {
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
                </div>
            </div>
        </>
    );
}
