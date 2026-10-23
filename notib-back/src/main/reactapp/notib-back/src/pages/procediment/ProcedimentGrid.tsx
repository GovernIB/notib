import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import {
    GridPage,
    springFilterBuilder as filterBuilder,
    MuiDataGridColDef,
    MuiDataGridApiRef, useBaseAppContext, MuiActionReportButton, useMuiDataGridApiRef,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import {ROLE_ADMIN, ROLE_ADMIN_LECTURA, useNotibContext} from '../../components/NotibContext';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { useAccionsProcediment } from '../accions/AccionsProcediment';
import { DataCommonAdditionalAction } from '../../../lib/components/mui/datacommon/MuiDataCommon.tsx';
import React from "react";
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { FilterClearButton } from '../../components/FilterButtons';
import { icons } from '../../utils/icons';
import {ProcedimentForm} from "./ProcedimentForm.tsx";
import AddButton from "../../components/AddButton.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 2,
    },
    {
        field: 'organGestor',
        flex: 3,
    },
    {
        field: 'retard',
        flex: 0.6,
    },
    {
        field: 'caducitat',
        flex: 0.6,
    },
    {
        field: 'entregaCieActiva',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'comu',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'requireDirectPermission',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'manual',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'actiu',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'grupCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={1} clickEnabled={params.row.agrupar}>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        },
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={params.row.agrupar ? 2 : 1} clickEnabled>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        },
    },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('codi', data.codi),
        filterBuilder.like('nom', data.nom),
        filterBuilder.eq('organGestor.id', data.organGestor?.id),
        data?.actiu && filterBuilder.eq('actiu', `'${data.actiu}'`),
        data?.comu && filterBuilder.eq('comu', `'${data.comu}'`),
        data?.entregaCieActiva ? filterBuilder.neq('entregaCie', null) : undefined,
        data?.manual && filterBuilder.eq('manual', `'${data.manual}'`),
        data?.requireDirectPermission && filterBuilder.eq('requireDirectPermission', `'${data.requireDirectPermission}'`)
    );
};

const ContentFilter: React.FC = () => {

    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    return (
        <Grid container spacing={2}>
            <GridFormField size={1} name="codi" />
            <GridFormField size={3} name="nom" />
            <GridFormField size={4} name="organGestor" optionRenderer={organGestorOptionRenderer} />
            <GridButtonField size={0.5} name="actiu" icon={'flash_on'} hiddenLabel />
            <GridButtonField size={0.5} name="comu" icon={'public'} hiddenLabel />
            <GridButtonField size={0.5} name="entregaCieActiva" icon={'email'} hiddenLabel />
            <GridButtonField size={0.5} name="manual" icon={'sync'} hiddenLabel />
            <GridButtonField size={0.5} name="requireDirectPermission" icon={'gpp_good'} hiddenLabel/>
            <FilterClearButton />
        </Grid>
    );
};

const NetejarCacheActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef; }> = (props) => {

    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    return (
        <MuiActionReportButton
            resourceName="procedimentResource"
            action="PROCEDIMENTS_NETEJAR_CACHE"
            title={t('page.procediments.grid.netejarCache.title')}
            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1} }}
            buttonIcon="delete"
            onSuccess={resposta => {
                const msg = resposta ? "success" : "error";
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.procediments.grid.netejarCache.' + msg), msg);
            }}
            // onError={error => temporalMessageShow(null, error?.message, "error")}
        />
    );
}

const ProcedimentSyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef; }> = (props) => {

    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    // Memoritzat: useFormDialog torna a aplicar els botons quan canvia la referència de l'array (un array nou a cada render provoca un bucle infinit)
    const formDialogButtons = React.useMemo(() => [
        {
            value: false,
            text: t('page.procediments.grid.sync.cancelar'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.procediments.grid.sync.actualitzar'),
            icon: 'check',
            componentProps: { variant: 'contained' },
        },
    ], [t]);
    return (
        <MuiActionReportButton
            resourceName="procedimentResource"
            action="PROCEDIMENTS_SYNC"
            title={t('page.procediments.grid.sync.title')}
            buttonComponentProps={{ variant: 'contained', sx: { mr: 1 } }}
            formDialogTitle={t('page.procediments.grid.sync.title')}
            formDialogContent={<Typography align="center">{t('page.procediments.grid.sync.confirmacio')}</Typography>}
            formDialogButtons={formDialogButtons}
            buttonIcon="refresh"
            onSuccess={resposta => {
                const msg = resposta ? "success" : "error";
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.procediments.grid.sync.' + msg), msg);
            }}
            // onError={error => temporalMessageShow(null, error?.message, "error")}
        />
    );
}

export const ProcedimentGrid = () => {

    const { t } = useTranslation();
    const { currentEntitatId } = useNotibContext();
    const dataGridApiRef = useMuiDataGridApiRef();
    const {currentRole} = useNotibContext();
    let isRoleAdmin = currentRole == ROLE_ADMIN;
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'procedimentResource',
        'FILTER_PROCEDIMENT',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const {
        activarProcediment,
        desactivarProcediment,
        actualitzarProcediment,
        syncManualProcediment,
        syncAutoProcediment,
    } = useAccionsProcediment(dataGridApiRef);
    // Els procediments comuns només els pot gestionar un administrador d'entitat.
    const rowActionsHidden = (row: { comu?: boolean }) => row.comu && !isRoleAdmin;
    const rowAdditionalActions: DataCommonAdditionalAction[] = [
        {
            label: t('page.procediments.grid.accions.actualitzar.title'),
            title: t('page.procediments.grid.accions.actualitzar.title'),
            icon: 'refresh',
            action: 'PROCEDIMENT_ACTUALITZAR',
            showInMenu: true,
            hidden: row => row.manual || rowActionsHidden(row),
            onClick: id => actualitzarProcediment(id),
        },
        {
            label: t('page.procediments.grid.accions.activar.title'),
            title: t('page.procediments.grid.accions.activar.title'),
            icon: 'check',
            action: 'PROCEDIMENT_ACTIVAR',
            showInMenu: true,
            hidden: row => row.actiu || rowActionsHidden(row),
            onClick: id => activarProcediment(id),
        },
        {
            label: t('page.procediments.grid.accions.desactivar.title'),
            title: t('page.procediments.grid.accions.desactivar.title'),
            icon: 'close',
            action: 'PROCEDIMENT_DESACTIVAR',
            showInMenu: true,
            hidden: row => !row.actiu || rowActionsHidden(row),
            onClick: id => desactivarProcediment(id),
        },
        {
            label: t('page.procediments.grid.accions.syncManual.title'),
            title: t('page.procediments.grid.accions.syncManual.title'),
            icon: 'back_hand',
            action: 'PROCEDIMENT_SYNC_MANUAL',
            showInMenu: true,
            hidden: row => row.manual || rowActionsHidden(row),
            onClick: id => syncManualProcediment(id),
        },
        {
            label: t('page.procediments.grid.accions.syncAuto.title'),
            title: t('page.procediments.grid.accions.syncAuto.title'),
            icon: 'settings',
            action: 'PROCEDIMENT_SYNC_AUTO',
            showInMenu: true,
            hidden: row => !row.manual || rowActionsHidden(row),
            onClick: id => syncAutoProcediment(id),
        },
    ];
    return (
        <>
            <PageTitle title={t('page.procediments.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.procediment} title={t('page.procediments.grid.title')}>
                    <StyledMuiGrid
                        resourceName="procedimentResource"
                        columns={columns}
                        fixedFilter={"tipus:'PROCEDIMENT' and entitat.id:" + currentEntitatId}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        toolbarType="upper"
                        popupEditActive
                        toolbarHideCreate
                        popupEditFormContent={<ProcedimentForm/>}
                        popupEditFormDialogResourceTitle={t('page.procediments.grid.popupResourceTitle')}
                        rowUpdateShowInMenu
                        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                        rowAdditionalActions={rowAdditionalActions}
                        apiRef={dataGridApiRef}
                        toolbarElementsWithPositions={ !isRoleAdmin ? [] : [
                            {
                                position: 2,
                                element: <NetejarCacheActionButton dataGridApiRef={dataGridApiRef} />},
                            {
                                position: 2,
                                element: <ProcedimentSyncActionButton dataGridApiRef={dataGridApiRef} />},
                            {
                                position: 2,
                                element: <AddButton gridApiRef={dataGridApiRef} text={t('page.procediments.grid.nou')} tooltip={t('page.procediments.grid.nouTooltip')}/>
                            },
                        ]}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default ProcedimentGrid;
