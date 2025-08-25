import { axiosInstance } from "@/lib/request";
import type { Export } from "@/types/export";
import type { Export as ExportRequest } from "@/types/requests/export";
import type { Response } from "@/types/response";

class ExportService {
    createExport = async (ex: ExportRequest): Promise<Response> => {
        return axiosInstance.post<Response>(`v1/export`, ex)
            .then(res => res.data);
    }

    fetchExports = async (): Promise<Response<Export[]>> => {
        return axiosInstance
            .get<Response<Export[]>>(`v1/export`)
            .then(res => res.data);
    }

    fetchExportRawContent = async (id: number): Promise<Blob> => {
        return axiosInstance.get(`v1/export/${id}/raw`, {
            responseType: 'blob'
        }).then(res => res.data);
    }

    deleteExport = async (id: number): Promise<Response> => {
        return axiosInstance.delete(`v1/export/${id}`)
            .then(res => res.data);
    }
}

export const exportService = new ExportService();
