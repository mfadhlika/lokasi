import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {useEffect, useState} from "react";
import {axiosInstance} from "@/lib/request.ts";
import {Smartphone} from "lucide-react";

export interface DeviceSelectProps {
    onSelectedDevice: (device: string | undefined) => void
}

export const DeviceSelect = ({onSelectedDevice}: DeviceSelectProps) => {
    const [devices, setDevices] = useState<string[]>([]);
    const [value, setValue] = useState<string>('all')

    useEffect(() => {
        axiosInstance.get("v1/user/devices").then(res => setDevices(res.data));
    }, []);

    useEffect(() => {
        onSelectedDevice(value);
    }, [value]);

    return (
        <Select value={value} onValueChange={setValue}>
            <SelectTrigger className="bg-white">
                <Smartphone/>
                <SelectValue placeholder="Select a device"/>
            </SelectTrigger>
            <SelectContent className="z-10000">
                <SelectItem key="all" value="all">All devices</SelectItem>
                {devices.map(device => <SelectItem key={device} value={device}>{device}</SelectItem>)}
            </SelectContent>
        </Select>
    );
};