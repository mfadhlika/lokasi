import { Button } from "@/components/ui/button";
import { FormLabel, FormControl, FormItem, FormField, Form, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useAuth } from "@/hooks/use-auth";
import { axiosInstance } from "@/lib/request";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import z from "zod";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { Checkbox } from "@/components/ui/checkbox";
import { useEffect, useState } from "react";

const accountFormSchema = z.object({
    username: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Password does not match'
});

const owntracksFormSchema = z.object({
    enable: z.boolean(),
    username: z.string(),
    password: z.string(),
});

const overlandFormSchema = z.object({
    enable: z.boolean(),
    apiKey: z.string(),
});


type Integration = {
    owntracksEnable: boolean;
    owntracksUsername: string;
    owntracksPassword: string;
    overlandEnable: boolean;
    overlandApiKey: string;
}

function AccountSettingsTab() {
    const { userInfo } = useAuth();

    const accountForm = useForm<z.infer<typeof accountFormSchema>>({
        resolver: zodResolver(accountFormSchema),
        defaultValues: {
            username: userInfo?.username ?? "",
            password: "",
            confirmPassword: "",
        }
    });

    const onSubmit = (values: z.infer<typeof accountFormSchema>) => {
        axiosInstance.put("v1/user", {
            username: values.username,
            password: values.password,
        })
            .catch(err => {
                console.error(err);
            });
    }

    return (
        <Form {...accountForm}>
            <form onSubmit={accountForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <FormField control={accountForm.control} name="username" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Username</FormLabel>
                        <FormControl>
                            <Input type="text" autoComplete="username"  {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <FormField control={accountForm.control} name="password" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Password</FormLabel>
                        <FormControl>
                            <Input type="password" autoComplete="password"  {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <FormField control={accountForm.control} name="confirmPassword" render={({ field, fieldState }) => (
                    <FormItem>
                        <FormLabel>Confirm Password</FormLabel>
                        <FormControl>
                            <Input type="password" autoComplete="password" {...field} />
                        </FormControl>
                        {fieldState.error && <FormMessage>{fieldState.error.message}</FormMessage>}
                    </FormItem>
                )} />
                <Button type="submit" className="w-[100px] self-end" disabled={accountForm.formState.isSubmitting}>Submit</Button>
            </form>
        </Form>
    );
}

function OwntracksIntegrationItem({ integration, doSubmit }: { integration: Integration, doSubmit: (integration: Integration) => void }) {
    const owntracksForm = useForm<z.infer<typeof owntracksFormSchema>>({
        resolver: zodResolver(owntracksFormSchema),
        defaultValues: {
            enable: integration?.owntracksEnable ?? false,
            username: integration?.owntracksUsername ?? "",
        }
    });

    const onSubmit = (values: z.infer<typeof owntracksFormSchema>) => {
        doSubmit({
            ...integration,
            owntracksEnable: values.enable,
            owntracksUsername: values.username,
            owntracksPassword: values.password,
        });
    }

    return (
        <Form {...owntracksForm}>
            <form onSubmit={owntracksForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <FormField control={owntracksForm.control} name="enable" render={({ field }) => (
                    <FormItem className="flex">
                        <FormControl>
                            <Checkbox checked={field.value} onCheckedChange={field.onChange} />
                        </FormControl>
                        <FormLabel>Enable</FormLabel>
                    </FormItem>
                )} />
                <FormField control={owntracksForm.control} name="username" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Username</FormLabel>
                        <FormControl>
                            <Input type="text" autoComplete="username"  {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <FormField control={owntracksForm.control} name="password" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Password</FormLabel>
                        <FormControl>
                            <Input type="password" autoComplete="password"  {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <Button type="submit" className="w-[100px] self-end" disabled={owntracksForm.formState.isSubmitting}>Save</Button>
            </form>
        </Form>
    );
}

function OverlandIntegrationItem({ integration, doSubmit }: { integration: Integration, doSubmit: (integration: Integration) => void }) {
    const overlandForm = useForm<z.infer<typeof overlandFormSchema>>({
        resolver: zodResolver(overlandFormSchema),
        defaultValues: {
            enable: integration?.overlandEnable ?? false,
            apiKey: integration?.overlandApiKey ?? "",
        }
    });

    const onSubmit = (values: z.infer<typeof overlandFormSchema>) => {
        doSubmit({
            ...integration,
            overlandEnable: values.enable,
            overlandApiKey: values.apiKey,
        })
    }

    return (
        <Form {...overlandForm}>
            <form onSubmit={overlandForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <FormField control={overlandForm.control} name="enable" render={({ field }) => (
                    <FormItem className="flex">
                        <FormControl>
                            <Checkbox checked={field.value} onCheckedChange={field.onChange} />
                        </FormControl>
                        <FormLabel>Enable</FormLabel>
                    </FormItem>
                )} />
                <FormField control={overlandForm.control} name="apiKey" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Api Key</FormLabel>
                        <FormControl>
                            <Input type="text" disabled {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <Button type="submit" className="w-[100px] self-end" disabled={overlandForm.formState.isSubmitting}>Save</Button>
            </form>
        </Form>
    );
}

function IntegrationSettingsTab() {
    const [integration, setIntegration] = useState<Integration>({
        owntracksEnable: false,
        owntracksUsername: "",
        owntracksPassword: "",
        overlandEnable: false,
        overlandApiKey: ""
    });

    useEffect(() => {
        axiosInstance.get("v1/integration").then(res => {
            setIntegration({ ...res.data });
        }).catch(err => console.error(err));
    }, []);

    const doSubmit = (data: Integration) => {
        axiosInstance.put("v1/integration", data)
            .then(res => {
                setIntegration({ ...res.data });
            })
            .catch(err => {
                console.error(err);
            });
    }

    return (
        <Accordion type="multiple">
            <AccordionItem value="owntracks">
                <AccordionTrigger>Owntracks</AccordionTrigger>
                <AccordionContent>
                    <OwntracksIntegrationItem integration={integration} doSubmit={doSubmit} />
                </AccordionContent>
            </AccordionItem>
            <AccordionItem value="overland">
                <AccordionTrigger>Overland</AccordionTrigger>
                <AccordionContent>
                    <OverlandIntegrationItem integration={integration} doSubmit={doSubmit} />
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    );
}

export default function Settings() {
    return (
        <div className="w-full h-full p-4">
            <Tabs defaultValue="account" className="w-full">
                <TabsList>
                    <TabsTrigger value="account">Account</TabsTrigger>
                    <TabsTrigger value="integration">Integration</TabsTrigger>
                </TabsList>
                <TabsContent value="account"><AccountSettingsTab /></TabsContent>
                <TabsContent value="integration"><IntegrationSettingsTab /></TabsContent>
            </Tabs>
        </div >
    );
}
