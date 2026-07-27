import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { GridPage, MuiDataGrid } from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import GridFormField from '../../components/GridFormField.tsx';
import {ROLE_ADMIN_LECTURA, useNotibContext} from "../../components/NotibContext.ts";

const columns = [
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'codi',
        flex: 4,
    },
    {
        field: 'organGestor',
        flex: 4,
    }
];

const GrupForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="codi" />
            <GridFormField size={12} name="nom" />
        </Grid>
    );
};

export const Grups: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.grups.grid.title')}
                resourceName="grupResource"
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                popupEditActive
                popupEditFormContent={<GrupForm />}
                popupEditFormDialogResourceTitle={t('page.grups.grid.popupResourceTitle')}
                rowHideUpdateButton={isRoleAdminLectura}
            />
        </GridPage>
    );
};

export default Grups;
