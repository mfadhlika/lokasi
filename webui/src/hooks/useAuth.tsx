import { createContext, useContext, useMemo, useState } from "react";
import * as React from "react";

interface AuthContextType {
    accessToken: string | null | undefined;
    login: (accessToken: string, callback: () => void) => void;
}

const AuthContext = createContext<AuthContextType>({
    accessToken: null,
    login: (_accessToken: string, _callback: () => void) => void {

    },
});

export const AuthProvider = ({ children }: React.ComponentProps<"div">) => {
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem("accessToken"));

    const login = (accessToken: string, callback: () => void) => {
        localStorage.setItem("accessToken", accessToken);
        setAccessToken(accessToken);
        callback();
    };

    const value = useMemo(() => ({
        accessToken,
        login,
    }), [accessToken]);

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
