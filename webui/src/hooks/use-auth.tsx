import { createContext, useContext, useMemo, useState } from "react";
import * as React from "react";
import { jwtDecode } from "jwt-decode";
interface AuthContextType {
    userInfo: {
        username: string
    } | null | undefined;
    accessToken: string | null | undefined;
    login: (accessToken: string, callback: () => void) => void;
    logout: (callback: () => void) => void;
}

const AuthContext = createContext<AuthContextType>({
    userInfo: undefined,
    accessToken: null,
    login: (_accessToken: string, _callback: () => void) => void {

    },
    logout: (_callback: () => void) => void {

    },
});

export const AuthProvider = ({ children }: React.ComponentProps<"div">) => {
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem("accessToken"));

    const login = (accessToken: string, callback: () => void) => {
        localStorage.setItem("accessToken", accessToken);
        setAccessToken(accessToken);
        callback();
    };

    const logout = (callback: () => void) => {
        localStorage.removeItem("accessToken");
        setAccessToken(null);
        callback();
    };

    const value = useMemo(() => {
        let decoded;
        if (accessToken) decoded = jwtDecode(accessToken);

        return {
            accessToken,
            userInfo: decoded && {
                username: decoded['sub']!
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
