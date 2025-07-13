import { createRoot } from 'react-dom/client'
import './index.css'
import { BrowserRouter, Route, Routes } from "react-router";
import LoginPage from "@/pages/login";
import { ProtectedRoute } from "@/components/protected-route.tsx";
import { AuthProvider } from "@/hooks/use-auth.tsx";
import Maps from "@/pages/maps";
import DataPage from '@/pages/data';
import SettingsPage from '@/pages/settings';

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Maps />} />
                    <Route path="/data" element={<DataPage />} />
                    <Route path="/settings" element={<SettingsPage />} />
                </Route>
            </Routes>
        </AuthProvider>
    </BrowserRouter>
)
