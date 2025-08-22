import { useAuth } from "@/hooks/use-auth";
import { useEffect } from "react";
import { useNavigate } from "react-router";
import { Input } from "@/components/ui/input.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Card, CardContent } from "@/components/ui/card";
import type { Response } from "@/types/response";
import { useForm } from "react-hook-form";
import type { Login as LoginRequest } from "@/types/requests/login";
import { zodResolver } from "@hookform/resolvers/zod";
import { FormControl, FormField, FormItem, FormLabel, Form, FormMessage } from "@/components/ui/form";
import type { AxiosError } from "axios";
import { loginFormSchema } from "@/types/schema/login";
import { authService } from "@/services/auth-service";


export default function LoginPage() {
    const { userInfo, login } = useAuth();
    const navigate = useNavigate();

    const loginForm = useForm<LoginRequest>({
        resolver: zodResolver(loginFormSchema),
        defaultValues: {}
    });

    const onSubmit = (values: LoginRequest) => {
        authService.login(values).then(({ data }) => {
            login(data.accessToken);
            navigate("/");
        })
            .catch((err: AxiosError<Response>) => {
                console.error(err);
                loginForm.setError("root", { message: err.response?.data?.message });
            });
    }

    useEffect(() => {
        if (userInfo) navigate("/");
    }, [navigate, userInfo])

    return (
        <div className="bg-muted flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
            <div className="flex w-full max-w-sm flex-col gap-6">
                <a href="#" className="flex items-center gap-2 self-center font-medium">
                    <img src="/lokasi.svg" className="w-6 h-6" />
                    Lokasi
                </a>
                <div className="flex flex-col gap-6">
                    <Card className="w-full max-w-sm">
                        <CardContent>
                            <Form {...loginForm}>
                                <form onSubmit={loginForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                                    <FormField control={loginForm.control} name="username" render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Username</FormLabel>
                                            <FormControl>
                                                <Input type="text" autoComplete="username"  {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )} />
                                    <FormField control={loginForm.control} name="password" render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Password</FormLabel>
                                            <FormControl>
                                                <Input type="password" autoComplete="password"  {...field} />
                                            </FormControl>
                                        </FormItem>
                                    )} />
                                    <div className="grid gap-3">
                                        <Button type="submit" className="w-full">Login</Button>
                                        {loginForm.formState.errors.root?.message && <FormMessage>{loginForm.formState.errors.root?.message}</FormMessage>}
                                    </div>
                                </form>
                            </Form>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
