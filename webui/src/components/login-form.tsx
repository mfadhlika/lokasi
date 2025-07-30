import * as React from "react";
import { cn } from "@/lib/utils.ts";
import { Input } from "@/components/ui/input.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Label } from "@/components/ui/label.tsx";
import { type FormEvent, useState } from "react";
import { useAuth } from "@/hooks/use-auth.tsx";
import { useNavigate } from "react-router";
import { Card, CardContent } from "@/components/ui/card";
import { axiosInstance } from "@/lib/request";
import type { Response } from "@/types/response";
import type { Login } from "@/types/login";

export function LoginForm({
    className,
    ...props
}: React.ComponentProps<"div">) {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const { login } = useAuth();
    const navigate = useNavigate();

    const onSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        axiosInstance.post<Response<Login>>("v1/login", {
            username,
            password
        }).then(({ data }) => {
            login(data.data.accessToken, () => {
                navigate("/");
            })
        })
            .catch(err => {
                console.error(err);
            });
    }

    return (
        <div className={cn("flex flex-col gap-6", className)} {...props}>
            <Card className="w-full max-w-sm">
                <CardContent>
                    <form onSubmit={onSubmit}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-3">
                                <Label htmlFor="username">Username</Label>
                                <Input id="username" type="text" autoComplete="username" required
                                    onChange={(e) => setUsername(e.target.value)} />
                            </div>
                            <div className="grid gap-3">
                                <Label htmlFor="password">Password</Label>
                                <Input id="password" type="password" autoComplete="password" required
                                    onChange={(e) => setPassword(e.target.value)} />
                            </div>
                            <div className="grid gap-3">
                                <Button type="submit" className="w-full">Login</Button>
                            </div>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
