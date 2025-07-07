import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useEffect, useState } from "react";
import { axiosInstance } from "@/lib/request.ts";
import { Smartphone } from "lucide-react";
import { cn } from "@/lib/utils";

export interface DeviceSelectProps {
    className?: string,
    selectedDevice: string,
    onSelectedDevice: (device: string) => void,
}

export const DeviceSelect = ({ className, selectedDevice, onSelectedDevice }: DeviceSelectProps) => {
    const [devices, setDevices] = useState<string[]>([]);

    useEffect(() => {
        axiosInstance.get("v1/user/devices").then(res => setDevices(res.data));
    }, []);


    return (
        <Select value={selectedDevice} onValueChange={onSelectedDevice}>
            <SelectTrigger className={cn("bg-white", className)}>
                <Smartphone />
                <SelectValue placeholder="Select a device" />
            </SelectTrigger>
            <SelectContent className="z-10000">
                <SelectItem key="all" value="all">All devices</SelectItem>
                {devices.map(device => <SelectItem key={device} value={device}>{device}</SelectItem>)}
            </SelectContent>
        </Select>
    );
};
