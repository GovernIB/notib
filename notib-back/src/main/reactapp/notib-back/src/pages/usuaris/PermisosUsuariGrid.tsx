import {Grid, Icon, IconButton} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiDataGrid, MuiDataGridColDef, springFilterBuilder as filterBuilder, useFilterApiContext,} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
        flex: 2,
    },
    {
        field: 'nom',
        flex: 4,
    },
    // {
    //     field: 'dataInici',
    //     fieldType: 'date',
    //     flex: 1,
    // },
    // {
    //     field: 'dataFinal',
    //     fieldType: 'date',
    //     flex: 1,
    // },
    // {
    //     field: 'avisNivell',
    //     flex: 0.6,
    // },
    // {
    //     field: 'actiu',
    //     flex: 0.6,
    //     type: 'boolean',
    // },
];

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
        'usuariResource',
        'FILTER_USUARI_PERMIS',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.avisos.grid.title')}
                resourceName="usuariResource"
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                rowHideUpdateButton
            />
        </GridPage>
    );
};

export default PermisosUsuariGrid;
