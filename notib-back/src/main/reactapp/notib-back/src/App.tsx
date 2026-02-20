import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import { CssBaseline } from '@mui/material';
import { ThemeProvider, useTheme, useColorScheme } from '@mui/material/styles';
import { envVar, OidcAuthProvider, ContainerAuthProvider, ResourceApiProvider } from 'reactlib';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import notibLogoLight from './assets/notib_logo_light.png';
import notibLogoDark from './assets/notib_logo_dark.png';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import NotibProvider from './components/NotibProvider';
import { useNotibContext, ROLE_SUPER, ROLE_ADMIN } from './components/NotibContext';
import theme from './theme';

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

const isAuthUrlPresent = envVar('VITE_AUTH_URL', envVars) != null;
const AuthProvider = isAuthUrlPresent ? OidcAuthProvider : ContainerAuthProvider;
const version = '0.0.0';

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const { mode } = useColorScheme();
    const { currentRole } = useNotibContext();
    const menuConfig = [
        {
            id: 'entitats',
            title: t('menu.entitats'),
            to: '/entitats',
            icon: 'layers',
            resourceName: 'entitatResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'avisos',
            title: t('menu.avisos'),
            to: '/avisos',
            icon: 'notifications',
            resourceName: 'avisResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'propietats',
            title: t('menu.propietats'),
            to: '/propietats',
            icon: 'settings',
            resourceName: 'configGroupResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'currentEntitat',
            title: t('menu.currentEntitat'),
            to: '/entitats/current',
            icon: 'my_location',
            resourceName: 'entitatResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'organs',
            title: t('menu.organsGestors'),
            to: '/organs',
            icon: 'account_tree',
            resourceName: 'organGestorResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'procediment',
            title: t('menu.procediments'),
            to: '/procediments',
            icon: 'view_timeline',
            resourceName: 'procedimentResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'servei',
            title: t('menu.serveis'),
            to: '/serveis',
            icon: 'miscellaneous_services',
            resourceName: 'procedimentResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'grups',
            title: t('menu.grups'),
            to: '/grups',
            icon: 'group',
            resourceName: 'grupResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'pagadorspostals',
            title: t('menu.pagadorsPostals'),
            to: '/pagadorspostals',
            icon: 'markunread_mailbox',
            resourceName: 'pagadorCieResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'pagadorscie',
            title: t('menu.pagadorsCie'),
            to: '/pagadorscie',
            icon: 'mark_as_unread',
            resourceName: 'pagadorPostalResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'integracions',
            title: t('menu.integracions'),
            to: '/integracions',
            icon: 'build',
            resourceName: 'monitorIntegracioResource',
            hidden: currentRole !== ROLE_SUPER,
        },
    ];
    const menuEntries = [
        {
            id: 'home',
            title: t('menu.home'),
            to: 'home',
            icon: 'home',
        },
        {
            id: 'config',
            title: t('menu.notificacions'),
            to: '/notificacions',
            icon: 'notifications',
        },
        {
            id: 'config',
            title: t('menu.config'),
            icon: 'settings',
            children: menuConfig,
        },
    ];
    const theme = useTheme();
    const bgColor = mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor = bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    return (
        mode && (
            <BaseApp
                code="not"
                logo={mode === 'light' ? goibLogoLight : goibLogoDark}
                logoStyle={{
                    '& img': { height: '49px' },
                    pl: 1,
                    pr: '29px',
                    borderRight: '1px solid ' + theme.palette.divider,
                }}
                title={
                    <img
                        style={{
                            marginLeft: '8px',
                            height: '49px',
                            verticalAlign: 'middle',
                        }}
                        src={mode === 'light' ? notibLogoLight : notibLogoDark}
                        alt="Notib"
                    />
                }
                version={version}
                availableLanguages={['ca', 'es']}
                menuEntries={menuEntries}
                appbarBackgroundColor={bgColor}
                appbarStyle={{ color: textColor }}
                footer={
                    <div style={{ height: '36px' }}>
                        <DrassanaFooter
                            title="NOTIB"
                            backgroundColor="#5F5D5D"
                            style={{ position: 'fixed', width: '100%', bottom: 0 }}
                        />
                    </div>
                }
                footerHeight={36}
            >
                <Outlet />
            </BaseApp>
        )
    );
};

export const App = () => {
    return (
        <AuthProvider
            appBaseUrl={import.meta.env.BASE_URL}
            logoutUrl={import.meta.env.BASE_URL}
            config={getAuthConfig()}
            mandatory
        >
            <ResourceApiProvider apiUrl={getEnvApiUrl()}>
                <ThemeProvider theme={theme}>
                    <CssBaseline />
                    <NotibProvider>
                        <InnerApp />
                    </NotibProvider>
                </ThemeProvider>
            </ResourceApiProvider>
        </AuthProvider>
    );
};

export default App;
