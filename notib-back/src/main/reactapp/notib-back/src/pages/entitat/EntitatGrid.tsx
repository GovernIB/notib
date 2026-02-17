import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    MuiFilter,
    FormField,
    useFilterApiRef,
} from 'reactlib';

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
    },
    {
        field: 'tipusDocCount',
        flex: 0.6,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
    {
        field: 'aplicacioCount',
        flex: 0.6,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        align: 'center',
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
];

const EntitatGridFilter: React.FC = () => {
    const springFilterBuilder = (_data: any) => {
        return '';
    };
    const filterApiRef = useFilterApiRef();
    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <MuiFilter
            resourceName="entitatResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            apiRef={filterApiRef}
            componentProps={{ sx: { mb: 2 } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <Grid container spacing={2}>
                <Grid size={4}>
                    <FormField name="codi" />
                </Grid>
                <Grid size={4}>
                    <FormField name="nom" />
                </Grid>
                <Grid size={4}>
                    <Button onClick={handleButtonClick}>Netejar</Button>
                </Grid>
            </Grid>
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
