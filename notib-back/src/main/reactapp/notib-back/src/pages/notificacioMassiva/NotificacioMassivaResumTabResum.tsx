import { TableCell, TableHead, TableRow } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage } from 'reactlib';
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import CancelIcon from "@mui/icons-material/Cancel";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";


interface PropsTabResum {
    notificacio: any;
}

const NotificacioMassivaResumDialogTabResum: React.FC<PropsTabResum> = (props) => {

    const { notificacio } = props;
    notificacio?.resum.forEach((obj, i) => obj.id = i + 1);
    const { t } = useTranslation();
    const columns = [
        { field: "enviamentTipus", headerName: t('page.notificacioMassiva.detall.resum.enviamentTipus') },
        { field: "codiDir3UnidadRemisora", headerName: t('page.notificacioMassiva.detall.resum.codiDir3UnidadRemisora') },
        { field: "concepto", headerName: t('page.notificacioMassiva.detall.resum.concepto') },
        { field: "descripcio", headerName: t('page.notificacioMassiva.detall.resum.descripcio') },
        { field: "prioridadServicio", headerName: t('page.notificacioMassiva.detall.resum.prioridadServicio') },
        { field: "nomComplert", headerName: t('page.notificacioMassiva.detall.resum.nomComplert') },
        { field: "cancelada", headerName: t('page.notificacioMassiva.detall.resum.cancelada') },
        { field: "errores", headerName: t('page.notificacioMassiva.detall.resum.errores') },
        { field: "errorsExecucio", headerName: t('page.notificacioMassiva.detall.resum.errorsExecucio') },
    ];

    return (
        <GridPage disableMargins={false}>
            <Table size="small" aria-label="simple table">
                <TableHead>
                    <TableRow>
                        {columns.map(c => <TableCell key={c.field}>{c.headerName ?? c.field}</TableCell>)}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {notificacio?.resum.length === 0 ? (
                        <TableRow>
                            <TableCell colSpan={columns.length} align="center">t('page.notificacioMassiva.detall.resum.noRows')</TableCell>
                        </TableRow>
                    ) : (
                        notificacio?.resum.map(row => (
                            <TableRow key={row.id} hover>
                                {columns.map(c => {
                                    let valor = row[c.field];
                                    if (c.field === 'nomComplert' && !row.cifNif && row.email) {
                                        valor += t('page.notificacioMassiva.detall.resum.interssatSenseNif');
                                    }
                                    if (c.field === 'cancelada' && row.cancelada) {
                                        valor = <CheckCircleIcon color="success" fontSize="small" />;
                                    }
                                    return  (<TableCell key={c.field}>{valor ?? ""}</TableCell>);
                                })}
                            </TableRow>
                        ))
                    )}
                </TableBody>
            </Table>
        </GridPage>
    );
};

export default NotificacioMassivaResumDialogTabResum;
