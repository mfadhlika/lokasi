import axios from "axios";
import router from "./router";
import { toast } from "sonner";
import type { Login } from "@/types/login";
import type { Response } from "@/types/response";

let refreshTokenPromise: Promise<string> | null = null;

const axiosInstance = axios.create({
    baseURL: "/api",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json"
    }
});

function logout() {
    axiosInstance.delete("v1/logout")
        .then(() => {
            localStorage.removeItem("accessToken");
            router.navigate("/login");
        }).catch(err => {
            console.error(err);
            toast.error("logging out failed", err);
        });
};

async function refreshToken(): Promise<string> {
    const { data } = await axiosInstance.get<Response<Login>>("v1/auth/refresh");
    localStorage.setItem("accessToken", data.data.accessToken);
    return data.data.accessToken;
};

axiosInstance.interceptors.request.use(
    (req) => {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) req.headers.Authorization = "Bearer " + accessToken;
        return req;
    },
    (err) => {
        return Promise.reject(err);
    }
);

axiosInstance.interceptors.response.use(
    (res) => res,
    async (err) => {
        const originalRequest = err.config;

        if (!/^(v1\/auth\/refresh|v1\/login)$/.test(originalRequest.url) &&
            err.response?.status == 401 &&
            !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                if (!refreshTokenPromise) {
                    console.debug("refreshing token");
                    refreshTokenPromise = refreshToken()
                        .then((token) => {
                            refreshTokenPromise = null;
                            return token;
                        });

                }

                const accessToken = await refreshTokenPromise;

                if (accessToken) originalRequest.headers.Authorization = "Bearer " + accessToken;

                console.debug("retry request");
                return axios(originalRequest);
            } catch {
                console.error("failed refreshing token");
                toast.error("Session expired. Please relogin");
                logout();
            }
        }

        return Promise.reject(err);
    }
);

export { axiosInstance };
