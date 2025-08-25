import { axiosInstance } from "@/lib/request";
import type { Response } from "@/types/response";
import type { Stats } from "@/types/stats";

class StatsService {
    fetchStats = async (): Promise<Response<Stats>> => {
        return await axiosInstance.get<Response<Stats>>(`v1/stats`).then(res => res.data);
    }
}

export const statsService = new StatsService();
