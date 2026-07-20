import { defineConfig, loadEnv, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// Vite prepends `base` to every root-relative src/href it finds in index.html.
// The backend endpoints referenced there (sysenv, manifest, authToken, authRoles)
// live under the servlet context path, not under the app's base path, so undo
// that prefix for them once Vite's own html transform has already run.
const fixBackendEndpointsBase = (): Plugin => {
    let base = '/';
    return {
        name: 'fix-backend-endpoints-base',
        configResolved(config) {
            base = config.base;
        },
        transformIndexHtml: {
            order: 'post',
            handler(html) {
                if (base === '/' || base === '') return html;
                return html.replaceAll(`${base}notibback/`, '/notibback/');
            },
        },
    };
};

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    // Load env file based on `mode` in the current working directory.
    // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
    const env = loadEnv(mode, process.cwd(), '');

    return {
        preview: {
            port: 5173,
        },
        server: {
            open: env.DISABLE_OPEN_ON_START !== 'true',
            hmr: {
                clientPort: 5173, // TODO Documentar esto
            },
        },
        plugins: [react(), tsconfigPaths(), fixBackendEndpointsBase()],
    };
});
