import { useTranslation } from 'react-i18next';
import { FormField, GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const columns: MuiDataGridColDef[] = [
    {
        field: 'id',
    },
    {
        field: 'entregaPostalActiva',
    },
    {
        field: 'perEmail',
    },
];

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
