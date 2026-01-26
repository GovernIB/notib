import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

export const EntitatGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
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
        ],
        []
    );
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default EntitatGrid;
