import axios from "axios";
import { redirect } from "react-router";

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
            redirect("/login");
        }).catch(err => console.error(err));
};

async function refreshToken(): Promise<string> {
    const { accessToken } = await axiosInstance.get("v1/auth/refresh").then(res => res.data);
    localStorage.setItem("accessToken", accessToken);
    return accessToken;
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

        if (err.response?.status == 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const accessToken = await refreshToken();
                if (accessToken) originalRequest.headers.Authorization = "Bearer " + accessToken;
                return axios(originalRequest);
            } catch {
                logout();
            }
        } else if (err.response?.status == 403) {
            logout();
        }
        return Promise.reject(err);
    }
);

export { axiosInstance };
