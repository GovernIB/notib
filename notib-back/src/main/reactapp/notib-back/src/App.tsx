import { useTranslation } from 'react-i18next';
import { useTheme, useColorScheme } from '@mui/material/styles';
import logo from './assets/goib_logo.svg';
import notibLogo from './assets/notib_logo.png';
import { BaseApp } from './components/BaseApp';
import AppRoutes from './AppRoutes';

const version = '0.0.0';

export const App = () => {
    const { mode } = useColorScheme();
    if (!mode) {
        return null;
    }
    const { t } = useTranslation();
    const menuEntries = [{
        id: 'home',
        title: t('menu.home'),
        to: 'home',
        icon: 'home'
    }, {
        id: 'enviament',
        title: t('menu.enviament'),
        to: '/enviament',
        icon: 'mail_outline',
        resourceName: 'enviamentResource',
    }];
    const theme = useTheme();
    const bgColor = theme.palette.background.paper;
    const textColor = theme.palette.getContrastText(bgColor);
    return (
        <BaseApp
            code="not"
            logo={logo}
            logoStyle={{
                '& img': { height: '45px' },
                pl: '15px',
                pr: 4,
                borderRight: '1px solid ' + theme.palette.divider,
            }}
            title={
                <img
                    style={{ marginLeft: '16px', height: '54px', verticalAlign: 'middle' }}
                    src={notibLogo}
                    alt="Notib" />
            }
            version={version}
            availableLanguages={['ca', 'es']}
            menuEntries={menuEntries}
            appbarBackgroundColor={bgColor}
            appbarStyle={{ color: textColor }}>
            <AppRoutes />
        </BaseApp>
    );
};

export default App;
