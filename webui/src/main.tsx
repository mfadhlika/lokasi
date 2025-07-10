import { createRoot } from 'react-dom/client'
import './index.css'
import { BrowserRouter, Route, Routes } from "react-router";
import Login from "@/pages/login";
import { ProtectedRoute } from "@/components/protected-route.tsx";
import { AuthProvider } from "@/hooks/use-auth.tsx";
import Maps from "@/pages/maps";
import Data from '@/pages/data';
import Settings from '@/pages/settings';

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Maps />} />
                    <Route path="/data" element={<Data />} />
                    <Route path="/settings" element={<Settings />} />
                </Route>
            </Routes>
        </AuthProvider>
    </BrowserRouter>
)
