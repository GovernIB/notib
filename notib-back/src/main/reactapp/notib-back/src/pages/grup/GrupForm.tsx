import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    FormPage,
    MuiForm,
    FormField,
} from 'reactlib';

const GrupFormContent: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={4}><FormField name="codi" /></Grid>
        <Grid size={4}><FormField name="nom" /></Grid>
    </Grid>;
}

export const GrupForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return <FormPage>
        <MuiForm
            resourceName="grupResource"
            id={id != null ? parseInt(id) : id}
            title={id != null ? t('page.grups.form.titleUpdate') : t('page.grups.form.titleCreate')}
            //createLink="./{{id}}"
            createLink="../"
            updateLink="../../"
            componentProps={{ style: { height: '100%' } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <GrupFormContent />
        </MuiForm>
    </FormPage>;
}
export default GrupForm;
