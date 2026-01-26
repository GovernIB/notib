import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

export const AvisGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'entitat',
                flex: 2,
            },
            {
                field: 'assumpte',
                flex: 4,
            },
            {
                field: 'dataInici',
                fieldType: 'date',
                flex: 1,
            },
            {
                field: 'dataFinal',
                fieldType: 'date',
                flex: 1,
            },
            {
                field: 'avisNivell',
                flex: 0.6,
            },
            {
                field: 'actiu',
                flex: 0.6,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.avisos.grid.title')}
                resourceName="avisResource"
                columns={columns}
                paginationActive
                toolbarBulkDelete
                toolbarCreateLink="form"
                //rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default AvisGrid;
