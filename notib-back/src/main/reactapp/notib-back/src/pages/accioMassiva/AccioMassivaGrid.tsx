import {GridPage, MuiDataGrid, MuiDataGridColDef, springFilterBuilder as filterBuilder, useFilterApiContext} from "reactlib";
import {useTranslation} from "react-i18next";
import React from "react";
import {
    GRID_DETAIL_PANEL_TOGGLE_COL_DEF, gridDetailPanelExpandedRowIdsSelector,
    gridDetailPanelExpandedRowsContentCacheSelector,
    GridRenderCellParams,
    useGridApiContext,
    useGridApiRef,
    useGridSelector
} from "@mui/x-data-grid-pro";
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from "../../hooks/useDataGrid.tsx";
import {formatEndOfDay, formatStartOfDay} from "../../utils/dateUtils.ts";
import {Box, Chip, Grid, Icon, IconButton} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import AccioMassivaGridEnviaments from "./AccioMassivaGridElements.tsx";

const CustomDetailPanelToggle = (props: Pick<GridRenderCellParams, 'id' | 'value'>) => {
    const { id } = props;
    const { t } = useTranslation();
    const apiRef = useGridApiContext();
    const contentCache = useGridSelector(apiRef, gridDetailPanelExpandedRowsContentCacheSelector);
    const hasDetail = React.isValidElement(contentCache[id]);
    const expandedRowIds = useGridSelector(apiRef, gridDetailPanelExpandedRowIdsSelector);
    const isExpanded = expandedRowIds.has(id);

    return (
        <IconButton
            size="small"
            tabIndex={-1}
            disabled={!hasDetail}
            title={
                isExpanded
                    ? t('page.accioMassiva.grid.ocultarElements')
                    : t('page.accioMassiva.grid.mostarElements')
            }
            aria-label={
                isExpanded
                    ? t('page.accioMassiva.grid.ocultarElements')
                    : t('page.accioMassiva.grid.mostarElements')
            }
        >
            <Icon
                sx={(theme) => ({
                    transform: `rotateZ(${isExpanded ? 180 : 0}deg)`,
                    transition: theme.transitions.create('transform', {
                        duration: theme.transitions.duration.shortest,
                    }),
                })}
                fontSize="inherit"
            >
                expand_more
            </Icon>
        </IconButton>
    );
};

const useDataGridColumns = (datagridApiRef: any) => {

    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'tipus',
                headerName: t('page.accioMassiva.grid.tipus'),
                flex: 1
            },
            {
                field: 'createdDate',
                headerName: t('page.accioMassiva.grid.createdDate'),
                flex: 0.5
            },
            {
                field: 'dataInici',
                headerName: t('page.accioMassiva.grid.dataInici'),
                flex: 0.5
            },
            {
                field: 'dataFi',
                headerName: t('page.accioMassiva.grid.dataFi'),
                flex: 0.5
            },
            {
                field: 'numErrors',
                headerName: t('page.accioMassiva.grid.okErrorPendent'),
                flex: 0.5,
                renderCell: (params: any) => {

                    const iconOk= React.cloneElement(<Icon>check</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center"} });
                    const iconError= React.cloneElement(<Icon>close</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center" } });
                    const iconPendent= React.cloneElement(<Icon>access_time</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center", ml: 0.1 } });
                    return (
                        <Box component="span" sx={{ display: "inline-flex", gap: 1, alignItems: "center" }}>
                            {params.row.numOk > 0 && (<Chip key="ok" label={<>{iconOk}{params.row.numOk}</>} color="success" />)}
                            {params.row.numErrors > 0 && (<Chip key="err" label={<>{iconError}{params.row.numErrors}</>} color="error" />)}
                            {params.row.numPendent > 0 && (<Chip key="pend" label={<>{iconPendent}{params.row.numPendent}</>} color="warning" />)}
                        </Box>
                    );
                }
            },
            {
                field: 'usuariNomComplet',
                headerName: t('page.accioMassiva.grid.createdBy'),
                flex: 0.5
            },
            {
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                hideable: false,
                sortable: false,
                resizable: false,
                width: 90,
                align: 'center',
                // renderHeader: () => <ButtonDetailExpandColapse datagridApiRef={datagridApiRef} />,
                renderCell: (params: any) => (
                    <CustomDetailPanelToggle id={params.id} value={params.value} />
                ),
            },
        ],
        []
    );
    return columns;
}

const useSpringFilterBuilder = () => {
    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('tipus', `'${data?.tipus}'`),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            filterBuilder.like('createdBy', data?.createdBy),
            data?.dataIniciInici &&
            filterBuilder.gte('dataInici', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi &&
            filterBuilder.lte('dataInici', `'${formatEndOfDay(data?.dataIniciFi)}'`),
        );
    };
};

const ContentFilter: React.FC = () => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };

    return (
        <Box>
            <Grid container spacing={1}>
                <GridFormField size={2.75} name="tipus" />
                <GridFormField size={1.75} name="createdBy" />
                <GridFormField size={1.75} name="dataIniciInici" />
                <GridFormField size={1.75} name="dataIniciFi" />
                <GridFormField size={2.5} name="estat" />

                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
        </Box>
    );
};


export const AccioMassivaGrid = () => {

    const { t } = useTranslation();
    const datagridApiRef = useGridApiRef();
    const columns = useDataGridColumns(datagridApiRef);
    const springFilterBuilder = useSpringFilterBuilder();
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const filterDataGridProps = useDatagridFilterProps(
        'accioMassivaResource',
        'FILTER_ACCIO_MASSIVA',
        springFilterBuilder,
        <ContentFilter />
    );

    return (<GridPage>
        <MuiDataGrid
            title={t('page.accioMassiva.grid.title')}
            resourceName="accioMassivaResource"
            columns={columns}
            paginationActive
            // persistentStateActive
            // persistentStateClearPageSortPropsOnTopLevelRouteChange
            {...filterDataGridProps}
            {...pageSizeOptionsDataGridProps}
            toolbarType="upper"
            getDetailPanelContent={({ row }) => <AccioMassivaGridEnviaments id={row.id} tipusElementSeleccionat={row.tipusElementSeleccionat}/>}
            getDetailPanelHeight={() => 'auto'}

        />
        </GridPage>);
}

export default AccioMassivaGrid;
