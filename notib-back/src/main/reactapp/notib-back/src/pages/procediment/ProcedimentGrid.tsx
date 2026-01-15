import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef
} from 'reactlib';

export const ProcedimentGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] =  React.useMemo(() => [{
        field: 'codi',
        flex: 1,
    }, {
        field: 'nom',
        flex: 4,
    }, {
        field: 'organNom',
        flex: 1,
    },  {
        field: 'retard',
        flex: .6,
    }, {
        field: 'caducitat',
        flex: .6,
    }, {
        field: 'cie',
        flex: .6
    }, {
        field: 'comu',
        flex: .6
    }, {
        field: 'requireDirectPermission',
        flex: .6
    }, {
        field: 'actiu',
        flex: .6
    }, {
        field: 'manual',
        flex: .6
    }, {
        field: 'grupsCount',
        flex: .6,
        align: 'center',
        renderCell: (params: any) => {
            return <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small" />;
        }
    } , {
        field: 'aclEntryCount',
        flex: .6,
        align: 'center',
        renderCell: (params: any) => {
            return <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small" />;
        }
    }], []);
    return <GridPage disableMargins={false}>
        <MuiDataGrid
            title={t('page.procediment.grid.title')}
            resourceName="entitatResource"
            columns={columns}
            paginationActive
            toolbarCreateLink="form"
            rowLink="form/{{id}}"
            rowUpdateLink="form/{{id}}" />
    </GridPage>;
};

export default ProcedimentGrid;