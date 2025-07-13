import type { DialogState } from "@/components/dialog-state";
import { create } from "zustand";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle } from "@/components/ui/sheet";

export const useRawDataDialogState = create<DialogState<string>>((set) => ({
    isOpen: false,
    toggleModal: () => {
        set((state: DialogState<string>) => ({ isOpen: !state.isOpen }))
    },
    data: null,
    setData: (data: string) => set(() => ({ data: data })),
}));

export default function RawDataSheet(props: Pick<DialogState<string>, "isOpen" | "data" | "toggleModal">) {
    return (
        <Sheet open={props.isOpen} onOpenChange={props.toggleModal}>
            <SheetContent className="sm:max-w-[425px]">
                <SheetHeader>
                    <SheetTitle>
                        Raw data
                    </SheetTitle>
                    <SheetDescription>
                        Payload sent by client
                    </SheetDescription>
                </SheetHeader>
                <div className="pl-2 pr-2">
                    <ScrollArea className="[&>[data-radix-scroll-area-viewport]]:max-h-[calc(100vh-125px)] rounded bg-muted p-2">
                        <pre className="text-wrap">
                            {props.data}
                        </pre>
                    </ScrollArea>
                </div>
            </SheetContent>
        </Sheet>
    )
}
