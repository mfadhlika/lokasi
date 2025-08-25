import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Layers } from "lucide-react";
import type { Checked } from "@/types/checked";
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

export type LayerState = React.ComponentProps<"div"> & {
    showLines: Checked,
    showPoints: Checked,
    showLastKnown: Checked,
    showMovingPoints: Checked,
    showVisits: Checked,
}

export const useLayerState = create<LayerState>()((
    persist(
        () => ({
            showLines: true as Checked,
            showPoints: true as Checked,
            showLastKnown: true as Checked,
            showMovingPoints: false as Checked,
            showVisits: false as Checked
        }),
        {
            name: 'layer-storage',
            storage: createJSONStorage(() => localStorage)
        }
    )
));

export const LayerCheckbox = ({ showLines, showPoints, showLastKnown, showMovingPoints, showVisits, className }: LayerState) => {
    const setShowPoints = (value: Checked) => useLayerState.setState((state) => ({ ...state, showPoints: value }));
    const setShowLines = (value: Checked) => useLayerState.setState((state) => ({ ...state, showLines: value }));
    const setShowLastKnown = (value: Checked) => useLayerState.setState((state) => ({ ...state, showLastKnown: value }));
    const setshowMovingPoints = (value: Checked) => useLayerState.setState((state) => ({ ...state, showMovingPoints: value }));
    const setShowVisits = (value: Checked) => useLayerState.setState((state) => ({ ...state, showVisits: value }));
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline" className={className}>
                    <Layers />
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
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
                <DropdownMenuCheckboxItem
                    checked={showMovingPoints}
                    onCheckedChange={setshowMovingPoints}
                    disabled={!showPoints}
                >
                    Moving points
                </DropdownMenuCheckboxItem>
                <DropdownMenuCheckboxItem
                    checked={showVisits}
                    onCheckedChange={setShowVisits}
                >
                    Visits
                </DropdownMenuCheckboxItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
