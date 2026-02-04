import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { GridPage, MuiDataGrid } from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';

const columns = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 4,
    },
    {
        field: 'organGestor',
        flex: 1,
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
        field: 'entregaCie',
        flex: 0.6,
    },
    {
        field: 'comu',
        flex: 0.6,
    },
    {
        field: 'requireDirectPermission',
        flex: 0.6,
    },
    {
        field: 'manual',
        flex: 0.6,
    },
    {
        field: 'actiu',
        flex: 0.6,
    },
    {
        field: 'grupCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
    {
        field: 'aclEntryCount',
        flex: 0.6,
        renderCell: (params: any) => {
            return (
                <Chip
                    label={params.value}
                    color={params.value ? 'primary' : undefined}
                    size="small"
                />
            );
        },
    },
];
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
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default ProcedimentGrid;
