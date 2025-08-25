import z from "zod";
import type { tripFormSchema } from "../schema/trip";

export type Trip = z.infer<typeof tripFormSchema>;
