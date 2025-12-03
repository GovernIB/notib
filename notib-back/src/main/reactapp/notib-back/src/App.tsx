import { useTranslation } from 'react-i18next';
import { useTheme, useColorScheme } from '@mui/material/styles';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import notibLogoLight from './assets/notib_logo_light.png';
import notibLogoDark from './assets/notib_logo_dark.png';
import { BaseApp } from './components/BaseApp';
import AppRoutes from './AppRoutes';

const version = '0.0.0';

export const App = () => {
    const { t } = useTranslation();
    const { mode } = useColorScheme();
    const menuEntries = [{
        id: 'home',
        title: t('menu.home'),
        to: 'home',
        icon: 'home'
    }, {
        id: 'entitats',
        title: t('menu.entitats'),
        to: '/entitats',
        icon: 'mail_outline',
        resourceName: 'entitatsResource',
    }, {
        id: 'enviaments',
        title: t('menu.enviaments'),
        to: '/enviaments',
        icon: 'mail_outline',
        resourceName: 'enviamentResource',
    }];
    const theme = useTheme();
    const bgColor = mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor = bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    return mode && <BaseApp
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
                style={{ marginLeft: '8px', height: '49px', verticalAlign: 'middle' }}
                src={mode === 'light' ? notibLogoLight : notibLogoDark}
                alt="Notib" />
        }
        version={version}
        availableLanguages={['ca', 'es']}
        menuEntries={menuEntries}
        appbarBackgroundColor={bgColor}
        appbarStyle={{ color: textColor }}>
        <AppRoutes />
    </BaseApp>;
};

export default App;
