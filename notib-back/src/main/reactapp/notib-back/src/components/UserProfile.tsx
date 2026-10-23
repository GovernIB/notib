import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import ToggleButton from '@mui/material/ToggleButton';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { MuiFormDialog, MuiDataFormDialogApi, useBaseAppContext, useAuthContext, useAuthButtonContext, useFormContext } from 'reactlib';
import { useNotibContext } from './NotibContext';
import { CardData } from './CardData';
import GridFormField from './GridFormField';
import {desarTemaCache, useSetTemaAplicacio} from "./TemaProvider.tsx";
import {MenuEstil, TemaAplicacio} from "../theme.ts";
import { normalitzaIdioma } from '../utils/idioma';

export const UserProfileMenu: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { getUserId: authGetUserId } = useAuthContext();
    const setPreview = useSetUserPreferencesPreview();
    const { apiRef: authButtonApiRef } = useAuthButtonContext();
    const showUserProfileDialog = () => {
        authButtonApiRef.current?.close();
        // Qualsevol tancament sense desar (cancel·lar, 'x', Escape) rebutja la promesa:
        // restaurem la previsualització del tema a la configuració desada.
        formDialogApiRef.current?.show(authGetUserId()).catch(() => setPreview({}));
    };

    return (
        <MenuItem onClick={() => showUserProfileDialog()} sx={{ width: '100%' }}>
            <ListItemIcon>
                <Icon fontSize="small">account_circle</Icon>
            </ListItemIcon>
            <ListItemText>{t('component.UserProfile.perfil')}</ListItemText>
        </MenuItem>
    );
};

const ToggleFieldLabel: React.FC<{ label?: string }> = ({ label }) => (
    <Typography
        component="label"
        sx={{
            display: 'block',
            mb: 0.75,
            fontSize: '0.75rem',
            lineHeight: 1,
            color: 'text.secondary',
        }}
    >
        {label}
    </Typography>
);

// ============================================================================
// Preferències de l'usuari, aplicades a tota l'aplicació.
//
// La font de veritat és el perfil desat a la base de dades: DistribucioProvider carrega el
// recurs de l'usuari en arrencar (useCurrentUser) i no deixa pintar res fins a tenir-lo, de
// manera que aquí ja hi són disponibles al primer render. Damunt d'aquests valors s'hi pot
// posar una previsualització transitòria mentre el diàleg de perfil és obert (el tema i
// l'estil de menú s'apliquen a l'instant per poder-los veure); en tancar el diàleg la
// previsualització desapareix i tornen a manar els valors desats.
//
// Per afegir una preferència nova n'hi ha prou amb declarar-la a UserPreferences i llegir-la
// a preferenciesDesades: qui la necessiti la té amb useUserPreferences().
export type UserPreferences = {
    /** Codi de dues lletres en minúscules ("ca"/"es"), normalitzat des del perfil. */
    idioma?: string;
    temaAplicacio?: TemaAplicacio;
    estilMenu?: MenuEstil;
    /** Mida de pàgina per defecte de tots els llistats (veure StyledMuiGrid). */
    numElementsPagina?: number;
};

const preferenciesDesades = (usuari: any): UserPreferences => ({
    idioma: normalitzaIdioma(usuari?.idioma),
    temaAplicacio: usuari?.tema,
    estilMenu: usuari?.estilMenu,
    numElementsPagina: usuari?.numElementsPagina,
});

const UserPreferencesContext = React.createContext<
    { preferences: UserPreferences; setPreview: (preview: UserPreferences) => void } | undefined
>(undefined);

export const UserPreferencesProvider: React.FC<React.PropsWithChildren> = ({ children }) => {

    const { currentUser } = useNotibContext();
    const setTemaAplicacio = useSetTemaAplicacio();
    const [preview, setPreview] = React.useState<UserPreferences>({});
    const desades = React.useMemo(() => preferenciesDesades(currentUser), [currentUser]);
    // En desar el perfil, DistribucioProvider actualitza currentUser: la previsualització ha de
    // desaparèixer perquè no tapi els valors que acaben d'arribar del servidor.
    React.useEffect(() => setPreview({}), [desades]);
    // Només es recorda el tema desat, no la previsualització: així la propera arrencada ja pinta
    // amb el tema correcte (veure TemaProvider) sense ressuscitar cap canvi descartat.
    React.useEffect(
        () => desarTemaCache(currentUser?.id, desades.temaAplicacio),
        [currentUser?.id, desades.temaAplicacio]
    );
    const preferences = React.useMemo(() => {
        // Només tapen les claus previsualitzades amb valor; una clau a undefined ha de deixar
        // veure la preferència desada, no esborrar-la.
        const efectives: UserPreferences = { ...desades };
        (Object.keys(preview) as (keyof UserPreferences)[]).forEach((clau) => {
            if (preview[clau] !== undefined) {
                (efectives as any)[clau] = preview[clau];
            }
        });
        return efectives;
    }, [desades, preview]);
    // El tema efectiu (inclosa la previsualització del diàleg de perfil, que s'ha de veure a
    // l'instant) puja al TemaProvider, que és qui munta el ThemeProvider de tota l'aplicació.
    // Sense perfil no s'hi puja res: amb l'API caiguda DistribucioProvider pinta igualment els
    // fills (mode offline) i s'esborraria el tema que TemaProvider ha recuperat de la memòria cau.
    React.useEffect(() => {
        if (currentUser != null) {
            setTemaAplicacio(preferences.temaAplicacio);
        }
    }, [currentUser, preferences.temaAplicacio, setTemaAplicacio]);
    return (
        <UserPreferencesContext.Provider value={{ preferences, setPreview }}>
            {children}
        </UserPreferencesContext.Provider>
    );
};

export const useUserPreferences = (): UserPreferences => {
    const context = React.useContext(UserPreferencesContext);
    if (context === undefined) {
        throw new Error('useUserPreferences must be used within a UserPreferencesProvider');
    }
    return context.preferences;
};

const useSetUserPreferencesPreview = () => {
    const context = React.useContext(UserPreferencesContext);
    if (context === undefined) {
        throw new Error('useSetUserPreferencesPreview must be used within a UserPreferencesProvider');
    }
    return context.setPreview;
};

/**
 * Previsualitza el tema i l'estil de menú mentre s'edita el perfil, perquè es vegin a l'instant
 * sense haver de desar. Són canvis purament de client i no toquen cap consulta.
 *
 * L'idioma NO es previsualitza a posta: canviar-lo obliga a tornar a demanar les etiquetes i els
 * enumerats al servidor (base-react refà l'índex de l'API quan canvia Accept-Language), i fer-ho
 * a cada tecla del formulari recarregava la pantalla de darrere. S'aplica en desar, quan
 * DistribucioProvider actualitza currentUser (veure UserProfileMenu).
 */
const PreferencesSync: React.FC = () => {
    const { data } = useFormContext();
    const setPreview = useSetUserPreferencesPreview();
    React.useEffect(() => {
        setPreview({ temaAplicacio: data?.tema, estilMenu: data?.estilMenu });
    }, [data?.tema, data?.estilMenu, setPreview]);
    // En tancar el diàleg (desant o cancel·lant) es retira la previsualització i tornen a manar
    // els valors del perfil desat.
    React.useEffect(() => () => setPreview({}), [setPreview]);
    return null;
};


// Selector del tema de l'aplicació (clar / obscur / dràcula / sistema), amb el mateix
// aspecte de grup de botons que la resta de configuracions d'aparença.
const TemaSelector: React.FC = () => {
    const { data, apiRef, fields } = useFormContext();
    const field = fields?.find?.((f: any) => f.name === 'tema');
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: string | null) => {
        if (newValue != null) {
            apiRef?.current?.setFieldValue('tema', newValue);
        }
    };
    return (
        <Grid size={12}>
            <ToggleFieldLabel label={field?.label} />
            <ToggleButtonGroup
                value={data?.tema ?? 'SISTEMA'}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{ display: 'flex', width: '100%' }}
            >
                <ToggleButton value="LIGHT" sx={{ flex: 1, gap: 1 }}>
                    <Icon>light_mode</Icon> {field?.options?.LIGHT}
                </ToggleButton>
                <ToggleButton value="DARK" sx={{ flex: 1, gap: 1 }}>
                    <Icon>dark_mode</Icon> {field?.options?.DARK}
                </ToggleButton>
                <ToggleButton value="DRACULA" sx={{ flex: 1, gap: 1 }}>
                    <Icon>auto_awesome</Icon> {field?.options?.DRACULA}
                </ToggleButton>
                <ToggleButton value="SISTEMA" sx={{ flex: 1, gap: 1 }}>
                    <Icon>settings_brightness</Icon> {field?.options?.SISTEMA}
                </ToggleButton>
            </ToggleButtonGroup>
        </Grid>
    );
};

// Selector de l'estil del menú lateral (tema / tema invertit / peu), amb el mateix
// aspecte de grup de botons que el selector de tema.
const EstilMenuSelector: React.FC = () => {
    const { data, apiRef, fields } = useFormContext();
    const field = fields?.find?.((f: any) => f.name === 'estilMenu');
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: string | null) => {
        if (newValue != null) {
            apiRef?.current?.setFieldValue('estilMenu', newValue);
        }
    };
    return (
        <Grid size={12}>
            <ToggleFieldLabel label={field?.label} />
            <ToggleButtonGroup
                value={data?.estilMenu ?? 'TEMA'}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{ display: 'flex', width: '100%' }}
            >
                <ToggleButton value="TEMA" sx={{ flex: 1, gap: 1 }}>
                    <Icon>palette</Icon> {field?.options?.TEMA}
                </ToggleButton>
                <ToggleButton value="TEMA_INVERTIT" sx={{ flex: 1, gap: 1 }}>
                    <Icon>invert_colors</Icon> {field?.options?.TEMA_INVERTIT}
                </ToggleButton>
                <ToggleButton value="PEU" sx={{ flex: 1, gap: 1 }}>
                    <Icon>vertical_align_bottom</Icon> {field?.options?.PEU}
                </ToggleButton>
            </ToggleButtonGroup>
        </Grid>
    );
};

// Mostra, a mode informatiu (no editable, com el desplegable deshabilitat de l'antiga interfície
// JSP), tots els rols que té concedits l'usuari actual -no només el seleccionat actualment a la
// capçalera, que ja es veu al selector de rol. Inclou tant els rols NOT_XXX de l'aplicació
// (rolesAvailable, ja calculats per al selector de rol) com els rols/grups que l'usuari té assignats
// i que s'utilitzen en algun permís (ACL) -rolsAmbPermis, calculat pel backend
// (UsuariResourceServiceImpl.afterConversion). Es mostra el codi del rol tal qual, sense traduir: a
// diferència del selector de rol (on l'usuari ha d'entendre l'opció que està triant), aquí l'objectiu
// és que es reconegui exactament el codi configurat (p.ex. per comparar-lo amb un permís concedit).
const UserRolsField: React.FC = () => {
    const { t } = useTranslation();
    const { rolesAvailable } = useNotibContext();
    const { data } = useFormContext();
    const rolsAmbPermis: string[] = data?.rolsAmbPermis ?? [];
    const rols = Array.from(new Set([...(rolesAvailable ?? []), ...rolsAmbPermis]));
    return (
        <Grid size={12}>
            <ToggleFieldLabel label={t('component.UserProfile.rols')} />
            <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: 'wrap' }}>
                {rols.map((rol) => (
                    <Chip key={rol} label={rol} size="small" />
                ))}
            </Stack>
        </Grid>
    );
};

const UserProfileForm: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2}>
            <CardData title={t('component.UserProfile.dades')} icon="person" variant="h6">
                <GridFormField size={4} name="codi" disabled />
                <GridFormField size={8} name="nomSencer" disabled />
                <GridFormField size={6} name="email" disabled />
                <GridFormField size={6} name="emailAlt" />
                <UserRolsField />
            </CardData>

            <CardData title={t('component.UserProfile.correu')} icon="email" variant="h6">
                <GridFormField size={6} name="rebreEmailsNotificacio" />
                <GridFormField size={6} name="rebreEmailsNotificacioCreats" />
            </CardData>

            <CardData title={t('component.UserProfile.general')} icon="settings" variant="h6">
                <GridFormField size={4} name="idioma" />
                <GridFormField
                    size={4}
                    name="numElementsPaginaDefecte"
                    emptyValueDescription={t('component.UserProfile.auto')}
                />
                <GridFormField size={12} name="entitatDefecte" />
                <GridFormField size={12} name="organDefecte" />
                <GridFormField size={12} name="procedimentDefecte" />
            </CardData>

            <CardData title={t('component.UserProfile.tema')} icon="palette" variant="h6">
                <TemaSelector />
                <EstilMenuSelector />
            </CardData>
        </Grid>
    );
};

export const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    const { currentUser, setCurrentUser } = useNotibContext();

    // El tema i l'estil del menú s'apliquen a l'instant, mentre s'editen (previsualització,
    // vegeu ThemeUserProvider); en desar, currentUser s'actualitza i la previsualització es
    // descarta perquè ja coincideix amb la configuració persistida.
    const handleSaveSuccess = (data: any, saveUser?: boolean) => {
        const profileLanguage = data?.idioma?.toLowerCase();
        if (profileLanguage != null && currentLanguage !== profileLanguage) {
            setCurrentLanguage(profileLanguage);
        }
        if (saveUser) {
            setCurrentUser(data);
        }
    };

    React.useEffect(() => {
        handleSaveSuccess(currentUser);
    }, [currentUser]);

    return (
        <MuiFormDialog
            resourceName="usuariResource"
            title={t('component.UserProfile.perfil')}
            apiRef={formDialogApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
            formComponentProps={{
                commonFieldComponentProps: { size: 'small' },
                onSaveSuccess: (data: any) => handleSaveSuccess(data, true),
            }}
        >
            <PreferencesSync />
            <UserProfileForm />
        </MuiFormDialog>
    );
};
