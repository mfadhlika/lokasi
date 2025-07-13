import * as React from "react"
import { Database, Map, Plane, Settings } from "lucide-react"

import { NavUser } from "@/components/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarGroupContent,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "@/components/ui/sidebar"
import { Link, useLocation } from "react-router"

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
                <SidebarGroup>
                    <SidebarGroupContent className="px-1.5 md:px-0">
                        <SidebarMenu>
                            {data.navMain.map((item) => (
                                <SidebarMenuItem key={item.title}>
                                    <SidebarMenuButton
                                        tooltip={{
                                            children: item.title,
                                            hidden: false,
                                        }}
                                        isActive={location.pathname == item.url}
                                        className="px-2.5 md:px-2"
                                        asChild
                                    >
                                        <Link to={item.url}>
                                            <item.icon />
                                            {item.title}
                                        </Link>

                                    </SidebarMenuButton>
                                </SidebarMenuItem>
                            ))}
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
            <SidebarFooter>
                <NavUser />
            </SidebarFooter>
        </Sidebar>
    );
}
