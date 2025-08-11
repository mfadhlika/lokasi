import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Layers } from "lucide-react";
import type { Checked } from "@/types/checked";
import { create } from 'zustand';

export type LayerState = {
    showLines: Checked,
    showPoints: Checked,
    showLastKnown: Checked,
}

export const useLayerState = create<LayerState>()(() => ({
    showLines: true,
    showPoints: true,
    showLastKnown: true,
}));

export const LayerCheckbox = ({ showLines, showPoints, showLastKnown, }: LayerState) => {
    const setShowPoints = (value: Checked) => useLayerState.setState((state) => ({ ...state, showPoints: value }));
    const setShowLines = (value: Checked) => useLayerState.setState((state) => ({ ...state, showLines: value }));
    const setShowLastKnown = (value: Checked) => useLayerState.setState((state) => ({ ...state, showLastKnown: value }));
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
