import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { GridPage, MuiDataGrid, FormField } from 'reactlib';

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
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.grups.grid.title')}
                resourceName="grupResource"
                columns={columns}
                paginationActive
                popupEditActive
                popupEditFormContent={<GrupForm />}
                popupEditFormDialogResourceTitle={t('page.grups.grid.popupResourceTitle')}
            />
        </GridPage>
    );
};

export default Grups;
