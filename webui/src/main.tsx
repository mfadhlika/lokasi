import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from "react-router";
import { AuthProvider } from "@/hooks/use-auth.tsx";
import { Toaster } from '@/components/ui/sonner';
import router from './lib/router';

createRoot(document.getElementById('root')!).render(
    <AuthProvider>
        <RouterProvider router={router} />
        <Toaster />
    </AuthProvider>
)
