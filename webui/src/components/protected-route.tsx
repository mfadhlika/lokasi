import {useAuth} from "@/hooks/useAuth.tsx";
import {Navigate, Outlet} from "react-router";

export const ProtectedRoute = () => {
    const {accessToken} = useAuth();

    if (!accessToken) {
        return <Navigate to="/login"/>
    }

    return <Outlet/>;
}