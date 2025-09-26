import { axiosInstance } from "@/lib/request";
import type { Response } from "@/types/response";

class DeciceService {
    fetchDevices = async (): Promise<Response<string[]>> => {
        return await axiosInstance.get<Response<string[]>>("v1/user/devices").then(res => res.data);
    }
}

export const deviceService: DeciceService = new DeciceService();
