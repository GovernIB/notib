import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    FormPage,
    MuiDataGrid,
    MuiForm,
    FormField,
 } from 'reactlib';

const EntitatFormContent: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={12}><FormField name="codi" /></Grid>
        <Grid size={12}><FormField name="nom" /></Grid>
        <Grid size={12}><FormField name="dir3Codi" /></Grid>
        <Grid size={12}><FormField name="activa" /></Grid>
        <Grid size={12}><FormField name="entregaPostalActiva" /></Grid>
        <Grid size={12}><FormField name="campProva" /></Grid>
    </Grid>;
}

export const EntitatForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return <FormPage>
        <MuiForm
            componentProps={{ style: { height: '100%' } }}
            id={id}
            title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
            resourceName="entitatResource">
            <EntitatFormContent />
        </MuiForm>
    </FormPage>;
}

const Entitats = () => {
    const { t } = useTranslation();
    const columns = [{
        field: 'codi',
        flex: 1,
    }, {
        field: 'nom',
        flex: 4,
    }, {
        field: 'dir3Codi',
        flex: 1,
    },  {
        field: 'activa',
        flex: .6,
    }];
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowUpdateLink="form/{{id}}" />
        </GridPage>
    );
};

export default Entitats;
