import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { GridPage, MuiDataGrid } from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import GridFormField from '../../components/GridFormField.tsx';
import {ROLE_ADMIN_LECTURA, useNotibContext} from "../../components/NotibContext.ts";
import PageTitle from "../../components/PageTitle.tsx";
import {useLocation} from "react-router-dom";
import {getMenuEntryByPath} from "../../routeAccess.ts";
import Box from "@mui/material/Box";
import Icon from "@mui/material/Icon";
import {Typography} from "@mui/material";

const columns = [
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'codi',
        flex: 4,
    },
    {
        field: 'organGestor',
        flex: 4,
    }
];

const GrupForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="codi" />
            <GridFormField size={12} name="nom" />
        </Grid>
    );
};

export const Grups: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const { pathname } = useLocation();
    const menuEntry = getMenuEntryByPath(pathname, currentRole, t);

    return (
        <>
            <PageTitle title={t('page.grups.grid.title')}></PageTitle>
            <GridPage>
                <MuiDataGrid
                    title={
                        <Box display="flex" alignItems="center" sx={{ gap: 1 }}>
                            {menuEntry?.icon && <Icon fontSize="small">{menuEntry.icon}</Icon>}
                            <Typography component="span" variant="h6" sx={{ mb: 0 }}>
                                {t('page.grups.grid.title')}
                            </Typography>
                        </Box>
                    }
                    resourceName="grupResource"
                    columns={columns}
                    striped
                    paginationActive
                    persistentStateActive
                    persistentStateClearPageSortPropsOnTopLevelRouteChange
                    {...pageSizeOptionsDataGridProps}
                    toolbarType="upper"
                    popupEditActive
                    popupEditFormContent={<GrupForm />}
                    popupEditFormDialogResourceTitle={t('page.grups.grid.popupResourceTitle')}
                    toolbarHideCreate={isRoleAdminLectura ? true : undefined}
                    rowHideUpdateButton={isRoleAdminLectura}
                    rowHideDeleteButton={isRoleAdminLectura}
                />
            </GridPage>
        </>
    );
};

export default Grups;
