import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import {
    GridPage,
    MuiDataGrid,
    useFilterApiContext,
    springFilterBuilder as filterBuilder,
    useMuiDataGridApiRef,
    useMuiActionReportLogic,
    useMuiDataGridContext, useFormContext,
} from 'reactlib';
import { Grid, IconButton } from '@mui/material';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useNotificacioDetailDialog } from '../notificacio/NotificacioDetailDialog';
import AccionsMassives, { MenuOption } from '../../components/AccionsMassives';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { useEnviamentDetailDialog } from './EnviamentDetailDialog';
import {
    ENVIAMENT_ESTAT_MAP,
    generateGridRowStylesFromMap,
    getGridRowColorClass,
} from '../../utils/estatConfig';
import {useSearchParams} from "react-router-dom";

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
        data?.dataEnviamentInici &&
            filterBuilder.gte('enviatDate', `'${formatStartOfDay(data?.dataEnviamentInici)}'`),
        data?.dataEnviamentFi &&
            filterBuilder.lte('enviatDate', `'${formatEndOfDay(data?.dataEnviamentFi)}'`),
        data?.dataCreacioInici &&
            filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataCreacioInici)}'`),
        data?.dataCreacioFi &&
            filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataCreacioFi)}'`),
        data?.enviamentDataProgramadaInici &&
            filterBuilder.gte(
                'enviamentDataProgramada',
                `'${formatStartOfDay(data?.enviamentDataProgramadaInici)}'`
            ),
        data?.enviamentDataProgramadaFi &&
            filterBuilder.lte(
                'enviamentDataProgramada',
                `'${formatEndOfDay(data?.enviamentDataProgramadaFi)}'`
            ),
        filterBuilder.like('notificaReferencia', data.notificaReferencia),
        filterBuilder.like('grupCodi', data.grupCodi),
        filterBuilder.eq('organId', data?.organGestor?.id),
        filterBuilder.like('procedimentId', data?.procedimentServei?.id),
        filterBuilder.eq('createdBy', `'${data?.createdBy}'`),
        filterBuilder.like('notificacioDescripcio', data?.notificacioDescripcio),
        filterBuilder.or(
            filterBuilder.like('titularNom', data.titularNomNif),
            filterBuilder.like('titularNif', data.titularNomNif)
        ),
        filterBuilder.like('representantsString', data?.representantsString),
        filterBuilder.like('registreNumeroFormatat', data.numRegistre),
        data?.dataCaducitatInici &&
            filterBuilder.gte(
                'notificaDataCaducitat',
                `'${formatStartOfDay(data?.dataCaducitatInici)}'`
            ),
        data?.dataCaducitatFi &&
            filterBuilder.lte(
                'notificaDataCaducitat',
                `'${formatEndOfDay(data?.dataCaducitatFi)}'`
            ),
        filterBuilder.like('referenciaEnviament', data.referenciaEnviament),
        filterBuilder.like('referenciaNotificacio', data.referenciaNotificacio),
        filterBuilder.like('codiCsvUuidDocument', data.codiCsvUuidDocument),
        filterBuilder.eq('entregaPostalActiva', `'${data.entregaPostalActiva}'`)
    );
};

const MassiveActionsButton: React.FC = () => {
    const { selection } = useMuiDataGridContext();

    const { exec: execExemple } = useMuiActionReportLogic(
        'notificacioEnviamentResource',
        undefined,
        'EXPORTAR_EXCEL',
        'CSV'
    );

    const opcionsMenu: MenuOption[] = [
        {
            label: 'Exportar a EXCEL',
            onClick: () => {
                execExemple(selection?.ids);
            },
        },
        {
            label: 'Tornar a enviar les que han donat error',
            onClick: () => console.log('Tornar a enviar les que han donat error'),
        },
        {
            label: 'Actualitzar estat',
            onClick: () => console.log('Actualitzar estat'),
        },
        {
            label: 'Anul·lar',
            onClick: () => console.log('Anul·lar'),
        },
        {
            label: 'Ampliar termini',
            onClick: () => console.log('Ampliar termini'),
        },
    ];

    return <AccionsMassives options={opcionsMenu} sizeSelection={selection?.ids?.size} />;
};

const ContentFilter: React.FC = () => {
    const filterApiRef = useFilterApiContext();
    const { t } = useTranslation();
    const [advancedFilter, setAdvancedFilter] = React.useState(false);
    const {apiRef, isReady} = useFormContext();
    const [searchParams] = useSearchParams();
    const referencia = searchParams.get('referencia');
    React.useEffect(() => {
        if (!isReady || !referencia) {
            return;
        }
        apiRef?.current?.setFieldValue('referenciaEnviament', referencia);
        advancedFilterClick();
    }, [apiRef, isReady, referencia]);

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    const advancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };
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
                    <GridFormField size={4} name="organGestor" />
                    <GridFormField size={3} name="procedimentServei" />
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
                    <GridButtonField
                        size={0.5}
                        name="entregaPostalActiva"
                        icon={'email'}
                        hiddenLabel
                    />
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton
                    onClick={advancedFilterClick}
                    title={t(
                        advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat'
                    )}
                >
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>
                        filter_list
                    </Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const EnviamentGrid = () => {

    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const { dialogComponent: enviamentDialogComponent, onDetailClick } = useEnviamentDetailDialog();
    const { dialogComponent: notificacioDialogComponent, onDetailClick: onNotificacioDetailClick } = useNotificacioDetailDialog();
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioEnviamentResource',
        'FILTER_ENVIAMENT',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                apiRef={gridApiRef}
                title={t('page.enviament.grid.title')}
                resourceName="notificacioEnviamentResource"
                columns={columns}
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
                        element: <MassiveActionsButton />,
                    },
                ]}
                onRowClick={(params) => onDetailClick(params.id)}
                rowActionsColumnIndex={19}
                rowAdditionalActions={[
                    {
                        label: t('page.enviament.grid.detalls'),
                        title: t('page.enviament.grid.detalls'),
                        icon: 'info',
                        showInMenu: false,
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
                        // onClick: (id) => onNotificacioDetailClick(id), // TODO FALTA AFEGIR L'ACCIÓ I QUE ES MOSTRI L'ENTRADA DEL MENU SEGONS CONDICIO
                    },
                    {
                        label: t('page.enviament.grid.ampliarTermini'),
                        title: t('page.enviament.grid.ampliarTermini'),
                        icon: 'calendar_month',
                        showInMenu: true,
                        // onClick: (id) => onNotificacioDetailClick(id), // TODO FALTA AFEGIR L'ACCIÓ I QUE ES MOSTRI L'ENTRADA DEL MENU SEGONS CONDICIO
                        // hidden: (row) => isRolActualAdministradorLectura && !row?.plazoAmpliable,
                    },
                ]}
                getRowClassName={(params) =>
                    getGridRowColorClass(params.row.notificaEstat, ENVIAMENT_ESTAT_MAP)
                }
                sx={generateGridRowStylesFromMap(ENVIAMENT_ESTAT_MAP)}
            />
            {enviamentDialogComponent}
            {notificacioDialogComponent}
        </GridPage>
    );
};

export default EnviamentGrid;
