import * as React from "react"
import { Database, Map, Plane, Settings, Upload } from "lucide-react"

import { NavUser } from "@/components/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
} from "@/components/ui/sidebar"
import { useLocation } from "react-router"
import { NavMain } from "./nav-main"

// This is sample data
const data = {
    navMain: [
        {
            title: "Maps",
            url: "/",
            icon: Map,
        },
        {
            title: "Trips",
            url: "/trips",
            icon: Plane,
        },
        {
            title: "Data",
            url: "/data",
            icon: Database,
            items: [
                {
                    title: "Import",
                    url: "/data/import",
                    icon: Upload,
                }
            ]
        },
        {
            title: "Settings",
            url: "/settings",
            icon: Settings,
        },
    ]
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
    const location = useLocation();

    return (
        <Sidebar {...props}>
            <SidebarHeader>
            </SidebarHeader>
            <SidebarContent>
                <NavMain items={data.navMain} location={location} />
            </SidebarContent>
            <SidebarFooter>
                <NavUser />
            </SidebarFooter>
        </Sidebar>
    );
}
