import type z from "zod";
import type { importFormSchema } from "../schema/import";

export type Import = z.infer<typeof importFormSchema>;
