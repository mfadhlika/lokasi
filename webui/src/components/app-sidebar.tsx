import * as React from "react"
import { Database, Download, Map, Plane, Settings, Upload } from "lucide-react"

import { NavUser } from "@/components/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
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
                    url: "/import",
                    icon: Upload,
                },
                {
                    title: "Export",
                    url: "/export",
                    icon: Download,
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
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton size="lg" asChild>
                            <div>
                                <div className="text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                                    <img className="size-4" src="/lokasi.svg" />
                                </div>
                                <span className="font-medium">Lokasi</span>
                            </div>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
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
