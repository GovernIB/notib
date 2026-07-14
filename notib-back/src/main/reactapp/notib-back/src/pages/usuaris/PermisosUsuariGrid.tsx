import {Grid, Icon, IconButton} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiDataGrid, MuiDataGridColDef, springFilterBuilder as filterBuilder, useFilterApiContext,} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from '../../hooks/useDataGrid';
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF} from "@mui/x-data-grid-pro";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";
import PermisosUsuariDetail from "./PermisosUsuariDetail.tsx";


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
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
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


    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.usuaris.permisos.grid.title')}
                resourceName="usuariPermisResource"
                columns={columns}
                paginationActive
                className="permisos-grid"
                // persistentStateActive
                // persistentStateClearPageSortPropsOnTopLevelRouteChange
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
    );
};

export default PermisosUsuariGrid;
