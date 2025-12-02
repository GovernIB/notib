import React from 'react';
import {
    useNavigate,
    useLocation,
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
} from 'react-router-dom';
import i18n from '../i18n/i18n';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiBaseApp,
    type MenuEntry,
    useBaseAppContext,
    useResourceApiContext,
} from 'reactlib';
import HeaderThemeSelector from './HeaderThemeSelector';
import HeaderLanguageSelector from './HeaderLanguageSelector';

export type MenuEntryWithResource = MenuEntry & {
    resourceName?: string;
}

export type HeaderBackgroundModuleItem = {
    color?: string;
    image?: string;
}

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    logo?: string;
    logoStyle?: any;
    title?: string | React.ReactElement;
    title_logo?: string;
    version: string;
    availableLanguages?: string[];
    menuEntries?: MenuEntryWithResource[];
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
    appbarStyle?: any;
};


export const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const useBaseAppMenuEntries = (menuEntries?: MenuEntryWithResource[]) => {
    const [processedMenuEntries, setprocessedMenuEntries] = React.useState<MenuEntry[]>();
    const { isReady: apiIsReady, indexState: apiIndex } = useResourceApiContext();
    React.useEffect(() => {
        if (apiIsReady) {
            const apiLinks = apiIndex?.links.getAll();
            const resourceNames = apiLinks?.map((l: any) => l.rel);
            const processedMenuEntries = menuEntries?.
                filter(e => e?.resourceName == null || resourceNames?.includes(e.resourceName)).
                map(e => {
                    const { resourceName, ...otherProps } = e;
                    return otherProps;
                });
            setprocessedMenuEntries(processedMenuEntries);
        }
    }, [apiIsReady, apiIndex]);
    return processedMenuEntries;
}

const useLocationPath = () => {
    const location = useLocation();
    return location.pathname;
}

const CustomLocalizationProvider = ({ children }: React.PropsWithChildren) => {
    const { currentLanguage } = useBaseAppContext();
    const adapterLocale = React.useMemo(() => {
        const languageTwoChars = currentLanguage?.substring(0, 2).toLowerCase();
        switch (languageTwoChars) {
            case 'ca':
            case 'es':
            case 'en':
                return languageTwoChars;
            default:
                return 'ca';
        }
    }, [currentLanguage]);
    const adapter = AdapterDayjs;
    return <LocalizationProvider dateAdapter={adapter} adapterLocale={adapterLocale}>
        {children}
    </LocalizationProvider>;
}

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        logoStyle,
        title,
        version,
        availableLanguages,
        menuEntries,
        appbarBackgroundColor,
        appbarBackgroundImg,
        appbarStyle,
        children
    } = props;
    const navigate = useNavigate();
    const location = useLocation();
    const baseAppMenuEntries = useBaseAppMenuEntries(menuEntries);
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    }
    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
    }
    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (anyHistoryEntryExist()) {
            navigate(-1);
        } else if (fallback != null) {
            navigate(fallback);
        } else {
            console.warn('[BACK] Couldn\'t go back, neither fallback specified nor previous entry exists in navigation history');
        }
    }
    return <MuiBaseApp
        code={code}
        headerTitle={title}
        headerLogo={logo}
        headerLogoStyle={logoStyle}
        headerVersion={version}
        headerAppbarStyle={appbarStyle}
        headerAppbarBackgroundColor={appbarBackgroundColor}
        headerAppbarBackgroundImg={appbarBackgroundImg}
        headerAdditionalAuthComponents={availableLanguages?.length ? [
            /*<Box sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
                <HeaderThemeSelector />
            </Box>,*/
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
                <HeaderLanguageSelector key="sel_lang" languages={availableLanguages} />
            </Box>
        ] : undefined}
        persistentSession
        persistentLanguage
        i18nUseTranslation={useTranslation}
        i18nCurrentLanguage={i18n.language}
        i18nHandleLanguageChange={i18nHandleLanguageChange}
        i18nAddResourceBundleCallback={i18nAddResourceBundleCallback}
        routerGoBack={goBack}
        routerNavigate={navigate}
        routerUseLocationPath={useLocationPath}
        routerAnyHistoryEntryExist={anyHistoryEntryExist}
        linkComponent={Link}
        menuEntries={baseAppMenuEntries}>
        <CustomLocalizationProvider>
            {children}
        </CustomLocalizationProvider>
    </MuiBaseApp>;
}

export default BaseApp;