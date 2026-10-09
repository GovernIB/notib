import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import GridFormField from '../../components/GridFormField.tsx';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer.tsx';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils.ts';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext.ts';
import PageTitle from "../../components/PageTitle.tsx";
import Box from "@mui/material/Box";
import {useLocation} from "react-router-dom";
import {getMenuEntryByPath} from "../../routeAccess.ts";
import {Typography} from "@mui/material";

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
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };

    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestor" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={1.5} name="contracteNum" />
            <GridFormField size={1.75} name="contracteDataVigInici" />
            <GridFormField size={1.75} name="contracteDataVigFinal" />
            <GridFormField size={1.5} name="facturacioClientCodi" />
            <Box sx={{display: 'flex', justifyContent: 'flex-end', flex:1}}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Box>
        </Grid>
    );
};

export const PagadorsPostalsGrid: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'pagadorPostalResource',
        'FILTER_PAGADOR_POSTAL',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const { pathname } = useLocation();
    const menuEntry = getMenuEntryByPath(pathname, currentRole, t);

    return (
        <>
            <PageTitle title={t('page.pagadorPostal.grid.title')}></PageTitle>
            <GridPage>
                <MuiDataGrid
                    title={
                        <Box display="flex" alignItems="center" sx={{ gap: 1 }}>
                            {menuEntry?.icon && <Icon fontSize="small">{menuEntry.icon}</Icon>}
                            <Typography component="span" variant="h6" sx={{ mb: 0 }}>
                                {t('page.pagadorPostal.grid.title')}
                            </Typography>
                        </Box>
                    }
                    resourceName="pagadorPostalResource"
                    columns={columns}
                    striped
                    paginationActive
                    persistentStateActive
                    persistentStateClearPageSortPropsOnTopLevelRouteChange
                    {...filterDataGridProps}
                    {...pageSizeOptionsDataGridProps}
                    toolbarType="upper"
                    toolbarCreateLink="form"
                    toolbarHideCreate={isRoleAdminLectura ? true : undefined}
                    rowLink="form/{{id}}"
                    rowUpdateLink="form/{{id}}"
                    rowHideUpdateButton={isRoleAdminLectura}
                    rowHideDeleteButton={isRoleAdminLectura}
                />
            </GridPage>
        </>
    );
};

export default PagadorsPostalsGrid;
