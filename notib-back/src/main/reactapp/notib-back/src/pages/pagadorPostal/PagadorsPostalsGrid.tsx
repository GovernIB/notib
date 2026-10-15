import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    MuiDataGrid,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField.tsx';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer.tsx';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils.ts';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext.ts';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import ScreenTitle from '../../components/ScreenTitle';
import { FilterClearButton } from '../../components/FilterButtons';
import { icons } from '../../utils/icons';

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
        data?.contracteDataVigInici && filterBuilder.gte('contracteDataVig', `'${formatStartOfDay(data?.contracteDataVigInici)}'`),
        data?.contracteDataVigFinal && filterBuilder.lte('contracteDataVig', `'${formatEndOfDay(data?.contracteDataVigFinal)}'`),
        filterBuilder.like('facturacioClientCodi', data.facturacioClientCodi)
    );
};



const ContentFilter: React.FC = () => {
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();

    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestor" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={1.5} name="contracteNum" />
            <GridFormField size={1.75} name="contracteDataVigInici" />
            <GridFormField size={1.75} name="contracteDataVigFinal" />
            <GridFormField size={1.5} name="facturacioClientCodi" />
            <Grid size={0.5}>
                <FilterClearButton />
            </Grid>
        </Grid>
    );
};

export const PagadorsPostalsGrid: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'pagadorPostalResource',
        'FILTER_PAGADOR_POSTAL',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    return (
        <>
            <PageTitle title={t('page.pagadorPostal.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame>
                    <MuiDataGrid
                        title={<ScreenTitle icon={icons.pagadorPostal} title={t('page.pagadorPostal.grid.title')} />}
                        resourceName="pagadorPostalResource"
                        columns={columns}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        toolbarType="upper"
                        toolbarCreateLink="form"
                        toolbarHideCreate={isRoleAdminLectura ? true : undefined}
                        rowLink="form/{{id}}"
                        rowUpdateLink="form/{{id}}"
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default PagadorsPostalsGrid;
