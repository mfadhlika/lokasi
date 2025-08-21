/// <reference types="vitest" />
/// <reference types="@vitest/browser/providers/playwright" />
/// <reference types="@vitest/browser/matchers" />

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite";
import path from "node:path";

// https://vite.dev/config/
export default defineConfig({
    plugins: [react(), tailwindcss()],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080'
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
})
