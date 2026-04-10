import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    MuiDataGrid,
    useFormContext,
    springFilterBuilder as filterBuilder,
    useFilterApiRef,
    MuiFilter,
    FilterApi,
    useMuiDataGridApiRef,
    MuiDataGridApiRef,
    MuiDataGridColDef,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Box, Icon, IconButton } from '@mui/material';
import GridToolbarButton from '../../components/GridToolbarButton';

const columns: MuiDataGridColDef[] = [
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
        type: 'boolean',
    },
];

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

const ContentFilter: React.FC<{
    filterApiRef: React.RefObject<FilterApi>;
    gridApiRef: MuiDataGridApiRef;
}> = (props) => {
    const { filterApiRef, gridApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };

    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="usuariCodi" />
            <GridFormField size={7.5} name="callbackUrl" />
            <GridFormField size={1} name="activa" />
            <Grid size={1.5}>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                    <GridToolbarButton gridApiRef={gridApiRef} />
                </Box>
            </Grid>
        </Grid>
    );
};

const EntitatFormTabAplicacionsFilter: React.FC<{ gridApiRef: MuiDataGridApiRef }> = (props) => {
    const { gridApiRef } = props;
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('usuariCodi', data.usuariCodi),
            filterBuilder.like('callbackUrl', data.callbackUrl),
            filterBuilder.eq('activa', `'${data?.activa}'`)
        );
    };

    return (
        <MuiFilter
            resourceName="aplicacioResource"
            code="FILTER_APLICACIO"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} gridApiRef={gridApiRef} />
        </MuiFilter>
    );
};

const EntitatFormTabAplicacions: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const gridApiRef = useMuiDataGridApiRef();
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            apiRef={gridApiRef}
            title=""
            resourceName="aplicacioResource"
            fixedFilter={'entitat.id:' + id}
            formAdditionalData={{ entitat: { id } }}
            columns={columns}
            paginationActive
            toolbarHideQuickFilter
            toolbarHide
            toolbarAdditionalRow={<EntitatFormTabAplicacionsFilter gridApiRef={gridApiRef} />}
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.aplicacio')}
            popupEditFormContent={<EntitatFormTabAplicacionsFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default EntitatFormTabAplicacions;
