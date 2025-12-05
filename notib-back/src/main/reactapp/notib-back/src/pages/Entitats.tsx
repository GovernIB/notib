import { useTranslation } from 'react-i18next';
import { FormField, GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const columns: MuiDataGridColDef[] = [{
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

const Entitats = () => {
    const { t } = useTranslation();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('menu.entitats')}
                resourceName="entitatResource"
                columns={columns}
                toolbarType="upper"
                popupEditCreateActive
                popupEditFormContent={<>
                    <FormField name="entregaPostalActiva" />
                    <FormField name="campProva" />
                </>}
            />
        </GridPage>
    );
};

export default Entitats;
