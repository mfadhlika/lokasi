import z from "zod";
import type { loginFormSchema } from "../schema/login";


export type Login = z.infer<typeof loginFormSchema>;

