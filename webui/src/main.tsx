import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from "react-router";
import { Toaster } from '@/components/ui/sonner';
import router from './lib/router';
import App from './app';

createRoot(document.getElementById('root')!).render(
    <App>
        <RouterProvider router={router} />
        <Toaster />
    </App>
)
