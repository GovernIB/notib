import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService } from 'reactlib';
import { Box, Grid, Icon, IconButton, LinearProgress, Typography } from '@mui/material';
import { ContenidoData, DetailCard, DetailCardContent } from '../../components/CardData.tsx';

const useSistemAction = () => {
    const { isReady: apiIsReady, artifactAction: apiAction } =
        useResourceApiService('threadInfoResource');
    const { temporalMessageShow } = useBaseAppContext();
    const [system, setSystem] = React.useState<any>();

    const apiSystem = () => {
        setSystem(undefined);
        apiAction(undefined, { code: 'SYSTEM_INFO' })
            .then((response) => {
                setSystem(response);
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    };

    return {
        apiIsReady,
        system,
        apiSystem,
    };
};

const LinearSpace = ({ value, total }: { value: number; total?: number }) => {
    let v = 0;

    if (total && total > 0) {
        v = Math.round((value * 100) / total);
    } else if (value) {
        v = value;
    }

    // Assegurem que el valor estigui entre 0 i 100
    const safeValue = Math.min(Math.max(v, 0), 100);

    return (
        <LinearProgress
            variant="determinate"
            value={safeValue}
            color={v >= 90 ? 'error' : v >= 80 ? 'warning' : 'success'}
            sx={{ width: '100%', height: 15, borderRadius: '4px' }}
        />
    );
};

const SistemaTab: React.FC = () => {
    const { t } = useTranslation();
    const { system, apiSystem, apiIsReady } = useSistemAction();

    React.useEffect(() => {
        if (apiIsReady) {
            apiSystem();
        }
    }, [apiIsReady]);

    // Configuració per a la informació general del sistema
    const generalInfo = [
        {
            title: t('page.monitorSistema.tab.sistema.sistemaOperatiu'),
            value: system?.informacioSistema?.sistemaOperatiu,
        },
        { title: t('page.monitorSistema.tab.sistema.arquitectura'), value: system?.arquitectura },
        {
            title: t('page.monitorSistema.tab.sistema.processadors'),
            value: system?.informacioSistema?.processadors,
        },
        { title: t('page.monitorSistema.tab.sistema.jbossVersion'), value: system?.jbossVersion },
        {
            title: t('page.monitorSistema.tab.sistema.applicationServerInfo'),
            value: system?.applicationServerInfo,
        },
        {
            title: t('page.monitorSistema.tab.sistema.tempsFuncionant'),
            value: system?.informacioSistema?.tempsFuncionant,
        },
    ];
    // Configuració per a les mètriques de la Màquina virtual de Java
    const jvmMetrics = [
        { title: 'daemonThreadCount', value: system?.jvmInfo?.daemonThreadCount },
        { title: 'gcCount', value: system?.jvmInfo?.gcCount },
        { title: 'gcTime', value: system?.jvmInfo?.gcTime },
        { title: 'peakThreadCount', value: system?.jvmInfo?.peakThreadCount },
        { title: 'threadCount', value: system?.jvmInfo?.threadCount },
    ];
    // Configuració pel Disc i CPU
    const cpuUsage = [
        { title: 'formatedLoadAverage', value: system?.cpuUsage?.formatedLoadAverage },
        { title: 'loadAverage', value: system?.cpuUsage?.loadAverage },
        { title: 'validProcessCpuLoad', value: String(!!system?.cpuUsage?.validProcessCpuLoad) },
        { title: 'validSystemCpuLoad', value: String(!!system?.cpuUsage?.validSystemCpuLoad) },
    ];

    return (
        <>
            <Box display={'flex'} justifyContent={'end'} mb={1}>
                <IconButton
                    title={t('component.GridToolbarButton.refresh')}
                    onClick={apiSystem}
                    color={'primary'}
                >
                    <Icon>refresh</Icon>
                </IconButton>
            </Box>
            <DetailCard>
                {/* Info general */}
                {generalInfo.map((item, index) => (
                    <DetailCardContent key={index} size={4} title={item.title}>
                        {item.value}
                    </DetailCardContent>
                ))}
                {/* Secció JVM Memory */}
                <DetailCardContent title={t('page.monitorSistema.tab.sistema.jvmMemory')}>
                    <Grid container display={'flex'} alignItems={'center'}>
                        {jvmMetrics.map((metric, index) => (
                            <ContenidoData
                                key={index}
                                xs={4}
                                titleXs={6}
                                textXs={6}
                                title={metric.title}
                            >
                                {metric.value}
                            </ContenidoData>
                        ))}
                        <Grid size={8} sx={{ mt: 2 }}>
                            <LinearSpace
                                value={system?.jvmMemory?.usedMemory}
                                total={system?.jvmMemory?.totalMemory}
                            />
                        </Grid>
                        <Grid size={1} />
                        <Grid size={3} sx={{ mt: 2 }}>
                            {`${system?.jvmMemory?.formatedFreeMemory} / ${system?.jvmMemory?.formatedTotalMemory}`}
                        </Grid>
                    </Grid>
                </DetailCardContent>

                {/* Disks i CPU */}
                <DetailCardContent title={t('page.monitorSistema.tab.sistema.disksUsage')}>
                    <Grid container display={'flex'} alignItems={'center'}>
                        {cpuUsage?.map((item, index) => (
                            <ContenidoData
                                key={index}
                                xs={6}
                                titleXs={6}
                                textXs={6}
                                title={item.title}
                            >
                                {item.value}
                            </ContenidoData>
                        ))}

                        {system?.disksUsage?.map((disk: any, index: number) => (
                            <React.Fragment key={index}>
                                <Grid size={2} sx={{ mt: 1 }}>
                                    <Typography>{disk.nom}</Typography>
                                </Grid>
                                <Grid size={6} sx={{ mt: 1 }}>
                                    <LinearSpace value={disk?.usedSpace} total={disk?.totalSpace} />
                                </Grid>
                                <Grid size={1} />
                                <Grid size={3} sx={{ mt: 1 }}>
                                    {`${disk?.formatedFreeSpace} / ${disk?.formatedTotalSpace}`}
                                </Grid>
                            </React.Fragment>
                        ))}
                    </Grid>
                </DetailCardContent>
            </DetailCard>
        </>
    );
};

export default SistemaTab;
