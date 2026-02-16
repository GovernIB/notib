import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import { FormPage, MuiForm, FormField } from 'reactlib';

const NotificacioFormEnviament: React.FC = () => {
    return (
        <>
            <Typography variant="h5" sx={{ mt: 3, mb: 1 }}>
                Enviaments
            </Typography>
            <Paper sx={{ p: 2 }}>
                <Grid container spacing={2}>
                    <FormField name="concepte" />
                </Grid>
            </Paper>
        </>
    );
};

const NotificacioFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <FormField name="concepte" />
            </Grid>
            <Grid size={12}>
                <FormField name="descripcio" type="textarea" />
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
                <FormField name="numExpedient" />
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
                <NotificacioFormEnviament />
            </MuiForm>
        </FormPage>
    );
};

export default NotificacioForm;
