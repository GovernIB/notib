import { useTranslation } from 'react-i18next';
import { useTheme, useColorScheme } from '@mui/material/styles';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import notibLogoLight from './assets/notib_logo_light.png';
import notibLogoDark from './assets/notib_logo_dark.png';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import NotibProvider from './components/NotibProvider';
import { useNotibContext, ROLE_SUPER, ROLE_ADMIN } from './components/NotibContext';
import AppRoutes from './AppRoutes';

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
            id: 'grups',
            title: t('menu.grups'),
            to: '/grups',
            icon: 'group',
            resourceName: 'grupResource',
            hidden: currentRole !== ROLE_ADMIN,
        },
        /*{
        id: 'procediment',
        title: t('menu.procediments'),
        to: '/procediment',
        icon: 'notifications',
        resourceName: 'procedimentResource',
    }*/
        /*, {
        id: 'enviaments',
        title: t('menu.enviaments'),
        to: '/enviaments',
        icon: 'mail_outline',
        resourceName: 'enviamentResource',
    }*/
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
                footerHeight={36}>
                <AppRoutes />
            </BaseApp>
        )
    );
};

export const App = () => {
    return (
        <NotibProvider>
            <InnerApp />
        </NotibProvider>
    );
};

export default App;
