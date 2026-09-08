import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import ToggleButton from '@mui/material/ToggleButton';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { MuiFormDialog, MuiDataFormDialogApi, useBaseAppContext, useAuthContext, useFormContext } from 'reactlib';
import { useNotibContext } from './NotibContext';
import { useThemeUserContext } from './ThemeUserProvider';
import { CardData } from './CardData';
import GridFormField from './GridFormField';

export const UserProfileMenu: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { getUserId: authGetUserId } = useAuthContext();
    const { removePreview } = useThemeUserContext();
    const showUserProfileDialog = () => {
        // Qualsevol tancament sense desar (cancel·lar, 'x', Escape) rebutja la promesa:
        // restaurem la previsualització del tema a la configuració desada.
        formDialogApiRef.current?.show(authGetUserId()).catch(() => removePreview());
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

const UserProfileForm: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2}>
            <CardData title={t('component.UserProfile.dades')} icon="person" variant="h6">
                <GridFormField size={4} name="codi" disabled />
                <GridFormField size={8} name="nomSencer" disabled />
                <GridFormField size={6} name="email" disabled />
                <GridFormField size={6} name="emailAlt" />
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
    const { setPreview } = useThemeUserContext();

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
                onDataChange: (data: any) => setPreview({ tema: data?.tema, estilMenu: data?.estilMenu }),
            }}
        >
            <UserProfileForm />
        </MuiFormDialog>
    );
};
