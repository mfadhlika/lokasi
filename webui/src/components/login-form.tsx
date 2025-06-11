import * as React from "react";
import {cn} from "@/lib/utils.ts";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Label} from "@/components/ui/label.tsx";
import {type FormEvent, useState} from "react";
import {useAuth} from "@/hooks/useAuth.tsx";
import {useNavigate} from "react-router";

export function LoginForm({
                              className,
                              ...props
                          }: React.ComponentProps<"div">) {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const auth = useAuth();
    const navigate = useNavigate();

    if (!auth) return null;

    const {login} = auth;

    const onSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        fetch("/api/v1/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                password
            })
        })
            .then(res => res.json())
            .then(res => {
                login(res.accessToken, () => {
                    navigate("/");
                })
            })
            .catch(err => {
                console.error(err);
            });
    }

    return (
        <div className={cn("flex flex-col gap-6", className)} {...props}>
            <form onSubmit={onSubmit}>
                <div className="flex flex-col gap-6">
                    <div className="grid gap-3">
                        <Label htmlFor="username">Username</Label>
                        <Input id="username" type="text" autoComplete="username" required
                               onChange={(e) => setUsername(e.target.value)}/>
                    </div>
                    <div className="grid gap-3">
                        <Label htmlFor="password">Password</Label>
                        <Input id="password" type="password" autoComplete="password" required
                               onChange={(e) => setPassword(e.target.value)}/>
                    </div>
                    <div className="grid gap-3">
                        <Button type="submit" className="w-full">Login</Button>
                    </div>
                </div>
            </form>
        </div>
    )
}