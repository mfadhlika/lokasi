import { useAuthStore } from "@/hooks/use-auth.tsx";
import { Navigate, Outlet } from "react-router";
import { AppSidebar } from "./app-sidebar";
import { SidebarProvider, SidebarInset } from "@/components/ui/sidebar";

export const ProtectedRoute = () => {
    const { accessToken } = useAuthStore();

    if (!accessToken) {
        return <Navigate to="/login" />
    }

    return (
        <SidebarProvider>
            <AppSidebar />
            <SidebarInset>
                <Outlet />
            </SidebarInset>
        </SidebarProvider >
    );
}
