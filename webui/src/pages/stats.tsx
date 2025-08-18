import { Header } from "@/components/header";
import { Button } from "@/components/ui/button";
import { axiosInstance } from "@/lib/request";
import { type Response } from "@/types/response";
import { toast } from "sonner";

export default function StatsPage() {
    const handleStartJob = () => {
        axiosInstance.post<Response>('v1/locations/reverse')
            .then(({ data }) => {
                toast.info(data.message);
            })
            .catch(err => {
                toast.error("failed to start reverse geocode job", err);
            });
    }
    return (
        <>
            <Header>
                <Button variant='outline' onClick={handleStartJob}>
                    Start reverse geocode job
                </Button>
            </Header>
            <div className="flex flex-1 flex-col">
                <div className="@container/main flex flex-1 flex-col gap-4 p-4">
                    WIP
                </div>
            </div>
        </>
    );
}
