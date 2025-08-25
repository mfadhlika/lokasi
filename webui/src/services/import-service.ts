import { axiosInstance } from "@/lib/request";
import type { Import } from "@/types/import";
import type { Import as ImportRequest } from "@/types/requests/import";
import type { Response } from "@/types/response";

class ImportService {
    createImport = async (im: ImportRequest): Promise<Response> => {
        const formData = new FormData();
        formData.set("source", im.source);
        formData.set("file", im.file.item(0)!);

        return axiosInstance.post(`v1/import`, formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        })
            .then(res => res.data);
    }

    fetchImports = async (): Promise<Response<Import[]>> => {
        return axiosInstance
            .get<Response<Import[]>>(`v1/import`)
            .then(res => res.data);
    }

    fetchImportRawContent = async (id: number): Promise<Blob> => {
        return axiosInstance.get(`v1/import/${id}/raw`, {
            responseType: 'blob'
        }).then(res => res.data);
    }

    deleteImport = async (id: number): Promise<Response> => {
        return axiosInstance.delete(`v1/import/${id}`)
            .then(res => res.data);
    }
}

export const importService = new ImportService();
