import { expect, vi, describe, it, beforeEach } from 'vitest';
import { render, type RenderResult } from 'vitest-browser-react'
import LoginPage from './login';
import { BrowserRouter } from 'react-router';

describe('login page', async () => {
    let loginPage: RenderResult;

    beforeEach(() => {
        loginPage = render(
            <BrowserRouter>
                <LoginPage />
            </BrowserRouter>
        );
    });

    it('should render login page', async () => {
        const header = loginPage.getByText('Lokasi');

        await expect.element(header).toHaveTextContent('Lokasi');
    });

    describe('login', async () => {
        const mocks = vi.hoisted(() => ({
            login: vi.fn(),
            navigate: vi.fn()
        }))

        vi.mock('@/services/auth-service', async () => {
            return {
                authService: {
                    login: mocks.login
                }
            };
        });

        vi.mock('react-router', async (importActual) => {
            const actual = await importActual<typeof import('react-router')>();
            return {
                ...actual,
                useNavigate: () => mocks.navigate
            }
        });

        it('should navigate if login success', async () => {
            mocks.login.mockResolvedValue({
                data: {
                    accessToken: 'random-access-token'
                },
                message: 'success'
            });

            await loginPage.getByRole("textbox", { name: "Username" }).fill("test");
            await loginPage.getByRole("textbox", { name: "Password" }).fill("test");
            await loginPage.getByRole("button", { name: "Login" }).click();

            expect(mocks.login).toHaveBeenCalledWith({
                username: 'test',
                password: 'test'
            });

            expect(mocks.navigate).toHaveBeenCalledWith("/");
        });

        it('should show error message if login failed', async () => {
            mocks.login.mockRejectedValue({
                response: {
                    data: {
                        message: 'invalid username/password'
                    }
                }
            });

            await loginPage.getByRole("textbox", { name: "Username" }).fill("test");
            await loginPage.getByRole("textbox", { name: "Password" }).fill("test");
            await loginPage.getByRole("button", { name: "Login" }).click();

            expect(mocks.login).toHaveBeenCalledWith({
                username: 'test',
                password: 'test'
            });

            expect(mocks.navigate).toHaveBeenCalledWith("/");

            await expect.element(loginPage.getByText("invalid username/password")).toBeVisible();
        });
    });
});


