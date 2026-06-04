import React from "react";
import Box from "@mui/material/Box";
import {useTranslation} from "react-i18next";
import {useResourceApiService} from "reactlib";
import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import Icon from "@mui/material/Icon";
import TableBody from "@mui/material/TableBody";
import {formatDate} from "../../utils/dateUtils.ts";
import Typography from "@mui/material/Typography";
import {Link} from "react-router-dom";

export const AccioMassivaGridEnviaments: React.FC<{ id: any, tipusElementSeleccionat: any }> = (props) => {

    const { id, tipusElementSeleccionat } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('accioMassivaElementResource');
    const [elements, setElements] = React.useState<any[]>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const args = {filter: 'accioMassiva.id:' + id, unpaged: true,};
        apiFind(args).then(response => setElements(response.rows));
    }, [apiIsReady]);
    const ruta = tipusElementSeleccionat === 'ENVIAMENT' ? 'enviaments' : 'notificacions'

    return (elements && (
            <TableContainer component={Paper} elevation={2} sx={{mx: 2, my: 2, width: 'calc(100% - 32px)'}}>
                <Box display="flex" alignItems="center" justifyContent="space-between" p={2}>
                    <Typography variant="h6">{t('page.accioMassiva.grid.elements.titol')}</Typography>
                    {/* optional actions: buttons, filters */}
                </Box>
                <Table size="small" aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                {t('page.accioMassiva.grid.elements.referencia')}
                            </TableCell>
                            <TableCell>
                                {t('page.accioMassiva.grid.elements.data')}
                            </TableCell>
                            <TableCell>
                                {t('page.accioMassiva.grid.elements.estat')}
                            </TableCell>
                            <TableCell>
                                {t('page.accioMassiva.grid.elements.errorDesc')}
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {elements.map((element) => (
                            <TableRow key={element.id}>
                                <TableCell component="th" scope="row">
                                    <Link to={`/${ruta}?referencia=${element.referencia}`} color="inherit" target="_blank">
                                        {element.referencia}
                                    </Link>
                                </TableCell>

                                <TableCell component="th" scope="row">{formatDate(element.dataExecucio)}</TableCell>
                                <TableCell component="th" scope="row">
                                    {element.pendent ?
                                        (<>
                                            <Icon sx={{fontSize:'small', verticalAlign: 'middle', mr: 0.5 }}>access_time</Icon>
                                            {t('page.accioMassiva.grid.elements.estatPendent')}
                                        </>)
                                        : element.executadaOk ?
                                            (<>
                                                <Icon sx={{fontSize:'small', verticalAlign: 'middle', mr: 0.5 }}>check_circle</Icon>
                                                {t('page.accioMassiva.grid.elements.estatFinalitzat')}
                                            </>)
                                            : (<>
                                                <Icon sx={{fontSize:'small', verticalAlign: 'middle', mr: 0.5 }}>cancel</Icon>
                                                {t('page.accioMassiva.grid.elements.estatError')}
                                            </>)
                                    }
                                </TableCell>
                                <TableCell component="th" scope="row">{element.errorDescripcio}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        )
     );
}

export default AccioMassivaGridEnviaments;
