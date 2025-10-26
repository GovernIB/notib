import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@emotion/react';
import { CssBaseline } from '@mui/material';
import { LicenseInfo } from '@mui/x-license';
import theme from './theme';
import App from './App.tsx';
import { envVar, ResourceApiProvider } from 'reactlib';

LicenseInfo.setLicenseKey(
    'd7a3848ee04d821438959d61037634beTz0xMDY1ODQsRT0xNzY5Mjk5MTk5MDAwLFM9cHJvLExNPXN1YnNjcmlwdGlvbixQVj1pbml0aWFsLEtWPTI='
);

export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
};

export const getEnvApiUrl = () => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return envApiPublicUrl ?? envApiUrl;
    } else {
        const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
        const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
        if (envApiBaseUrl) {
            return envApiBaseUrl + envApiSuffix;
        } else {
            return (
                window.location.protocol +
                '//' +
                window.location.host +
                ':' +
                window.location.port +
                envApiSuffix
            );
        }
    }
};

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ResourceApiProvider apiUrl={getEnvApiUrl()} userSessionActive>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <BrowserRouter basename={import.meta.env.BASE_URL}>
                    <App />
                </BrowserRouter>
            </ThemeProvider>
        </ResourceApiProvider>
    </StrictMode>
);
