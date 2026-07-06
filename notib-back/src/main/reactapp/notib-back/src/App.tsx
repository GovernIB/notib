import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import Alert from '@mui/material/Alert';
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
import { useNotibContext, ROLE_SUPER, ROLE_ADMIN, ROLE_USER } from './components/NotibContext';
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
    }
    const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
    const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
    if (envApiBaseUrl) {
        return envApiBaseUrl + envApiSuffix;
    }
    return (window.location.protocol + '//' + window.location.host + ':' + window.location.port + envApiSuffix);
};

const isAuthUrlPresent = envVar('VITE_AUTH_URL', envVars) != null;
const AuthProvider = isAuthUrlPresent ? OidcAuthProvider : ContainerAuthProvider;
const version = '0.0.0';

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const { mode } = useColorScheme();
    const { currentRole, currentEntitatId } = useNotibContext();

    const menuEnviamentMassiu = [
        {
            id: 'nouEnviamentMassiu',
            title: t('app.menu.nouEnviamentmassiu'),
            to: '/notificacio/massiva/form',
            icon: 'add',
            resourceName: 'notificacioMassivaResource',
            hidden: currentRole !== ROLE_USER,
        },
        {
                id: 'enviamentMassiu',
            title: t('app.menu.consultaEnviamentmassiu'),
            to: '/notificacio/massiva',
            icon: 'forward_to_inbox',
            resourceName: 'notificacioMassivaResource',
            hidden: currentRole !== ROLE_USER,
        },
    ];
    const menuGestio = [
        {
            id: 'notificacionsErrorRegistre',
            title: t('app.menu.errorRegistre'),
            to: '/notificacionsErrorRegistre',
            icon: 'error',
            resourceName: 'notificacioResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'enviamentMassiu',
            title: t('app.menu.consultaEnviamentmassiu'),
            to: '/notificacio/massiva',
            icon: 'forward_to_inbox',
            resourceName: 'notificacioMassivaResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'notificacionsEsborrades',
            title: t('app.menu.notificacioEsborrades'),
            to: '/notificacionsEsborrades',
            icon: 'delete_outline',
            resourceName: 'notificacioResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'callbackPendent',
            title: t('app.menu.callbackPendent'),
            to: '/callbacks',
            icon: 'pending_actions',
            resourceName: 'callbackResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'accionsMassives',
            title: t('app.menu.accionsMassives'),
            to: '/accions/massives',
            icon: 'format_list_bulleted',
            resourceName: 'accioMassivaResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'permisosUsuari',
            title: t('app.menu.permisosUsuari'),
            to: '/permisos',
            icon: 'group',
            resourceName: '',
            hidden: currentRole !== ROLE_ADMIN,
        },
    ];
    const menuConfig = [
        {
            id: 'entitats',
            title: t('app.menu.entitats'),
            to: '/entitats',
            icon: 'layers',
            resourceName: 'entitatResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'propietats',
            title: t('app.menu.propietats'),
            to: '/propietats',
            icon: 'settings',
            resourceName: 'configGroupResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'currentEntitat',
            title: t('app.menu.currentEntitat'),
            to: '/entitats/current',
            icon: 'my_location',
            resourceName: 'entitatResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'organs',
            title: t('app.menu.organsGestors'),
            to: '/organs',
            icon: 'account_tree',
            resourceName: 'organGestorResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'procediment',
            title: t('app.menu.procediments'),
            to: '/procediments',
            icon: 'view_timeline',
            resourceName: 'procedimentResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'servei',
            title: t('app.menu.serveis'),
            to: '/serveis',
            icon: 'miscellaneous_services',
            resourceName: 'procedimentResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'grups',
            title: t('app.menu.grups'),
            to: '/grups',
            icon: 'group',
            resourceName: 'grupResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'pagadorspostals',
            title: t('app.menu.pagadorsPostals'),
            to: '/pagadorspostals',
            icon: 'markunread_mailbox',
            resourceName: 'pagadorCieResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'pagadorscie',
            title: t('app.menu.pagadorsCie'),
            to: '/pagadorscie',
            icon: 'mark_as_unread',
            resourceName: 'pagadorPostalResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'caches',
            title: t('app.menu.cache'),
            to: '/caches',
            icon: 'sd_storage',
            resourceName: 'cacheResource',
            hidden: currentRole !== ROLE_SUPER,
        }
    ];
    const menuMonitoritza = [
        {
            id: 'notificacionsCallbackError',
            title: t('app.menu.notificacionsCallbacksError'),
            to: '/notificacionsCallbackError',
            icon: 'running_with_errors',
            resourceName: 'notificacioResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'integracions',
            title: t('app.menu.integracions'),
            to: '/integracions',
            icon: 'build',
            resourceName: 'monitorIntegracioResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'metriques',
            title: t('app.menu.metriques'),
            to: '/metriques',
            icon: 'bar_chart',
            resourceName: 'metriquesResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'monitorSistema',
            title: t('app.menu.monitorSistema'),
            to: '/monitorSistema',
            icon: 'monitor_heart',
            // resourceName: 'threadInfoResource',
            // resourceName: 'integracioResource',
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'activemq',
            title: t('app.menu.activemq'),
            to: '/activemq',
            icon: 'subscriptions',
            resourceName: 'activeMqResource',
            hidden: currentRole !== ROLE_SUPER,
        },

    ];
    const menuEntries = [
        {
            id: 'home',
            title: t('app.menu.home'),
            to: 'home',
            icon: 'home',
        },
        {
            id: 'notificacions',
            title: t('app.menu.notificacions'),
            to: '/notificacions',
            icon: 'mail',
            resourceName: 'notificacioResource',
            hidden: currentRole === ROLE_SUPER,
        },
        {
            id: 'enviaments',
            title: t('app.menu.enviaments'),
            to: '/enviaments',
            icon: 'send',
            resourceName: 'notificacioEnviamentResource',
        },
        {
            id: 'enviamentsMassius',
            title: t('app.menu.enviamentMassiu'),
            icon: 'dashboard',
            children: menuEnviamentMassiu,
            hidden: currentRole !== ROLE_USER,
        },
        {
            id: 'gestio',
            title: t('app.menu.gestio'),
            icon: 'dashboard',
            children: menuGestio,
            hidden: currentRole !== ROLE_ADMIN,
        },
        {
            id: 'monitoritza',
            title: t('app.menu.monitoritza'),
            icon: 'monitor',
            children: menuMonitoritza,
            hidden: currentRole !== ROLE_SUPER,
        },
        {
            id: 'config',
            title: t('app.menu.config'),
            icon: 'settings',
            children: menuConfig,
            hidden: currentRole === ROLE_USER,
        },
        {
            id: 'avisos',
            title: t('app.menu.avisos'),
            to: '/avisos',
            icon: 'notifications',
            resourceName: 'avisResource',
            hidden: currentRole !== ROLE_SUPER,
        },
    ];
    const theme = useTheme();
    const bgColor= mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor= bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    const currentRoleSuperOrEntitatSelected= currentRole === ROLE_SUPER || currentEntitatId != null;
    return (
        mode && (
            <BaseApp
                code="NOTIB"
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
                menuEntries={currentRoleSuperOrEntitatSelected ? menuEntries : undefined}
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
                {currentRoleSuperOrEntitatSelected ? (
                    <Outlet />
                ) : (
                    <Alert severity="error">{t('app.noEntitat')}</Alert>
                )}
            </BaseApp>
        )
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
