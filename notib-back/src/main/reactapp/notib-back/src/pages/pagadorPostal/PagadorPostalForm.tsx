import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm, FormField } from 'reactlib';

const PagadorPostalFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={6}>
                <FormField name="nom" />
            </Grid>
            <Grid size={6} />
            <Grid size={6}>
                <FormField name="organGestor" />
            </Grid>
            <Grid size={6}>
                <FormField name="contracteNum" />
            </Grid>
            <Grid size={6}>
                <FormField name="facturacioClientCodi" />
            </Grid>
            <Grid size={6}>
                <FormField name="contracteDataVig" />
            </Grid>
        </Grid>
    );
};

export const PagadorPostalForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorPostalResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.pagadorPostal.form.titleUpdate')
                        : t('page.pagadorPostal.form.titleCreate')
                }
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <PagadorPostalFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default PagadorPostalForm;
