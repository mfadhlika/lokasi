import MapsPage from "@/pages/maps";
import { ProtectedRoute } from "@/components/protected-route";
import { createBrowserRouter } from "react-router"
import DataPage from "@/pages/data";
import SettingsPage from "@/pages/settings";
import LoginPage from "@/pages/login";
import TripsPage from "@/pages/trips";

const router = createBrowserRouter([
    {
        path: "/login",
        element: <LoginPage />
    },
    {
        path: "/",
        element: <ProtectedRoute />,
        children: [
            {
                path: "/",
                element: <MapsPage />
            },
            {
                path: "/trips",
                element: <TripsPage />
            },
            {
                path: "/data",
                element: <DataPage />
            },
            {
                path: "/settings",
                element: <SettingsPage />
            }
        ]
    }
]);

export default router
