import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import LinkToTab from '../../components/LinkToTab';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { ROLE_ADMIN_LECTURA, useNotibContext } from '../../components/NotibContext';

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
                <LinkToTab id={params.id} tab={1}>
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
                <LinkToTab id={params.id} tab={2}>
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

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="nom" />
            <GridFormField size={3} name="organGestorEmissor" optionRenderer={organGestorOptionRenderer} />
            <GridFormField size={3} name="organGestorPagador" optionRenderer={organGestorOptionRenderer} />
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

export const PagadorCieGrid: React.FC = () => {

    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const filterDataGridProps = useDatagridFilterProps(
        'pagadorCieResource',
        'FILTER_PAGADOR_CIE',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.pagadorCie.grid.title')}
                resourceName="pagadorCieResource"
                columns={columns}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                toolbarHideCreate={isRoleAdminLectura ? true : undefined}
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                rowHideUpdateButton={isRoleAdminLectura}
                rowHideDeleteButton={isRoleAdminLectura}
            />
        </GridPage>
    );
};

export default PagadorCieGrid;
