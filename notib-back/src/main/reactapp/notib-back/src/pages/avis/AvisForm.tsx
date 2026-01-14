import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    FormPage,
    MuiForm,
    FormField,
} from 'reactlib';

const AvisFormContent: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={12}><FormField name="entitat" /></Grid>
        <Grid size={12}><FormField name="assumpte" /></Grid>
        <Grid size={12}><FormField name="missatge" type="textarea" /></Grid>
        <Grid size={6}><FormField name="dataInici" type="date" /></Grid>
        <Grid size={6}><FormField name="dataFinal" type="date" /></Grid>
        <Grid size={6}><FormField name="avisNivell" /></Grid>
        <Grid size={6}><FormField name="actiu" /></Grid>
    </Grid>;
}

export const AvisForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return <FormPage>
        <MuiForm
            resourceName="avisResource"
            id={id != null ? parseInt(id) : id}
            title={id != null ? t('page.avisos.form.titleUpdate') : t('page.avisos.form.titleCreate')}
            //createLink="./{{id}}"
            createLink="../"
            updateLink="../../"
            componentProps={{ style: { height: '100%' } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <AvisFormContent />
        </MuiForm>
    </FormPage>;
}
export default AvisForm;
