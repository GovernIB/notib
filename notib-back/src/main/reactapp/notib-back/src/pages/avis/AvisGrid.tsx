import { Grid, Icon, IconButton } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridApiPro, useGridApiRef } from '@mui/x-data-grid-pro';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridApiRef,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useBaseAppContext,
    useMuiDataGridApiRef,
    useMuiDataGridContext,
} from 'reactlib';
import AccionsMassives, { MenuOption, useAccionsMassives } from '../../components/AccionsMassives';
import GridFormField from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import ScreenTitle from '../../components/ScreenTitle';
import { FilterClearButton } from '../../components/FilterButtons';
import { icons } from '../../utils/icons';

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
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="entitat" />
            <GridFormField size={4} name="assumpte" />
            <GridFormField size={1.5} name="dataInici" />
            <GridFormField size={1.5} name="dataFinal" />
            <GridFormField size={1.5} name="avisNivell" />
            <GridFormField size={1} name="actiu" />
            <FilterClearButton />
        </Grid>
    );
};

const AvisFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="entitat" />
            <GridFormField size={12} name="assumpte" />
            <GridFormField size={12} name="missatge" type="textarea" />
            <GridFormField size={6} name="dataInici" type="date" />
            <GridFormField size={6} name="dataFinal" type="date" />
            <GridFormField size={6} name="avisNivell" />
            <GridFormField size={6} name="actiu" />
        </Grid>
    );
};

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>, refresh: () => void }> = ({ apiRef, refresh }) => {

    const { selection } = useMuiDataGridContext();
    const { t } = useTranslation();
    const { activarAvisosMassiu, desactivarAvisosMassiu, esborrarAvisosMassiu } = useAccionsMassives('avisResource', refresh);

    const opcionsMenu: MenuOption[] = [
        {
            label: t('page.avisos.accionsMassives.activar.title'),
            tooltip: t('page.avisos.accionsMassives.activar.title'),
            icon: 'check_circle',
            onClick: () => activarAvisosMassiu(selection?.ids),
        },
        {
            label: t('page.avisos.accionsMassives.desactivar.title'),
            tooltip: t('page.avisos.accionsMassives.desactivar.title'),
            icon: 'block',
            onClick: () => desactivarAvisosMassiu(selection?.ids),
        },
        {
            label: t('page.avisos.accionsMassives.esborrar.title'),
            tooltip: t('page.avisos.accionsMassives.esborrar.title'),
            icon: 'delete',
            onClick: () => esborrarAvisosMassiu(selection?.ids),
        },
    ];
    return <AccionsMassives options={opcionsMenu} apiRef={apiRef} resource="avisResource" sizeSelection={selection?.ids?.size} />;
};

const AvisAddButton: React.FC<{ gridApiRef: MuiDataGridApiRef }> = ({ gridApiRef }) => {
    const { t } = useBaseAppContext();
    return (
        <IconButton title={t('datacommon.create.label')} onClick={() => gridApiRef.current?.triggerCreate()}>
            <Icon>add</Icon>
        </IconButton>
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
    const datagridApiRef = useGridApiRef();
    const gridApiRef = useMuiDataGridApiRef();
    const refresh = React.useCallback(() => gridApiRef.current?.refresh(), [gridApiRef]);
    return (
        <>
            <PageTitle title={t('page.avisos.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame>
                    <MuiDataGrid
                        title={<ScreenTitle icon={icons.avis} title={t('page.avisos.grid.title')} />}
                        resourceName="avisResource"
                        datagridApiRef={datagridApiRef}
                        apiRef={gridApiRef}
                        columns={columns}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        selectionActive
                        toolbarType="upper"
                        toolbarHideCreate
                        toolbarElementsWithPositions={[
                            { position: 2, element: <MassiveActionsButton apiRef={datagridApiRef} refresh={refresh} /> },
                            { position: 2, element: <AvisAddButton gridApiRef={gridApiRef} /> },
                        ]}
                        popupEditActive
                        popupEditFormContent={<AvisFormContent/>}
                        popupEditFormDialogResourceTitle={t('page.avisos.grid.popupResourceTitle')}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default AvisGrid;
