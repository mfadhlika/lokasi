import type { Claim } from "@/types/claim";
import { jwtDecode } from "jwt-decode";
import { create } from "zustand";

export interface AuthStore {
    userInfo: Claim | null;
    accessToken: string | null;
    login: (accessToken: string) => void;
    logout: () => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
    accessToken: localStorage.getItem('accessToken'),
    userInfo: localStorage.getItem('accessToken') ? jwtDecode<Claim>(localStorage.getItem('accessToken')!) : null,
    setUserInfo: (userInfo: Claim) => set({ userInfo }),
    login: (accessToken: string) => {
        localStorage.setItem('accessToken', accessToken);
        const userInfo = jwtDecode<Claim>(accessToken);
        set({ accessToken, userInfo });
    },
    logout: () => {
        localStorage.removeItem('accessToken');
        set({ accessToken: null, userInfo: null });
    }
}));
