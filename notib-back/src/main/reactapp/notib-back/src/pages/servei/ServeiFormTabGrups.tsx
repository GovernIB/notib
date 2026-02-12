import React from 'react';
import { useTranslation } from 'react-i18next';
import Table from '@mui/material/Table';
import TableHead from '@mui/material/TableHead';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import Paper from '@mui/material/Paper';
import Switch from '@mui/material/Switch';
import { useFormContext, useResourceApiService, useBaseAppContext } from 'reactlib';

const ServeiFormTabGrups: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const {
        isReady: procedimentGrupApiIsReady,
        find: procedimentGrupApiFind,
        create: procedimentGrupApiCreate,
        delete: procedimentGrupApiDelete,
    } = useResourceApiService('procedimentGrupResource');
    const { isReady: grupApiIsReady, find: grupApiFind } = useResourceApiService('grupResource');
    const { temporalMessageShow } = useBaseAppContext();
    const [grupRows, setGrupRows] = React.useState<any[]>();
    const [procedimentGrupRows, setProcedimentGrupRows] = React.useState<any[]>();
    const refreshProcedimentGrupRows = () => {
        const args = {
            filter: 'procediment.id:' + id,
            unpaged: true,
        };
        procedimentGrupApiFind(args).then((response) => {
            setProcedimentGrupRows(response.rows);
        });
    };
    React.useEffect(() => {
        if (procedimentGrupApiIsReady) {
            refreshProcedimentGrupRows();
        }
    }, [procedimentGrupApiIsReady]);
    React.useEffect(() => {
        if (grupApiIsReady) {
            const args = {
                unpaged: true,
            };
            grupApiFind(args).then((response) => {
                setGrupRows(response.rows);
            });
        }
    }, [grupApiIsReady]);
    const hanldleSwitchOnChange = (grupId: any, checked: boolean) => {
        const found = procedimentGrupRows?.find((r) => r.grup.id === grupId);
        if (checked) {
            if (!found) {
                const data = {
                    procediment: { id },
                    grup: { id: grupId },
                };
                procedimentGrupApiCreate({ data })
                    .then(() => {
                        refreshProcedimentGrupRows();
                        formApiRef.current?.refresh();
                        temporalMessageShow(
                            null,
                            t('page.serveis.form.grups.enable.success'),
                            'success'
                        );
                    })
                    .catch((error) => {
                        temporalMessageShow(
                            t('page.serveis.form.grups.enable.error'),
                            error.message,
                            'error'
                        );
                    });
            }
        } else {
            if (found) {
                procedimentGrupApiDelete(found.id)
                    .then(() => {
                        refreshProcedimentGrupRows();
                        formApiRef.current?.refresh();
                        temporalMessageShow(
                            null,
                            t('page.serveis.form.grups.disable.success'),
                            'success'
                        );
                    })
                    .catch((error) => {
                        temporalMessageShow(
                            t('page.serveis.form.grups.disable.error'),
                            error.message,
                            'error'
                        );
                    });
            }
        }
    };
    return (
        grupRows &&
        procedimentGrupRows && (
            <TableContainer component={Paper} variant="outlined">
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>{t('page.serveis.form.grups.tableColumn.grup')}</TableCell>
                            <TableCell align="right">
                                {t('page.serveis.form.grups.tableColumn.actiu')}
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {grupRows.map((gr) => {
                            const checked = procedimentGrupRows.some(
                                (pgr) => pgr.grup.id === gr.id
                            );
                            return (
                                <TableRow
                                    key={gr.id}
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                    <TableCell component="th" scope="row">
                                        {gr.nom}
                                    </TableCell>
                                    <TableCell align="right">
                                        <Switch
                                            size="small"
                                            defaultChecked={checked}
                                            onChange={(event) =>
                                                hanldleSwitchOnChange(gr.id, event.target.checked)
                                            }
                                        />
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </TableContainer>
        )
    );
};

export default ServeiFormTabGrups;
