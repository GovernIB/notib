import React from 'react';
import { EventSource } from 'eventsource';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import {
    GridPage,
    MuiDataGrid,
    MuiActionReportButton,
    useBaseAppContext,
    useResourceApiService,
    useAuthContext,
    springFilterBuilder as filterBuilder,
    useFilterApiRef,
    MuiFilter,
    FilterApi,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Icon, IconButton } from '@mui/material';

const columns = [
    {
        field: 'codi',
        flex: 2,
    },
    {
        field: 'nom',
        flex: 6,
    },
    {
        field: 'codiPare',
        flex: 2,
    },
    {
        field: 'nomPare',
        flex: 6,
    },
    {
        field: 'llibre',
        flex: 4,
    },
    {
        field: 'estat',
        flex: 2,
    },
    {
        field: 'entregaCieActiva',
        flex: 2,
    },
    {
        field: 'permetreSir',
        flex: 2,
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
];

const useSse = (queueId: string, eventName: string, onEvent: (event: any) => void) => {
    const { getToken } = useAuthContext();
    const { isReady: apiIsReady, currentLinks } = useResourceApiService('sse');
    React.useEffect(() => {
        if (apiIsReady) {
            const subscribeHref = currentLinks['subscribe'].href;
            const eventSourceHref = subscribeHref.replace('{queueId}', queueId);
            const eventSource = new EventSource(eventSourceHref, {
                fetch: (input, init) =>
                    fetch(input, {
                        ...init,
                        headers: {
                            ...init.headers,
                            Authorization: 'Bearer ' + getToken(),
                        },
                    }),
            });
            eventSource.addEventListener(eventName, (event) => {
                const data = JSON.parse(event.data);
                onEvent?.(data);
            });
            eventSource.onerror = () => {
                eventSource.close();
            };
            return () => {
                eventSource.close();
            };
        }
    }, [apiIsReady]);
};

const OrganGridDir3SyncLoading: React.FC = () => {
    const [percent, setPercent] = React.useState<number>();
    const [message, setMessage] = React.useState<string>();
    useSse('PROGRESS', 'DIR3_SYNC', (event: any) => {
        setPercent(event.percent);
        setMessage(event.message);
    });
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <Box sx={{ textAlign: 'center', my: 4 }}>
                <CircularProgress enableTrackSlot variant="determinate" value={percent} size={50} />
                <Typography variant="body2">{message}</Typography>
            </Box>
        </Box>
    );
};

const OrganGridDir3SyncActionResults: React.FC<{ result: any }> = (props) => {
    const { result } = props;
    return (
        <Grid container>
            <Grid size={12}>
                {result.senseCanvis ? (
                    <p>Sense canvis</p>
                ) : (
                    <>
                        <p>Creacions: {result.creacions?.length ?? 0}</p>
                        <p>Modificacions: {result.modificacions?.length ?? 0}</p>
                        <p>Substitucions: {result.substitucions?.length ?? 0}</p>
                        <p>Extincions: {result.extincions?.length ?? 0}</p>
                        <p>Fusions: {result.fusions?.length ?? 0}</p>
                        <p>Divisions: {result.divisions?.length ?? 0}</p>
                        <p>Faci clic al botó d'aplicar per a fer efectius els canvis.</p>
                    </>
                )}
            </Grid>
        </Grid>
    );
};

const OrganGridDir3SyncActionButton: React.FC = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const [simular, setSimular] = React.useState<boolean>(true);
    const [senseCanvis, setSenseCanvis] = React.useState<boolean>();
    const resultProcessor = (result: any) => {
        setSenseCanvis(result.senseCanvis);
        if (result.simulat) {
            setSimular(false);
            return <OrganGridDir3SyncActionResults result={result} />;
        } else {
            setSimular(true);
        }
    };
    const handleSuccess = (result?: any) => {
        if (!result.simulat) {
            temporalMessageShow(null, t('page.organs.grid.sync.success'), 'success');
        }
    };
    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.sync.dialogButton.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: simular
                ? t('page.organs.grid.sync.dialogButton.query')
                : t('page.organs.grid.sync.dialogButton.apply'),
            icon: simular ? 'search' : 'check',
            componentProps: { variant: 'contained', disabled: senseCanvis === true },
        },
    ];
    return (
        <MuiActionReportButton
            resourceName="organGestorResource"
            action="DIR3_SYNC"
            title={t('page.organs.grid.sync.title')}
            icon="sync"
            formAdditionalData={{ simular }}
            formDialogTitle={t('page.organs.grid.sync.dialogTitle')}
            formDialogButtons={formDialogButtons}
            formDialogLoading={<OrganGridDir3SyncLoading />}
            formDialogResultProcessor={resultProcessor}
            buttonComponentProps={{ variant: 'contained' }}
            onSuccess={handleSuccess}
            onClose={() => {
                setSimular(true);
                setSenseCanvis(undefined);
            }}
            dialogAutoSubmit
        />
    );
};

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };

    // TODO: Revisar es camp nomPare i entregaCieActiva/entregaCieDesactivada
    return (
        <Grid container spacing={2}>
            <GridFormField size={1} name="codi" />
            <GridFormField size={2.5} name="nom" />
            <GridFormField size={1} name="codiPare" />
            <GridFormField size={2.25} name="nomPare" />
            <GridFormField size={1.5} name="llibre" />
            <GridFormField size={1.25} name="estat" />
            <GridFormField size={1} name="entregaCieActiva" />
            <GridFormField size={1} name="permetreSir" />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

const OrganGestorGridFilter: React.FC = () => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('codi', data?.codi),
            filterBuilder.like('nom', data?.nom),
            filterBuilder.like('codiPare', data?.codiPare), // TODO: Revisar si aquest camp funciona o no
            filterBuilder.like('nomPare', data?.nomPare),
            filterBuilder.like('llibre', data?.llibre),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            filterBuilder.eq('entregaCieActiva', `'${data?.entregaCieActiva}'`), // TODO: Revisar si aquest camp funciona o no
            filterBuilder.eq('permetreSir', `'${data?.permetreSir}'`)
        );
    };

    return (
        <MuiFilter
            resourceName="organGestorResource"
            code="FILTER_ORGAN_GESTOR"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const OrganGrid = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.organs.grid.title')}
                resourceName="organGestorResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                toolbarAdditionalRow={<OrganGestorGridFilter />}
                toolbarHideQuickFilter
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <OrganGridDir3SyncActionButton />,
                    },
                ]}
            />
        </GridPage>
    );
};

export default OrganGrid;
