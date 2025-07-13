import { axiosInstance } from "@/lib/request";
import type { FeatureCollection, Point } from "geojson";
import { useState, useEffect } from "react";
import type { DateRange } from "react-day-picker";
import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "@/components/data-table";
import { DatePicker } from "@/components/date-picker";
import { DeviceSelect } from "@/components/device-select";
import { Button } from "@/components/ui/button";
import { ImportDialog } from "@/components/import-dialog";
import { Header } from "@/components/header";

type Location = {
    timestamp: string,
    coordinates: string,
    device: string,
    altitude: number,
    speed: number,
    accuracy: number,
    motions: string[],
    course: number,
    courseAccuracy: number,
    battery: number,
    batteryState: string,
    ssid: string
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
    },
    {
        accessorKey: "speed",
        header: "Speed"
    },
    {
        accessorKey: "accuracy",
        header: "Accuracy"
    },
    {
        accessorKey: "motions",
        header: "Motions"
    },
    {
        accessorKey: "course",
        header: "Course"
    },
    {
        accessorKey: "courseAccuracy",
        header: "Course Accuracy"
    },
    {
        accessorKey: "battery",
        header: "Battery"
    },
    {
        accessorKey: "batteryState",
        header: "Battery State"
    },
    {
        accessorKey: "ssid",
        header: "SSID"
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
    const [offset, setOffset] = useState<number>(0);
    const [limit, _setLimit] = useState<number>(25);

    useEffect(() => {
        const params = new URLSearchParams();
        params.set('offset', `${offset}`);
        params.set('limit', `${limit}`);
        if (date?.from) {
            params.set('start', date.from.toISOString());
        }
        if (date?.to) {
            params.set('end', date.to.toISOString());
        }
        if (device !== 'all') {
            params.set('device', device);
        }
        axiosInstance.get(`v1/locations/raw?${params.toString()}`)
            .then((res) => {
                const newData = (res.data as FeatureCollection).features.map(feature => {
                    const coordinates = (feature.geometry as Point).coordinates;
                    return {
                        ...feature.properties,
                        timestamp: (new Date(feature.properties!['timestamp'] * 1000)).toLocaleString(),
                        coordinates: `${coordinates[1]}, ${coordinates[0]}`,
                    } as Location;
                })
                setData(newData);
            });
    }, [date, device, limit, offset]);

    return (
        <>
            <Header>
                <DatePicker variant="outline" date={date} setDate={setDate} />
                <DeviceSelect className="shadow-xs border-solid" selectedDevice={device} onSelectedDevice={setDevice} />
                <ImportDialog />
            </Header>
            <div className="flex flex-1 flex-col gap-4">
                <div className="w-full h-full p-4">
                    <DataTable columns={columns} data={data} />

                    <div className="flex items-center justify-end space-x-2 py-4">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                if (offset - limit >= 0) setOffset(offset - limit);
                            }}
                            disabled={offset === 0}
                        >
                            Previous
                        </Button>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                setOffset(offset + limit);
                            }}
                            disabled={false}
                        >
                            Next
                        </Button>
                    </div>
                </div>
            </div>
        </>
    );
}
