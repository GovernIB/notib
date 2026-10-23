import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {GridPage, useMuiDataGridApiRef} from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid.tsx';
import GridFormField from '../../components/GridFormField.tsx';
import {ROLE_ADMIN_LECTURA, useNotibContext} from "../../components/NotibContext.ts";
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import AddButton from "../../components/AddButton.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

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
    const gridApiRef = useMuiDataGridApiRef();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <>
            <PageTitle title={t('page.grups.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.grup} title={t('page.grups.grid.title')}>
                    <StyledMuiGrid
                        resourceName="grupResource"
                        columns={columns}
                        apiRef={gridApiRef}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...pageSizeOptionsDataGridProps}
                        toolbarType="upper"
                        popupEditActive
                        popupEditFormContent={<GrupForm />}
                        popupEditFormDialogResourceTitle={t('page.grups.grid.popupResourceTitle')}
                        toolbarHideCreate
                        toolbarShowQuickFilter
                        toolbarElementsWithPositions={ isRoleAdminLectura ? [] : [{
                                position: 2,
                                element: <AddButton gridApiRef={gridApiRef} text={t('page.grups.grid.nou')} tooltip={t('page.grups.grid.nouTooltip')} /> },
                        ]}
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default Grups;
