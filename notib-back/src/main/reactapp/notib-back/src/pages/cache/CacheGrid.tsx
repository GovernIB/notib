import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
} from 'reactlib';

export const CacheGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'codi',
                flex: 2,
            },
            {
                field: 'descripcio',
                flex: 4,
            },
            {
                field: 'localHeapSize',
                flex: 1,
            }
        ],
        []
    );
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.cache.grid.title')}
                resourceName="cacheResource"
                columns={columns}
                toolbarBulkDelete
                toolbarHideQuickFilter
                toolbarType="upper"
            />
        </GridPage>
    );
};

export default CacheGrid;
