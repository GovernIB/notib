import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { FormPage, MuiForm, FormField } from 'reactlib';
import NotificacioFormEnviaments from './NotificacioFormEnviaments';

const NotificacioFormContent: React.FC = () => {
    const { t } = useTranslation();

    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <Typography variant="h6" sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                    {t('page.notificacio.form.tabs.remesa')}
                </Typography>
                <FormField name="concepte" debounce />
            </Grid>
            <Grid size={12}>
                <FormField name="descripcio" type="textarea" debounce />
            </Grid>
            <Grid size={6}>
                <FormField name="organGestor" />
            </Grid>
            <Grid size={6}>
                <FormField name="procediment" />
            </Grid>
            <Grid size={12}>
                <FormField name="enviamentDataProgramada" />
            </Grid>
            <Grid size={4}>
                <FormField name="caducitat" />
            </Grid>
            <Grid size={8}>
                <FormField name="caducitatOriginal" />
            </Grid>
            <Grid size={6}>
                <FormField name="numExpedient" debounce />
            </Grid>
            <Grid size={6}>
                <FormField name="idioma" />
            </Grid>
        </Grid>
    );
};

export const NotificacioForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return (
        <FormPage>
            <MuiForm
                resourceName="notificacioResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.notificacio.form.titleUpdate')
                        : t('page.notificacio.form.titleCreate')
                }
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <NotificacioFormContent />
                <NotificacioFormEnviaments />
            </MuiForm>
        </FormPage>
    );
};

export default NotificacioForm;
