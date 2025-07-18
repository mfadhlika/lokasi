import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Loader2Icon, Upload } from "lucide-react";
import { Input } from "@/components/ui/input.tsx";
import { z } from "zod/v4";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form.tsx";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group.tsx";
import { axiosInstance } from "@/lib/request.ts";
import { useState } from "react";
import { cn } from "@/lib/utils";
import { toast } from "sonner";

const formSchema = z.object({
    source: z.string(),
    file: z.instanceof(FileList)
});

export const ImportDialog = ({ className }: React.ComponentProps<"div">) => {
    const [open, setOpen] = useState(false);

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            source: "dawarich",
        }
    });

    const { formState } = form;

    const fileRef = form.register("file");

    const onSubmit = (values: z.infer<typeof formSchema>) => {
        const formData = new FormData();
        formData.set("source", values.source);
        formData.set("file", values.file.item(0)!);

        axiosInstance.post(`v1/import`, formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        })
            .then(_ => {
                toast.success("File uploaded succesfully");
                setOpen(false);
            })
            .catch(err => {
                toast.error(`Failed to get user's devices: ${err}`);
            });
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" className={cn("shadow-xs", className)}>
                    <Upload />
                    Import
                </Button>
            </DialogTrigger>
            <DialogContent className="z-10000">
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <DialogHeader>
                            <DialogTitle>Import locations</DialogTitle>
                            <DialogDescription>
                                Upload exported location from another sources
                            </DialogDescription>
                        </DialogHeader>
                        <FormField control={form.control} name="source" render={({ field }) => (
                            <FormItem>
                                <FormLabel>Source</FormLabel>
                                <FormControl>
                                    <RadioGroup onValueChange={field.onChange} defaultValue={field.value}>
                                        <FormItem className="flex items-center gap-3">
                                            <FormControl>
                                                <RadioGroupItem value="dawarich" />
                                            </FormControl>
                                            <FormLabel>Dawarich</FormLabel>
                                        </FormItem>
                                    </RadioGroup>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )} />
                        <FormField control={form.control} name="file"
                            render={() => (
                                <FormItem>
                                    <FormLabel>File to import</FormLabel>
                                    <FormControl>
                                        <Input type="file" {...fileRef} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )} />
                        <Button type="submit" disabled={formState.isSubmitting}>
                            {formState.isSubmitting && <Loader2Icon className="animate-spin" />}
                            Import
                        </Button>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
};
