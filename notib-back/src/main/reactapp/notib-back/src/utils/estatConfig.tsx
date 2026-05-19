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

export interface EstatConfigItem {
    icona: React.ReactNode;
    color: string;
}

export const ESTAT_ENUM_MUI_MAP: Record<string, EstatConfigItem> = {
    PENDENT: {
        icona: <ClockIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    ENVIADA: {
        icona: <SendIcon sx={{ fontSize: '14px' }} />,
        color: '#0275d8',
    },
    REGISTRADA: {
        icona: <FileIcon sx={{ fontSize: '16px' }} />,
        color: '#5bc0de',
    },
    FINALITZADA: {
        icona: <CheckIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    PROCESSADA: {
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    EXPIRADA: {
        icona: <AsteriskIcon sx={{ fontSize: '14px' }} />,
        color: '#d9534f',
    },
    NOTIFICADA: {
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    REBUTJADA: {
        icona: <CloseIcon sx={{ fontSize: '16px' }} />,
        color: '#d9534f',
    },
    ENVIAT_SIR: {
        icona: (
            <Avatar
                variant="rounded"
                sx={{
                    width: 16,
                    height: 16,
                    fontSize: '10px',
                    fontWeight: 'bold',
                    backgroundColor: '#0275d8',
                    color: 'white',
                }}
            >
                S
            </Avatar>
        ),
        color: '#0275d8',
    },
    ENVIADA_AMB_ERRORS: {
        icona: <SendIcon sx={{ fontSize: '14px' }} />,
        color: '#f0ad4e',
    },
    FINALITZADA_AMB_ERRORS: {
        icona: <CheckIcon sx={{ fontSize: '16px' }} />,
        color: '#f0ad4e',
    },
    ENVIANT: {
        icona: <ClockIcon sx={{ fontSize: '16px' }} />,
        color: '#a0a0a0',
    },
    OFICI_ACCEPTAT: {
        icona: <CheckCircleIcon sx={{ fontSize: '16px' }} />,
        color: '#5cb85c',
    },
    REBUTJADA_SIR: {
        icona: <CloseIcon sx={{ fontSize: '16px' }} />,
        color: '#d9534f',
    },
    ANULADA: {
        icona: <BanIcon sx={{ fontSize: '16px' }} />,
        color: '#d9534f',
    },
};
