import { Grid, Icon, IconButton } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    FilterApi,
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    MuiFilter,
    useFilterApiRef,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
    const { t } = useTranslation();

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="entitat" />
            <GridFormField size={4} name="assumpte" />
            <GridFormField size={1.5} name="dataInici" />
            <GridFormField size={1.5} name="dataFinal" />
            <GridFormField size={1.5} name="avisNivell" />
            <GridFormField size={1} name="actiu" />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

const AvisGridFilter: React.FC = () => {
    const filterApiRef = useFilterApiRef();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('entitat.id', data?.entitat?.id),
            filterBuilder.like('assumpte', data.assumpte),
            data?.dataInici && filterBuilder.gte('dataInici', `'${formatStartOfDay(data?.dataInici)}'`),
            data?.dataFinal && filterBuilder.lte('dataFinal', `'${formatEndOfDay(data?.dataFinal)}'`),
            filterBuilder.eq('avisNivell', `'${data?.avisNivell}'`),
            filterBuilder.eq('actiu', `'${data?.actiu}'`)
        );
    };

    return (
        <MuiFilter
            resourceName="avisResource"
            code="FILTER_AVIS"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

export const AvisGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'entitat',
                flex: 2,
            },
            {
                field: 'assumpte',
                flex: 4,
            },
            {
                field: 'dataInici',
                fieldType: 'date',
                flex: 1,
            },
            {
                field: 'dataFinal',
                fieldType: 'date',
                flex: 1,
            },
            {
                field: 'avisNivell',
                flex: 0.6,
            },
            {
                field: 'actiu',
                flex: 0.6,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.avisos.grid.title')}
                resourceName="avisResource"
                columns={columns}
                paginationActive
                toolbarBulkDelete
                toolbarCreateLink="form"
                toolbarAdditionalRow={<AvisGridFilter />}
                toolbarHideQuickFilter
                //rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default AvisGrid;
