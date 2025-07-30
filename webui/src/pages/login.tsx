import { LoginForm } from "@/components/login-form.tsx";
import { useAuth } from "@/hooks/use-auth";
import { useEffect } from "react";
import { useNavigate } from "react-router";

export default function LoginPage() {
    const { userInfo } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (userInfo) {
            console.info("redirect if logged in");
            navigate("/");
        }
    }, [navigate, userInfo])

    return (
        <div className="bg-muted flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
            <div className="flex w-full max-w-sm flex-col gap-6">
                <a href="#" className="flex items-center gap-2 self-center font-medium">
                    Lokasi
                </a>
                <LoginForm />
            </div>
        </div>
    );
}
