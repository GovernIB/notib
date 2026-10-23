import React from 'react';
import {
    useNavigate,
    useLocation,
    useBlocker,
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
} from 'react-router-dom';
import { saveAs } from 'file-saver';
import i18n from '../i18n/i18n';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { alpha, useTheme } from '@mui/material/styles';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiBaseApp,
    type MenuEntry,
    useBaseAppContext,
    useResourceApiContext,
    useMuiFormDialogApiRef,
} from 'reactlib';
import AppFormFieldReference from './AppFormFieldReference';
import Offline from './Offline';
import { UserProfileMenu, UserProfileFormDialog } from './UserProfile';
import SwitchInterfaceMenuItem from './SwitchInterfaceMenuItem';
import { MAIN_GAP, MAIN_PADDING } from '../utils/layout';
import { darkTheme, lightTheme } from '../theme';
import { roleBadgeIcon } from '../utils/roleIcons';
import { useNotibContext } from './NotibContext';
import {AvisosBanner} from "./AvisosBanner.tsx";
import reactlibCa from "../i18n/reactlibCa.ts";
import reactlibEs from "../i18n/reactlibEs.ts";

export type MenuEntryWithResource = MenuEntry & {
    resourceName?: string;
    hidden?: boolean | (() => boolean);
    children?: MenuEntryWithResource[];
};

export type HeaderBackgroundModuleItem = {
    color?: string;
    image?: string;
};

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    logo?: string;
    logoStyle?: any;
    title?: string | React.ReactElement;
    title_logo?: string;
    version: string;
    availableLanguages?: string[];
    menuEntries?: MenuEntryWithResource[];
    menuAppearance?: string;
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
    appbarStyle?: any;
    footer?: React.ReactElement;
    footerHeight?: number;
};

export const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

export const filterMenuEntries = (
    menuEntries: MenuEntryWithResource[] | undefined,
    resourceNames?: string[]
): MenuEntry[] | undefined => {
    return menuEntries
        ?.map((e) => {
            const filteredChildren = filterMenuEntries(e.children, resourceNames);
            const passesResource =
                e.resourceName == null || resourceNames?.includes(e.resourceName);
            const passesHidden = e.hidden == null || !e.hidden;
            if (!passesResource || !passesHidden) {
                return filteredChildren && filteredChildren.length > 0
                    ? { ...e, children: filteredChildren }
                    : null;
            }
            const { resourceName, ...otherProps } = e;
            return {
                ...otherProps,
                ...(filteredChildren ? { children: filteredChildren } : {}),
            };
        })
        .filter((e): e is MenuEntry => e !== null);
};

const useBaseAppMenuEntries = (menuEntries?: MenuEntryWithResource[]) => {
    const { isReady: apiIsReady, indexState: apiIndex } = useResourceApiContext();
    return React.useMemo(() => {
        if (apiIsReady) {
            const apiLinks = apiIndex?.links.getAll();
            const resourceNames = apiLinks?.map((l: any) => l.rel);
            return filterMenuEntries(menuEntries, resourceNames);
        } else {
            return [];
        }
    }, [apiIsReady, apiIndex]);
};

const useLocationPath = () => {
    const location = useLocation();
    return location.pathname;
};

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
    return (
        <LocalizationProvider dateAdapter={adapter} adapterLocale={adapterLocale}>
            {children}
        </LocalizationProvider>
    );
};

// Paleta de colors del menú lateral segons l'estil escollit (MenuEstil):
// 'TEMA' reutilitza directament la paleta activa (no retorna cap color propi),
// 'TEMA_INVERTIT' pren els colors del tema contrari (clar <-> fosc), de manera que el menú
// es veu igual que amb l'estil 'TEMA' a l'altre tema, i 'PEU' aplica sempre el mateix gris
// fosc del peu de pàgina de l'aplicació. Tots mantenen els accents en el verd de Notib.
const getMenuColorSet = (theme: any, appearance?: string): any | undefined => {
    if (appearance === 'PEU') {
        return {
            background: '#5F5D5D',
            textPrimary: '#F6F6F6',
            textSecondary: '#E5E5E5',
            divider: '#807D7D',
            accent: darkTheme.palette.primary.main,
            selectedBackground: alpha(darkTheme.palette.primary.main, 0.25),
            hoverBackground: 'rgba(255, 255, 255, 0.08)',
        };
    }
    if (appearance !== 'TEMA_INVERTIT') {
        return undefined;
    }
    const invertedTheme = theme.palette.mode === 'dark' ? lightTheme : darkTheme;
    const { palette } = invertedTheme;
    return {
        background: palette.background.paper,
        textPrimary: palette.text.primary,
        textSecondary: palette.action.active,
        divider: palette.divider,
        accent: palette.primary.main,
        selectedBackground: alpha(palette.primary.main, palette.mode === 'dark' ? 0.2 : 0.1),
        hoverBackground: palette.action.hover,
    };
};

/**
 * Marc de l'aplicació: l'arrel ocupa tota l'alçada de la finestra (capçalera dalt, peu baix) i només
 * l'àrea de contingut (`<main>`) fa scroll. La llibreria només ho fa amb `100vh` a les pantalles que
 * l'activen (GridPage), i no fixa cap marge propi quan es desactiven (`marginsDisabled`). Per això:
 *  - `> div`: arrel del contingut de la llibreria (ContentComponentDefault, lib/components/BaseApp.tsx);
 *  - `> div > div > main`: l'àrea de contingut. El `gap` és la separació entre els elements apilats
 *    (la pantalla, els avisos...) i el padding, el marge de l'àrea.
 * Si la llibreria canvia aquesta estructura, aquest és l'únic lloc a revisar.
 */
const frameSx = {
    height: '100vh',
    '& > div': { height: '100%' },
    '& > div > div > main': {
        display: 'flex',
        flexDirection: 'column',
        gap: MAIN_GAP + 'px',
        padding: MAIN_PADDING + 'px',
        boxSizing: 'border-box',
        minHeight: 0,
        overflowY: 'auto',
    },
    '& nav .MuiDrawer-root .MuiList-root .MuiList-root .MuiListItemButton-root.Mui-selected': {
        boxShadow: 'none',
    },
} as const;

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        logoStyle,
        title,
        version,
        menuEntries,
        menuAppearance,
        appbarBackgroundColor,
        appbarBackgroundImg,
        appbarStyle,
        footer,
        footerHeight,
        children,
    } = props;
    const navigate = useNavigate();
    const location = useLocation();
    const theme = useTheme();
    const { currentRole } = useNotibContext();
    const baseAppMenuEntries = useBaseAppMenuEntries(menuEntries);
    const formDialogApiRef = useMuiFormDialogApiRef();
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    };

    // Espai de noms on base-react registra les seves traduccions (LIB_I18N_NS a lib/components/
    // BaseApp.tsx, que la llibreria no exporta) i sobreescriptures de l'aplicació per idioma.
    // base-react també hi registra l'anglès, que aquí no es fa servir i per això no té paquet.
    const REACTLIB_I18N_NS = 'reactlib';
    const REACTLIB_OVERRIDES: Record<string, any> = { ca: reactlibCa, es: reactlibEs };

    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
        // base-react registra aquí les seves traduccions. Com que no s'hi pot tocar, els seus
        // textos es corregeixen fusionant-hi a sobre el paquet de sobreescriptures de
        // l'aplicació (veure i18n/reactlibCa.ts): fusió profunda -- només se substitueixen les
        // claus declarades -- i amb sobreescriptura, perquè la clau ja existeix.
        const overrides = namespace === REACTLIB_I18N_NS ? REACTLIB_OVERRIDES[language] : undefined;
        if (overrides != null) {
            i18n.addResourceBundle(language, namespace, overrides, true, true);
        }
    };
    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (anyHistoryEntryExist()) {
            navigate(-1);
        } else if (fallback != null) {
            navigate(fallback);
        } else {
            console.warn(
                "[BACK] Couldn't go back, neither fallback specified nor previous entry exists in navigation history"
            );
        }
    };
    const menuColorSet = getMenuColorSet(theme, menuAppearance);
    const menuColorSetSx = {
        '& nav .MuiDrawer-root': {
            '& .MuiPaper-root, & .MuiList-root': {
                backgroundColor: menuColorSet?.background,
                color: menuColorSet?.textPrimary,
                '& > div .MuiBox-root': {
                    backgroundColor: menuColorSet?.background,
                    borderColor: menuColorSet?.divider,
                },
                '& > div > .MuiBox-root': {
                    borderLeft: `1px solid ${menuColorSet?.divider}`,
                },
                '& p': {
                    color: menuColorSet?.textPrimary,
                },
                '& h6': {
                    color: menuColorSet?.accent,
                },
            },
            '& .menu-item-icon': {
                color: menuColorSet?.textSecondary,
            },
            '& .MuiListItemButton-root': {
                // La icona i la vora esquerra de l'entrada seleccionada prenen l'accent del joc de
                // colors del menú (a TEMA les posa el tema, veure MuiListItemButton a theme.ts).
                '&.Mui-selected .menu-item-icon': {
                    color: menuColorSet?.accent,
                },
                '&.Mui-selected': {
                    backgroundColor: menuColorSet?.selectedBackground,
                    boxShadow: `inset 4px 0 0 ${menuColorSet?.accent}`,
                },
                '&.Mui-selected:has(+ .MuiBox-root)': {
                    boxShadow: 'none',
                },
                '&.Mui-selected > ::before': {
                    background: menuColorSet?.accent,
                },
                '&.Mui-selected:hover': {
                    backgroundColor: menuColorSet?.selectedBackground,
                },
                '&:hover': {
                    backgroundColor: menuColorSet?.hoverBackground,
                },
            },
        },
    };
    return (
        <Box sx={menuColorSet ? { ...frameSx, ...menuColorSetSx } : frameSx}>
        <MuiBaseApp
            code={code}
            headerTitle={title}
            headerLogo={logo}
            headerLogoStyle={logoStyle}
            headerVersion={version}
            headerAppbarStyle={appbarStyle}
            headerAppbarBackgroundColor={appbarBackgroundColor}
            headerAppbarBackgroundImg={appbarBackgroundImg}
            headerAdditionalComponents={[
                <SwitchInterfaceMenuItem key="fast_rewind" />,
            ]}
            headerAuthBadgeIcon={roleBadgeIcon(currentRole)}
            headerAdditionalAuthComponents={[
                <Box key="user_profile" sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
                    <UserProfileMenu formDialogApiRef={formDialogApiRef} />
                </Box>,
            ]}
            offline={<Offline />}
            footer={footer}
            footerHeight={footerHeight}
            persistentLanguage
            i18nUseTranslation={useTranslation}
            i18nCurrentLanguage={i18n.language}
            i18nHandleLanguageChange={i18nHandleLanguageChange}
            i18nAddResourceBundleCallback={i18nAddResourceBundleCallback}
            routerGoBack={goBack}
            routerNavigate={navigate}
            routerUseBlocker={useBlocker}
            routerUseLocationPath={useLocationPath}
            routerAnyHistoryEntryExist={anyHistoryEntryExist}
            linkComponent={Link}
            // Els marges d'`<main>` els posa frameSx (la llibreria els deixa a 16px 24px).
            marginsDisabled
            saveAs={saveAs}
            formFieldComponents={[{ type: 'reference', component: AppFormFieldReference }]}
            menuEntries={baseAppMenuEntries}
        >
            <CustomLocalizationProvider>
                <UserProfileFormDialog formDialogApiRef={formDialogApiRef} />
                <AvisosBanner/>
                {children}
            </CustomLocalizationProvider>
        </MuiBaseApp>
        </Box>
    );
};

export default BaseApp;
