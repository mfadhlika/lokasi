import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Layers } from "lucide-react";
import type { Checked } from "@/types/checked";


export type LayerCheckboxProps = {
    showLines: Checked,
    setShowLines: (value: Checked) => void,
    showPoints: Checked,
    setShowPoints: (value: Checked) => void,
    showLastKnown: Checked,
    setShowLastKnown: (value: Checked) => void,
}


export const LayerCheckbox = ({ showLines, setShowLines, showPoints, setShowPoints, showLastKnown, setShowLastKnown }: LayerCheckboxProps) => {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline">
                    <Layers />
                    Layers
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56">
                <DropdownMenuCheckboxItem
                    checked={showLastKnown}
                    onCheckedChange={setShowLastKnown}
                >
                    Last known location
                </DropdownMenuCheckboxItem>
                <DropdownMenuCheckboxItem
                    checked={showLines}
                    onCheckedChange={setShowLines}
                >
                    Lines
                </DropdownMenuCheckboxItem>
                <DropdownMenuCheckboxItem
                    checked={showPoints}
                    onCheckedChange={setShowPoints}
                >
                    Points
                </DropdownMenuCheckboxItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
