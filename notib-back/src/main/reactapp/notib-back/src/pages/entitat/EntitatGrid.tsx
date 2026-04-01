import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    MuiFilter,
    useFilterApiRef,
    springFilterBuilder as filterBuilder,
    FilterApi,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Icon, IconButton } from '@mui/material';
import LinkToTab from '../../components/LinkToTab';

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
                <LinkToTab id={params.id} tab={2}>
                    <Chip
                        label={params.value}
                        color={params.value ? 'primary' : undefined}
                        size="small"
                    />
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
                <LinkToTab id={params.id} tab={3}>
                    <Chip
                        label={params.value}
                        color={params.value ? 'primary' : undefined}
                        size="small"
                    />
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
                <LinkToTab id={params.id} tab={4}>
                    <Chip
                        label={params.value}
                        color={params.value ? 'primary' : undefined}
                        size="small"
                    />
                </LinkToTab>
            );
        },
    },
];

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
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

const EntitatGridFilter: React.FC = () => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('codi', data.codi),
            filterBuilder.like('nom', data.nom),
            // filterBuilder.eq('tipus', data.tipus),
            filterBuilder.like('dir3Codi', data.dir3Codi),
            filterBuilder.eq('activa', `'${data?.activa}'`)
        );
    };

    return (
        <MuiFilter
            resourceName="entitatResource"
            code="FILTER_ENTITAT"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const EntitatGrid: React.FC = () => {
    const { t } = useTranslation();

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                toolbarAdditionalRow={<EntitatGridFilter />}
                toolbarHideQuickFilter
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default EntitatGrid;
