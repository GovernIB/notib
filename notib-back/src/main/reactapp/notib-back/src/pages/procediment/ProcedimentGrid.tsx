import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    springFilterBuilder as filterBuilder,
    MuiDataGridColDef,
    useFilterApiContext, MuiDataGridApiRef, useBaseAppContext, MuiActionReportButton, useMuiDataGridApiRef,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import {ROLE_ADMIN, useNotibContext} from '../../components/NotibContext';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import React from "react";

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
                <LinkToTab id={params.id} tab={1} clickEnabled={params.row.agrupar}>
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
                <LinkToTab id={params.id} tab={2} clickEnabled>
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

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const handleButtonClick = () => filterApiRef.current?.clear();
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
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
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
    const formDialogButtons = [
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
    ];
    return (
        <MuiActionReportButton
            resourceName="procedimentResource"
            action="PROCEDIMENTS_SYNC"
            title={t('page.procediments.grid.sync.title')}
            buttonComponentProps={{ variant: 'contained', sx: { mr: 1 } }}
            formDialogTitle={t('page.serveis.grid.sync.title')}
            formDialogButtons={formDialogButtons}
            buttonIcon="refresh"
            onSuccess={resposta => {
                const msg = resposta ? "success" : "error";
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.serveis.grid.sync.' + msg), msg);
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
    const filterDataGridProps = useDatagridFilterProps(
        'procedimentResource',
        'FILTER_PROCEDIMENT',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.procediments.grid.title')}
                resourceName="procedimentResource"
                columns={columns}
                fixedFilter={"tipus:'PROCEDIMENT' and entitat.id:" + currentEntitatId}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                toolbarElementsWithPositions={ !isRoleAdmin ? [] : [
                    {
                        position: 2,
                        element: <NetejarCacheActionButton dataGridApiRef={dataGridApiRef} />,

                    } ,
                    {
                        position: 2,
                        element: <ProcedimentSyncActionButton dataGridApiRef={dataGridApiRef} />,
                    }
                ]}
            />
        </GridPage>
    );
};

export default ProcedimentGrid;
