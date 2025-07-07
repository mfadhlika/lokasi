import { createRoot } from 'react-dom/client'
import './index.css'
import { BrowserRouter, Route, Routes } from "react-router";
import Login from "@/pages/login";
import { ProtectedRoute } from "@/components/protected-route.tsx";
import { AuthProvider } from "@/hooks/useAuth.tsx";
import Maps from "@/pages/maps";

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Maps />} />
                </Route>
            </Routes>
        </AuthProvider>
    </BrowserRouter>
)
