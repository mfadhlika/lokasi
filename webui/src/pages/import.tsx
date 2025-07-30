import { axiosInstance } from "@/lib/request";
import { useState, useEffect } from "react";
import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { ImportDialog } from "@/components/import-dialog";
import { Header } from "@/components/header";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { MoreHorizontal } from "lucide-react";
import { toast } from "sonner";
import type { Response } from "@/types/response";
import type { Import } from "@/types/import";

export default function ImportPage() {
    const [data, setData] = useState<Import[]>([]);

    useEffect(() => {
        axiosInstance.get<Response<Import[]>>(`v1/import`)
            .then(({ data }) => {
                setData(data.data);
            }).catch(err => toast.error(`Failed to get user's import data: ${err}`));
    }, []);


    const columns: ColumnDef<Import>[] = [
        {
            accessorKey: "createdAt",
            header: "Created At",
            cell: ({ row }) => (<span>{(new Date(row.getValue('createdAt'))).toLocaleString()}</span>)
        },
        {
            accessorKey: "source",
            header: "Source",
        },
        {
            accessorKey: "filename",
            header: "Filename"
        },
        {
            accessorKey: "count",
            header: "Count"
        },
        {
            accessorKey: "done",
            header: "Done"
        },
        {
            id: "actions",
            enableHiding: false,
            cell: () => (
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8 p-0">
                            <span className="sr-only">Open menu</span>
                            <MoreHorizontal />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem asChild>
                            <Button variant="ghost" onClick={() => {

                            }}>
                                Download
                            </Button>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                            <Button variant="ghost" onClick={() => {

                            }}>
                                Delete
                            </Button>
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            )
        }
    ];

    return (
        <>
            <Header>
                <ImportDialog />
            </Header>
            <div className="flex flex-1 flex-col">
                <div className="@container/main flex flex-1 flex-col gap-4 p-4">
                    <DataTable columns={columns} data={data} />
                </div>
            </div>
        </>
    );
}
