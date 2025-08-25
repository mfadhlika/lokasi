import z from "zod";

export const importFormSchema = z.object({
    source: z.string(),
    file: z.instanceof(FileList)
});
