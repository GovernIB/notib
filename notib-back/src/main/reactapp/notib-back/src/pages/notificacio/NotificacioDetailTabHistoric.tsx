import React from 'react';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const NotificacioDetailTabHistoric: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'tipusOperacio',
                width: 120,
            },
            {
                field: 'joinPoint',
                width: 150,
            },
            {
                field: 'createdBy',
                width: 120,
            },
            {
                field: 'createdDate',
                width: 150,
            },
            {
                field: 'notificacioId',
                width: 120,
            },
            {
                field: 'comunicacioTipus',
                width: 130,
            },
            {
                field: 'tipusUsuari',
                width: 120,
            },
            {
                field: 'usuari',
                width: 100,
            },
            {
                field: 'emisor',
                width: 120,
            },
            {
                field: 'tipus',
                width: 120,
            },
            {
                field: 'entitatId',
                width: 100,
            },
            {
                field: 'organ',
                width: 120,
            },
            {
                field: 'procediment',
                width: 120,
            },
            {
                field: 'grup',
                width: 100,
            },
            {
                field: 'concepte',
                width: 160,
            },
            {
                field: 'descripcio',
                width: 160,
            },
            {
                field: 'numExpedient',
                width: 120,
            },
            {
                field: 'enviamentDataProgramada',
                width: 150,
            },
            {
                field: 'retard',
                width: 100,
            },
            {
                field: 'caducitat',
                width: 150,
            },
            {
                field: 'documentId',
                width: 120,
            },
            {
                field: 'estat',
                width: 100,
            },
            {
                field: 'estatDate',
                width: 150,
            },
            {
                field: 'estatProcessatDate',
                width: 150,
            },
            {
                field: 'motiu',
                width: 150,
            },
            {
                field: 'registreEnviamentIntent',
                width: 100,
            },
            {
                field: 'registreNumero',
                width: 100,
            },
            {
                field: 'registreNumeroFormatat',
                width: 100,
            },
            {
                field: 'registreData',
                width: 150,
            },
            {
                field: 'notificaEnviamentData',
                width: 150,
            },
            {
                field: 'notificaEnviamentIntent',
                width: 100,
            },
            {
                field: 'errorLastCallback',
                width: 120,
            },
            {
                field: 'errorEventId',
                width: 120,
            },
            {
                field: 'referencia',
                width: 150,
            },
        ],
        []
    );

    return (
        <GridPage>
            <MuiDataGrid
                title=""
                resourceName="notificacioAuditResource"
                fixedFilter={'notificacioId:' + id}
                sortModel={[{ field: 'tipusOperacio', sort: 'desc' }]}
                columns={columns}
                readOnly
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                paginationActive
                {...pageSizeOptionsDataGridProps}
            />
        </GridPage>
    );
};

export default NotificacioDetailTabHistoric;
