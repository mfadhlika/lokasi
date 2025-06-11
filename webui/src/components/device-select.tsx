import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {useEffect, useState} from "react";
import {axiosInstance} from "@/lib/request.ts";

export interface DeviceSelectProps {
    onSelectedDevice: (device: string | undefined) => void
}

export const DeviceSelect = ({onSelectedDevice}: DeviceSelectProps) => {
    const [devices, setDevices] = useState<string[]>([]);

    useEffect(() => {
        axiosInstance.get("v1/user/devices").then(res => setDevices(res.data));
    }, []);

    return (
        <Select onValueChange={onSelectedDevice}>
            <SelectTrigger className="bg-white">
                <SelectValue placeholder="Select a device"/>
            </SelectTrigger>
            <SelectContent className="z-10000">
                {devices.map(device => <SelectItem value={device}>{device}</SelectItem>)}
            </SelectContent>
        </Select>
    );
};