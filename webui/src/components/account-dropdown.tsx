import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import { Button } from "@/components/ui/button.tsx";
import { User } from "lucide-react";
import { useAuthStore } from "@/hooks/use-auth.tsx";
import { useNavigate } from "react-router";

export const AccountDropdown = () => {
    const { logout } = useAuthStore();
    const navigate = useNavigate();

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline">
                    <User />
                    My Account
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="z-10000">
                <DropdownMenuItem onClick={() => {
                    logout();
                    navigate("/login");
                }}>
                    Logout
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
};
