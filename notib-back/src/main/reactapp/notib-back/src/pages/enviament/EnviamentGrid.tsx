import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import {
    GridPage,
    MuiDataGrid,
    useFilterApiContext,
    springFilterBuilder as filterBuilder,
    useMuiDataGridApiRef,
    useMuiDataGridContext,
} from 'reactlib';
import { Grid, IconButton } from '@mui/material';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useNotificacioDetailDialog } from '../notificacio/NotificacioDetailDialog';
import AccionsMassives, {MenuOption, MenuOptionDivider, useAccionsMassives} from '../../components/AccionsMassives';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { useEnviamentDetailDialog } from './EnviamentDetailDialog';
import {
    ENVIAMENT_ESTAT_MAP,
    generateGridRowStylesFromMap,
    getGridRowColorClass,
} from '../../utils/estatConfig';
import {useSearchParams} from "react-router-dom";
import useAccionsNotificacio from "../accions/AccionsNotificacio.tsx";
import {ROLE_ADMIN_LECTURA, useNotibContext} from "../../components/NotibContext.ts";
import {GridApiPro, useGridApiRef} from "@mui/x-data-grid-pro";

const columns = [
    {
        field: 'createdDate',
    },
    {
        field: 'enviatDate',
    },
    {
        field: 'enviamentDataProgramada',
    },
    {
        field: 'notificaReferencia',
    },
    {
        field: 'createdBy',
    },
    {
        field: 'notificacioOrganGestor',
    },
    {
        field: 'notificacioProcediment',
    },
    {
        field: 'notificacioConcepte',
    },
    {
        field: 'notificacioDescripcio',
    },
    {
        field: 'titular',
    },
    {
        field: 'representantsString',
    },
    {
        field: 'registreNumeroFormatat',
    },
    {
        field: 'notificaDataCaducitat',
    },
    {
        field: 'tipusEnviament',
    },
    {
        field: 'referenciaEnviament',
    },
    {
        field: 'referenciaNotificacio',
    },
    {
        field: 'codiCsvUuidDocument',
        flex: 2,
    },
    {
        field: 'notificaEstat',
    },
    {
        field: 'entregaPostalActiva',
    },
];

const springFilterBuilder = (data: any) => {

    return filterBuilder.and(
        filterBuilder.eq('tipusEnviament', `'${data?.tipusEnviament}'`),
        filterBuilder.like('notificacioConcepte', data.notificacioConcepte),
        filterBuilder.eq('notificaEstat', `'${data?.notificaEstat}'`),
        data?.dataEnviamentInici && filterBuilder.gte('enviatDate', `'${formatStartOfDay(data?.dataEnviamentInici)}'`),
        data?.dataEnviamentFi && filterBuilder.lte('enviatDate', `'${formatEndOfDay(data?.dataEnviamentFi)}'`),
        data?.dataCreacioInici && filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataCreacioInici)}'`),
        data?.dataCreacioFi && filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataCreacioFi)}'`),
        data?.enviamentDataProgramadaInici && filterBuilder.gte('enviamentDataProgramada', `'${formatStartOfDay(data?.enviamentDataProgramadaInici)}'`),
        data?.enviamentDataProgramadaFi && filterBuilder.lte('enviamentDataProgramada', `'${formatEndOfDay(data?.enviamentDataProgramadaFi)}'`),
        filterBuilder.like('notificaReferencia', data.notificaReferencia),
        filterBuilder.like('grupCodi', data.grupCodi),
        filterBuilder.eq('organId', data?.organGestor?.id),
        filterBuilder.like('procedimentId', data?.procedimentServei?.id),
        filterBuilder.eq('createdBy', `'${data?.createdBy}'`),
        filterBuilder.like('notificacioDescripcio', data?.notificacioDescripcio),
        filterBuilder.or(filterBuilder.like('titularNom', data.titularNomNif), filterBuilder.like('titularNif', data.titularNomNif)),
        filterBuilder.like('representantsString', data?.representantsString),
        filterBuilder.like('registreNumeroFormatat', data.numRegistre),
        data?.dataCaducitatInici && filterBuilder.gte('notificaDataCaducitat', `'${formatStartOfDay(data?.dataCaducitatInici)}'`),
        data?.dataCaducitatFi && filterBuilder.lte('notificaDataCaducitat', `'${formatEndOfDay(data?.dataCaducitatFi)}'`),
        filterBuilder.like('referenciaEnviament', data.referenciaEnviament),
        filterBuilder.like('referenciaNotificacio', data.referenciaNotificacio),
        filterBuilder.like('codiCsvUuidDocument', data.codiCsvUuidDocument),
        filterBuilder.eq('entregaPostalActiva', `'${data.entregaPostalActiva}'`)
    );
};

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>, refresh: () => void }> = ({apiRef, refresh}) => {

    const { selection } = useMuiDataGridContext();
    const { t } = useTranslation();
    const { currentRole} = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    let amagarEntrada = currentRole === 'tothom' || currentRole === 'NOT_ADMIN_LECTURA';
    const { descarregarExcel,
        actualitzarEstat,
        reenviarAmbError,
        reactivarConsulesCanviEstatMassiu,
        reactivarCallbacksMassiu,
        enviarNotificacionsMovilMassiu,
        anularRemesaMassiu, anularRemesaMassiuDialog,
        ampliarTerminiMassiu, ampliarTerminiMassiuDialog
    } = useAccionsMassives("notificacioResource", refresh);

    const opcionsMenu: (MenuOption | MenuOptionDivider)[] = isRoleAdminLectura ? [
        {
            label: t('page.accioMassiva.accions.exportarFullCalcul.label'),
            tooltip: t('page.accioMassiva.accions.exportarFullCalcul.tooltip'),
            onClick: () => descarregarExcel(selection?.ids, "ENVIAMENT"),
        }
    ] : [
        {
            label: t('page.accioMassiva.accions.exportarFullCalcul.label'),
            tooltip: t('page.accioMassiva.accions.exportarFullCalcul.tooltip'),
            onClick: () => descarregarExcel(selection?.ids, "ENVIAMENT"),
        },
        {
            label: t('page.accioMassiva.accions.reenviarAmbError.label'),
            tooltip: t('page.accioMassiva.accions.reenviarAmbError.tooltip'),
            onClick: () => reenviarAmbError(selection?.ids, "ENVIAMENT"),
        },
        {
            label: t('page.accioMassiva.accions.actualitzarEstat.label'),
            tooltip: t('page.accioMassiva.accions.actualitzarEstat.tooltip'),
            onClick: () => actualitzarEstat(selection?.ids, "ENVIAMENT"),
        },
        {
            label: t('page.accioMassiva.accions.anular.label'),
            tooltip: t('page.accioMassiva.accions.anular.labtooltipel'),
            onClick: () => anularRemesaMassiu(null, t('page.accioMassiva.accions.anular.label'), {ids: selection?.ids ? [...selection.ids] : [], seleccioTipus: "ENVIAMENT"})
        },
        {
            label: t('page.accioMassiva.accions.ampliarTermini.label'),
            tooltip: t('page.accioMassiva.accions.ampliarTermini.tooltip'),
            onClick: () => ampliarTerminiMassiu(null, t('page.accioMassiva.accions.ampliarTermini.label'), {ids: selection?.ids ? [...selection.ids] : [], seleccioTipus: "ENVIAMENT"})
        },
        ...(amagarEntrada ? [] : [
            { type: 'divider' } as MenuOptionDivider,
            {
                label: t('page.accioMassiva.accions.reactivarCanviEstat.label'),
                tooltip: t('page.accioMassiva.accions.reactivarCanviEstat.tooltip'),
                onClick: () => reactivarConsulesCanviEstatMassiu(selection?.ids, "ENVIAMENT"),
            },
            {
                label: t('page.accioMassiva.accions.reactivarCallbacks.label'),
                tooltip: t('page.accioMassiva.accions.reactivarCallbacks.tooltip'),
                onClick: () => reactivarCallbacksMassiu(selection?.ids, "ENVIAMENT"),
            },
            {
                label: t('page.accioMassiva.accions.notificacionsMovil.label'),
                tooltip: t('page.accioMassiva.accions.notificacionsMovil.tooltip'),
                onClick: () => enviarNotificacionsMovilMassiu(selection?.ids, "ENVIAMENT"),
            }]
        )
    ];

    return (<>
            <AccionsMassives options={opcionsMenu} apiRef= {apiRef} resource={"notificacioEnviamentResource"} sizeSelection={selection?.ids?.size}/>
            {anularRemesaMassiuDialog}
            {ampliarTerminiMassiuDialog}
        </>
    )
};

const ContentFilter: React.FC<{openByDefault?: boolean}> = ({openByDefault}) => {
    const filterApiRef = useFilterApiContext();
    const { t } = useTranslation();
    const [advancedFilter, setAdvancedFilter] = React.useState(openByDefault ?? false);

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    const advancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };
    const organId = filterApiRef.current?.getData()?.organGestor?.id;
    let procedimentFiltre;
    if (organId) {
        procedimentFiltre = filterBuilder.and(
                            filterBuilder.or(filterBuilder.eq('tipus', `'PROCEDIMENT'`), filterBuilder.eq('tipus', `'SERVEI'`)),
                            filterBuilder.eq('organGestor', organId));
    } else {
        procedimentFiltre = filterBuilder.or(filterBuilder.eq('tipus', `'PROCEDIMENT'`), filterBuilder.eq('tipus', `'SERVEI'`));
    }
    return (
        <Grid container spacing={1}>
            <GridFormField size={2} name="tipusEnviament" />
            <GridFormField size={advancedFilter ? 4 : 2.5} name="notificacioConcepte" />
            <GridFormField size={2.5} name="notificaEstat" />
            <GridFormField size={1.75} name="dataEnviamentInici" />
            <GridFormField size={1.75} name="dataEnviamentFi" />

            {advancedFilter && (
                <>
                    <GridFormField size={2} name="dataCreacioInici" />
                    <GridFormField size={2} name="dataCreacioFi" />
                    <GridFormField size={2} name="enviamentDataProgramadaInici" />
                    <GridFormField size={2} name="enviamentDataProgramadaFi" />
                    <GridFormField size={2} name="notificaReferencia" />
                    <GridFormField size={2} name="grupCodi" />
                    <GridFormField size={4} name="organGestor" namedQueries={`PERM_READ`} />
                    <GridFormField size={3} name="procedimentServei" filter={procedimentFiltre} />
                    <GridFormField size={2} name="createdBy" />
                    <GridFormField size={3} name="notificacioDescripcio" />
                    <GridFormField size={3} name="titularNomNif" />
                    <GridFormField size={3} name="representantsString" />
                    <GridFormField size={2} name="numRegistre" />
                    <GridFormField size={2} name="dataCaducitatInici" />
                    <GridFormField size={2} name="dataCaducitatFi" />
                    <GridFormField size={2} name="referenciaEnviament" />
                    <GridFormField size={2} name="referenciaNotificacio" />
                    <GridFormField size={2} name="codiCsvUuidDocument" />
                    <GridButtonField size={0.5} name="entregaPostalActiva" icon={'email'} hiddenLabel/>
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={advancedFilterClick} title={t(advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat')}>
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>filter_list</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const EnviamentGrid = () => {

    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const { currentRole} = useNotibContext();
    const { dialogComponent: enviamentDialogComponent, onDetailClick } = useEnviamentDetailDialog();
    const { dialogComponent: notificacioDialogComponent, onDetailClick: onNotificacioDetailClick } = useNotificacioDetailDialog(false);
    const [searchParams] = useSearchParams();
    const datagridApiRef = useGridApiRef();
    const [reloadKey, setReloadKey] = React.useState(0);
    const refreshGrid = React.useCallback(() => setReloadKey(k => k + 1), []);
    const referencia = searchParams.get('referencia');
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioEnviamentResource',
        'FILTER_ENVIAMENT',
        springFilterBuilder,
        <ContentFilter openByDefault={!!referencia} />,
        undefined,
        {referenciaEnviament: referencia}
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const {anularRemesa,  anularRemesaDialog, ampliarTermini, ampliarTerminiDialog } = useAccionsNotificacio();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                key={`${reloadKey}`}
                datagridApiRef={datagridApiRef}
                apiRef={gridApiRef}
                title={t('page.enviament.grid.title')}
                resourceName="notificacioEnviamentResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                selectionActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <MassiveActionsButton apiRef={datagridApiRef} refresh={refreshGrid} />,
                    },
                ]}
                onRowClick={(params) => onDetailClick(params.id)}
                rowActionsColumnIndex={19}
                rowAdditionalActions={[
                    {
                        label: t('page.enviament.grid.detalls'),
                        title: t('page.enviament.grid.detalls'),
                        icon: 'info',
                        showInMenu: true,
                        onClick: (id) => onDetailClick(id),
                    },
                    {
                        label: t('page.enviament.grid.remesa'),
                        title: t('page.enviament.grid.remesa'),
                        icon: 'info',
                        showInMenu: true,
                        onClick: (row) => onNotificacioDetailClick(row?.notificacio?.id),
                    },
                    {
                        label: t('page.enviament.grid.anular'),
                        title: t('page.enviament.grid.anular'),
                        icon: 'do_disturb',
                        showInMenu: true,
                        onClick: (id, row) => {
                            anularRemesa(row?.notificacio?.id, t('page.notificacio.grid.accions.anular.modalTitle'), {enviamentId:id})
                        },
                        hidden: row => !row.isAnulable
                    },
                    {
                        label: t('page.enviament.grid.ampliarTermini'),
                        title: t('page.enviament.grid.ampliarTermini'),
                        icon: 'calendar_month',
                        showInMenu: true,
                        onClick: (id, row) => {
                            ampliarTermini(row?.notificacio?.id, t('page.notificacio.grid.accions.ampliarTermini.modalTitle'), {enviamentId:id, caducitat: row.caducitat})
                        },
                        hidden: row => currentRole === 'NOT_ADMIN_LECTURA' || row?.entregaPostalActiva || row?.notifcacioEstat !== 'ENVIADA',
                    },
                ]}
                getRowClassName={(params) => getGridRowColorClass(params.row.notificaEstat, ENVIAMENT_ESTAT_MAP)}
                sx={generateGridRowStylesFromMap(ENVIAMENT_ESTAT_MAP)}
            />
            {enviamentDialogComponent}
            {notificacioDialogComponent}
            {anularRemesaDialog}
            {ampliarTerminiDialog}
        </GridPage>
    );
};

export default EnviamentGrid;
