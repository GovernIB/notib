import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    springFilterBuilder as filterBuilder, useMuiDataGridApiRef,
} from 'reactlib';
import GridFormField from '../../components/GridFormField.tsx';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer.tsx';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils.ts';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext.ts';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { FilterClearButton } from '../../components/FilterButtons';
import { icons } from '../../utils/icons';
import AddButton from "../../components/AddButton.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

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

const PagadorPostalFormContent: React.FC = () => {

    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="nom" />
            <GridFormField size={12} name="organGestor" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={4} name="contracteNum" />
            <GridFormField size={4} name="facturacioClientCodi" />
            <GridFormField size={4} name="contracteDataVig" />
        </Grid>
    );
};

export const PagadorsPostalsGrid: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const gridApiRef = useMuiDataGridApiRef()
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
                <ScreenFrame icon={icons.pagadorPostal} title={t('page.pagadorPostal.grid.title')}>
                    <StyledMuiGrid
                        resourceName="pagadorPostalResource"
                        columns={columns}
                        apiRef={gridApiRef}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        toolbarType="upper"
                        toolbarHideCreate
                        toolbarElementsWithPositions={ isRoleAdminLectura ? [] : [
                            { position: 2, element: <AddButton gridApiRef={gridApiRef} text={t('page.pagadorPostal.grid.nou')} tooltip={t('page.pagadorPostal.grid.nouTooltip')} /> },
                        ]}
                        popupEditActive
                        popupEditFormContent={<PagadorPostalFormContent/>}
                        popupEditFormDialogResourceTitle={t('page.pagadorPostal.grid.popupResourceTitle')}
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default PagadorsPostalsGrid;
