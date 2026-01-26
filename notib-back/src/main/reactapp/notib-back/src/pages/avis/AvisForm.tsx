import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const AvisFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="entitat" />
            <GridFormField size={12} name="assumpte" />
            <GridFormField size={12} name="missatge" type="textarea" />
            <GridFormField size={6} name="dataInici" type="date" />
            <GridFormField size={6} name="dataFinal" type="date" />
            <GridFormField size={6} name="avisNivell" />
            <GridFormField size={6} name="actiu" />
        </Grid>
    );
};

export const AvisForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();

    return (
        <FormPage>
            <MuiForm
                resourceName="avisResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.avisos.form.titleUpdate')
                        : t('page.avisos.form.titleCreate')
                }
                //createLink="./{{id}}"
                createLink="../"
                updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <AvisFormContent />
            </MuiForm>
        </FormPage>
    );
};
export default AvisForm;
