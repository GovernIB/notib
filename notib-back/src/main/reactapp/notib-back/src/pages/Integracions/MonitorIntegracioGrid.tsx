import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

export const MonitorIntegracioGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'data',
                flex: 2,
            },
            {
                field: 'descripcio',
                flex: 4,
            },
            {
                field: 'aplicacio',
                flex: 2,
            },
            {
                field: 'tipus',
                flex: 2,
            },
            {
                field: 'entitat',
                flex: 2,
            },
            {
                field: 'tempsResposta',
                flex: 1,
            },
            {
                field: 'estat',
                flex: 1,
            }
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.integracions.grid.title')}
                resourceName="monitorIntegracioResource"
                columns={columns}
                paginationActive
                toolbarBulkDelete
                rowLink="detail/{{id}}"
            />
        </GridPage>
    );
};

export default MonitorIntegracioGrid;
