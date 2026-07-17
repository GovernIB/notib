import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
    useMuiDataGridContext,
    useResourceApiService
} from "reactlib";
import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import {Link} from "react-router-dom";
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from "../../hooks/useDataGrid.tsx";
import React from "react";
import {Grid, Icon, IconButton} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {formatEndOfDay, formatStartOfDay} from "../../utils/dateUtils.ts";
import {DataCommonAdditionalAction} from "../../../lib/components/mui/datacommon/MuiDataCommon.tsx";
import {useAccionsCallbacks} from "../accions/AccionsCallbacks.tsx";
import {GridApiPro, useGridApiRef} from "@mui/x-data-grid-pro";
import {ROLE_ADMIN_LECTURA, ROLE_USER, useNotibContext} from "../../components/NotibContext.ts";
import AccionsMassives, {MenuOption, useAccionsMassives} from "../../components/AccionsMassives.tsx";


const columns: MuiDataGridColDef[] = [
    {
        field: 'usuariCodi',
        flex: 2.5,
    },
    {
        field: 'endpoint',
        flex: 4.5,
    },
    {
        field: 'intents',
        flex: 2,
        renderCell: params => <Box>{params.row.intents}/{params.row.maxIntents}</Box>
    },
    {
        field: 'dataCreacio',
        flex: 2.5,
    },
    {
        field: 'ultimIntent',
        flex: 2.5,
    },
    {
        field: 'properIntent',
        flex: 2,
    },
    {
        field: 'estat',
        flex: 2,
    },
    {
        field: 'pausat',
        flex: 1,
    },
    {
        field: 'notificacioReferencia',
        flex: 4,
        renderCell: params => {
            return <Link to={`/notificacions?referencia=${params.row.notificacioReferencia}`} style={{ color: '#fff' }} target="_blank">{params.row.notificacioReferencia}</Link>
        }
    },
];

const ContentFilter: React.FC = () => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };

    return (
        <Box>
            <Grid container spacing={1}>
                <GridFormField size={2} name="usuariCodi" />
                <GridFormField size={1.75} name="notificacioReferencia" />
                <GridFormField size={1.75} name="dataCreacioInici" />
                <GridFormField size={1.75} name="dataCreacioFinal" />
                <GridFormField size={2} name="dataUltimIntentInici" />
                <GridFormField size={2} name="dataUltimIntentFinal" />
                <GridFormField size={2.5} name="estat" />
                <GridFormField size={2.5} name="fiReintents" />

                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
        </Box>
    );
};

const useSpringFilterBuilder = (maxRetries: number | null) => {

    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('usuariCodi', data?.usuariCodi),
            filterBuilder.like('notificacio.referencia', data?.notificacioReferencia),
            data?.dataCreacioInici && filterBuilder.gte('dataCreacio', `'${formatStartOfDay(data?.dataCreacioInici)}'`),
            data?.dataCreacioFinal && filterBuilder.lte('dataCreacio', `'${formatEndOfDay(data?.dataCreacioFinal)}'`),
            data?.dataUltimIntentInici && filterBuilder.gte('ultimIntent', `'${formatStartOfDay(data?.dataUltimIntentInici)}'`),
            data?.dataUltimIntentFinal && filterBuilder.lte('ultimIntent', `'${formatEndOfDay(data?.dataUltimIntentFinal)}'`),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            data?.fiReintents === "true" && filterBuilder.gte('intents', maxRetries),
            data?.fiReintents === "false" && filterBuilder.lt('intents', maxRetries),
        );
    };
};

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>, refresh: () => void}> = ({apiRef, refresh}) => {

    const {selection} = useMuiDataGridContext();
    const {t} = useTranslation();
    const {
        enviarCallbacksPendentsMassiu,
        pausarCallbacksPendentsMassiu,
        activarCallbacksPendentsMassiu,
        esborrarCallbacksPendentsMassiu
    } = useAccionsMassives("callbackResource", refresh);

    const opcionsMenu: MenuOption[] = [
        {
            label: t('page.callbacks.pendents.grid.accions.enviar.title'),
            tooltip: t('page.callbacks.pendents.grid.accions.enviar.title'),
            onClick: () => enviarCallbacksPendentsMassiu(selection?.ids)
        },
        {
            label: t('page.callbacks.pendents.grid.accions.pausar.title'),
            tooltip: t('page.callbacks.pendents.grid.accions.pausar.title'),
            onClick: () => pausarCallbacksPendentsMassiu(selection?.ids)
        },
        {
            label: t('page.callbacks.pendents.grid.accions.activar.title'),
            tooltip: t('page.callbacks.pendents.grid.accions.activar.title'),
            onClick: () => activarCallbacksPendentsMassiu(selection?.ids)
        },
        {
            label: t('page.callbacks.pendents.grid.accions.esborrar.title'),
            tooltip: t('page.callbacks.pendents.grid.accions.esborrar.title'),
            onClick: () => esborrarCallbacksPendentsMassiu(selection?.ids)
        }
    ]
    return (<AccionsMassives options={opcionsMenu} apiRef={apiRef} resource={"callbackResource"} sizeSelection={selection?.ids?.size}/>)
}

const CallbackPendentsGrid = () => {

    const { t } = useTranslation();
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
    const [maxRetries, setMaxRetries] = React.useState(null);
    React.useEffect(() => {
        const fetchParams = async () => {
            if (!apiIsReady) {
                return;
            }
            try {
                const args = { filter: "key: 'es.caib.notib.tasca.callback.pendents.notifica.events.intents.max'", unpaged: true };
                const resposta = await apiFind(args);
                setMaxRetries(resposta?.rows?.[0]?.value ?? null);
            } catch (error) {
                console.error('Error obtinguent el maxim nombre de reintents:', error);
            }
        };
        fetchParams();
    }, [apiIsReady, apiFind]);
    const springFilterBuilder = useSpringFilterBuilder(maxRetries);
    const filterDataGridProps = useDatagridFilterProps(
        'callbackResource',
        'FILTER_CALLBACK_PENDENTS',
        springFilterBuilder,
        <ContentFilter />
    );
    const datagridApiRef = useGridApiRef();

    const {
        enviarCallbackPendent,
        pausarCallbackPendent,
        activarCallbackPendent
    } = useAccionsCallbacks();

    const {currentRole} = useNotibContext();
    let amagarEntrada = currentRole === ROLE_USER || currentRole === ROLE_ADMIN_LECTURA;
    let isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const rowAdditionalActions: DataCommonAdditionalAction[] = [
        {
            label: t('page.callbacks.pendents.grid.accions.enviar.title'),
            title: t('page.callbacks.pendents.grid.accions.enviar.title'),
            icon: 'send',
            showInMenu: true,
            hidden: isRoleAdminLectura,
            onClick: id => enviarCallbackPendent(id),
        },
        {
            label: t('page.callbacks.pendents.grid.accions.pausar.title'),
            title: t('page.callbacks.pendents.grid.accions.pausar.title'),
            icon: 'pause',
            showInMenu: true,
            onClick: id => pausarCallbackPendent(id),
            hidden: row => row.pausat || isRoleAdminLectura
        },
        {
            label: t('page.callbacks.pendents.grid.accions.activar.title'),
            title: t('page.callbacks.pendents.grid.accions.activar.title'),
            icon: 'play_arrow',
            showInMenu: true,
            onClick: id => activarCallbackPendent(id),
            hidden: row => !row.pausat || isRoleAdminLectura
        }

    ];
    const [reloadKey, setReloadKey] = React.useState(0);
    const refreshGrid = React.useCallback(() => setReloadKey(k => k + 1), []);
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                key={reloadKey}
                title={t('page.callbacks.pendents.grid.title')}
                resourceName="callbackResource"
                datagridApiRef={datagridApiRef}
                columns={columns}
                rowAdditionalActions={rowAdditionalActions}
                paginationActive
                selectionActive
                rowHideUpdateButton
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
               {...filterDataGridProps}
               {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                toolbarElementsWithPositions={[
                    ...(amagarEntrada ? []
                        : [{
                            position: 2,
                            element: <MassiveActionsButton apiRef={datagridApiRef} refresh={refreshGrid}/>,
                        }])
                ]}
            />
        </GridPage>
    );
}

export default CallbackPendentsGrid;
