import React from 'react';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import {MuiActionReportButton, useResourceApiService} from 'reactlib';
import { useEnviamentDetailDialog } from '../enviament/EnviamentDetailDialog';
import {Box, Tooltip} from "@mui/material";
import WarningIcon from "@mui/icons-material/Warning";
import ErrorIcon from "@mui/icons-material/Error";
import PhoneIphoneIcon from "@mui/icons-material/PhoneIphone";

export const NotificacioGridEnviaments: React.FC<{ id: any }> = (props) => {

    const { id } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('notificacioEnviamentResource');
    const { dialogComponent, onDetailClick } = useEnviamentDetailDialog();
    const [enviaments, setEnviaments] = React.useState<any[]>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const args = {filter: 'notificacio.id:' + id, unpaged: true, perspectives: ["ULTIM_EVENT", "ENTREGA_POSTAL"]};
        apiFind(args).then((response) => setEnviaments(response.rows));
    }, [apiIsReady]);

    // WEB-INF/jsp/includes/notificacioList.jsp:283

    return (
        enviaments != null && (
            <TableContainer component={Paper} elevation={2} sx={{mx: 2, my: 2, width: 'calc(100% - 32px)',}}>
                <Table size="small" aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>{t('page.notificacio.grid.enviament.column.interessat')}</TableCell>
                            <TableCell>{t('page.notificacio.grid.enviament.column.representant.title')}</TableCell>
                            <TableCell>{t('page.notificacio.grid.enviament.column.estatPostal.title')}</TableCell>
                            <TableCell>{t('page.notificacio.grid.enviament.column.estatTelematica')}</TableCell>
                            <TableCell></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {enviaments.map((enviament) => (
                            <TableRow key={enviament.id}>
                                <TableCell component="th" scope="row" sx={{ width: '50%'}}>
                                    {enviament.titular.description}
                                </TableCell>
                                <TableCell component="th" scope="row">
                                    {enviament.titular.representant ? enviament.titular.representant : t('page.notificacio.grid.enviament.column.representant.senseRepresentant')}
                                </TableCell>
                                <TableCell component="th" scope="row">
                                    <Box sx={{display: 'flex', alignItems: 'center', gap: 0.5, flexWrap: 'wrap'}}>

                                        {   enviament.entregaPostalInfo && !enviament.cieExtern ? t('page.notificacio.grid.enviament.column.estatPostal.cieNotifica') :
                                            enviament.entregaPostalInfo?.cieEstat ? enviament.entregaPostalInfo?.cieEstat : t('page.notificacio.grid.enviament.column.estatPostal.senseCie')
                                        }
                                        {enviament?.ultimEventInfo?.ultimEventCie && (<>
                                            {enviament?.ultimEventInfo?.error && (
                                                <Tooltip title={enviament.ultimEventInfo.errorDescripcio} arrow>
                                                    <WarningIcon color="error" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}
                                            {enviament?.ultimEventInfo?.fiReintents && (
                                                <Tooltip title={enviament?.ultimEventInfo?.fiReintentsDesc} arrow>
                                                    <WarningIcon color="warning" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}
                                        </>)}
                                    </Box>
                                </TableCell>
                                <TableCell component="th" scope="row" sx={{borderLeft: "1.5px solid " + enviament.estatColor}}>
                                    <Box sx={{display: 'flex', alignItems: 'center', gap: 0.5, flexWrap: 'wrap'}}>
                                        {t('utils.estatConfig.ENVIAMENT_ESTAT_MAP.' + enviament.notificaEstat)}
                                        {!enviament?.ultimEventInfo?.ultimEventCie && (<>
                                            {enviament?.ultimEventInfo?.error && (
                                                <Tooltip title={enviament.ultimEventInfo.errorDescripcio} arrow>
                                                    <WarningIcon color="error" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}
                                            {enviament?.ultimEventInfo?.fiReintents && (
                                                <Tooltip title={enviament?.ultimEventInfo?.fiReintentsDesc} arrow>
                                                    <WarningIcon color="warning" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}
                                            {/*{notificacio?.tipusUsuari === 'APLICACIO' && enviament?.ultimEventInfo?.errorLastCallback && (*/}
                                            {enviament?.ultimEventInfo?.errorLastCallback && (
                                                <Tooltip title={t('page.notificacio.detail.dades.errorCanviEstat')} arrow>
                                                    <ErrorIcon color="primary" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}

                                            {/* Fi de Reintents de Callback */}
                                            {enviament?.ultimEventInfo?.callbackFiReintents && (
                                                <Tooltip title={enviament?.ultimEventInfo?.callbackFiReintentsDesc} arrow>
                                                    <WarningIcon color="info" sx={{ fontSize: '16px' }} />
                                                </Tooltip>
                                            )}

                                            {/* Errors de dispositius mòbils */}
                                            {enviament?.ultimEventInfo?.notificacionsMovilErrorDesc?.map((errorText: string, id: number) => (
                                                <Tooltip key={id} title={errorText} arrow>
                                                    <PhoneIphoneIcon sx={{fontSize: '18px', color: (theme) => theme.palette.mode === 'dark' ? '#f5c777' : '#8a6d3b'}}/>
                                                </Tooltip>
                                            ))}
                                        </>)}
                                    </Box>
                                </TableCell>
                                <TableCell component="th" scope="row" sx={{ width: 'fit-content', display:'flex' }}>
                                    { enviament.notificaCertificacioData &&(<MuiActionReportButton
                                        id={enviament?.id}
                                        resourceName={"notificacioEnviamentResource"}
                                        report="DESCARREGAR_CERTIFICACIO_ENVIAMENT"
                                        reportFileType="CUSTOM"
                                        title={t('page.notificacio.detail.dades.enviaments.registre.certificacio')}
                                        buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                        buttonIcon="file_download"/>)}
                                    <Button variant="outlined"
                                        size="small"
                                        startIcon={<Icon>info</Icon>}
                                        onClick={() => onDetailClick(enviament.id)}>
                                        Detalls
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                {dialogComponent}
            </TableContainer>
        )
    );
};

export default NotificacioGridEnviaments;
