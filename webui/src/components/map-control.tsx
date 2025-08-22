import { type ControlPosition, Control, DomUtil, DomEvent } from "leaflet";
import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { useMap } from "react-leaflet";

type MapControlProps = React.ComponentProps<"div"> & {
    position?: ControlPosition,
    disableClickPropagation?: boolean,
    disableScrollPropagation?: boolean,
    className?: string
}

function MapControl({ children, position, disableClickPropagation, disableScrollPropagation, className }: MapControlProps) {
    const [container, setContainer] = useState<HTMLElement | null>(null);
    const map = useMap();

    useEffect(() => {
        const mapControl = new Control({ position });

        mapControl.onAdd = () => {
            const section = DomUtil.create('section', className);
            if (disableClickPropagation) {
                DomEvent.disableClickPropagation(section);
            }
            if (disableScrollPropagation) {
                DomEvent.disableScrollPropagation(section);
            }
            return section;
        };

        map.addControl(mapControl);

        setContainer(mapControl.getContainer() ?? null);

        return () => {
            map.removeControl(mapControl);
        };
    }, [map, position, disableClickPropagation, disableScrollPropagation, className]);

    return container ? createPortal(children, container) : null;
}

export { MapControl, type MapControlProps };
