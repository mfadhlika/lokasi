import { isAfter } from "date-fns";
import z from "zod";

export const tripFormSchema = z.object({
    title: z.string(),
    startAt: z.coerce.date<Date>(),
    endAt: z.coerce.date<Date>(),
    isPublic: z.boolean()
}).refine((data) => isAfter(data.endAt, data.startAt), {
    path: ['endAt'],
    error: 'End at must be after start at'
});
