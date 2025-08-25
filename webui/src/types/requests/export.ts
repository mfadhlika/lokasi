import type z from "zod";
import type { exportFormSchema } from "../schema/export";

export type Export = z.infer<typeof exportFormSchema>;
