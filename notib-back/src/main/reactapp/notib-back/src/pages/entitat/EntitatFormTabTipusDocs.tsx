import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Table from '@mui/material/Table';
import TableHead from '@mui/material/TableHead';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import Paper from '@mui/material/Paper';
import Switch from '@mui/material/Switch';
import { useBaseAppContext, useFormContext, useResourceApiService } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const EntitatFormTabTipusDocs: React.FC = () => {
    const { t } = useTranslation();
    const { id, fields, apiRef: formApiRef } = useFormContext();
    const {
        isReady: apiIsReady,
        find: apiFind,
        create: apiCreate,
        delete: apiDelete,
    } = useResourceApiService('entitatTipusDocumentResource');
    const { temporalMessageShow } = useBaseAppContext();
    const [tipusDocumentOptions, setTipusDocumentOptions] = React.useState<any>();
    const [entitatTipusDocumentRows, setEntitatTipusDocumentRows] = React.useState<any[]>();
    const [hiddenEnumValues, setHiddenEnumValues] = React.useState<string[]>();
    const refreshRows = () => {
        const args = {
            filter: 'entitat.id:' + id,
            unpaged: true,
        };
        apiFind(args).then((response) => {
            setEntitatTipusDocumentRows(response.rows);
        });
    };
    React.useEffect(() => {
        if (fields?.length) {
            const tipusDocDefaultField = fields.find((f) => f.name === 'tipusDocDefault');
            tipusDocDefaultField && setTipusDocumentOptions(tipusDocDefaultField.options);
        }
    }, [fields]);
    React.useEffect(() => {
        if (apiIsReady) {
            refreshRows();
        }
    }, [apiIsReady]);
    React.useEffect(() => {
        if (tipusDocumentOptions) {
            const hiddenEnumValues = Object.keys(tipusDocumentOptions).filter((k) => {
                return entitatTipusDocumentRows?.find((r) => r.tipusDocument === k) == null;
            });
            setHiddenEnumValues(hiddenEnumValues);
        }
    }, [tipusDocumentOptions, entitatTipusDocumentRows]);
    const hanldleSwitchOnChange = (key: string, checked: boolean) => {
        const found = entitatTipusDocumentRows?.find((r) => r.tipusDocument === key);
        if (checked) {
            if (!found) {
                const data = {
                    entitat: { id },
                    tipusDocument: key,
                };
                apiCreate({ data })
                    .then(() => {
                        refreshRows();
                        formApiRef.current?.refresh();
                        temporalMessageShow(
                            null,
                            t('page.entitats.form.tipusDocuments.enable.success'),
                            'success'
                        );
                    })
                    .catch((error) => {
                        temporalMessageShow(
                            t('page.entitats.form.tipusDocuments.enable.error'),
                            error.message,
                            'error'
                        );
                    });
            }
        } else {
            if (found) {
                apiDelete(found.id)
                    .then(() => {
                        refreshRows();
                        formApiRef.current?.refresh();
                        temporalMessageShow(
                            null,
                            t('page.entitats.form.tipusDocuments.disable.success'),
                            'success'
                        );
                    })
                    .catch((error) => {
                        temporalMessageShow(
                            t('page.entitats.form.tipusDocuments.disable.error'),
                            error.message,
                            'error'
                        );
                    });
            }
        }
    };
    return (
        <>
            <Grid container spacing={2} sx={{ mb: 2 }}>
                <GridFormField
                    size={4}
                    name="tipusDocDefault"
                    hiddenEnumValues={hiddenEnumValues}
                />
            </Grid>
            {tipusDocumentOptions && entitatTipusDocumentRows && (
                <TableContainer component={Paper} variant="outlined">
                    <Table sx={{ minWidth: 650 }} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Tipus de document</TableCell>
                                <TableCell align="right">Actiu</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {Object.entries(tipusDocumentOptions).map(([key, value]) => {
                                const checked =
                                    entitatTipusDocumentRows.find((r) => r.tipusDocument === key) !=
                                    null;
                                return (
                                    <TableRow
                                        key={key}
                                        sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                        <TableCell component="th" scope="row">
                                            {value as string}
                                        </TableCell>
                                        <TableCell align="right">
                                            <Switch
                                                size="small"
                                                defaultChecked={checked}
                                                onChange={(event) =>
                                                    hanldleSwitchOnChange(key, event.target.checked)
                                                }
                                            />
                                        </TableCell>
                                    </TableRow>
                                );
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </>
    );
};

export default EntitatFormTabTipusDocs;
