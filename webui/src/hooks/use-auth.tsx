import { createContext, useContext, useMemo, useState } from "react";
import * as React from "react";
import { jwtDecode } from "jwt-decode";
import { axiosInstance } from "@/lib/request";
import { toast } from "sonner";
import type { Claim } from "@/types/claim";
interface AuthContextType {
    userInfo: {
        username: string
    } | null | undefined;
    accessToken: string | null | undefined;
    login: (accessToken: string) => void;
    logout: (callback: () => void) => void;
}

const AuthContext = createContext<AuthContextType>({
    userInfo: undefined,
    accessToken: null,
    login: (_accessToken: string) => void {

    },
    logout: (_callback: () => void) => void {

    },
});

export const AuthProvider = ({ children }: React.ComponentProps<"div">) => {
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem("accessToken"));

    const login = (accessToken: string) => {
        if (!accessToken) return;
        localStorage.setItem("accessToken", accessToken);
        setAccessToken(accessToken);
    };

    const logout = (callback: () => void) => {
        axiosInstance.delete("v1/logout").then(() => {
            localStorage.removeItem("accessToken");
            setAccessToken(null);
            callback();
        }).catch(err => {
            console.error(err);
            toast.error("logging out failed", err);
        });
    };

    const value = useMemo(() => {
        let decoded;
        if (accessToken) decoded = jwtDecode<Claim>(accessToken);

        return {
            accessToken,
            userInfo: decoded && {
                username: decoded.username ?? ""
            },
            login,
            logout,
        };
    }, [accessToken]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
    return useContext(AuthContext);
}

export default AuthProvider;
