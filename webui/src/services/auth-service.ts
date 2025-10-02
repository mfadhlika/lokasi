import { axiosInstance } from "@/lib/request";
import type { Login as LoginResponse } from "@/types/responses/login";
import type { Login as LoginRequest } from "@/types/requests/login";
import type { Response } from "@/types/response";

class AuthService {
    login = async (req: LoginRequest): Promise<Response<LoginResponse>> => {
        return await axiosInstance.post<Response<LoginResponse>>("v1/login", {
            username: req.username,
            password: req.password
        }).then(res => res.data);
    }

    validate = async (): Promise<Response> => {
        return await axiosInstance.get<Response>("v1/auth/validate").then(res => res.data);
    }

    logout = async (): Promise<void> => axiosInstance.delete("v1/logout");
}

export const authService: AuthService = new AuthService();
