import type { Point } from "geojson";
import { useState, useEffect } from "react";
import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "@/components/data-table";
import { DatePicker } from "@/components/date-picker";
import { DeviceSelect } from "@/components/device-select";
import { Button } from "@/components/ui/button";
import { Header } from "@/components/header";
import { toast } from "sonner";
import { useLocationFilter } from "@/hooks/use-location-filter";
import type { Location } from "@/types/location";
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip";
import { PreviewMaps } from "@/components/preview-maps";
import { locationService } from "@/services/location-service";

export default function DataPage() {
    const [data, setData] = useState<Location[]>([]);
    const [filter, setFilter] = useLocationFilter();

    const date = filter.date;
    const device = filter.device || 'all';
    const offset = filter.offset || 0;
    const limit = filter.limit || 25;

    useEffect(() => {
        locationService.fetchLocations({ start: date?.from, end: date?.to, device })
            .then(({ data }) => {
                const newData = data.features.map(feature => {
                    return {
                        ...feature.properties,
                        coordinates: feature.geometry,
                    } as Location;
                })
                setData(newData);
            }).catch(err => toast.error(`Failed to get user's location data: ${err}`));
    }, [date, device, limit, offset]);

    const columns: ColumnDef<Location>[] = [
        {
            accessorKey: "timestamp",
            header: "Timestamp",
            cell: ({ row }) => (<span>{(new Date(row.getValue('timestamp'))).toLocaleString()}</span>)
        },
        {
            accessorKey: "coordinates",
            header: "Coordinates",
            cell: ({ row }) => {
                const coordinates = (row.getValue("coordinates") as Point).coordinates;
                return (
                    <Tooltip>
                        <TooltipTrigger><span>{`${coordinates[1]}, ${coordinates[0]}`}</span></TooltipTrigger>
                        <TooltipContent className="p-1" side="right">
                            <PreviewMaps className="rounded-md w-[250px] h-[200px] m-0" locations={row.getValue("coordinates")} zoom={13} />
                        </TooltipContent>
                    </Tooltip>
                );
            }
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
            accessorKey: "batteryLevel",
            header: "Battery level"
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

    return (
        <>
            <Header>
                <DatePicker variant="outline" date={date} setDate={(date) => setFilter({
                    ...filter,
                    date,
                })} />
                <DeviceSelect className="shadow-xs border-solid" selectedDevice={device} onSelectedDevice={(device) => setFilter({ ...filter, device })} />
            </Header>
            <div className="flex flex-1 flex-col">
                <div className="@container/main flex flex-1 flex-col gap-4 p-4">
                    <DataTable columns={columns} data={data} />
                    <div className="flex items-center justify-end space-x-2 py-4">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                if (offset - limit >= 0) setFilter({
                                    ...filter,
                                    offset: offset - limit
                                });
                            }}
                            disabled={offset === 0}
                        >
                            Previous
                        </Button>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                setFilter({ ...filter, offset: offset + limit });
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
