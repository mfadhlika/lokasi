import { SidebarTrigger } from "@/components/ui/sidebar";
import { Separator } from "@/components/ui/separator";
import type React from "react";
import { cn } from "@/lib/utils";

export function Header({ className, children }: React.ComponentProps<"div">) {
    return (
        <header className={cn("flex h-16 shrink-0 items-center gap-2 px-4", className)}>
            <SidebarTrigger className="-ml-1" />
            <Separator orientation="vertical" className="mr-2 data-[orientation=vertical]:h-4" />
            <div className="flex gap-2 overflow-x-auto scrollbar-hide">
                {children}
            </div>
        </header>
    );
}
