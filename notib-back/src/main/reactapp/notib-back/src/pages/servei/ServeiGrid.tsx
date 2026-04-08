import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import {
    FilterApi,
    GridPage,
    MuiDataGrid,
    MuiFilter,
    useFilterApiRef,
    springFilterBuilder as filterBuilder,
    MuiDataGridColDef,
} from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { Grid, Icon, IconButton } from '@mui/material';
import LinkToTab from '../../components/LinkToTab';

const columns: MuiDataGridColDef[] = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 2,
    },
    {
        field: 'organGestor',
        flex: 3,
    },
    {
        field: 'retard',
        flex: 0.6,
    },
    {
        field: 'caducitat',
        flex: 0.6,
    },
    {
        field: 'entregaCieActiva',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'comu',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'requireDirectPermission',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'manual',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'actiu',
        flex: 0.6,
        type: 'boolean',
    },
    {
        field: 'grupCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} tab={1}>
                    <Chip
                        label={params.value}
                        color={params.value ? 'primary' : undefined}
                        size="small"
                    />
                </LinkToTab>
            );
        },
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <LinkToTab id={params.id} tab={2}>
                    <Chip
                        label={params.value}
                        color={params.value ? 'primary' : undefined}
                        size="small"
                    />
                </LinkToTab>
            );
        },
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
            <GridFormField size={1} name="codi" />
            <GridFormField size={3} name="nom" />
            <GridFormField size={4} name="organGestor" />
            <GridButtonField size={0.5} name="actiu" icon={'flash_on'} hiddenLabel />
            <GridButtonField size={0.5} name="comu" icon={'public'} hiddenLabel />
            <GridButtonField size={0.5} name="entregaCieActiva" icon={'email'} hiddenLabel />
            <GridButtonField size={0.5} name="manual" icon={'sync'} hiddenLabel />
            <GridButtonField
                size={0.5}
                name="requireDirectPermission"
                icon={'gpp_good'}
                hiddenLabel
            />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

const ProcedimentGridFilter: React.FC = () => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('codi', data.codi),
            filterBuilder.like('nom', data.nom),
            filterBuilder.eq('organGestor.id', data.organGestor?.id),
            data?.actiu && filterBuilder.eq('actiu', `'${data.actiu}'`),
            data?.comu && filterBuilder.eq('comu', `'${data.comu}'`),
            data?.entregaCieActiva ? filterBuilder.neq('entregaCie', null) : undefined,
            data?.manual && filterBuilder.eq('manual', `'${data.manual}'`),
            data?.requireDirectPermission &&
                filterBuilder.eq('requireDirectPermission', `'${data.requireDirectPermission}'`)
        );
    };

    return (
        <MuiFilter
            resourceName="procedimentResource"
            code="FILTER_PROCEDIMENT"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const ProcedimentGrid = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = useNotibContext();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.serveis.grid.title')}
                resourceName="procedimentResource"
                columns={columns}
                staticFilter={"tipus:'SERVEI' and entitat.id:" + currentEntitatId}
                paginationActive
                toolbarAdditionalRow={<ProcedimentGridFilter />}
                toolbarHideQuickFilter
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default ProcedimentGrid;
