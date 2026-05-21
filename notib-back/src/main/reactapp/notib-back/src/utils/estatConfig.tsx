import React from 'react';
import ClockIcon from '@mui/icons-material/AccessTime';
import SendIcon from '@mui/icons-material/Send';
import FileIcon from '@mui/icons-material/InsertDriveFileOutlined';
import CheckIcon from '@mui/icons-material/Check';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import AsteriskIcon from '@mui/icons-material/Emergency';
import CloseIcon from '@mui/icons-material/Close';
import BanIcon from '@mui/icons-material/Block';
import { Avatar } from '@mui/material';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import ErrorIcon from '@mui/icons-material/Error';

export interface MapConfigItem {
    translationKey: string;
    icona?: React.ReactNode;
    color?: string;
}

// es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto
export const NOTIFICACIO_ESTAT_ENUM_MAP: Record<string, MapConfigItem> = {
    PENDENT: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.PENDENT',
        icona: <ClockIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    ENVIADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.ENVIADA',
        icona: <SendIcon sx={{ fontSize: '14px' }} />,
        color: '#5bc0de',
    },
    REGISTRADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.REGISTRADA',
        icona: <FileIcon sx={{ fontSize: '16px' }} />,
        color: '#007bff',
    },
    FINALITZADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.FINALITZADA',
        icona: <CheckIcon sx={{ fontSize: '16px' }} />,
        color: '#00ff00',
    },
    PROCESSADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.PROCESSADA',
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    EXPIRADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.EXPIRADA',
        icona: <AsteriskIcon sx={{ fontSize: '14px' }} />,
        color: '#00ff00',
    },
    NOTIFICADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.NOTIFICADA',
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#00ff00',
    },
    REBUTJADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.REBUTJADA',
        icona: <CloseIcon sx={{ fontSize: '16px' }} />,
        color: '#00ff00',
    },
    ENVIAT_SIR: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.ENVIAT_SIR',
        icona: (
            <Avatar
                variant="rounded"
                sx={{
                    width: 16,
                    height: 16,
                    fontSize: '10px',
                    fontWeight: 'bold',
                    backgroundColor: '#5bc0de',
                    color: 'white',
                }}
            >
                S
            </Avatar>
        ),
        color: '#5bc0de',
    },
    ENVIADA_AMB_ERRORS: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.ENVIADA_AMB_ERRORS',
        icona: <SendIcon sx={{ fontSize: '14px' }} />,
        color: '#d9534f',
    },
    FINALITZADA_AMB_ERRORS: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.FINALITZADA_AMB_ERRORS',
        icona: <CheckIcon sx={{ fontSize: '16px' }} />,
        color: '#d9534f',
    },
    ENVIANT: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.ENVIANT',
        icona: <ClockIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    OFICI_ACCEPTAT: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.OFICI_ACCEPTAT',
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    REBUTJADA_SIR: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.REBUTJADA_SIR',
        icona: <CloseIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    ANULADA: {
        translationKey: 'utils.estatConfig.ESTAT_ENUM_MAP.ANULADA',
        icona: <BanIcon sx={{ fontSize: '16px' }} />,
        color: '#00ff00',
    },
};

// es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto
export const NOTIFICACIO_REGISTRE_ESTAT_ENUM_MAP: Record<string, MapConfigItem> = {
    VALID: {
        translationKey: 'utils.estatConfig.VALID',
    },
    RESERVA: {
        translationKey: 'utils.estatConfig.RESERVA',
    },
    PENDENT: {
        translationKey: 'utils.estatConfig.PENDENT',
    },
    OFICI_EXTERN: {
        translationKey: 'utils.estatConfig.OFICI_EXTERN',
    },
    OFICI_INTERN: {
        translationKey: 'utils.estatConfig.OFICI_INTERN',
    },
    OFICI_ACCEPTAT: {
        translationKey: 'utils.estatConfig.OFICI_ACCEPTAT',
    },
    DISTRIBUIT: {
        translationKey: 'utils.estatConfig.DISTRIBUIT',
    },
    ANULAT: {
        translationKey: 'utils.estatConfig.ANULAT',
    },
    RECTIFICAT: {
        translationKey: 'utils.estatConfig.RECTIFICAT',
    },
    REBUTJAT: {
        translationKey: 'utils.estatConfig.REBUTJAT',
    },
    REENVIAT: {
        translationKey: 'utils.estatConfig.REENVIAT',
    },
    DISTRIBUINT: {
        translationKey: 'utils.estatConfig.DISTRIBUINT',
    },
    OFICI_SIR: {
        translationKey: 'utils.estatConfig.OFICI_SIR',
    },
    ENVIAT_NOTIFICAR: {
        translationKey: 'utils.estatConfig.ENVIAT_NOTIFICAR',
    },
};

// es.caib.notib.client.domini.EnviamentEstatGrup
const ESTAT_TRAMITACIO_ENUM_MAP: Record<string, { icona?: React.ReactNode; color?: string }> = {
    TRAMITACIO: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.TRAMITACIO',
        icona: <ClockIcon sx={{ fontSize: '16px' }} />,
        color: '#777',
    },
    PENDENT_COMPAREIXENCA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA',
        icona: <MailOutlineIcon sx={{ fontSize: '16px' }} />,
        color: '#e67e22',
    },
    LLEGIDA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.LLEGIDA',
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    REBUTJADA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.REBUTJADA',
        icona: <CloseIcon sx={{ fontSize: '16px' }} />,
        color: '#6F5647',
    },
    EXPIRADA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.EXPIRADA',
        icona: <AsteriskIcon sx={{ fontSize: '14px' }} />,
        color: '#F1D629',
    },
    ANULADA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.ANULADA',
        icona: <BanIcon sx={{ fontSize: '16px' }} />,
        color: '#337ab7',
    },
    ERROR: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.ERROR',
        icona: <ErrorIcon sx={{ fontSize: '16px' }} />,
        color: '#d9534f',
    },
    ESTAT_FICTICI: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.ESTAT_FICTICI',
        icona: <SendIcon sx={{ fontSize: '16px' }} />,
        color: 'purple',
    },
    FINALITZADA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.FINALITZADA',
        icona: <SendIcon sx={{ fontSize: '16px' }} />,
        color: '#1d541d',
    },
    PROCESSADA: {
        // translationKey: 'utils.estatConfig.ESTAT_TRAMITACIO_ENUM_MAP.PROCESSADA',
        icona: <SendIcon sx={{ fontSize: '16px' }} />,
        color: '#1d541d',
    },
};

// es.caib.notib.client.domini.EnviamentEstat
export const ENVIAMENT_ESTAT_MAP: Record<string, MapConfigItem> = {
    NOTIB_PENDENT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.TRAMITACIO,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.NOTIB_PENDENT',
    },
    NOTIB_ENVIADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.TRAMITACIO,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.NOTIB_ENVIADA',
    },
    ABSENT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ABSENT',
    },
    ADRESA_INCORRECTA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ADRESA_INCORRECTA',
    },
    DESCONEGUT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.DESCONEGUT',
    },
    ENVIADA_CI: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIADA_CI',
    },
    ENVIADA_DEH: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIADA_DEH',
    },
    ENVIAMENT_PROGRAMAT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.TRAMITACIO,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIAMENT_PROGRAMAT',
    },
    ENTREGADA_OP: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENTREGADA_OP',
    },
    ERROR_ENTREGA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ERROR_ENTREGA',
    },
    EXPIRADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.EXPIRADA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.EXPIRADA',
    },
    EXTRAVIADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.EXTRAVIADA',
    },
    MORT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.MORT',
    },
    LLEGIDA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.LLEGIDA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.LLEGIDA',
    },
    NOTIFICADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.LLEGIDA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.NOTIFICADA',
    },
    PENDENT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PENDENT',
    },
    PENDENT_ENVIAMENT: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PENDENT_ENVIAMENT',
    },
    PENDENT_SEU: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PENDENT_SEU',
    },
    PENDENT_CIE: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PENDENT_CIE',
    },
    PENDENT_DEH: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PENDENT_DEH',
    },
    REBUTJADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.REBUTJADA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.REBUTJADA',
    },
    SENSE_INFORMACIO: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ERROR,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.SENSE_INFORMACIO',
    },
    FINALITZADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ESTAT_FICTICI,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.FINALITZADA',
    },
    ENVIADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIADA',
    },
    REGISTRADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.TRAMITACIO,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.REGISTRADA',
    },
    PROCESSADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ESTAT_FICTICI,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.PROCESSADA',
    },
    ANULADA: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ANULADA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ANULADA',
    },
    ENVIAT_SIR: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.PENDENT_COMPAREIXENCA,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIAT_SIR',
    },
    ENVIADA_AMB_ERRORS: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ESTAT_FICTICI,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.ENVIADA_AMB_ERRORS',
    },
    FINALITZADA_AMB_ERRORS: {
        ...ESTAT_TRAMITACIO_ENUM_MAP.ESTAT_FICTICI,
        translationKey: 'utils.estatConfig.ENVIAMENT_ESTAT_MAP.FINALITZADA_AMB_ERRORS',
    },
};


/** Genera el nom de la classe CSS basat en el color de l'estat d'una fila. */
export const getGridRowColorClass = (rowEstat: string, estatEnumMap: Record<string, any>): string => {
    const colorEstat = estatEnumMap[rowEstat]?.color;
    return colorEstat ? `color-${colorEstat.replace('#', '')}` : '';
};

/**
 * Genera de manera dinàmica l'objecte d'estils per a la propietat 'sx' del DataGrid
 * a partir de qualsevol mapa d'estats que contingui colors, per posar-ho a l'inici de la fila.
 */
export const generateGridRowStylesFromMap = (estatEnumMap: Record<string, any>): Record<string, any> => {
    return Object.values(estatEnumMap).reduce(
        (acc, estat) => {
            const colorNet = estat?.color?.replace('#', '');
            if (colorNet) {
                acc[`& .MuiDataGrid-row.color-${colorNet} .MuiDataGrid-cellCheckbox`] = {
                    borderLeft: `3px solid ${estat?.color}`,
                };
            }
            return acc;
        },
        {} as Record<string, any>
    );
};
