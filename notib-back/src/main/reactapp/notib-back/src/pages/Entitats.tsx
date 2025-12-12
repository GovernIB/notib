import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import {
    GridPage,
    FormPage,
    MuiDataGrid,
    MuiForm,
    FormField,
    MuiFormTabs,
    MuiFormTabContent,
 } from 'reactlib';

const EntitatFormContent: React.FC = () => {
    const { t } = useTranslation();
    const aplicacionsTabLabel = <Badge badgeContent={0} color="primary">
        {t('page.entitats.form.tabs.aplicacions')}
    </Badge>;
    const permisosTabLabel = <Badge badgeContent={0} color="primary">
        {t('page.entitats.form.tabs.permisos')}
    </Badge>;
    const tabs = [
        t('page.entitats.form.tabs.dades'),
        t('page.entitats.form.tabs.personalitzar'),
        { label: aplicacionsTabLabel },
        { label: permisosTabLabel },
    ];
    return <MuiFormTabs
        tabs={tabs}
        tabIndexesWithGrids={[1]}>
        <MuiFormTabContent index={0} showOnCreate>
            <Grid container spacing={2}>
                <Grid size={12}><FormField name="codi" /></Grid>
                <Grid size={12}><FormField name="nom" /></Grid>
                <Grid size={12}><FormField name="dir3Codi" /></Grid>
                <Grid size={12}><FormField name="activa" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={1}>
            <Grid container spacing={2}>
                <Grid size={12}><FormField name="colorLletra" type="color" /></Grid>
                <Grid size={12}><FormField name="colorFons" type="color" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={2}>
        </MuiFormTabContent>
        <MuiFormTabContent index={3}>
        </MuiFormTabContent>
    </MuiFormTabs>;
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
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}" />
        </GridPage>
    );
};

export default Entitats;
