import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    springFilterBuilder as filterBuilder, useFormContext, useMuiDataGridApiRef,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext';
import PageTitle from "../../components/PageTitle.tsx";
import React from "react";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import {useTabParam} from "../../hooks/useSearchParams.ts";
import { FormTabs, FormTabContent } from '../../components/FormTabs';
import Badge from "@mui/material/Badge";
import PagadorCieFormTabFulles from "./PagadorCieFormTabFulles.tsx";
import PagadorCieFormTabSobres from "./PagadorCieFormTabSobres.tsx";
import AddButton from "../../components/AddButton.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const columns = [
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'organGestorEmissor',
        flex: 2,
    },
    {
        field: 'organGestorPagador',
        flex: 2,
    },
    {
        field: 'contracteDataVig',
        flex: 1,
    },
    {
        field: 'fullaCount',
        flex: 1,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={1} clickEnabled>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        }
    },
    {
        field: 'sobreCount',
        flex: 1,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} row={params.row} popup tab={2} clickEnabled>
                    <Chip label={params.value} color={params.value ? 'primary' : undefined} size="small"/>
                </LinkToTab>
            );
        },
    },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('nom', data.nom),
        filterBuilder.eq('organEmisor.id', data?.organGestorEmissor?.id), //TODO: Revisar aquest filtre, el backend no te aquest camp a l'entity
        filterBuilder.eq('organGestor.id', data?.organGestorPagador?.id),
        data?.contracteDataVigInici && filterBuilder.gte('contracteDataVig', `'${formatStartOfDay(data?.contracteDataVigInici)}'`),
        data?.contracteDataVigFinal && filterBuilder.lte('contracteDataVig', `'${formatEndOfDay(data?.contracteDataVigFinal)}'`)
    );
};

const ContentFilter: React.FC = () => {

    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="organGestorEmissor" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="organGestorPagador" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="contracteDataVigInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="contracteDataVigFinal" />
        </>
    );
};

const PagadorCieFormContent: React.FC = () => {

    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();

    const fullesTabLabel = (
        <Badge badgeContent={data.fullaCount} color="primary">
            {t('page.pagadorCie.form.tabs.fulles')}
        </Badge>
    );

    const sobresTabLabel = (
        <Badge badgeContent={data.sobreCount} color="primary">
            {t('page.pagadorCie.form.tabs.sobres')}
        </Badge>
    );

    const tabs = [
        t('page.pagadorCie.form.tabs.dades'),
        { label: fullesTabLabel },
        { label: sobresTabLabel },
    ];

    return (
        <FormTabs tabs={tabs} tabIndexesWithGrids={[1, 2]} initialIndex={initialTab}>
            <FormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={6} name="nom" />
                    <Grid size={6}></Grid>
                    <GridFormField size={6} name="organGestorEmissor" optionRenderer={organGestorOptionRenderer} />
                    <GridFormField size={6} name="organGestorPagador" optionRenderer={organGestorOptionRenderer} />
                    <GridFormField size={6} name="apiKey" />
                    <GridFormField size={6} name="contracteDataVig" />
                    <GridFormField size={6} name="cieExtern" />
                </Grid>
            </FormTabContent>
            <FormTabContent index={1} grid>
                <PagadorCieFormTabFulles />
            </FormTabContent>
            <FormTabContent index={2} grid>
                <PagadorCieFormTabSobres />
            </FormTabContent>
        </FormTabs>
    );
};

export const PagadorCieGrid: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const gridApiRef = useMuiDataGridApiRef()
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'pagadorCieResource',
        'FILTER_PAGADOR_CIE',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <>
            <PageTitle title={t('page.pagadorCie.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.pagadorCie} title={t('page.pagadorCie.grid.title')}>
                    <StyledMuiGrid
                        resourceName="pagadorCieResource"
                        columns={columns}
                        apiRef={gridApiRef}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        toolbarHideCreate
                        toolbarElementsWithPositions={ isRoleAdminLectura ? [] : [
                            { position: 2, element: <AddButton gridApiRef={gridApiRef} text={t('page.pagadorCie.grid.nou')} tooltip={t('page.pagadorCie.grid.nouTooltip')} /> },
                        ]}
                        popupEditActive
                        popupEditFormContent={<PagadorCieFormContent/>}
                        popupEditFormDialogResourceTitle={t('page.pagadorCie.grid.popupResourceTitle')}
                        rowHideUpdateButton={isRoleAdminLectura}
                        rowHideDeleteButton={isRoleAdminLectura}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default PagadorCieGrid;
