import type { DialogState } from "@/components/dialog-state";
import { create } from "zustand";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import type { FeatureCollection, Point } from "geojson";
import { MapContainer, TileLayer, ZoomControl, GeoJSON } from "react-leaflet";

export const useVisitDialogState = create<DialogState<FeatureCollection>>((set) => ({
    isOpen: false,
    toggleModal: () => {
        set((state: DialogState<FeatureCollection>) => ({ isOpen: !state.isOpen }))
    },
    data: null,
    setData: (data: FeatureCollection) => set(() => ({ data: data })),
}));

export default function VisitSheet(props: Pick<DialogState<FeatureCollection>, "isOpen" | "data" | "toggleModal">) {
    return (
        <Sheet open={props.isOpen} onOpenChange={props.toggleModal}>
            <SheetContent className="sm:max-w-[425px]">
                <SheetHeader>
                    <SheetTitle>
                        Visit
                    </SheetTitle>
                    <SheetDescription>

                    </SheetDescription>
                </SheetHeader>
                <div className="pl-2 pr-2 flex flex-col">
                    {props.data?.features.map(feature => <>{feature.properties && feature.properties['name']}</>)}
                    <MapContainer
                        className="w-full min-h-[200px]"
                        center={props.data ? [(props.data?.features[0].geometry as Point).coordinates[1], (props.data?.features[0].geometry as Point).coordinates[0]] : [-6.175, 106.8275]}
                        zoom={13}
                        scrollWheelZoom={true}
                        zoomControl={false}>
                        <ZoomControl position="bottomright" />
                        <TileLayer
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />
                        {props.data && <GeoJSON data={props.data} />}
                    </MapContainer>
                </div>
            </SheetContent>
        </Sheet>
    )
}
