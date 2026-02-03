import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, MuiDataGrid, useFormContext } from 'reactlib';

const ProcedimentFormTabAplicacionsFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <Grid size={4}>
                <FormField name="usuariCodi" />
            </Grid>
            <Grid size={8} />
            <Grid size={12}>
                <FormField name="callbackUrl" />
            </Grid>
            <Grid size={6}>
                <FormField name="activa" />
            </Grid>
            <Grid size={6}>
                <FormField name="headerCsrf" />
            </Grid>
            <Grid size={6}>
                <FormField name="horariLaboralInici" />
            </Grid>
            <Grid size={6}>
                <FormField name="horariLaboralFi" />
            </Grid>
            <Grid size={6}>
                <FormField name="maxEnviamentsMinutLaboral" />
            </Grid>
            <Grid size={6}>
                <FormField name="maxEnviamentsMinutNoLaboral" />
            </Grid>
            <Grid size={6}>
                <FormField name="maxEnviamentsDiaLaboral" />
            </Grid>
            <Grid size={6}>
                <FormField name="maxEnviamentsDiaNoLaboral" />
            </Grid>
        </Grid>
    );
};

const ProcedimentFormTabGrups: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const columns = React.useMemo(
        () => [
            {
                field: 'usuariCodi',
                sortable: false,
                flex: 1,
            },
            {
                field: 'callbackUrl',
                sortable: false,
                flex: 4,
            },
            {
                field: 'activa',
                sortable: false,
                flex: 1,
            },
        ],
        []
    );
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            title=""
            resourceName="grupResource"
            staticFilter={'entitat.id:' + id}
            formAdditionalData={{ entitat: { id } }}
            columns={columns}
            paginationActive
            toolbarHideQuickFilter
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.aplicacio')}
            popupEditFormContent={<ProcedimentFormTabAplicacionsFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default ProcedimentFormTabGrups;
