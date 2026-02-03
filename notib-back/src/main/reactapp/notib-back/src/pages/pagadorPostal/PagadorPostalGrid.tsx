import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

export const PagadorPostalGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'nom',
                flex: 4,
            },
            {
                field: 'codi',
                flex: 4,
            },
        ],
        []
    );
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.pagador.postal.grid.title')}
                resourceName="pagadorPostalResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                //rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default PagadorPostalGrid;
