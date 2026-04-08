import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    GridPage,
    MuiDataGrid,
    MuiFilter,
    FilterApi,
    useFilterApiRef,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../components/GridFormField';
import { Icon, IconButton } from '@mui/material';
import { formatEndOfDay, formatStartOfDay } from '../utils/dateUtils';

const columns = [
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'organGestor',
        flex: 6,
    },
    {
        field: 'contracteNum',
        flex: 2,
    },
    {
        field: 'contracteDataVig',
        flex: 3,
    },
    {
        field: 'facturacioClientCodi',
        flex: 2,
    },
];

const PagadorPostalForm: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={6} name="nom" />
            <Grid size={6} />
            <GridFormField size={6} name="organGestor" />
            <GridFormField size={6} name="contracteNum" />
            <GridFormField size={6} name="facturacioClientCodi" />
            <GridFormField size={6} name="contracteDataVig" />
        </Grid>
    );
};

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestor" />
            <GridFormField size={1.5} name="contracteNum" />
            <GridFormField size={1.75} name="contracteDataVigInici" />
            <GridFormField size={1.75} name="contracteDataVigFinal" />
            <GridFormField size={1.5} name="facturacioClientCodi" />
            <Grid size={0.5}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const AvisGridFilter: React.FC = () => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('nom', data.nom),
            filterBuilder.eq('organGestor.id', data?.organGestor?.id),
            filterBuilder.like('contracteNum', data.contracteNum),
            data?.contracteDataVigInici &&
                filterBuilder.gte(
                    'contracteDataVig',
                    `'${formatStartOfDay(data?.contracteDataVigInici)}'`
                ),
            data?.contracteDataVigFinal &&
                filterBuilder.lte(
                    'contracteDataVig',
                    `'${formatEndOfDay(data?.contracteDataVigFinal)}'`
                ),
            filterBuilder.like('facturacioClientCodi', data.facturacioClientCodi)
        );
    };

    return (
        <MuiFilter
            resourceName="pagadorPostalResource"
            code="FILTER_PAGADOR_POSTAL"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const PagadorsPostals: React.FC = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.pagadorPostal.grid.title')}
                resourceName="pagadorPostalResource"
                columns={columns}
                paginationActive
                toolbarHideQuickFilter
                toolbarAdditionalRow={<AvisGridFilter />}
                popupEditActive
                popupEditFormContent={<PagadorPostalForm />}
                popupEditFormDialogResourceTitle={t('page.pagadorPostal.grid.popupResourceTitle')}
            />
        </GridPage>
    );
};

export default PagadorsPostals;
