import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import {
    GridPage,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder, useMuiDataGridApiRef,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import { EntitatFormContent } from "./EntitatForm.tsx";
import AddButton from "../../components/AddButton.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'dir3Codi',
        flex: 1,
    },
    {
        field: 'activa',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'tipusDocCount',
        flex: 0.8,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={2} clickEnabled>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        },
    },
    {
        field: 'aplicacioCount',
        flex: 0.6,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={3} clickEnabled>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        },
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={4} clickEnabled>
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
        // filterBuilder.eq('tipus', data.tipus),
        filterBuilder.like('dir3Codi', data.dir3Codi),
        filterBuilder.eq('activa', `'${data?.activa}'`)
    );
};

const ContentFilter: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 5 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dir3Codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="activa" />
        </>
    );
};

export const EntitatGrid: React.FC = () => {
    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const gridApiRef = useMuiDataGridApiRef();
    const filterDataGridProps = useDatagridFilterProps(
        'entitatResource',
        'FILTER_ENTITAT',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <>
            <PageTitle title={t('page.entitats.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.entitat} title={t('page.entitats.grid.title')}>
                    <StyledMuiGrid
                        resourceName="entitatResource"
                        columns={columns}
                        apiRef={gridApiRef}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        toolbarHideCreate
                        toolbarElementsWithPositions={ isRoleAdminLectura ? [] : [
                            { position: 2, element: <AddButton gridApiRef={gridApiRef} text={t('page.entitats.grid.nou')} tooltip={t('page.entitats.grid.nouTooltip')} /> },
                        ]}
                        popupEditActive
                        popupEditFormContent={<EntitatFormContent />}
                        popupEditFormDialogResourceTitle={t('page.entitats.grid.popupResourceTitle')}
                        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'xl' }}
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default EntitatGrid;
