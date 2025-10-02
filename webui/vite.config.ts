/// <reference types="vitest" />
/// <reference types="@vitest/browser/providers/playwright" />
/// <reference types="@vitest/browser/matchers" />

import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite";
import path from "node:path";


// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, path.dirname(process.cwd()), 'VITE_');

    return {
        plugins: [react(), tailwindcss()],
        resolve: {
            alias: {
                "@": path.resolve(__dirname, "./src"),
            },
        },
        server: {
            proxy: {
                '/api': env.VITE_API_BASE_URL,
                '/api/ws': {
                    target: env.VITE_WS_BASE_URL,
                    ws: true,
                }
            }
        },
        test: {
            include: ['**/*.spec.tsx'],
            browser: {
                enabled: true,
                provider: 'playwright',
                instances: [
                    {
                        browser: 'chromium',
                        headless: true
                    }
                ]
            },
        },
        define: {
            global: 'globalThis',
        }
    };
})
