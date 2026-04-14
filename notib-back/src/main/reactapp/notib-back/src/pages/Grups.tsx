import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { GridPage, MuiDataGrid, FormField } from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../hooks/useDataGrid';

const columns = [
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'codi',
        flex: 4,
    },
];

const GrupForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <FormField name="codi" />
            </Grid>
            <Grid size={12}>
                <FormField name="nom" />
            </Grid>
        </Grid>
    );
};

export const Grups: React.FC = () => {
    const { t } = useTranslation();
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
            />
        </GridPage>
    );
};

export default Grups;
