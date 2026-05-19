import React from 'react';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const EnviamentDetailTabHistoric: React.FC<{ id: any }> = (props) => {
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
                field: 'titularId',
                width: 120,
            },
            {
                field: 'destinataris',
                width: 120,
            },
            {
                field: 'domiciliTipus',
                width: 120,
            },
            {
                field: 'domicili',
                width: 120,
            },
            {
                field: 'serveiTipus',
                width: 120,
            },
            {
                field: 'cie',
                width: 120,
            },
            {
                field: 'formatSobre',
                width: 120,
            },
            {
                field: 'formatFulla',
                width: 120,
            },
            {
                field: 'dehObligat',
                width: 120,
            },
            {
                field: 'dehNif',
                width: 120,
            },
            {
                field: 'notificaReferencia',
                width: 120,
            },
            {
                field: 'notificaIdentificador',
                width: 120,
            },
            {
                field: 'notificaDataCreacio',
                width: 150,
            },
            {
                field: 'notificaDataDisposicio',
                width: 150,
            },
            {
                field: 'notificaDataCaducitat',
                width: 150,
            },
            {
                field: 'notificaEmisorDir3',
                width: 120,
            },
            {
                field: 'notificaArrelDir3',
                width: 120,
            },
            {
                field: 'notificaEstat',
                width: 120,
            },
            {
                field: 'notificaEstatData',
                width: 150,
            },
            {
                field: 'notificaEstatFinal',
                width: 120,
            },
            {
                field: 'notificaDatatOrigen',
                width: 150,
            },
            {
                field: 'notificaDatatReceptorNif',
                width: 120,
            },
            {
                field: 'notificaDatatNumSeguiment',
                width: 150,
            },
            {
                field: 'notificaCertificacioData',
                width: 120,
            },
            {
                field: 'notificaCertificacioArxiuId',
                width: 120,
            },
            {
                field: 'notificaCertificacioOrigen',
                width: 120,
            },
            {
                field: 'notificaCertificacioTipus',
                width: 120,
            },
            {
                field: 'notificaCertificacioArxiuTipus',
                width: 120,
            },
            {
                field: 'notificaCertificacioNumSeguiment',
                width: 120,
            },
            {
                field: 'registreNumeroFormatat',
                width: 120,
            },
            {
                field: 'registreData',
                width: 150,
            },
            {
                field: 'registreEstat',
                width: 120,
            },
            {
                field: 'registreEstatFinal',
                width: 120,
            },
            {
                field: 'sirConsultaData',
                width: 150,
            },
            {
                field: 'sirRecepcioData',
                width: 150,
            },
            {
                field: 'sirRegDestiData',
                width: 150,
            },
            {
                field: 'notificacioErrorEvent',
                width: 120,
            },
            {
                field: 'notificaError',
                width: 120,
            },
            {
                field: 'notificaDatatErrorDescripcio',
                width: 120,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=""
                resourceName="notificacioEnviamentAuditResource"
                fixedFilter={'enviamentId:' + id}
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

export default EnviamentDetailTabHistoric;
