import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
} from 'reactlib';

export const ActiveMqGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'nom',
                flex: 1.5
            },
            {
                field: 'descripcio',
                flex: 4,
            },
            {
                field: 'mida',
                flex: 1,
            },
            {
                field: 'enqueueCount',
                flex: 1,
            },
            {
                field: 'dequeueCount',
                flex: 1,
            },
            {
                field: 'forwardCount',
                flex: 1,
            },
            {
                field: 'inFlightCount',
                flex: 1,
            },
            {
                field: 'expiredCount',
                flex: 1,
            },
            {
                field: 'storeMessageSize',
                flex: 1,
            }

        ],
        []
    );

    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.activemq.grid.title')}
                resourceName="activeMqResource"
                columns={columns}
                // paginationActive
                toolbarType="upper"
                toolbarBulkDelete
                toolbarHideQuickFilter
            />
        </GridPage>
    );
};

export default ActiveMqGrid;
