import { Button } from "@/components/ui/button";
import { FormLabel, FormControl, FormItem, FormField, Form, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { useAuth } from "@/hooks/useAuth";
import { axiosInstance } from "@/lib/request";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import z from "zod";

const accountFormSchema = z.object({
    username: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Password does not match'
});

export default function Settings() {
    const { userInfo } = useAuth();

    const accountForm = useForm<z.infer<typeof accountFormSchema>>({
        resolver: zodResolver(accountFormSchema),
        defaultValues: {
            username: userInfo?.username ?? "",
            password: "",
            confirmPassword: "",
        }
    });

    const onSubmit = (values: z.infer<typeof accountFormSchema>) => {
        axiosInstance.put("v1/user", {
            username: values.username,
            password: values.password,
        })
            .catch(err => {
                console.error(err);
            });
    }

    return (
        <div className="w-full h-full p-4">
            <Form {...accountForm}>
                <form onSubmit={accountForm.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                    <FormField control={accountForm.control} name="username" render={({ field }) => (
                        <FormItem>
                            <FormLabel>Username</FormLabel>
                            <FormControl>
                                <Input type="text" autoComplete="username"  {...field} />
                            </FormControl>
                        </FormItem>
                    )} />
                    <FormField control={accountForm.control} name="password" render={({ field }) => (
                        <FormItem>
                            <FormLabel>Password</FormLabel>
                            <FormControl>
                                <Input type="password" autoComplete="password"  {...field} />
                            </FormControl>
                        </FormItem>
                    )} />
                    <FormField control={accountForm.control} name="confirmPassword" render={({ field, fieldState }) => (
                        <FormItem>
                            <FormLabel>Confirm Password</FormLabel>
                            <FormControl>
                                <Input type="password" autoComplete="password" {...field} />
                            </FormControl>
                            {fieldState.error && <FormMessage>{fieldState.error.message}</FormMessage>}
                        </FormItem>
                    )} />
                    <Button type="submit" className="w-full" disabled={accountForm.formState.isSubmitting}>Submit</Button>
                </form>
            </Form>
        </div>
    );
}
