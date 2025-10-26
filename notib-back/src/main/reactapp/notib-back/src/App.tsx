import { useTranslation } from 'react-i18next';
import { BaseApp } from './components/BaseApp';
import logo from './assets/goib_logo.svg';
import notibLogo from './assets/notib_logo.png'; // TODO Cambiar por SVG
import AppRoutes from './AppRoutes';

const version = '0.0.0';

export const App = () => {
    const { t } = useTranslation();
    const menuSalut = {
        id: 'salut',
        title: t('menu.salut'),
        to: '/',
        icon: 'monitor_heart',
        resourceName: 'salut',
    };
    const menuEntries = [
        menuSalut,
    ];
    const appMenuEntries = [
        menuSalut,
    ];
    return (
        <BaseApp
            code="not"
            logo={logo}
            logoStyle={{
                '& img': { height: '38px' },
                pl: 2,
                pr: 4,
                mr: 4,
                borderRight: '2px solid #000',
            }}
            title={
                <img
                    style={{ height: '64px', verticalAlign: 'middle' }}
                    src={notibLogo}
                    alt="Logo de l'aplicació de Notib"
                />
            }
            version={version}
            availableLanguages={['ca', 'es']}
            menuEntries={menuEntries}
            appMenuEntries={appMenuEntries}
            appbarBackgroundColor="#ff9523"
        >
            <AppRoutes />
        </BaseApp>
    );
};

export default App;
