import {GridPage, MuiDataGrid, MuiDataGridColDef, springFilterBuilder as filterBuilder, useFilterApiContext, useMuiDataGridContext} from "reactlib";
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
import {useNotibContext} from "../../components/NotibContext.ts";
import AccionsMassives, {MenuOption, useAccionsMassives} from "../../components/AccionsMassives.tsx";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";


const columns: MuiDataGridColDef[] = [
    {
        field: 'usuariCodi',
        flex: 2,
    },
    {
        field: 'endpoint',
        flex: 4,
    },
    {
        field: 'intents',
        flex: 2,
        renderCell: params => <Box>{params.row.intents}/{params.row.maxIntents}</Box>
    },
    {
        field: 'dataCreacio',
        flex: 2,
    },
    {
        field: 'ultimIntent',
        flex: 2,
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
            return <Link to={`/notificacions?referencia=${params.row.notificacioReferencia}`} color="#ffff" target="_blank">{params.row.notificacioReferencia}</Link>
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

const useSpringFilterBuilder = () => {
    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('usuariCodi', data?.usuariCodi),
            filterBuilder.like('notificacioReferencia', `'${data?.notificacioReferencia}'`),
            data?.dataIniciInici && filterBuilder.gte('dataCreacio', `'${formatStartOfDay(data?.dataCreacioInici)}'`),
            data?.dataIniciFi && filterBuilder.lte('dataCreacio', `'${formatEndOfDay(data?.dataCreacioFinal)}'`),
            data?.dataUltimIntentInici && filterBuilder.gte('dataInici', `'${formatStartOfDay(data?.dataUltimIntentInici)}'`),
            data?.dataUltimIntentFi && filterBuilder.lte('dataInici', `'${formatEndOfDay(data?.dataUltimIntentFi)}'`),
            filterBuilder.eq('estat', data?.estat),
        );
    };
};

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>}> = ({apiRef}) => {

    const {selection} = useMuiDataGridContext();
    const {t} = useTranslation();
    const {
        enviarCallbacksPendentsMassiu,
        pausarCallbacksPendentsMassiu,
        activarCallbacksPendentsMassiu
    } = useAccionsMassives("callbackResource");

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
    ];

    return (<AccionsMassives options={opcionsMenu} apiRef={apiRef} resource={"callbackResource"} sizeSelection={selection?.ids?.size}/>
    )
}

const CallbackPendentsGrid = () => {

    const { t } = useTranslation();
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const springFilterBuilder = useSpringFilterBuilder();
    const datagridApiRef = useGridApiRef();
    const filterDataGridProps = useDatagridFilterProps(
        'callbackResource',
        'FILTER_CALLBACK_PENDENTS',
        springFilterBuilder,
        <ContentFilter />
    );

    const {
        enviarCallbackPendent,
        pausarCallbackPendent,
        activarCallbackPendent
    } = useAccionsCallbacks();

    const rowAdditionalActions: DataCommonAdditionalAction[] = [
        {
            label: t('page.callbacks.pendents.grid.accions.enviar.title'),
            title: t('page.callbacks.pendents.grid.accions.enviar.title'),
            icon: 'send',
            showInMenu: true,
            onClick: id => enviarCallbackPendent(id),
        },
        {
            label: t('page.callbacks.pendents.grid.accions.pausar.title'),
            title: t('page.callbacks.pendents.grid.accions.pausar.title'),
            icon: 'pause',
            showInMenu: true,
            onClick: id => pausarCallbackPendent(id),
            hidden: row => row.pausat
        },
        {
            label: t('page.callbacks.pendents.grid.accions.activar.title'),
            title: t('page.callbacks.pendents.grid.accions.activar.title'),
            icon: 'play_arrow',
            showInMenu: true,
            onClick: id => activarCallbackPendent(id),
            hidden: row => !row.pausat
        }

    ];
    const {currentRole} = useNotibContext();
    let amagarEntrada = currentRole === 'tothom' || currentRole === 'NOT_ADMIN_LECTURA';

    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.callbacks.pendents.grid.title')}
                resourceName="callbackResource"
                datagridApiRef={datagridApiRef}
                columns={columns}
                rowAdditionalActions={rowAdditionalActions}
                paginationActive
                selectionActive
                rowHideUpdateButton
                // persistentStateActive
                // persistentStateClearPageSortPropsOnTopLevelRouteChange
               {...filterDataGridProps}
               {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                toolbarElementsWithPositions={[
                    ...(amagarEntrada ? []
                        : [{
                            position: 2,
                            element: <MassiveActionsButton apiRef={datagridApiRef}/>,
                        }])
                ]}
            />
        </GridPage>
    );
}

export default CallbackPendentsGrid;
