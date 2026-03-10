import { dateFormatLocale, useResourceApiService } from 'reactlib';
import React from 'react';
import { Box, Icon, IconButton, Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import ChipEstat from '../../components/ChipEstat';

type CopyType = 'params' | 'error';
type Tipus = 'ENVIAMENT' | 'RECEPCIO' | 'PROCESSAR';

const useGetTipusLabel = () => {
    const { t } = useTranslation();
    const getTipusLabel = (tipus: Tipus) => {
        if (tipus === 'ENVIAMENT') return t('page.integracio.detall.tipusEnum.enviament');
        if (tipus === 'RECEPCIO') return t('page.integracio.detall.tipusEnum.recepcio');
        if (tipus === 'PROCESSAR') return t('page.integracio.detall.tipusEnum.processar');
        return '';
    };
    return getTipusLabel;
};

export const MonitorIntegracioParamDetail: React.FC<{ id: string }> = ({ id }) => {
    const { t } = useTranslation();
    const getTipusLabel = useGetTipusLabel();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService(
        'monitorIntegracioParamResource'
    );
    const { getOne: apiGetOne } = useResourceApiService('monitorIntegracioResource');
    const [params, setParams] = React.useState<any[]>([]);
    const [integracio, setIntegracio] = React.useState<any>(null);
    const [copyParamSuccess, setCopyParamSuccess] = React.useState(false);
    const [copyErrorSuccess, setCopyErrorSuccess] = React.useState(false);
    const ensenyarError =
        integracio?.errorDescripcio ||
        integracio?.excepcioMessage ||
        integracio?.excepcioStacktrace;

    const handleCopy = async (data: any, type: CopyType) => {
        if (!data) return;

        let textToCopy = '';

        if (type === 'params' && Array.isArray(data)) {
            // Format: CODI: Valor
            textToCopy = data.map((p) => `${p.codi}: ${p.valor}`).join('\n');
        } else if (type === 'error') {
            const parts = [
                integracio?.errorDescripcio
                    ? t('page.integracio.detall.errorDescripcio') + ` ${integracio.errorDescripcio}`
                    : '',
                integracio?.excepcioStacktrace
                    ? `${t('page.integracio.detall.excepcioStacktrace')}:\n${integracio.excepcioStacktrace}`
                    : '',
            ];

            textToCopy = parts.filter((part) => part !== '').join('\n\n');
        } else {
            textToCopy = String(data);
        }

        try {
            await navigator.clipboard.writeText(textToCopy);

            if (type === 'params') {
                setCopyParamSuccess(true);
                setTimeout(() => setCopyParamSuccess(false), 2000);
            } else {
                setCopyErrorSuccess(true);
                setTimeout(() => setCopyErrorSuccess(false), 2000);
            }
        } catch (err) {
            console.error('Error en copiar', err);
        }
    };

    React.useEffect(() => {
        const fetchParams = async () => {
            if (!apiIsReady || !id) {
                return;
            }

            try {
                const args = { filter: `monitorIntegracio.id:${id}`, unpaged: true };
                const paramResponse = await apiFind(args);
                setParams(paramResponse?.rows || []);
                if (paramResponse?.rows.length > 0) {
                    const integracio = await apiGetOne(id);
                    setIntegracio(integracio);
                }
            } catch (error) {
                console.error('Error fetching params:', error);
            }
        };
        fetchParams();
    }, [apiIsReady, id, apiFind, apiGetOne]);

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Box id="integracio-details-section">
                <Box sx={{ display: 'flex', gap: 4 }}>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.integracio.detall.data')}&nbsp;
                        <Typography component={'span'}>
                            {dateFormatLocale(integracio?.data)}
                        </Typography>
                    </Typography>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.integracio.detall.tipus')}&nbsp;
                        <Typography component={'span'}>
                            {getTipusLabel(integracio?.tipus)}
                        </Typography>
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography sx={{ fontWeight: 'bold' }}>
                            {t('page.integracio.detall.estat')}
                        </Typography>
                        <ChipEstat estat={integracio?.estat} />
                    </Box>
                </Box>
                <Typography sx={{ fontWeight: 'bold' }}>
                    {t('page.integracio.detall.descripcio')}&nbsp;
                    <Typography component={'span'}>{integracio?.descripcio}</Typography>
                </Typography>
            </Box>
            {params && (
                <Box id="params-section">
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                        }}
                    >
                        <Typography sx={{ fontWeight: 'bold' }}>
                            {t('page.integracio.detall.parametres')}
                        </Typography>
                    </Box>
                    <Box
                        sx={{
                            p: 2,
                            borderRadius: 2,
                            border: '1px solid #ccc',
                            backgroundColor: 'greyBackground',
                            position: 'relative',
                        }}
                    >
                        <Tooltip
                            title={
                                copyParamSuccess
                                    ? t('page.integracio.detall.tooltipCopiat')
                                    : t('page.integracio.detall.tooltipCopiarParametres')
                            }
                            placement="left"
                        >
                            <IconButton
                                onClick={() => handleCopy(params, 'params')}
                                sx={{ position: 'absolute', top: 8, right: 8, zIndex: 10 }}
                            >
                                <Icon fontSize="small">
                                    {copyParamSuccess ? 'check' : 'content_copy'}
                                </Icon>
                            </IconButton>
                        </Tooltip>
                        <Box>
                            {params.map((param) => (
                                <Typography
                                    key={param.id}
                                    variant="body2"
                                    sx={{ fontWeight: 'bold', ml: 1 }}
                                >
                                    {param.codi}:&nbsp;
                                    <Typography component={'span'}>{param.valor}</Typography>
                                </Typography>
                            ))}
                        </Box>
                    </Box>
                </Box>
            )}

            {ensenyarError && (
                <Box id="error-section">
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                        }}
                    >
                        <Typography sx={{ fontWeight: 'bold' }}>
                            {t('page.integracio.detall.error')}
                        </Typography>
                    </Box>
                    {integracio?.errorDescripcio && (
                        <Typography sx={{ fontWeight: 'bold' }}>
                            {t('page.integracio.detall.errorDescripcio')}&nbsp;
                            <Typography component={'span'}>
                                {integracio?.errorDescripcio}
                            </Typography>
                        </Typography>
                    )}
                    {integracio?.excepcioMessage && (
                        <Typography sx={{ fontWeight: 'bold' }}>
                            {t('page.integracio.detall.excepcioMessage')}&nbsp;
                            <Typography component={'span'}>
                                {integracio?.excepcioMessage}
                            </Typography>
                        </Typography>
                    )}
                    {integracio?.excepcioStacktrace && (
                        <Box
                            component="pre"
                            sx={{
                                backgroundColor: 'greyBackground',
                                p: 2,
                                borderRadius: 2,
                                border: '1px solid #ccc',
                                fontSize: '0.7rem',
                                overflowX: 'auto',
                                whiteSpace: 'pre-wrap',
                                maxHeight: '400px',
                                overflowY: 'auto',
                                position: 'relative',
                            }}
                        >
                            <Tooltip
                                title={
                                    copyErrorSuccess
                                        ? t('page.integracio.detall.tooltipCopiat')
                                        : t('page.integracio.detall.tooltipCopiarError')
                                }
                                placement="left"
                            >
                                <IconButton
                                    onClick={() =>
                                        handleCopy(integracio?.excepcioStacktrace, 'error')
                                    }
                                    sx={{ position: 'absolute', top: 8, right: 8, zIndex: 10 }}
                                >
                                    <Icon fontSize="small">
                                        {copyErrorSuccess ? 'check' : 'content_copy'}
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                            {integracio?.excepcioStacktrace}
                        </Box>
                    )}
                </Box>
            )}
        </Box>
    );
};

export default MonitorIntegracioParamDetail;
