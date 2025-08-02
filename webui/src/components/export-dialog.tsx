import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Loader2Icon, Map } from "lucide-react";
import { Input } from "@/components/ui/input.tsx";
import { z } from "zod/v4";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form.tsx";
import { axiosInstance } from "@/lib/request.ts";
import { useEffect, useState } from "react";
import { cn, toISOLocal } from "@/lib/utils";
import { toast } from "sonner";
import type { FeatureCollection, LineString } from "geojson";
import type { LatLngTuple } from "leaflet";
import type { Response } from "@/types/response";
import { Maps } from "./maps";

const formSchema = z.object({
    startAt: z.coerce.date<Date>(),
    endAt: z.coerce.date<Date>()
}).refine((data) => data.endAt > data.startAt, {
    path: ['endAt'],
    error: 'End at must be after start at'
});

export const ExportDialog = ({ className }: React.ComponentProps<"div">) => {
    const [open, setOpen] = useState(false);
    const [locations, setLocations] = useState<FeatureCollection>({ type: "FeatureCollection", features: [] });
    const [position, setPosition] = useState<LatLngTuple>([-6.175, 106.8275]);


    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {

        }
    });

    const { formState, watch } = form;

    const [startAt, endAt] = watch(['startAt', 'endAt']);

    useEffect(() => {
        if (!startAt || !endAt) {
            return;
        }

        const params = new URLSearchParams();
        params.set("start", (new Date(startAt)).toISOString());
        params.set("end", (new Date(endAt)).toISOString());

        axiosInstance.get<Response<FeatureCollection>>(`v1/locations?${params.toString()}`)
            .then(({ data }) => {
                setLocations(data.data);
                if (!data.data || data.data.features.length == 0) return;
                const last = data.data.features[data.data.features.length - 1].geometry as LineString;
                setPosition([last.coordinates[1][1], last.coordinates[1][0]]);
            });
    }, [startAt, endAt]);

    const onSubmit = (values: z.infer<typeof formSchema>) => {
        axiosInstance.post<Response>(`v1/export`, {
            startAt: values.startAt,
            endAt: values.endAt
        })
            .then(({ data }) => {
                toast.success(data.message)
                setOpen(false);
            })
            .catch(err => {
                toast.error(`Failed to export`, err);
            });
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" className={cn("shadow-xs", className)}>
                    <Map />
                    Export
                </Button>
            </DialogTrigger>
            <DialogContent className="z-10000 min-w-[50%]">
                <DialogHeader>
                    <DialogTitle>New Export</DialogTitle>
                    <DialogDescription>
                        Create new export
                    </DialogDescription>
                </DialogHeader>
                <div className="flex gap-4 flex-col md:flex-row">
                    <Maps className="rounded-md min-h-[200px] flex-1" locations={locations} position={position} />
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="flex-1 space-y-8">
                            <FormField control={form.control} name="startAt"
                                render={({ field, fieldState }) => (
                                    <FormItem>
                                        <FormLabel>Start at</FormLabel>
                                        <FormControl>
                                            <Input type="datetime-local" ref={field.ref} value={toISOLocal(field.value)} onChange={e => field.onChange(e.target.valueAsDate)} />
                                        </FormControl>
                                        {fieldState.error && <FormMessage>{fieldState.error.message}</FormMessage>}
                                    </FormItem>
                                )} />
                            <FormField control={form.control} name="endAt"
                                render={({ field, fieldState }) => (
                                    <FormItem>
                                        <FormLabel>End at</FormLabel>
                                        <FormControl>
                                            <Input type="datetime-local" ref={field.ref} value={toISOLocal(field.value)} onChange={e => field.onChange(e.target.valueAsDate)} />
                                        </FormControl>
                                        {fieldState.error && <FormMessage>{fieldState.error.message}</FormMessage>}
                                    </FormItem>
                                )} />
                            <Button type="submit" disabled={formState.isSubmitting}>
                                {formState.isSubmitting && <Loader2Icon className="animate-spin" />}
                                Save
                            </Button>
                        </form>
                    </Form>
                </div>
            </DialogContent>
        </Dialog>
    );
};
