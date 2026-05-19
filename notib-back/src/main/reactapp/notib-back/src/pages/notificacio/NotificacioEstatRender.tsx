import React from 'react';
import { Box, Chip, Tooltip, Typography } from '@mui/material';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import BlockIcon from '@mui/icons-material/Block';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import { ESTAT_ENUM_MUI_MAP } from '../../utils/estatConfig';

export type NotificacioEstatRenderProps = {
    estatObjecte: any;
    estatEnum: string;
};

const NotificacioEstatRender: React.FC<NotificacioEstatRenderProps> = (props) => {
    const { estatObjecte, estatEnum } = props;
    const configEstat = ESTAT_ENUM_MUI_MAP[estatEnum];

    // console.log(estatObjecte);

    if (!estatObjecte) return null;

    return (
        <Box
            sx={{
                width: '100%',
                display: 'flex',
                flexDirection: 'column',
                p: 0.5,
            }}
        >
            {/* Gestió d'Entrega Postal */}
            {estatObjecte?.entregaPostal && (
                <Tooltip title={estatObjecte.entregaPostal.title} arrow>
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            alignSelf: 'flex-end',
                            width: '30px',
                            height: '20px',
                            backgroundColor: estatObjecte.entregaPostal.label.includes('danger')
                                ? '#d9534f'
                                : '#5bc0de',
                            borderRadius: '4px',
                        }}
                    >
                        <MailOutlineIcon style={{ fontSize: '1rem', color: 'white' }} />
                    </Box>
                </Tooltip>
            )}
            {/* Registre d'Estats  */}
            {estatObjecte?.registreEstat && estatObjecte.registreEstat.length > 0 && (
                <Box sx={{ display: 'flex', gap: 0.5 }}>
                    {estatObjecte.registreEstat.map((registre: any, index: number) => (
                        <Tooltip key={index} title={registre.title} arrow>
                            <Chip
                                label={registre?.label}
                                size="small"
                                sx={{
                                    height: '20px',
                                    backgroundColor: registre?.backgroundColor,
                                    color: 'white',
                                    borderRadius: '4px',
                                    fontSize: '11px',
                                    px: '5px',
                                    '& .MuiChip-label': { padding: 0 },
                                }}
                            />
                        </Tooltip>
                    ))}
                </Box>
            )}

            {/* L'Estat Principal i l'Anul·lació */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                {!estatObjecte?.anulat ? (
                    <>
                        {configEstat && (
                            <Box
                                component="span"
                                sx={{
                                    display: 'inline-flex',
                                    alignItems: 'center',
                                    color: configEstat.color,
                                }}
                            >
                                {configEstat.icona}
                            </Box>
                        )}
                        <Typography variant="body2" sx={{ fontWeight: 500 }}>
                            {estatObjecte?.nomEstat}
                        </Typography>
                    </>
                ) : (
                    <>
                        <Tooltip title={estatObjecte.anulat} arrow>
                            <BlockIcon color="error" sx={{ fontSize: '16px' }} />
                        </Tooltip>
                        <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                            {estatObjecte?.nomEstat}
                        </Typography>
                    </>
                )}
            </Box>

            {/* Gestió d'Errors i Avisos */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                {/* Errors crítics (Acumulats) */}
                {estatObjecte?.eventError?.error && estatObjecte.eventError.error.length > 0 && (
                    <Tooltip
                        title={estatObjecte.eventError.error
                            .map((err: any) => err.title)
                            .join(' | ')}
                        arrow
                    >
                        <WarningIcon color="error" sx={{ fontSize: '16px' }} />
                    </Tooltip>
                )}

                {/* Fi de reintents d'enviament */}
                {estatObjecte?.eventError?.errorFiReintents && (
                    <Tooltip title={estatObjecte.eventError.errorFiReintents} arrow>
                        <WarningIcon color="warning" sx={{ fontSize: '16px' }} />
                    </Tooltip>
                )}

                {/* Error en Callback */}
                {estatObjecte?.eventError?.errorCallback?.errorCallback && (
                    <Tooltip title={estatObjecte.eventError.errorCallback.errorCallback} arrow>
                        <ErrorIcon color="info" sx={{ fontSize: '16px' }} />
                    </Tooltip>
                )}

                {/* Fi de reintents de Callback */}
                {estatObjecte?.callbackFiReintents && (
                    <Tooltip title={estatObjecte.callbackFiReintents} arrow>
                        <WarningIcon color="info" sx={{ fontSize: '16px' }} />
                    </Tooltip>
                )}
                {/* Error en Notificació Mòbil */}
                {estatObjecte?.notificacioMovilError &&
                    estatObjecte.notificacioMovilError.length > 0 && (
                        <Tooltip
                            title={estatObjecte.notificacioMovilError
                                .map((errorMovil: any) => errorMovil.eventCarpeta)
                                .join(' | ')}
                            arrow
                        >
                            <PhoneIphoneIcon
                                sx={{
                                    fontSize: '16px',
                                    color: (theme) =>
                                        theme.palette.mode === 'dark' ? '#f5c777' : '#8a6d3b',
                                }}
                            />
                        </Tooltip>
                    )}
            </Box>

            {/* Data i Històric de Notificacions */}
            <Box sx={{ mt: 0.5 }}>
                {estatObjecte?.dataEstat && (
                    <Typography
                        variant="caption"
                        sx={{
                            display: 'block',
                            color: 'text.secondary',
                            fontSize: '10px',
                        }}
                    >
                        {estatObjecte.dataEstat}
                    </Typography>
                )}

                {/* Històric / Sub-estats de Notificació */}
                {estatObjecte?.notificaEstats && estatObjecte.notificaEstats.length > 0 && (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '2px', mt: 0.5 }}>
                        {estatObjecte.notificaEstats.map((subEstat: any, index: number) => (
                            <Box
                                key={index}
                                sx={{
                                    fontSize: '10px',
                                    borderLeft: `3px solid ${subEstat.color || '#ccc'}`,
                                    pl: '5px',
                                    display: 'flex',
                                    gap: 0.5,
                                    alignItems: 'center',
                                }}
                            >
                                <Typography
                                    component="span"
                                    sx={{ fontSize: '10px', fontWeight: 'bold' }}
                                >
                                    {subEstat?.value}
                                </Typography>
                                <Typography
                                    component="span"
                                    sx={{ fontSize: '10px', color: 'text.primary' }}
                                >
                                    {subEstat?.message}
                                </Typography>
                            </Box>
                        ))}
                    </Box>
                )}
            </Box>
        </Box>
    );
};

export default NotificacioEstatRender;
