import React from 'react';
import {useTranslation} from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import {
    GridPage,
    MuiActionReportButton,
    MuiDataGrid,
    MuiDataGridApiRef,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useBaseAppContext,
    useFilterApiContext,
    useMuiDataGridApiRef,
    useResourceApiService,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import {OrganGestorNoVigentIcon, useOrganGestorOptionRenderer} from '../../components/OrganGestorOptionRenderer';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps, useDatagridTreeData,} from '../../hooks/useDataGrid';
import {useSse} from '../../hooks/useSse';
import {OrganFormContent} from './OrganForm';
import {FormGroup} from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";
import Switch from "@mui/material/Switch";
import Dir3SyncBranch, { Dir3SyncNode } from './Dir3SyncBranch';
import OrgansProcedimentsSyncActionButton from './OrgansProcedimentsSyncActionButton';
import {ROLE_ADMIN_LECTURA, useNotibContext} from '../../components/NotibContext';

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
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
        renderCell: (params: any) => (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <span>{params.formattedValue}</span>
                {params.value != null && params.value !== 'V' && <OrganGestorNoVigentIcon />}
            </Box>
        ),
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
                    <LinkToTab id={params.id} tab={1} clickEnabled>
                        <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
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

const useColumns = (treeDataActive: boolean) => {
    return !treeDataActive ? columns
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
    return { treeDataViewActive, viewSwitchComponent};
};

const OrganGridDir3SyncLoading: React.FC<{ percent?: number; message?: string }> = (props) => {

    const { percent, message } = props;
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <Box sx={{ textAlign: 'center', my: 4 }}>
                <CircularProgress variant="indeterminate" size={50} />
                {percent != null && (
                    <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                        {percent}%
                    </Typography>
                )}
                <Typography variant="body2">{message}</Typography>
            </Box>
        </Box>
    );
};

const toNode = (item: any): Dir3SyncNode => ({
    codi: item.codi,
    nom: item.nomCooficial || item.nom,
});

const Dir3SyncSection: React.FC<{ title: string; children: React.ReactNode }> = ({ title, children }) => (
    <Box className="dir3-section">
        <Typography className="dir3-section-title">{title}</Typography>
        {children}
    </Box>
);

const dir3SyncChangeKeys = ['creacions', 'modificacions', 'substitucions', 'extincions', 'fusions', 'divisions'] as const;

const hasCanvis = (result: any) =>
    dir3SyncChangeKeys.some((k) => result?.[k]?.length > 0);

const OrganGridDir3SyncActionResults: React.FC<{ result: any }> = (props) => {

    const { result } = props;
    const { t } = useTranslation();
    if (!hasCanvis(result)) {
        return <Typography>{t('page.organs.grid.sync.dialogButton.senseCanvis')}</Typography>;
    }
    return (
        <Grid container id="dir3-sync-preview">
            <Grid size={12}>
                {result.divisions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.divisions')}>
                        {result.divisions.map((d: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(d.vell)}
                                rootColor="red"
                                leaves={d.nous.map((n: any) => ({ node: toNode(n), color: 'green' as const }))}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.fusions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.fusions')}>
                        {result.fusions.map((f: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="right"
                                root={toNode(f.nou)}
                                rootColor="green"
                                leaves={f.vells.map((n: any) => ({ node: toNode(n), color: 'red' as const }))}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.substitucions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.substitucions')}>
                        {/* NOTE: for substitucions (and fusions/divisions), OrganGestorSyncHelper's DTO
                            has vell/nou meaning the OPPOSITE of what the names suggest: `vell` is the
                            SURVIVING (vigent) org, `nou` is the one going EXTINCT — see
                            OrganGestorSyncHelper.java's substitucionsMap construction (key=vigent
                            successor, value=extinct code). Do not "fix" this to look like modificacions
                            (where vell=old/nou=new correctly) — it's a different field, confirmed by
                            tracing getDir3SyncNodesExistentsDarreraVersioExtincio's estat checks. */}
                        {result.substitucions.map((s: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="right"
                                root={toNode(s.vell)}
                                rootColor="green"
                                leaves={[{ node: toNode(s.nou), color: 'red' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.modificacions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.modificacions')}>
                        {result.modificacions.map((m: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(m.vell)}
                                rootColor="green"
                                leaves={[{ node: toNode(m.nou), color: 'yellow' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.creacions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.creacions')}>
                        {result.creacions.map((c: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={null}
                                rootColor="green"
                                leaves={[{ node: toNode(c.nou), color: 'green' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.extincions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.extincions')}>
                        {result.extincions.map((e: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(e.vell)}
                                rootColor="red"
                                leaves={[{ node: null, color: 'red' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
            </Grid>
        </Grid>
    );
};

const useDir3JsonDownload = () => {
    const { t } = useTranslation();
    const { saveAs, temporalMessageShow } = useBaseAppContext();
    const { artifactReport } = useResourceApiService('organGestorResource');
    return React.useCallback(() => {
        artifactReport(undefined, { code: 'REPORT_DESCARREGAR_DIR3_JSON', data: {}, fileType: 'CUSTOM' })
            .then((result: any) => {
                const blob = result?.blob instanceof Blob ? result.blob : new Blob([JSON.stringify(result.blob, null, 2)], { type: 'application/json; charset=utf-8' });
                saveAs?.(blob, result.fileName ?? 'organsDir3JSON.json');
            })
            .catch(() => {
                temporalMessageShow(null, t('page.organs.grid.sync.dialogButton.descarregarJsonError'), 'error');
            });
    }, [artifactReport, saveAs, temporalMessageShow, t]);
};

const Dir3SyncResultActions: React.FC<{ result: any }> = () => {
    const { t } = useTranslation();
    const downloadJson = useDir3JsonDownload();
    const printPdf = () => window.print();
    return (
        <Box
            className="dir3-sync-no-print"
            sx={{
                display: 'flex',
                justifyContent: 'flex-end',
                gap: 1,
                mt: 3,
                pt: 2,
                borderTop: 1,
                borderColor: 'divider',
            }}
        >
            <Button variant="outlined" size="small" onClick={downloadJson} startIcon={<Icon>download</Icon>}>
                {t('page.organs.grid.sync.dialogButton.descarregarJson')}
            </Button>
            <Button variant="outlined" size="small" onClick={printPdf} startIcon={<Icon>picture_as_pdf</Icon>}>
                {t('page.organs.grid.sync.dialogButton.descarregarPdf')}
            </Button>
        </Box>
    );
};

const OficinesSyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef; }> = (props) => {

    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.sync.oficines.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.organs.grid.sync.oficines.actualitzar'),
            icon: 'check',
            componentProps: { variant: 'contained' },
        },
    ];
    return (
        <MuiActionReportButton
            resourceName="organGestorResource"
            action="OFICINES_SYNC"
            title={t('page.organs.grid.sync.oficines.title')}
            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
            formDialogTitle={t('page.organs.grid.sync.oficines.title')}
            formDialogButtons={formDialogButtons}
            buttonIcon="refresh"
            onSuccess={resposta => {
                const msg = resposta?.ok ? "success" : "error";
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.organs.grid.sync.oficines.' + msg), msg);
            }}
            // onError={error => temporalMessageShow(null, error?.message, "error")}
        />
    );
}

const OrganGridDir3SyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef; }> = (props) => {

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

        setSenseCanvis(!hasCanvis(result));
        setSimular(false);
        return (
            <>
                <OrganGridDir3SyncActionResults result={result} />
                {hasCanvis(result) && <Dir3SyncResultActions result={result} />}
            </>
        );
    };

    const handleSuccess = (result?: any) => {

        if (result.simulat) {
            return;
        }
        dataGridApiRef.current?.refresh();
        temporalMessageShow(null, t('page.organs.grid.sync.success'), 'success');
    };

    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.sync.dialogButton.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.organs.grid.sync.dialogButton.sincronitzar'),
            icon: 'save',
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
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={1} name="codi" />
            <GridFormField size={2} name="nom" />
            <GridFormField size={4} name="pare" optionRenderer={organGestorOptionRenderer} />
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
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const { treeDataViewActive, viewSwitchComponent } = useTreeDataViewSwitch(t('page.organs.grid.viewSwitch'), true);
    const columns = useColumns(treeDataViewActive);
    const treeDataProps = useDatagridTreeData(
        treeDataViewActive,
        t('page.organs.grid.groupColumn'),
        false,
        1,
        { flex: 6 }
    );
    const filterDataGridProps = useDatagridFilterProps(
        'organGestorResource',
        'FILTER_ORGAN_GESTOR',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.organs.grid.title')}
                resourceName="organGestorResource"
                columns={columns}
                {...treeDataProps}
                // persistentStateActive
                // persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                popupEditActive
                popupEditFormContent={<OrganFormContent />}
                popupEditFormDialogResourceTitle={t('page.organs.grid.popupDialogTitle')}
                popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                toolbarHideCreate={isRoleAdminLectura ? true : undefined}
                rowHideUpdateButton={isRoleAdminLectura}
                rowHideDeleteButton={isRoleAdminLectura}
                toolbarElementsWithPositions={[
                    {
                        position: 1,
                        element: viewSwitchComponent,
                    },
                    {
                        position: 2,
                        element: <OficinesSyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                    {
                        position: 2,
                        element: <OrganGridDir3SyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                    {
                        position: 2,
                        element: <OrgansProcedimentsSyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                ]}
                apiRef={dataGridApiRef}
                density="compact"
            />
        </GridPage>
    );
};

export default OrganGrid;
