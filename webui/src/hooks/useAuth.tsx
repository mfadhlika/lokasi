import {createContext, useContext, useEffect, useMemo, useState} from "react";
import * as React from "react";
import {axiosInstance} from "@/lib/request.ts";
import axios from "axios";
import {useNavigate} from "react-router";

interface AuthContextType {
    accessToken: string | null | undefined;
    login: (accessToken: string, callback: () => void) => void;
    logout: (callback: () => void) => void;
}

const AuthContext = createContext<AuthContextType>({
    accessToken: null,
    login: (_accessToken: string, _callback: () => void) => {
    },
    logout: (_callback: () => void) => {
    },
});

export const AuthProvider = ({children}: React.ComponentProps<"div">) => {
    const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem("accessToken"));
    const navigate = useNavigate();

    const login = (accessToken: string, callback: () => void) => {
        localStorage.setItem("accessToken", accessToken);
        setAccessToken(accessToken);
        callback();
    };

    const logout = (callback: (() => void) | undefined = undefined) => {
        axiosInstance.post("v1/logout")
            .then(_ => {
                setAccessToken(null);
                localStorage.removeItem("accessToken");
                if (callback) callback();
            }).catch(err => console.error(err));
    };

    const refreshToken = async (): Promise<string> => {
        const {accessToken} = await axiosInstance.get("v1/auth/refresh").then(res => res.data);
        localStorage.setItem("accessToken", accessToken);
        setAccessToken(accessToken);
        return accessToken;
    };

    useEffect(() => {
        const requestInterceptor = axiosInstance.interceptors.request.use(
            (req) => {
                if (accessToken) req.headers.Authorization = "Bearer " + accessToken;
                return req;
            },
            (err) => {
                return Promise.reject(err);
            }
        )

        const responseInterceptor = axiosInstance.interceptors.response.use(
            (res) => res,
            async (err) => {
                const originalRequest = err.config;

                if (err.response?.status == 401 && !originalRequest._retry) {
                    originalRequest._retry = true;

                    try {
                        const accessToken = await refreshToken();
                        if (accessToken) originalRequest.headers.Authorization = "Bearer " + accessToken;
                        return axios(originalRequest);
                    } catch (err) {
                        logout(() => {
                            navigate("/login");
                        });
                    }
                } else if (err.response?.status == 403) {
                    logout(() => {
                        navigate("/login");
                    });
                }
                return Promise.reject(err);
            }
        );

        return () => {
            axiosInstance.interceptors.request.eject(requestInterceptor);
            axiosInstance.interceptors.response.eject(responseInterceptor);
        }
    }, [accessToken]);

    const value = useMemo(() => ({
        accessToken,
        login,
        logout,
    }), [accessToken]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
}

export default AuthProvider;
