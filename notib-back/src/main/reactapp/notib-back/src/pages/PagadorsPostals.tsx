import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import GridFormField from '../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../hooks/useDataGrid';

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

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('nom', data.nom),
        filterBuilder.eq('organGestor.id', data?.organGestor?.id),
        filterBuilder.like('contracteNum', data.contracteNum),
        data?.contracteDataVigInici &&
            filterBuilder.gte(
                'contracteDataVig',
                `'${formatStartOfDay(data?.contracteDataVigInici)}'`
            ),
        data?.contracteDataVigFinal &&
            filterBuilder.lte(
                'contracteDataVig',
                `'${formatEndOfDay(data?.contracteDataVigFinal)}'`
            ),
        filterBuilder.like('facturacioClientCodi', data.facturacioClientCodi)
    );
};

const PagadorPostalForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={6} name="nom" />
            <Grid size={6} />
            <GridFormField size={6} name="organGestor" />
            <GridFormField size={6} name="contracteNum" />
            <GridFormField size={6} name="facturacioClientCodi" />
            <GridFormField size={6} name="contracteDataVig" />
        </Grid>
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
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestor" />
            <GridFormField size={1.5} name="contracteNum" />
            <GridFormField size={1.75} name="contracteDataVigInici" />
            <GridFormField size={1.75} name="contracteDataVigFinal" />
            <GridFormField size={1.5} name="facturacioClientCodi" />
            <Grid size={0.5}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const PagadorsPostals: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'pagadorPostalResource',
        'FILTER_PAGADOR_POSTAL',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.pagadorPostal.grid.title')}
                resourceName="pagadorPostalResource"
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                popupEditActive
                popupEditFormContent={<PagadorPostalForm />}
                popupEditFormDialogResourceTitle={t('page.pagadorPostal.grid.popupResourceTitle')}
            />
        </GridPage>
    );
};

export default PagadorsPostals;
