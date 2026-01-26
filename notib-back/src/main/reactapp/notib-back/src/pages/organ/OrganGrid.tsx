import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

export const OrganGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'codi',
                flex: 2,
            },
            {
                field: 'nom',
                flex: 6,
            },
            {
                field: 'codiPare',
                flex: 2,
            },
            {
                field: 'nomPare',
                flex: 6,
            },
            {
                field: 'llibre',
                flex: 4,
            },
            {
                field: 'estat',
                flex: 2,
            },
            {
                field: 'entregaCieActiva',
                flex: 2,
            },
            {
                field: 'permetreSir',
                flex: 2,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.organs.grid.title')}
                resourceName="organGestorResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default OrganGrid;
