import { axiosInstance } from "@/lib/request";
import type { FeatureCollection, Point } from "geojson";
import { useState, useEffect } from "react";
import type { DateRange } from "react-day-picker";
import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "@/components/data-table";
import { DatePicker } from "@/components/date-picker";
import { DeviceSelect } from "@/components/device-select";
import { Button } from "@/components/ui/button";

type Location = {
    timestamp: string,
    coordinates: string,
    device: string,
    altitude: number,
}

const columns: ColumnDef<Location>[] = [
    {
        accessorKey: "timestamp",
        header: "Timestamp"
    },
    {
        accessorKey: "coordinates",
        header: "Coordinates"
    },
    {
        accessorKey: "device",
        header: "Device"
    },
    {
        accessorKey: "altitude",
        header: "Altitude"
    }
];


export default function Data() {
    const [data, setData] = useState<Location[]>([]);
    const [date, setDate] = useState<DateRange | undefined>(() => {
        const start = new Date();
        start.setHours(0, 0, 0, 0);

        const end = new Date();
        end.setHours(23, 59, 59, 59);

        return {
            from: start,
            to: end,
        };
    });
    const [device, setDevice] = useState<string>('all');
    useEffect(() => {
        const params = new URLSearchParams();
        params.set('raw', "true");
        if (date?.from) {
            params.set('start', date.from.toISOString());
        }
        if (date?.to) {
            params.set('end', date.to.toISOString());
        }
        if (device !== 'all') {
            params.set('device', device);
        }
        axiosInstance.get(`v1/locations?${params.toString()}`)
            .then((res) => {
                const newData = (res.data as FeatureCollection).features.map(feature => {
                    const coordinates = (feature.geometry as Point).coordinates;
                    const properties = feature.properties!;
                    return {
                        timestamp: properties['timestamp'],
                        coordinates: `${coordinates[1]}, ${coordinates[0]}`,
                        device: properties['device'],
                        altitude: properties['altitude']
                    };
                })
                setData(newData);
            });
    }, [date, device]);
    return (
        <div className="w-full h-full p-4">
            <div className="flex items-center gap-4 py-4">
                <DatePicker variant="outline" date={date} setDate={setDate} />
                <DeviceSelect className="shadow-xs border-solid" selectedDevice={device} onSelectedDevice={setDevice} />
            </div>
            <DataTable columns={columns} data={data} />

            <div className="flex items-center justify-end space-x-2 py-4">
                <Button
                    variant="outline"
                    size="sm"
                    onClick={() => { }}
                    disabled={false}
                >
                    Previous
                </Button>
                <Button
                    variant="outline"
                    size="sm"
                    onClick={() => { }}
                    disabled={false}
                >
                    Next
                </Button>
            </div>
        </div>
    );
}
