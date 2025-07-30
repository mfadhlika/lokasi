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
import { useEffect, useState } from "react";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card";
import { useNavigate } from "react-router";
import { Header } from "@/components/header";
import { toast } from "sonner";
import type { Integration } from "@/types/integration";
import type { Response } from "@/types/response";

const accountFormSchema = z.object({
    username: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Password does not match'
});

const owntracksFormSchema = z.object({
    username: z.string(),
    password: z.string(),
});

const overlandFormSchema = z.object({
    apiKey: z.string(),
});


function AccountSettingsTab() {
    const { userInfo, logout } = useAuth();
    const navigate = useNavigate();

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
            .then((_res) => {
                if (values.username !== userInfo?.username) {
                    logout(() => {
                        navigate("/login");
                    });
                }
            })
            .catch(err => {
                toast.error(`Failed to updated user's account: ${err}`);
            });
    }

    return (
        <Form {...accountForm}>
            <form onSubmit={accountForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <Card>
                    <CardHeader>
                        <CardTitle>Account</CardTitle>
                        <CardDescription>
                            Change your username and password here. After saving, you&apos;ll be logged
                            out.
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="grid gap-6">
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
                    </CardContent>
                    <CardFooter>
                        <Button type="submit" className="w-[100px] self-end" disabled={accountForm.formState.isSubmitting}>Submit</Button>
                    </CardFooter>

                </Card>
            </form>
        </Form>

    );
}

function OwntracksIntegrationItem({ integration, doSubmit }: { integration: Integration, doSubmit: (integration: Integration) => void }) {
    const owntracksForm = useForm<z.infer<typeof owntracksFormSchema>>({
        resolver: zodResolver(owntracksFormSchema),
        defaultValues: {
            username: integration?.owntracksUsername ?? "",
        }
    });

    const onSubmit = (values: z.infer<typeof owntracksFormSchema>) => {
        doSubmit({
            ...integration,
            owntracksUsername: values.username,
            owntracksPassword: values.password,
        });
    }

    return (
        <Form {...owntracksForm}>
            <form onSubmit={owntracksForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
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
            apiKey: integration?.overlandApiKey ?? "",
        }
    });

    const onSubmit = (_: z.infer<typeof overlandFormSchema>) => {
        doSubmit({
            ...integration,
            overlandApiKey: "",
        });
    }

    return (
        <Form {...overlandForm}>
            <form onSubmit={overlandForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <FormField control={overlandForm.control} name="apiKey" render={({ field }) => (
                    <FormItem>
                        <FormLabel>Api Key</FormLabel>
                        <FormControl>
                            <Input type="text" disabled {...field} />
                        </FormControl>
                    </FormItem>
                )} />
                <Button type="submit" className="w-[100px] self-end" disabled={overlandForm.formState.isSubmitting}>Reset</Button>
            </form>
        </Form>
    );
}

function IntegrationSettingsTab() {
    const [integration, setIntegration] = useState<Integration>({
        owntracksUsername: "",
        owntracksPassword: "",
        overlandApiKey: ""
    });

    useEffect(() => {
        axiosInstance.get<Response<Integration>>("v1/integration").then(res => {
            setIntegration({ ...res.data.data });
        }).catch(err => console.error(err));
    }, []);

    const doSubmit = (data: Integration) => {
        console.info(data);
        axiosInstance.put("v1/integration", data)
            .then(res => {
                setIntegration({ ...res.data });
                toast.success("Integration saved succesfully");
            })
            .catch(err => {
                toast.error(`Failed to update integration: ${err}`);
            });
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Integration</CardTitle>
                <CardDescription>
                    Configure integration with other apps here.
                </CardDescription>
            </CardHeader>
            <CardContent className="grid gap-6">
                <Accordion key={JSON.stringify(integration)} type="multiple">
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
            </CardContent>
        </Card>
    );
}

export default function SettingsPage() {
    return (
        <Tabs defaultValue="account" className="w-full">
            <Header>
                <TabsList>
                    <TabsTrigger value="account">Account</TabsTrigger>
                    <TabsTrigger value="integration">Integration</TabsTrigger>
                </TabsList>
            </Header>
            <div className="pr-4 pl-4">
                <TabsContent value="account">
                    <AccountSettingsTab />
                </TabsContent>
                <TabsContent value="integration"><IntegrationSettingsTab /></TabsContent>
            </div>
        </Tabs>
    );
}
