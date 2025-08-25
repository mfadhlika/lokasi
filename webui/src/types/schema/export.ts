import z from "zod";

export const exportFormSchema = z.object({
    startAt: z.coerce.date<Date>(),
    endAt: z.coerce.date<Date>()
}).refine((data) => data.endAt > data.startAt, {
    path: ['endAt'],
    error: 'End at must be after start at'
});
