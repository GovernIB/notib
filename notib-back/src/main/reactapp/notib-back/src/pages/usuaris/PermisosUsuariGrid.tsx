import {Grid, Icon, IconButton, Typography} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiDataGrid, MuiDataGridColDef, springFilterBuilder as filterBuilder, useFilterApiContext,} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from '../../hooks/useDataGrid';
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF} from "@mui/x-data-grid-pro";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";
import PermisosUsuariDetail from "./PermisosUsuariDetail.tsx";
import PageTitle from "../../components/PageTitle.tsx";
import Box from "@mui/material/Box";
import {useLocation} from "react-router-dom";
import {getMenuEntryByPath} from "../../routeAccess.ts";
import {useNotibContext} from "../../components/NotibContext.ts";


const springFilterBuilder = (data: any) => filterBuilder.and(filterBuilder.like('codi', data.codi));

const ContentFilter: React.FC = () => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="codi" />
            <Box sx={{display: 'flex', justifyContent: 'flex-end', flex:1}}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Box>
        </Grid>
    );
};

export const PermisosUsuariGrid = () => {

    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'usuariPermisResource',
        'FILTER_USUARI_PERMIS',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columns: MuiDataGridColDef[] = [
        {
            field: 'codi',
            flex: 1,
        },
        {
            field: 'nom',
            flex: 1,
        },
        {
            field: 'llinatges',
            flex: 1,
        },
        {
            field: 'nif',
            flex: 1,
        },
        {
            field: 'email',
            flex: 1,
        },
        {
            field: 'emailAlt',
            flex: 1,
        },
        {
            ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
            sortable: false,
            resizable: false,
            width: 90,
            align: 'center',
            renderCell: (params: any) => (
                <CustomDetailPanelToggle id={params.id}
                                         value={params.value}
                                         msgMostrar={t('page.notificacio.grid.column.mostrar')}
                                         msgOcultar={t('page.notificacio.grid.column.ocultar')} />
            ),
        },
    ];

    const { currentRole} = useNotibContext();
    const { pathname } = useLocation();
    const menuEntry = getMenuEntryByPath(pathname, currentRole, t);

    return (
        <>
            <PageTitle title={t('page.usuaris.permisos.grid.title')}></PageTitle>
            <GridPage>
                <MuiDataGrid
                    title={
                        <Box display="flex" alignItems="center" sx={{ gap: 1 }}>
                            {menuEntry?.icon && <Icon fontSize="small">{menuEntry.icon}</Icon>}
                            <Typography component="span" variant="h6" sx={{ mb: 0 }}>
                                {t('page.usuaris.permisos.grid.title')}
                            </Typography>
                        </Box>
                    }
                    resourceName="usuariPermisResource"
                    columns={columns}
                    striped
                    paginationActive
                    className="permisos-grid"
                    persistentStateActive
                    persistentStateClearPageSortPropsOnTopLevelRouteChange
                    {...filterDataGridProps}
                    {...pageSizeOptionsDataGridProps}
                    toolbarType="upper"
                    rowHideUpdateButton
                    getDetailPanelContent={({ row }) => <PermisosUsuariDetail id={row.id} />}
                    getDetailPanelHeight={() => 'auto'}
                    sx={{
                        '&.permisos-grid .MuiDataGrid-row:not(:first-of-type)': {
                            borderTop: '2px solid rgb(80, 80, 80)',
                        },
                        '&.permisos-grid .permisos-detail-grid .MuiDataGrid-row:not(:first-of-type)': {
                            borderTop: 'none !important',
                        },
                    }}
                />
            </GridPage>
        </>
    );
};

export default PermisosUsuariGrid;
