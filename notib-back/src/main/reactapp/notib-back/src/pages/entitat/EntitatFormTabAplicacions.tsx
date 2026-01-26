import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { MuiDataGrid, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const EntitatFormTabAplicacionsFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="usuariCodi" />
            <Grid size={8} />
            <GridFormField size={12} name="callbackUrl" />
            <GridFormField size={6} name="activa" />
            <GridFormField size={6} name="headerCsrf" />
            <GridFormField size={6} name="horariLaboralInici" />
            <GridFormField size={6} name="horariLaboralFi" />
            <GridFormField size={6} name="maxEnviamentsMinutLaboral" />
            <GridFormField size={6} name="maxEnviamentsMinutNoLaboral" />
            <GridFormField size={6} name="maxEnviamentsDiaLaboral" />
            <GridFormField size={6} name="maxEnviamentsDiaNoLaboral" />
        </Grid>
    );
};

const EntitatFormTabAplicacions: React.FC = () => {
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
            resourceName="aplicacioResource"
            staticFilter={'entitat.id:' + id}
            formAdditionalData={{ entitat: { id } }}
            columns={columns}
            paginationActive
            toolbarHideQuickFilter
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.aplicacio')}
            popupEditFormContent={<EntitatFormTabAplicacionsFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default EntitatFormTabAplicacions;
