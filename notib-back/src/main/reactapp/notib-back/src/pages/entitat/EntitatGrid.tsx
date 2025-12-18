import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid } from 'reactlib';

export const EntitatGrid = () => {
    const { t } = useTranslation();
    const columns =  React.useMemo(() => [{
        field: 'codi',
        flex: 1,
    }, {
        field: 'nom',
        flex: 4,
    }, {
        field: 'dir3Codi',
        flex: 1,
    },  {
        field: 'activa',
        flex: .6,
    }], []);
    return <GridPage disableMargins={false}>
        <MuiDataGrid
            title={t('page.entitats.grid.title')}
            resourceName="entitatResource"
            columns={columns}
            paginationActive
            toolbarCreateLink="form"
            rowLink="form/{{id}}"
            rowUpdateLink="form/{{id}}" />
    </GridPage>;
};

export default EntitatGrid;