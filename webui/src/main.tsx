import { createRoot } from 'react-dom/client'
import './index.css'
import { BrowserRouter, Route, Routes } from "react-router";
import Login from "@/pages/login";
import { ProtectedRoute } from "@/components/protected-route.tsx";
import { AuthProvider } from "@/hooks/useAuth.tsx";
import Maps from "@/pages/maps";
import { Data } from '@/pages/data';

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Maps />} />
                    <Route path="/data" element={<Data />} />
                </Route>
            </Routes>
        </AuthProvider>
    </BrowserRouter>
)
