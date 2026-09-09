import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext';

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
                <LinkToTab id={params.id} tab={2} clickEnabled>
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
                <LinkToTab id={params.id} tab={3} clickEnabled>
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
                <LinkToTab id={params.id} tab={4} clickEnabled>
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
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={1.5} name="codi" />
            <GridFormField size={5} name="nom" />
            <GridFormField size={1.5} name="dir3Codi" />
            <GridFormField size={1} name="activa" />
            {/* <GridFormField size={2} name="tipus" /> */}
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

export const EntitatGrid: React.FC = () => {
    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'entitatResource',
        'FILTER_ENTITAT',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columns}
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
    );
};

export default EntitatGrid;
