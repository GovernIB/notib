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
        field: 'organGestor',
        flex: 6,
    },
    {
        field: 'contracteNum',
        flex: 2,
    },
    {
        field: 'contracteDataVig',
        flex: 3,
    },
    {
        field: 'facturacioClientCodi',
        flex: 2,
    },
];

const PagadorPostalForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={6}>
                <FormField name="nom" />
            </Grid>
            <Grid size={6} />
            <Grid size={6}>
                <FormField name="organGestor" />
            </Grid>
            <Grid size={6}>
                <FormField name="contracteNum" />
            </Grid>
            <Grid size={6}>
                <FormField name="facturacioClientCodi" />
            </Grid>
            <Grid size={6}>
                <FormField name="contracteDataVig" />
            </Grid>
        </Grid>
    );
};

export const PagadorsPostals: React.FC = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.pagadorPostal.grid.title')}
                resourceName="pagadorPostalResource"
                columns={columns}
                paginationActive
                popupEditActive
                popupEditFormContent={<PagadorPostalForm />}
                popupEditFormDialogResourceTitle={t('page.pagadorPostal.grid.popupResourceTitle')}
            />
        </GridPage>
    );
};

export default PagadorsPostals;
