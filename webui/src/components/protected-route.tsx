import { useAuth } from "@/hooks/use-auth.tsx";
import { Navigate, Outlet } from "react-router";
import { AppSidebar } from "./app-sidebar";
import { SidebarProvider, SidebarInset } from "@/components/ui/sidebar";

export const ProtectedRoute = () => {
    const { accessToken } = useAuth();

    if (!accessToken) {
        return <Navigate to="/login" />
    }

    return (
        <SidebarProvider defaultOpen={false}>
            <AppSidebar />
            <SidebarInset>
                <div className="flex flex-1 flex-col gap-4">
                    <Outlet />
                    <div className="bg-muted/50 min-h-[100vh] flex-1 rounded-xl md:min-h-min" />
                </div>
            </SidebarInset>
        </SidebarProvider>
    );
}
