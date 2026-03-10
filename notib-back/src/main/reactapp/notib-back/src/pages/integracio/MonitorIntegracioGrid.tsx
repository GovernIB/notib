import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    MuiFilter,
    useFilterApiRef,
    useResourceApiService,
    springFilterBuilder as filterBuilder,
    FilterApi,
    useCloseDialogButtons,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Badge, Box, Chip, Grid, Icon, IconButton, Tab, Tabs } from '@mui/material';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import ErrorIcon from '@mui/icons-material/Error';
import { useContentDialog } from '../../../lib/components/mui/Dialog';
import MonitorIntegracioParamDetail from './MonitorIntegracioParamDetail';

// INTERFACES
interface MonitorProps {
    options: Record<string, string>;
    report: ReportItem[];
    selectedTab: string | undefined;
    onTabChange: (key: string | undefined) => void;
}
interface ReportItem {
    grup: string;
    countOk: number;
    countWarn: number;
    countError: number;
    countTotal: number;
}

const CustomTabs = ({ options, report, selectedTab, onTabChange }: MonitorProps) => {
    const keys = Object.keys(options);
    const currentIndex = selectedTab ? keys.indexOf(selectedTab) : 0;

    const handleChange = (_event: React.SyntheticEvent, newValue: number) => {
        const keySeleccionada = keys[newValue];
        onTabChange(keySeleccionada);
    };

    const getErrorCount = (key: string) => {
        const item = report.find((r: ReportItem) => r.grup === key);
        return item ? item.countError : 0;
    };

    return (
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mt: 1 }}>
            <Tabs
                value={currentIndex === -1 ? 0 : currentIndex}
                onChange={handleChange}
                aria-label="basic tabs example"
                variant="scrollable"
                scrollButtons
                sx={{
                    '& .MuiTabScrollButton-root.Mui-disabled': {
                        opacity: 0.3,
                    },
                }}
            >
                {Object.entries(options).map(([key, label], index) => {
                    const errorCount = getErrorCount(key);

                    return (
                        <Tab
                            key={key}
                            id={`simple-tab-${index}`}
                            aria-controls={`simple-tabpanel-${index}`}
                            sx={{
                                textTransform: 'none',
                                mx: 0.5,
                                px: 3,
                                overflow: 'visible',
                                minWidth: 'fit-content',
                            }}
                            label={
                                errorCount > 0 ? (
                                    <Badge
                                        badgeContent={errorCount}
                                        color="error"
                                        sx={(theme) => ({
                                            '& .MuiBadge-badge': {
                                                right: -12,
                                                top: 0,
                                                border: `2px solid ${(theme.vars ?? theme).palette.background.paper}`,
                                                padding: '0 6px',
                                                height: '20px',
                                                minWidth: '20px',
                                                borderRadius: '10px',
                                            },
                                        })}
                                    >
                                        {label}
                                    </Badge>
                                ) : (
                                    label
                                )
                            }
                        />
                    );
                })}
            </Tabs>
        </Box>
    );
};

const ContentFilter = ({ filterApiRef }: { filterApiRef: React.RefObject<FilterApi> }) => {
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={1.5} name="dataInici" />
            <GridFormField size={1.5} name="dataFi" />
            <GridFormField size={3} name="descripcio" />
            <GridFormField size={1.75} name="aplicacio" />
            <GridFormField size={1.5} name="codiEntitat" />
            <GridFormField size={1.25} name="tipus" />
            <GridFormField size={1} name="estat" />
            <Grid size={0.5}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const MonitorIntegracioGridFilter = ({
    options,
    report,
    selectedTab,
    onTabChange,
}: MonitorProps) => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            data?.dataInici && filterBuilder.gte('data', `'${formatStartOfDay(data?.dataInici)}'`),
            data?.dataFi && filterBuilder.lte('data', `'${formatEndOfDay(data?.dataFi)}'`),
            filterBuilder.like('descripcio', data.descripcio),
            filterBuilder.like('aplicacio', data.aplicacio),
            filterBuilder.like('codiEntitat', data.codiEntitat),
            filterBuilder.eq('tipus', `'${data.tipus}'`),
            filterBuilder.eq('estat', `'${data.estat}'`)
        );
    };

    return (
        <Box>
            <MuiFilter
                resourceName="monitorIntegracioResource"
                code="FILTER_MONITOR_INTEGRACIO"
                apiRef={filterApiRef}
                springFilterBuilder={springFilterBuilder}
                componentProps={{ sx: { mb: 2, mt: 0 } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ContentFilter filterApiRef={filterApiRef} />
                <CustomTabs
                    options={options}
                    report={report}
                    selectedTab={selectedTab}
                    onTabChange={onTabChange}
                />
            </MuiFilter>
        </Box>
    );
};

export const MonitorIntegracioGrid = () => {
    const { t } = useTranslation();
    const [options, setOptions] = React.useState<Record<string, string>>({});
    const [report, setReport] = React.useState<ReportItem[]>([]);
    const [selectedTab, setSelectedTab] = React.useState<string | undefined>(undefined);
    const {
        isReady: apiIsReady,
        artifactReport: apiArtifactReport,
        currentFields: apiCurrentFields,
    } = useResourceApiService('monitorIntegracioResource');

    const columns = [
        {
            field: 'data',
            flex: 2,
        },
        {
            field: 'descripcio',
            flex: 3,
        },
        {
            field: 'aplicacio',
            flex: 2,
        },
        {
            field: 'notificacioId',
            flex: 2,
        },
        {
            field: 'tipus',
            flex: 1,
        },
        {
            field: 'codiEntitat',
            flex: 1,
        },
        {
            field: 'tempsResposta',
            flex: 1,
        },
        {
            field: 'estat',
            flex: 1.5,
            renderCell: (params: any) => {
                const getColor = () => {
                    if (params.value === 'ERROR') return 'error';
                    if (params.value === 'WARN') return 'warning';
                    if (params.value === 'OK') {
                        return 'success';
                    }
                };
                const getIcon = () => {
                    if (params.value === 'ERROR') return <CancelIcon />;
                    if (params.value === 'WARN') return <ErrorIcon />;
                    if (params.value === 'OK') return <CheckCircleIcon />;
                };
                return (
                    <Chip
                        label={params.formattedValue}
                        color={getColor()}
                        size="small"
                        icon={getIcon()}
                        sx={{ px: 0.5 }}
                    />
                );
            },
        },
    ];
    const buttons = useCloseDialogButtons();
    const [showDialog, dialog] = useContentDialog(buttons);

    const openDialog = () => {
        showDialog('Titol del Dialeg', <MonitorIntegracioParamDetail />);
    };

    // Filtre estàtic basat en la pestanya seleccionada
    const staticFilter = React.useMemo(() => {
        if (!selectedTab) return undefined;
        return filterBuilder.eq('codi', `'${selectedTab}'`);
    }, [selectedTab]);

    React.useEffect(() => {
        const keys = Object.keys(options);
        if (keys.length > 0 && !selectedTab) {
            setSelectedTab(keys[0]);
        }
    }, [options, selectedTab]);

    React.useEffect(() => {
        if (apiIsReady) {
            apiArtifactReport(null, { code: 'AGRUPACIONS' }).then((response) => {
                setReport(response as ReportItem[]);
            });
        }
    }, [apiIsReady]);

    React.useEffect(() => {
        const codiField = apiCurrentFields?.find((f) => f.name === 'codi');
        if (codiField?.options) {
            setOptions(codiField.options);
        }
    }, [apiCurrentFields]);

    const rowAdditionalActions = [
        {
            label: 'Label',
            title: 'Tooltip',
            icon: 'visibility',
            showInMenu: false,
            onClick: openDialog,
        },
    ];

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.integracio.grid.title')}
                resourceName="monitorIntegracioResource"
                columns={columns}
                staticFilter={staticFilter}
                paginationActive
                toolbarHideQuickFilter
                readOnly
                rowAdditionalActions={rowAdditionalActions} // TODO: NO FUNCIONA ...
                toolbarAdditionalRow={
                    <MonitorIntegracioGridFilter
                        options={options}
                        report={report}
                        selectedTab={selectedTab}
                        onTabChange={setSelectedTab}
                    />
                }
                rowLink="detail/{{id}}"
            />
            {dialog}
        </GridPage>
    );
};

export default MonitorIntegracioGrid;
