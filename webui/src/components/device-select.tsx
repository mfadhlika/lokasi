import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useEffect, useState } from "react";
import { Smartphone } from "lucide-react";
import { cn } from "@/lib/utils";
import { deviceService } from "@/services/device-service";
import { toast } from "sonner";

export interface DeviceSelectProps {
    className?: string,
    selectedDevice: string,
    onSelectedDevice: (device: string) => void,
}

export const DeviceSelect = ({ className, selectedDevice, onSelectedDevice }: DeviceSelectProps) => {
    const [devices, setDevices] = useState<string[]>([]);

    useEffect(() => {
        deviceService.fetchDevices()
            .then(res => setDevices(res.data))
            .catch(err => {
                toast.error("faled to fetch trips", err);
            });
    });

    return (
        <Select value={selectedDevice} onValueChange={onSelectedDevice}>
            <SelectTrigger className={cn("bg-white", className)}>
                <Smartphone />
                <SelectValue placeholder="Select a device" />
            </SelectTrigger>
            <SelectContent className="z-10000">
                <SelectItem key="all" value="all">All devices</SelectItem>
                {devices.filter(device => device !== "").map(device => <SelectItem key={device} value={device}>{device}</SelectItem>)}
            </SelectContent>
        </Select>
    );
};
