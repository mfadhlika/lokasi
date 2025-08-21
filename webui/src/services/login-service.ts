import { axiosInstance } from "@/lib/request";
import type { Login as LoginResponse } from "@/types/responses/login";
import type { Login as LoginRequest } from "@/types/requests/login";
import type { Response } from "@/types/response";

class LoginService {
    login = async (req: LoginRequest): Promise<Response<LoginResponse>> => {
        return await axiosInstance.post<Response<LoginResponse>>("v1/login", {
            username: req.username,
            password: req.password
        }).then(res => res.data);
    }
}

export const loginService: LoginService = new LoginService();
