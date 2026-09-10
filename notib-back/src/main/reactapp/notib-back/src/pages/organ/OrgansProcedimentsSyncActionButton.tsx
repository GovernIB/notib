import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import LinearProgress from '@mui/material/LinearProgress';
import { MuiActionReportButton, MuiDataGridApiRef, useBaseAppContext } from 'reactlib';
import { useSse } from '../../hooks/useSse';

type LogLine = { message: string; isError: boolean };

const OrgansProcedimentsSyncLoading: React.FC<{ percent: number; lines: LogLine[] }> = ({ percent, lines }) => {
    const logRef = React.useRef<HTMLDivElement>(null);
    React.useEffect(() => {
        if (logRef.current) {
            logRef.current.scrollTop = logRef.current.scrollHeight;
        }
    }, [lines.length]);
    return (
        <Box>
            <LinearProgress variant="determinate" value={percent} sx={{ mb: 1 }} />
            <Box ref={logRef} sx={{ maxHeight: 300, overflow: 'auto', bgcolor: 'action.hover', p: 1 }}>
                {lines.map((line, i) => (
                    <Typography key={i} variant="body2" color={line.isError ? 'error' : undefined}>
                        {line.message}
                    </Typography>
                ))}
            </Box>
        </Box>
    );
};

export const OrgansProcedimentsSyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef }> = (props) => {

    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const [percent, setPercent] = React.useState<number>(0);
    const [lines, setLines] = React.useState<LogLine[]>([]);

    useSse('PROGRESS', 'ORGANS_PROCEDIMENTS_SYNC', (event: any) => {
        setPercent(event.percent);
        if (event.message) {
            setLines((prev) => [...prev, { message: event.message, isError: event.status === 'ERROR' }]);
        }
    });

    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.syncCombined.cancelar'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.organs.grid.syncCombined.sincronitzar'),
            icon: 'sync',
            componentProps: { variant: 'contained' },
        },
    ];

    return (
        <MuiActionReportButton
            resourceName="organGestorResource"
            action="ORGANS_PROCEDIMENTS_SYNC"
            title={t('page.organs.grid.syncCombined.title')}
            buttonIcon="sync_alt"
            formDialogTitle={t('page.organs.grid.syncCombined.dialogTitle')}
            formDialogContent={<Typography>{t('page.organs.grid.syncCombined.nota')}</Typography>}
            formDialogButtons={formDialogButtons}
            formDialogLoading={<OrgansProcedimentsSyncLoading percent={percent} lines={lines} />}
            formDialogResultProcessor={() => <Typography>{t('page.organs.grid.syncCombined.success')}</Typography>}
            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
            onSuccess={() => {
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.organs.grid.syncCombined.success'), 'success');
            }}
            onClose={() => {
                setPercent(0);
                setLines([]);
            }}
        />
    );
};

export default OrgansProcedimentsSyncActionButton;
