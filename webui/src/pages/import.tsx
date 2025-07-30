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
            accessorKey: "id",
            header: "ID",
        },
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
            cell: ({ row }) => (
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
                                const importId = row.getValue("id") as number;
                                const filename = row.getValue("filename") as number;
                                const promise = axiosInstance.get(`v1/import/${importId}/raw`, {
                                    responseType: 'blob'
                                });
                                toast.promise(promise, {
                                    loading: `downloading import ${importId}`,
                                    success: (res) => {
                                        const href = URL.createObjectURL(res.data);
                                        return <span>download completed, click <a className="underline" href={href} download={filename}>here</a> to save</span>;
                                    },
                                    error: (err) => `failed to download: ${err}`,
                                    duration: Infinity,
                                    closeButton: true
                                });

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
