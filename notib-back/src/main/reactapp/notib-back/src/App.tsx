import { useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import { CssBaseline } from '@mui/material';
import { ThemeProvider, useTheme } from '@mui/material/styles';
import { envVar, OidcAuthProvider, ContainerAuthProvider, ResourceApiProvider } from 'reactlib';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import notibLogoLight from './assets/notib_logo_light.png';
import notibLogoDark from './assets/notib_logo_dark.png';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import NotibProvider from './components/NotibProvider';
import FormInitialTabProvider from './components/FormInitialTabProvider';
import {useNotibContext, ROLE_SUPER, ROLE_APLICACIO} from './components/NotibContext';
import { lightTheme } from './theme';
import { getMenuEntries } from './routeAccess';
import {
    APPBAR_PADDING_LEFT,
    FOOTER_HEIGHT,
    HEADER_HEIGHT,
    LOGO_BOX_LEFT,
    LOGO_HEIGHT,
    LOGO_SEPARATOR_GAP,
    MENU_WIDTH,
} from './utils/layout';
import SseProvider from "./components/SseClient.tsx";
import {UserPreferencesProvider, useUserPreferences} from "./components/UserProfile.tsx";
import TemaProvider from "./components/TemaProvider.tsx";

export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
    VITE_AUTH_URL: import.meta.env.VITE_AUTH_URL,
    VITE_AUTH_REALM: import.meta.env.VITE_AUTH_REALM,
    VITE_AUTH_CLIENTID: import.meta.env.VITE_AUTH_CLIENTID,
    VITE_APP_VERSION: import.meta.env.VITE_APP_VERSION,
};

const getAuthConfig = () => ({
    url: envVar('VITE_AUTH_URL', envVars),
    realm: envVar('VITE_AUTH_REALM', envVars),
    clientId: envVar('VITE_AUTH_CLIENTID', envVars),
});

export const getEnvApiUrl = () => {

    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return envApiPublicUrl ?? envApiUrl;
    }
    const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
    const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
    if (envApiBaseUrl) {
        return envApiBaseUrl + envApiSuffix;
    }
    return (window.location.protocol + '//' + window.location.host + ':' + window.location.port + envApiSuffix);
};

// const isAuthUrlPresent = envVar('VITE_AUTH_URL', envVars) != null;
const isAuthUrlPresent = Boolean(envVar('VITE_AUTH_URL', envVars));
const AuthProvider = isAuthUrlPresent ? OidcAuthProvider : ContainerAuthProvider;
const version = '0.0.0';

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const { estilMenu } = useUserPreferences();
    const { isReady, currentRole, currentEntitatId } = useNotibContext();
    const menuEntries = getMenuEntries(currentRole, t);
    const theme = useTheme();
    const mode = theme.palette.mode;
    const bgColor= mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor= bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    const currentRoleSuperOrEntitatSelected= currentRole === ROLE_SUPER || currentRole === ROLE_APLICACIO || currentEntitatId != null;
    // Mentre es canvia de rol o d'entitat, isReady torna a ser false momentàniament i, per tant, també
    // currentRoleSuperOrEntitatSelected (l'entitat es reinicia fins que se'n selecciona una de nova).
    // Sense distingir-ho de la situació real "l'usuari no té accés a cap entitat", es mostraria
    // momentàniament aquest avís cada vegada. Un cop seleccionat una vegada es manté el menú visible
    // (les seves entrades ja reflecteixen el nou rol) i, al contingut, no es mostra l'avís fins que
    // isReady torna a ser true i encara no hi ha entitat seleccionada.
    const hasBeenSelectedRef = useRef(false);
    if (currentRoleSuperOrEntitatSelected) {
        hasBeenSelectedRef.current = true;
    }
    const logoColor = mode === 'light' ? notibLogoLight : notibLogoDark;
    return (
        <BaseApp
            code="NOTIB"
            logo={mode === 'light' ? goibLogoLight : goibLogoDark}
            logoStyle={{
                '& img': { height: LOGO_HEIGHT + 'px', display: 'block' },
                // El separador vertical ha de caure sobre la vora dreta del menú obert, i omplir
                // l'alçada de la capçalera. El botó de menú duu un marge esquerre de -12 i un dret de
                // 16, així que la caixa del logo arrenca a LOGO_BOX_LEFT. Fixant-ne l'amplada (en
                // comptes de deixar que la mida del logo mani) la vora cau sempre a MENU_WIDTH.
                width: MENU_WIDTH - LOGO_BOX_LEFT + 'px',
                boxSizing: 'border-box',
                alignSelf: 'stretch',
                borderRight: `1px solid ${theme.palette.divider}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                // Substitueix el mr/pt/pr de la llibreria (l'sx s'hi fusiona al final).
                mr: 0,
                pt: 0,
                pl: 0,
                pr: LOGO_SEPARATOR_GAP + 'px',
            }}
            title={
                <img
                    style={{ marginLeft: LOGO_SEPARATOR_GAP + 'px', height: LOGO_HEIGHT + 'px', verticalAlign: 'middle' }}
                    src={logoColor}
                    alt="Notib"
                />
            }
            version={version}
            availableLanguages={['ca', 'es']}
            menuEntries={(currentRoleSuperOrEntitatSelected || hasBeenSelectedRef.current) ? menuEntries : undefined}
            menuAppearance={estilMenu}
            appbarBackgroundColor={bgColor}
            // El botó de menú duu ml -12, així que amb APPBAR_PADDING_LEFT queda centrat a la
            // mateixa columna que les icones del menú lateral. L'alçada mínima substitueix els 64px
            // que la llibreria dona a la barra.
            appbarStyle={{
                color: textColor,
                paddingLeft: APPBAR_PADDING_LEFT + 'px',
                minHeight: HEADER_HEIGHT + 'px',
            }}
            footerHeight={FOOTER_HEIGHT}
            // El peu va per flux, no fixat: l'arrel de l'aplicació ocupa l'alçada de la finestra
            // (veure frameSx a BaseApp.tsx), així que queda sempre a baix i sense tapar el contingut.
            footer={
                <div style={{ height: FOOTER_HEIGHT + 'px', overflow: 'hidden' }}>
                    <DrassanaFooter title="NOTIB" backgroundColor="#5F5D5D" />
                </div>
            }
        >
            {currentRoleSuperOrEntitatSelected ? (
                <Outlet />
            ) : isReady ? (
                <Alert severity="error">{t('app.noEntitat')}</Alert>
            ) : (
                <Box sx={{ display: 'flex', justifyContent: 'center', pt: 4 }}>
                    <CircularProgress />
                </Box>
            )}
        </BaseApp>
    );
};

export const App = () => {
    const authConfig = getAuthConfig();
    return (
        <AuthProvider
            appBaseUrl={import.meta.env.BASE_URL}
            logoutUrl={import.meta.env.BASE_URL}
            config={authConfig}
            mandatory
        >
            <ResourceApiProvider apiUrl={getEnvApiUrl()}>
                <ThemeProvider theme={lightTheme}>
                    <CssBaseline />
                    <NotibProvider>
                        <SseProvider>
                            <TemaProvider>
                                <UserPreferencesProvider>
                                        <FormInitialTabProvider>
                                            <InnerApp />
                                        </FormInitialTabProvider>
                                </UserPreferencesProvider>
                            </TemaProvider>
                        </SseProvider>
                    </NotibProvider>
                </ThemeProvider>
            </ResourceApiProvider>
        </AuthProvider>
    );
};

export default App;
