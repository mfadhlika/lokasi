import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from "react-router";
import { Toaster } from '@/components/ui/sonner';
import router from './lib/router';

createRoot(document.getElementById('root')!).render(
    <>
        <RouterProvider router={router} />
        <Toaster />
    </>
)
