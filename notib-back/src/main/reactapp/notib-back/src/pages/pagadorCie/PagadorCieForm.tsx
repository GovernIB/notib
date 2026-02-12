import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm, FormField } from 'reactlib';

const PagadorCieFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={6}>
                <FormField name="nom" />
            </Grid>
            <Grid size={6}></Grid>
            <Grid size={6}>
                <FormField name="organGestorEmissor" />
            </Grid>
            <Grid size={6}>
                <FormField name="organGestorPagador" />
            </Grid>
            <Grid size={6}>
                <FormField name="apiKey" />
            </Grid>
            <Grid size={6}>
                <FormField name="contracteDataVig" />
            </Grid>
            <Grid size={6}>
                <FormField name="cieExtern" />
            </Grid>
        </Grid>
    );
};

export const PagadorCieForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorCieResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.pagadorCie.form.titleUpdate')
                        : t('page.pagadorCie.form.titleCreate')
                }
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <PagadorCieFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default PagadorCieForm;
