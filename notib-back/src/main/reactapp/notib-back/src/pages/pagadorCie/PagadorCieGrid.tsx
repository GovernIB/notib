import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import {
    FilterApi,
    GridPage,
    MuiDataGrid,
    MuiFilter,
    useFilterApiRef,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { Grid, Icon, IconButton } from '@mui/material';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';

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
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
        flex: 1,
    },
    {
        field: 'sobreCount',
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
        flex: 1,
    },
];

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestorEmissor" />
            <GridFormField size={3} name="organGestorPagador" />
            <GridFormField size={1.75} name="contracteDataVigInici" />
            <GridFormField size={1.75} name="contracteDataVigFinal" />
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
            filterBuilder.eq('organEmisor.id', data?.organGestorEmissor?.id), //TODO: Revisar aquest filtre, el backend no te aquest camp a l'entity
            filterBuilder.eq('organGestor.id', data?.organGestorPagador?.id),
            data?.contracteDataVigInici &&
                filterBuilder.gte(
                    'contracteDataVig',
                    `'${formatStartOfDay(data?.contracteDataVigInici)}'`
                ),
            data?.contracteDataVigFinal &&
                filterBuilder.lte(
                    'contracteDataVig',
                    `'${formatEndOfDay(data?.contracteDataVigFinal)}'`
                )
        );
    };

    return (
        <MuiFilter
            resourceName="pagadorCieResource"
            code="FILTER_PAGADOR_CIE"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const PagadorCieGrid: React.FC = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.pagadorCie.grid.title')}
                resourceName="pagadorCieResource"
                columns={columns}
                paginationActive
                toolbarHideQuickFilter
                toolbarAdditionalRow={<AvisGridFilter />}
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default PagadorCieGrid;
