import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, GridPage, MuiDataGrid } from 'reactlib';

const EntitatFormContent: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={12}><FormField name="entregaPostalActiva" /></Grid>
        <Grid size={12}><FormField name="campProva" /></Grid>
    </Grid>;
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
                title={t('page.entitats.title')}
                resourceName="entitatResource"
                columns={columns}
                paginationActive
                popupEditCreateActive
                popupEditFormDialogResourceTitle={t('page.entitats.resourceTitle')}
                popupEditFormContent={<EntitatFormContent />}
            />
        </GridPage>
    );
};

export default Entitats;
