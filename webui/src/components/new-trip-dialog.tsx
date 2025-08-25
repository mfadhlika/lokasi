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
import { Checkbox } from "@/components/ui/checkbox";
import { locationService } from "@/services/location-service";
import type { Trip } from "@/types/requests/trip";
import { tripFormSchema } from "@/types/schema/trip";
import { tripService } from "@/services/trip-service";

export type NewTripDialogProps = React.ComponentProps<"div"> & {
    onClose?: () => void
}

export const NewTripDialog = ({ className, onClose }: NewTripDialogProps) => {
    const [open, setOpen] = useState(false);
    const [locations, setLocations] = useState<Feature<MultiLineString>>(turf.multiLineString([]));

    const form = useForm<Trip>({
        resolver: zodResolver(tripFormSchema),
        defaultValues: {
            isPublic: false
        },
    });

    const { formState, watch, } = form;

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

    const onSubmit = (values: Trip) => {
        tripService.createTrip(values)
            .then(_ => {
                toast.success("Trip saved successfully");
                setOpen(false);
                if (onClose) onClose();
            })
            .catch(err => {
                toast.error(`Failed to save trip`, err);
            });
    }

    return (
        <Dialog open={open} onOpenChange={setOpen} >
            <DialogTrigger asChild>
                <Button variant="outline" className={cn("shadow-xs", className)}>
                    <Map />
                    New Trip
                </Button>
            </DialogTrigger>
            <DialogContent className="z-10000 min-w-[50%]">
                <DialogHeader>
                    <DialogTitle>New trip</DialogTitle>
                    <DialogDescription>
                        Create new trip
                    </DialogDescription>
                </DialogHeader>
                <div className="flex gap-4 flex-col md:flex-row">
                    <PreviewMaps className="rounded-md min-h-[200px] flex-1" locations={locations} />
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="flex-1 space-y-8">
                            <FormField control={form.control} name="title" render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Title</FormLabel>
                                    <FormControl>
                                        <Input type="text" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )} />
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
                            <FormField control={form.control} name="isPublic"
                                render={({ field }) => (
                                    <FormItem className="flex flex-row items-center gap-2">
                                        <FormControl>
                                            <Checkbox
                                                checked={field.value}
                                                onCheckedChange={field.onChange}
                                            />
                                        </FormControl>
                                        <FormLabel>Public</FormLabel>
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
