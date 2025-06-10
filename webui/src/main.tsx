import {createRoot} from 'react-dom/client'
import './index.css'
import {BrowserRouter, Route, Routes} from "react-router";
import Login from "@/routes/login.tsx";
import {ProtectedRoute} from "@/components/protected-route.tsx";
import {AuthProvider} from "@/hooks/useAuth.tsx";
import Maps from "@/routes/maps.tsx";


createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route element={<ProtectedRoute/>}>
                    <Route path="/maps" element={<Maps/>}/>
                    <Route path="/logout"/>
                </Route>
            </Routes>
        </AuthProvider>
    </BrowserRouter>
)
