import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    useResourceApiService,
    springFilterBuilder as filterBuilder,
    useCloseDialogButtons,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Badge, Box, Tab, Tabs } from '@mui/material';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useContentDialog } from '../../../lib/components/mui/Dialog';
import MonitorIntegracioParamDetail from './MonitorIntegracioParamDetail';
import ChipEstat from '../../components/ChipEstat';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import FilterCount from '../../components/FilterCount';
import { icons } from '../../utils/icons';
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import StyledMuiFilter from '../../components/StyledMuiFilter';

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
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mt: 2 }}>
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

const ContentFilter = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="dataInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="dataFi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="descripcio" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="aplicacio" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codiEntitat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.25 }} name="tipus" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1 }} name="estat" />
        </>
    );
};

const MonitorIntegracioGridFilter = ({
    options,
    report,
    selectedTab,
    onTabChange,
}: MonitorProps) => {
    // Últim Spring Filter aplicat, per al recompte de filtres de la barra de títol
    const [springFilter, setSpringFilter] = React.useState<string | undefined>();

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
            <StyledMuiFilter
                resourceName="monitorIntegracioResource"
                code="FILTER_MONITOR_INTEGRACIO"
                springFilterBuilder={springFilterBuilder}
                onSpringFilterChange={setSpringFilter}
                componentProps={{ sx: { mb: 0, mt: 0 } }}
                footer={
                    <CustomTabs
                        options={options}
                        report={report}
                        selectedTab={selectedTab}
                        onTabChange={onTabChange}
                    />
                }
            >
                <ContentFilter />
                <FilterCount springFilter={springFilter} />
            </StyledMuiFilter>
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
            flex: 1.75,
        },
        {
            field: 'descripcio',
            flex: 4,
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
                return <ChipEstat estat={params?.value} label={params.formattedValue} />;
            },
        },
    ];
    const buttons = useCloseDialogButtons();
    const [showDialog, dialog] = useContentDialog(buttons);

    const openDialog = (id: string) => {
        showDialog(
            t('page.integracio.detall.title'),
            <MonitorIntegracioParamDetail id={id} />,
            undefined,
            {
                maxWidth: 'lg',
                fullWidth: true,
            }
        );
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

    return (
        <>
            <PageTitle title={t('page.integracio.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.integracio} title={t('page.integracio.grid.title')}>
                    <StyledMuiGrid
                        resourceName="monitorIntegracioResource"
                        columns={columns}
                        fixedFilter={staticFilter}
                        paginationActive
                        toolbarHideQuickFilter
                        readOnly
                        toolbarAdditionalRow={
                            <MonitorIntegracioGridFilter
                                options={options}
                                report={report}
                                selectedTab={selectedTab}
                                onTabChange={setSelectedTab}
                            />
                        }
                        onRowClick={(params: any) => openDialog(params.id)}
                        sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
                    />
                    {dialog}
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default MonitorIntegracioGrid;
