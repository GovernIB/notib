import React from 'react';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const EnviamentDetailTabHistoric: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'tipusOperacio',
                flex: 1,
            },
            {
                field: 'joinPoint',
                flex: 3,
            },
            {
                field: 'createdBy',
                flex: 1,
            },
            {
                field: 'createdDate',
                flex: 1,
            },
            {
                field: 'titularId',
                flex: 1,
            },
            {
                field: 'destinataris',
                flex: 1,
            },
            {
                field: 'domiciliTipus',
                flex: 1,
            },
            {
                field: 'domicili',
                flex: 1,
            },
            {
                field: 'serveiTipus',
                flex: 1,
            },
            {
                field: 'cie',
                flex: 1,
            },
            {
                field: 'formatSobre',
                flex: 1,
            },
            {
                field: 'formatFulla',
                flex: 1,
            },
            {
                field: 'dehObligat',
                flex: 1,
            },
            {
                field: 'dehNif',
                flex: 1,
            },
            {
                field: 'notificaReferencia',
                flex: 1,
            },
            {
                field: 'notificaIdentificador',
                flex: 1,
            },
            {
                field: 'notificaDataCreacio',
                flex: 1,
            },
            {
                field: 'notificaDataDisposicio',
                flex: 1,
            },
            {
                field: 'notificaDataCaducitat',
                flex: 1,
            },
            {
                field: 'notificaEmisorDir3',
                flex: 1,
            },
            {
                field: 'notificaArrelDir3',
                flex: 1,
            },
            {
                field: 'notificaEstat',
                flex: 1,
            },
            {
                field: 'notificaEstatData',
                flex: 1,
            },
            {
                field: 'notificaEstatFinal',
                flex: 1,
            },
            {
                field: 'notificaDatatOrigen',
                flex: 1,
            },
            {
                field: 'notificaDatatReceptorNif',
                flex: 1,
            },
            {
                field: 'notificaDatatNumSeguiment',
                flex: 1,
            },
            {
                field: 'notificaCertificacioData',
                flex: 1,
            },
            {
                field: 'notificaCertificacioArxiuId',
                flex: 1,
            },
            {
                field: 'notificaCertificacioOrigen',
                flex: 1,
            },
            {
                field: 'notificaCertificacioTipus',
                flex: 1,
            },
            {
                field: 'notificaCertificacioArxiuTipus',
                flex: 1,
            },
            {
                field: 'notificaCertificacioNumSeguiment',
                flex: 1,
            },
            {
                field: 'registreNumeroFormatat',
                flex: 1,
            },
            {
                field: 'registreData',
                flex: 1,
            },
            {
                field: 'registreEstat',
                flex: 1,
            },
            {
                field: 'registreEstatFinal',
                flex: 1,
            },
            {
                field: 'sirConsultaData',
                flex: 1,
            },
            {
                field: 'sirRecepcioData',
                flex: 1,
            },
            {
                field: 'sirRegDestiData',
                flex: 1,
            },
            {
                field: 'notificacioErrorEvent',
                flex: 1,
            },
            {
                field: 'notificaError',
                flex: 1,
            },
            {
                field: 'notificaDatatErrorDescripcio',
                flex: 1,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=''
                resourceName="notificacioEnviamentAuditResource"
                fixedFilter={'enviamentId:' + id}
                columns={columns}
                readOnly
                toolbarHideQuickFilter
            />
        </GridPage>
    );
};

export default EnviamentDetailTabHistoric;
