import { Grid, Icon, IconButton } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    {
        field: 'entitat',
        flex: 2,
    },
    {
        field: 'assumpte',
        flex: 4,
    },
    {
        field: 'dataInici',
        fieldType: 'date',
        flex: 1,
    },
    {
        field: 'dataFinal',
        fieldType: 'date',
        flex: 1,
    },
    {
        field: 'avisNivell',
        flex: 0.6,
    },
    {
        field: 'actiu',
        flex: 0.6,
        type: 'boolean',
    },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.eq('entitat.id', data?.entitat?.id),
        filterBuilder.like('assumpte', data.assumpte),
        data?.dataInici && filterBuilder.gte('dataInici', `'${formatStartOfDay(data?.dataInici)}'`),
        data?.dataFinal && filterBuilder.lte('dataFinal', `'${formatEndOfDay(data?.dataFinal)}'`),
        filterBuilder.eq('avisNivell', `'${data?.avisNivell}'`),
        filterBuilder.eq('actiu', `'${data?.actiu}'`)
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="entitat" />
            <GridFormField size={4} name="assumpte" />
            <GridFormField size={1.5} name="dataInici" />
            <GridFormField size={1.5} name="dataFinal" />
            <GridFormField size={1.5} name="avisNivell" />
            <GridFormField size={1} name="actiu" />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

export const AvisGrid = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'avisResource',
        'FILTER_AVIS',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.avisos.grid.title')}
                resourceName="avisResource"
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarBulkDelete
                toolbarCreateLink="form"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default AvisGrid;
