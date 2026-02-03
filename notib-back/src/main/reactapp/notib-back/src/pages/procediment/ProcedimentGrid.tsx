import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { GridPage, MuiDataGrid } from 'reactlib';

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
        field: 'cie',
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
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.procediment.grid.title')}
                resourceName="procedimentResource"
                columns={columns}
                staticFilter="tipus:'PROCEDIMENT'"
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default ProcedimentGrid;
