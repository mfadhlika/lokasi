import { useEffect } from "react";
import { useAuthStore } from "./hooks/use-auth";
import { stompClient } from "./lib/websocket";

export default function App({ children }: React.ComponentProps<"div">) {
    const { accessToken } = useAuthStore();

    useEffect(() => {
        if (!stompClient.active && accessToken) {
            stompClient.configure({
                connectHeaders: {
                    Authorization: `Bearer ${accessToken}`

                }
            });
            stompClient.activate();
        } else if (stompClient.active && !accessToken) {
            stompClient.deactivate();
        }
    }, [accessToken]);

    return children;
}
