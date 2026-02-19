import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    MuiDataGrid,
    FormField,
    useFormContext,
    springFilterBuilder as filterBuilder,
    useFilterApiRef,
    MuiFilter,
    FilterApi,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Icon, IconButton } from '@mui/material';

const columns = [
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
];

const EntitatFormTabAplicacionsFormContent: React.FC = () => {
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

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="usuariCodi" />
            <GridFormField size={7.5} name="callbackUrl" />
            <GridFormField size={1.5} name="activa" />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

const EntitatFormTabAplicacionsFilter: React.FC = () => {
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
            componentProps={{ sx: { mb: 2 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

const EntitatFormTabAplicacions: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
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
            toolbarAdditionalRow={<EntitatFormTabAplicacionsFilter />}
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.aplicacio')}
            popupEditFormContent={<EntitatFormTabAplicacionsFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default EntitatFormTabAplicacions;
