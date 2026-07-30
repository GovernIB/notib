import React from 'react';
import { Box, Chip, Tooltip, Typography } from '@mui/material';
import MailIcon from '@mui/icons-material/Mail';
import BlockIcon from '@mui/icons-material/Block';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import RefreshIcon from '@mui/icons-material/Refresh';
import {
    ENVIAMENT_ESTAT_MAP,
    NOTIFICACIO_ESTAT_ENUM_MAP,
    NOTIFICACIO_REGISTRE_ESTAT_ENUM_MAP,
} from '../../utils/estatConfig';
import { useTranslation } from 'react-i18next';
import useAccionsNotificacio from "../accions/AccionsNotificacio.tsx";
import {ROLE_USER, useNotibContext} from "../../components/NotibContext.ts";

export type NotificacioEstatRenderProps = {
    estatJson: any;
    estatEnum: string;
    notificacioId: number,
    refreshGrid: any
};

// Gestió d'Entrega Postal
const EntregaPostal: React.FC<{ entregaPostal: any }> = ({ entregaPostal }) => {
    if (!entregaPostal) return null;

    return (
        <Tooltip title={entregaPostal?.title} arrow>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    alignSelf: 'flex-end',
                    p: '2px',
                    backgroundColor: entregaPostal?.label.includes('danger')
                        ? '#d9534f'
                        : '#5cb85c',
                    borderRadius: '4px',
                }}
            >
                <MailIcon style={{ fontSize: '15px', color: 'white' }} />
            </Box>
        </Tooltip>
    );
};

// Registre d'Estats
const RegistreEstats: React.FC<{ registreEstat: any[] }> = ({ registreEstat }) => {
    if (!registreEstat || registreEstat.length === 0) return null;

    return (
        <Box sx={{ display: 'flex', gap: 0.5 }}>
            {registreEstat.map((registre: any, index: number) => (
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
    );
};

// L'Estat Principal i l'Anul·lació
const EstatPrincipal: React.FC<{ estatEnum: string; nomEstat: string; anulat?: string }> = (
    props
) => {
    const { estatEnum, nomEstat, anulat } = props;
    const configEstat = NOTIFICACIO_ESTAT_ENUM_MAP[estatEnum];

    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {!anulat ? (
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
                        {nomEstat}
                    </Typography>
                </>
            ) : (
                <>
                    <Tooltip title={anulat} arrow>
                        <BlockIcon color="error" sx={{ fontSize: '16px' }} />
                    </Tooltip>
                    <Typography variant="body2" sx={{ fontWeight: 500 }}>
                        {nomEstat}
                    </Typography>
                </>
            )}
        </Box>
    );
};

// Gestió d'Errors, Avisos i Mòbil
const ErrorsIAvisos: React.FC<{
    eventError: any;
    callbackFiReintents: any;
    notificacioMovilError: any[];
}> = (props) => {
    const { eventError, callbackFiReintents, notificacioMovilError } = props;

    // Si no hi ha cap error de cap tipus, evitem renderitzar el contenidor
    const teErrors =
        (eventError?.error && eventError.error.length > 0) ||
        eventError?.errorFiReintents ||
        eventError?.errorCallback?.errorCallback ||
        callbackFiReintents ||
        (notificacioMovilError && notificacioMovilError.length > 0);

    if (!teErrors) return null;

    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            {/* Errors crítics (Acumulats) */}
            {eventError?.error && eventError.error.length > 0 && (
                <Tooltip title={eventError.error.map((err: any) => err.title).join(' | ')} arrow>
                    <WarningIcon color="error" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Fi de reintents d'enviament */}
            {eventError?.errorFiReintents && (
                <Tooltip title={eventError.errorFiReintents} arrow>
                    <WarningIcon color="warning" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Error en Callback */}
            {eventError?.errorCallback?.errorCallback && (
                <Tooltip title={eventError.errorCallback.errorCallback} arrow>
                    <ErrorIcon color="info" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Fi de reintents de Callback */}
            {callbackFiReintents && (
                <Tooltip title={callbackFiReintents} arrow>
                    <WarningIcon color="info" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Error en Notificació Mòbil */}
            {notificacioMovilError && notificacioMovilError.length > 0 && (
                <Tooltip
                    title={notificacioMovilError
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
    );
};

// Data i Històric de Notificacions
const DataIHistoric: React.FC<{ dataEstat: string; notificaEstats: any[] }> = (props) => {
    const { dataEstat, notificaEstats } = props;
    const teDataIHistoric = dataEstat || (notificaEstats && notificaEstats.length > 0);

    if (!teDataIHistoric) return null;

    return (
        <Box>
            {dataEstat && (
                <Typography
                    variant="caption"
                    sx={{ display: 'block', color: 'text.secondary', fontSize: '10px' }}
                >
                    {dataEstat}
                </Typography>
            )}

            {notificaEstats && notificaEstats.length > 0 && (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: '2px', mt: 0.5 }}>
                    {notificaEstats.map((subEstat: any, index: number) => (
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
    );
};


const refrescarEstatString = async (event: React.MouseEvent<HTMLDivElement, MouseEvent>, notificacioId: number, refrescarEstat : any) => {

    event.preventDefault();
    event.stopPropagation();
    refrescarEstat(notificacioId);
}

// Dissenyat específicament per la cel·la del Grid de Notificacions
export const NotificacioEstatGrid: React.FC<NotificacioEstatRenderProps> = (props) => {

    const { estatJson, estatEnum, notificacioId, refreshGrid } = props;
    const { refrescarEstat } = useAccionsNotificacio(refreshGrid);
    const { t } = useTranslation();
    let estatObjecte: any = null;
    const { currentRole} = useNotibContext();
    const isRoleUser = currentRole === ROLE_USER;
    const refrescar = (<Box sx={{display: "flex", justifyContent: "flex-end"}}>
                                    <Tooltip title={t('page.notificacio.detail.dades.refrescar')} arrow
                                             onClick={(event) => refrescarEstatString(event, notificacioId, refrescarEstat)}>
                                        <RefreshIcon color="info" sx={{fontSize: '16px'}}/>
                                    </Tooltip>
                                </Box>);
    try {
        if (!estatJson) {
            return !isRoleUser ? refrescar : null;
        }
        estatObjecte = JSON.parse(estatJson);
    } catch (error) {
        console.error('La cadena no és un JSON vàlid:', error);
    }

    return (
        <Box sx={{width: '100%', display: 'flex', flexDirection: 'row', p: 0.5, alignItems: 'flex-start',}}>
            <Box sx={{ flex: 10, display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                <RegistreEstats registreEstat={estatObjecte?.registreEstat} />
                <EstatPrincipal estatEnum={estatEnum} nomEstat={estatObjecte?.nomEstat} anulat={estatObjecte?.anulat}/>
                <ErrorsIAvisos
                    eventError={estatObjecte?.eventError}
                    callbackFiReintents={estatObjecte?.callbackFiReintents}
                    notificacioMovilError={estatObjecte?.notificacioMovilError}
                />
                <DataIHistoric dataEstat={estatObjecte?.dataEstat} notificaEstats={estatObjecte?.notificaEstats}
                />
            </Box>
            <Box sx={{flex: 2, display: 'flex', justifyContent: 'flex-end', alignItems: 'flex-start',}}>
                <EntregaPostal entregaPostal={estatObjecte?.entregaPostal} />
            </Box>
            {!isRoleUser && refrescar}
        </Box>
    );
};


// Dissenyat específicament per a les vistes de Detall de la Notificació
export const NotificacioEstatDetall: React.FC<{ notificacio: any }> = (props) => {

    const { notificacio } = props;
    const { t } = useTranslation();
    if (!notificacio?.estat) {
        return null;
    }
    // Comprova si notificacio.enviant és true per forçar la icona i el text ENVIANT
    const estatRealEnum = notificacio?.enviant ? 'ENVIANT' : notificacio.estat;
    const configEstat = NOTIFICACIO_ESTAT_ENUM_MAP[estatRealEnum];
    // Condició per mostrar sub-estats d'enviaments
    const mostraSubEstats = estatRealEnum === 'FINALITZADA' || estatRealEnum === 'PROCESSADA';

    return (
        <Box sx={{width: '100%', display: 'flex', flexDirection: 'row', alignItems: 'center', gap: 0.5, flexWrap: 'wrap'}}>
            {/* Icona i Text de l'Estat Principal */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                {configEstat && (
                    <Box component="span" sx={{display: 'inline-flex', alignItems: 'center', color: configEstat.color,}}>
                        {configEstat.icona}
                    </Box>
                )}
                <Typography variant="body2" sx={{ fontWeight: 500 }}>
                    {t(configEstat.translationKey)}
                </Typography>
            </Box>

            {/* Bucle d'Enviaments / Sub-estats */}
            {mostraSubEstats &&
                notificacio?.enviamentsInfo &&
                notificacio.enviamentsInfo.length > 0 && (
                    <Typography variant="body2" sx={{color: 'text.secondary', display: 'inline-flex', gap: '4px', flexWrap: 'wrap',}}>
                        (
                        {notificacio.enviamentsInfo.map((enviament: any, index: number) => {
                            let textEnviament = '';
                            if (enviament.notificat == true) {
                                textEnviament = t('utils.estatConfig.ESTAT_ENUM_MAP.NOTIFICADA');
                            } else if (notificacio?.comunicacioSir) {
                                textEnviament = t(
                                    NOTIFICACIO_REGISTRE_ESTAT_ENUM_MAP[enviament.registreEstat]
                                        .translationKey
                                );
                            } else {
                                textEnviament = t(
                                    ENVIAMENT_ESTAT_MAP[enviament.notificaEstat].translationKey
                                );
                            }

                            // Si finalment no s'ha pogut calcular cap text
                            if (!textEnviament) {
                                return null;
                            }

                            return (
                                <Typography key={index} component={'span'} variant="body2">
                                    {textEnviament}
                                    {index < notificacio.enviamentsInfo.length - 1 ? ', ' : ''}
                                </Typography>
                            );
                        })}
                        )
                    </Typography>
                )}

            {/* Icona d'Error de Notificació */}
            {notificacio?.notificaErrorData && (
                <Tooltip title={notificacio?.notificaErrorDescripcio} arrow>
                    <WarningIcon color="error" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Gestió d'Anul·lació */}
            {notificacio.motiuAnulacio ? (
                <Tooltip title={notificacio.motiuAnulacio} arrow>
                    <BlockIcon color="error" sx={{ fontSize: '16px' }} />
                </Tooltip>
            ) : notificacio.anulat ? (
                <Tooltip title={t('page.notificacio.detail.dades.anulada')} arrow>
                    <BlockIcon color="error" sx={{ fontSize: '16px' }} />
                </Tooltip>
            ) : null}

            {/* Fi de Reintents de l'Enviament */}
            {notificacio?.fiReintents && (
                <Tooltip title={notificacio?.fiReintentsDesc} arrow>
                    <WarningIcon color="warning" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Error de l'aplicació client */}
            {notificacio?.tipusUsuari === 'APLICACIO' && notificacio?.errorLastCallback && (
                <Tooltip title={t('page.notificacio.detail.dades.errorCanviEstat')} arrow>
                    <ErrorIcon color="primary" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Fi de Reintents de Callback */}
            {notificacio?.callbackFiReintents && (
                <Tooltip title={notificacio?.callbackFiReintentsDesc} arrow>
                    <WarningIcon color="info" sx={{ fontSize: '16px' }} />
                </Tooltip>
            )}

            {/* Errors de dispositius mòbils */}
            {notificacio?.notificacionsMovilErrorDesc?.map((errorText: string, id: number) => (
                <Tooltip key={id} title={errorText} arrow>
                    <PhoneIphoneIcon sx={{fontSize: '18px', color: (theme) => theme.palette.mode === 'dark' ? '#f5c777' : '#8a6d3b'}}/>
                </Tooltip>
            ))}

        </Box>
    );
};
