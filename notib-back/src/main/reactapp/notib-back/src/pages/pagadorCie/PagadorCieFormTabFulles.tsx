import React from 'react';
import { MuiDataGrid, useFormContext } from 'reactlib';

const columns = [
    {
        field: 'codi',
        sortable: false,
        flex: 1,
    },
];

const PagadorCieFormTabFulles: React.FC = () => {
    const { id, apiRef: formApiRef } = useFormContext();
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            title=""
            resourceName="pagadorCieFormatFullaResource"
            staticFilter={'pagadorCie.id:' + id}
            formAdditionalData={{ pagadorCie: { id } }}
            columns={columns}
            paginationActive
            toolbarHideQuickFilter
            inlineEditActive
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default PagadorCieFormTabFulles;
