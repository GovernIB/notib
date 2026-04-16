import React from 'react';
import { EventSource } from 'eventsource';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import Switch from '@mui/material/Switch';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import CircularProgress from '@mui/material/CircularProgress';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridApi,
    MuiActionReportButton,
    useBaseAppContext,
    useResourceApiService,
    useAuthContext,
    useMuiDataGridApiRef,
    springFilterBuilder as filterBuilder,
    MuiDataGridColDef,
    useFilterApiContext,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import {
    useDatagridFilterProps,
    useDatagridPageSizeOptionsProps,
    useDatagridTreeData,
} from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
        flex: 2,
    },
    {
        field: 'nom',
        flex: 6,
    },
    {
        field: 'pare',
        flex: 6,
    },
    {
        field: 'llibre',
        flex: 1,
    },
    {
        field: 'estat',
        flex: 1.5,
    },
    {
        field: 'entregaCieActiva',
        flex: 1,
        type: 'boolean',
    },
    {
        field: 'permetreSir',
        flex: 1.5,
        type: 'boolean',
    },
    {
        field: 'aclEntryCount',
        flex: 1,
        renderCell: (params: any) => {
            return (
                params.value != null && (
                    <LinkToTab id={params.id} tab={1}>
                        <Chip
                            label={params.value}
                            color={params.value ? 'primary' : undefined}
                            size="small"
                        />
                    </LinkToTab>
                )
            );
        },
    },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('codi', data?.codi),
        filterBuilder.like('nom', data?.nom),
        filterBuilder.eq('pare.id', data.pare?.id),
        filterBuilder.like('llibre', data?.llibre),
        filterBuilder.eq('estat', `'${data?.estat}'`),
        data?.entregaCieActiva === 'true'
            ? filterBuilder.neq('entregaCie', null)
            : data?.entregaCieActiva === 'false'
              ? filterBuilder.eq('entregaCie', null)
              : null,
        filterBuilder.eq('permetreSir', `'${data?.permetreSir}'`)
    );
};

const useSse = (
    queueId: string,
    eventName: string,
    onEvent: (event: any) => void,
    closeOnError?: boolean
) => {
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
                closeOnError && eventSource.close();
            };
            return () => {
                eventSource.close();
            };
        }
    }, [apiIsReady]);
};

const useColumns = (treeDataActive: boolean) => {
    return !treeDataActive
        ? columns
        : columns.filter((c) => c.field !== 'codi' && c.field !== 'nom' && c.field !== 'pare');
};

const useTreeDataViewSwitch = (label: string, defaultValue: boolean) => {
    const [treeDataViewActive, setTreeDataViewActive] = React.useState<boolean>(defaultValue);
    const viewSwitchComponent = (
        <FormGroup sx={{ ml: 4 }}>
            <FormControlLabel
                control={
                    <Switch
                        checked={treeDataViewActive}
                        onChange={(event) => setTreeDataViewActive(event.target.checked)}
                        slotProps={{ input: { 'aria-label': 'controlled' } }}
                    />
                }
                label={label}
            />
        </FormGroup>
    );
    return {
        treeDataViewActive,
        viewSwitchComponent,
    };
};

const OrganGridDir3SyncLoading: React.FC<{ percent?: number; message?: string }> = (props) => {
    const { percent, message } = props;
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

const OrganGridDir3SyncActionButton: React.FC<{
    dataGridApiRef: React.RefObject<MuiDataGridApi>;
}> = (props) => {
    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const [simular, setSimular] = React.useState<boolean>(true);
    const [senseCanvis, setSenseCanvis] = React.useState<boolean>();
    const [percent, setPercent] = React.useState<number>();
    const [message, setMessage] = React.useState<string>();
    useSse('PROGRESS', 'DIR3_SYNC', (event: any) => {
        setPercent(event.percent);
        setMessage(event.message);
    });
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
            dataGridApiRef.current.refresh();
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
            buttonIcon="sync"
            formAdditionalData={{ simular }}
            formDialogTitle={t('page.organs.grid.sync.dialogTitle')}
            formDialogButtons={formDialogButtons}
            formDialogLoading={<OrganGridDir3SyncLoading percent={percent} message={message} />}
            formDialogResultProcessor={resultProcessor}
            buttonComponentProps={{ variant: 'contained', sx: { mr: 1 } }}
            onSuccess={handleSuccess}
            onClose={() => {
                setSimular(true);
                setSenseCanvis(undefined);
            }}
            dialogAutoSubmit
        />
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={1} name="codi" />
            <GridFormField size={2} name="nom" />
            <GridFormField size={4} name="pare" />
            <GridFormField size={1} name="llibre" />
            <GridFormField size={1.25} name="estat" />
            <GridFormField size={1} name="entregaCieActiva" />
            <GridFormField size={1} name="permetreSir" />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

export const OrganGrid = () => {
    const { t } = useTranslation();
    const dataGridApiRef = useMuiDataGridApiRef();
    const filterDataGridProps = useDatagridFilterProps(
        'organGestorResource',
        'FILTER_ORGAN_GESTOR',
        springFilterBuilder,
        <ContentFilter />
    );
    const { treeDataViewActive, viewSwitchComponent } = useTreeDataViewSwitch(
        'Vista en arbre',
        true
    );
    const columns = useColumns(treeDataViewActive);
    const treeDataProps = useDatagridTreeData(
        treeDataViewActive,
        t('page.organs.grid.groupColumn'),
        2,
        { flex: 6 }
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.organs.grid.title')}
                resourceName="organGestorResource"
                columns={columns}
                {...treeDataProps}
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                toolbarElementsWithPositions={[
                    {
                        position: 1,
                        element: viewSwitchComponent,
                    },
                    {
                        position: 2,
                        element: <OrganGridDir3SyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                ]}
                apiRef={dataGridApiRef}
                density="compact"
            />
        </GridPage>
    );
};

export default OrganGrid;
