import React from 'react';
import {
    useNavigate,
    useLocation,
    useBlocker,
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
} from 'react-router-dom';
import i18n from '../i18n/i18n';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { useColorScheme } from '@mui/material/styles';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiBaseApp,
    type MenuEntry,
    MuiFormDialog,
    MuiDataFormDialogApi,
    useAuthContext,
    useBaseAppContext,
    useResourceApiContext,
    useMuiDataFormDialogApiRef,
    FormField,
} from 'reactlib';
import { useNotibContext, ROLE_SUPER } from './NotibContext';
import HeaderThemeModeSelector from './HeaderThemeModeSelector';
import HeaderLanguageSelector from './HeaderLanguageSelector';
import Offline from './Offline';
import RoleSelector from './RoleSelector';
import EntitatSelector from './EntitatSelector';
import { Divider } from '@mui/material';

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
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
    appbarStyle?: any;
    footer?: React.ReactElement;
    footerHeight?: number;
};

export const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const filterMenuEntries = (
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

const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { mode, setMode } = useColorScheme();
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    const { currentUser } = useNotibContext();
    const handleSaveSuccess = (data: any) => {
        const profileLanguage = data?.idioma.toLowerCase();
        if (profileLanguage != null && currentLanguage !== profileLanguage) {
            setCurrentLanguage(profileLanguage);
        }
        const profileMode = data?.tema?.toLowerCase() ?? 'system';
        if (mode !== profileMode) {
            setMode(profileMode);
        }
    };
    React.useEffect(() => {
        handleSaveSuccess(currentUser);
    }, [currentUser]);
    return (
        <MuiFormDialog
            resourceName="usuariResource"
            title="Perfil de l'usuari"
            apiRef={formDialogApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
            formComponentProps={{
                commonFieldComponentProps: { size: 'small' },
                onSaveSuccess: handleSaveSuccess,
            }}
        >
            <Grid container spacing={2}>
                <Grid size={3}>
                    <FormField name="codi" disabled />
                </Grid>
                <Grid size={9}>
                    <FormField name="nomSencer" disabled />
                </Grid>
                <Grid size={6}>
                    <FormField name="email" disabled />
                </Grid>
                <Grid size={6}>
                    <FormField name="emailAlt" />
                </Grid>
                <Grid size={6}>
                    <FormField name="rebreEmailsNotificacio" />
                </Grid>
                <Grid size={6}>
                    <FormField name="rebreEmailsNotificacioCreats" />
                </Grid>
                <Grid size={6}>
                    <FormField name="idioma" />
                </Grid>
                <Grid size={6}>
                    <FormField name="tema" />
                </Grid>
                <Grid size={12}>
                    <FormField name="entitatDefecte" />
                </Grid>
                <Grid size={12}>
                    <FormField name="organDefecte" />
                </Grid>
                <Grid size={12}>
                    <FormField name="procedimentDefecte" />
                </Grid>
            </Grid>
        </MuiFormDialog>
    );
};

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
        footer,
        footerHeight,
        children,
    } = props;
    const navigate = useNavigate();
    const location = useLocation();
    const { currentRole } = useNotibContext();
    const { isReady: authIsReady, getUserId: authGetUserId } = useAuthContext();
    const baseAppMenuEntries = useBaseAppMenuEntries(menuEntries);
    const formDialogApiRef = useMuiDataFormDialogApiRef();
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    };
    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
    };
    const anyHistoryEntryExist = () => location.key !== 'default';
    const showUserProfileDialog = () => {
        formDialogApiRef.current.show(authGetUserId()).catch(() => null);
    };
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
    return (
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
                <RoleSelector key="role_selector" />,
                ...(currentRole !== ROLE_SUPER ? [<EntitatSelector key="entitat_selector" />] : []),
            ]}
            headerAdditionalAuthComponents={[
                <Box
                    key="sel_lang"
                    sx={{ display: 'flex', justifyContent: 'center', mt: 2, mb: 2 }}
                >
                    <HeaderLanguageSelector languages={availableLanguages} />
                </Box>,
                <Box key="sel_theme_mode" sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                    <HeaderThemeModeSelector />
                </Box>,
                authIsReady ? (
                    <Box
                        key="user_profile"
                        sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}
                    >
                        <MenuItem onClick={() => showUserProfileDialog()} sx={{ width: '100%' }}>
                            <ListItemIcon>
                                <Icon fontSize="small">account_circle</Icon>
                            </ListItemIcon>
                            <ListItemText>Perfil de l'usuari</ListItemText>
                        </MenuItem>
                    </Box>
                ) : (
                    <></>
                ),
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
            menuEntries={baseAppMenuEntries}
        >
            <CustomLocalizationProvider>
                <UserProfileFormDialog formDialogApiRef={formDialogApiRef} />
                {children}
            </CustomLocalizationProvider>
        </MuiBaseApp>
    );
};

export default BaseApp;
