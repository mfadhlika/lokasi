import { axiosInstance } from "@/lib/request";
import { useState, useEffect } from "react";
import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { Header } from "@/components/header";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { MoreHorizontal } from "lucide-react";
import { toast } from "sonner";
import type { Response } from "@/types/response";
import type { Export } from "@/types/export";
import { ExportDialog } from "@/components/export-dialog";

export default function ExportPage() {
    const [data, setData] = useState<Export[]>([]);

    useEffect(() => {
        axiosInstance.get<Response<Export[]>>(`v1/export`)
            .then(({ data }) => {
                setData(data.data);
            }).catch(err => toast.error(`Failed to get user's export data: ${err}`));
    }, []);


    const columns: ColumnDef<Export>[] = [
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
            accessorKey: "filename",
            header: "Filename"
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
                                const exportId = row.getValue("id") as number;
                                const filename = row.getValue("filename") as number;
                                const promise = axiosInstance.get(`v1/export/${exportId}/raw`, {
                                    responseType: 'blob'
                                });
                                toast.promise(promise, {
                                    loading: `downloading export ${exportId}`,
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
                <ExportDialog />
            </Header>
            <div className="flex flex-1 flex-col">
                <div className="@container/main flex flex-1 flex-col gap-4 p-4">
                    <DataTable columns={columns} data={data} />
                </div>
            </div>
        </>
    );
}
