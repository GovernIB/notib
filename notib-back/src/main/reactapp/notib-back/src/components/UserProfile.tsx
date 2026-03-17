import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import { useColorScheme } from '@mui/material/styles';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiFormDialog,
    MuiDataFormDialogApi,
    useBaseAppContext,
    FormField,
    useAuthContext,
} from 'reactlib';
import { useNotibContext } from './NotibContext';

export const UserProfileMenu: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { getUserId: authGetUserId } = useAuthContext();
    const showUserProfileDialog = () => {
        formDialogApiRef.current.show(authGetUserId()).catch(() => null);
    };
    return (
        <MenuItem onClick={() => showUserProfileDialog()} sx={{ width: '100%' }}>
            <ListItemIcon>
                <Icon fontSize="small">account_circle</Icon>
            </ListItemIcon>
            <ListItemText>{t('component.UserProfileFormDialog.perfil')}</ListItemText>
        </MenuItem>
    );
};

export const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
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
            title={t('component.UserProfileFormDialog.perfil')}
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
