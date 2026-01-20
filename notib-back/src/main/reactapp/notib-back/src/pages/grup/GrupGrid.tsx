import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef
} from 'reactlib';

export const GrupGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] =  React.useMemo(() => [{
        field: 'nom',
        flex: 4,
    }, {
        field: 'rol',
        flex: 4,
    }], []);
    return <GridPage disableMargins={false}>
        <MuiDataGrid
            title={t('page.grups.grid.title')}
            resourceName="grupResource"
            columns={columns}
            paginationActive
            toolbarBulkDelete
            toolbarCreateLink="form"
            //rowLink="form/{{id}}"
            rowUpdateLink="form/{{id}}" />
    </GridPage>;
};

export default GrupGrid;