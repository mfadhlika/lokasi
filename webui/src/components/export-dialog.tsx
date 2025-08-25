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
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form.tsx";
import { useEffect, useState } from "react";
import { cn, toISOLocal } from "@/lib/utils";
import { toast } from "sonner";
import type { Feature, MultiLineString } from "geojson";
import { PreviewMaps } from "./preview-maps";
import * as turf from "@turf/turf";
import { locationService } from "@/services/location-service";
import type { Export as ExportRequest } from "@/types/requests/export";
import { exportFormSchema } from "@/types/schema/export";
import { exportService } from "@/services/export-service";



export const ExportDialog = ({ className }: React.ComponentProps<"div">) => {
    const [open, setOpen] = useState(false);
    const [locations, setLocations] = useState<Feature<MultiLineString>>(turf.multiLineString([]));

    const form = useForm<ExportRequest>({
        resolver: zodResolver(exportFormSchema),
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

        locationService.fetchLocations({ start: new Date(startAt), end: new Date(endAt) })
            .then(({ data }) => {
                const coordinates = data.features.map(feature => feature.geometry.coordinates);
                setLocations(turf.multiLineString([coordinates]));
            });
    }, [startAt, endAt]);

    const onSubmit = (values: ExportRequest) => {
        exportService.createExport(values)
            .then((data) => {
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
                    <PreviewMaps className="rounded-md min-h-[200px] flex-1" locations={locations} />
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
