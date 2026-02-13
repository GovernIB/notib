import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { GridPage, MuiDataGrid } from 'reactlib';

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

export const PagadorCieGrid: React.FC = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.pagadorCie.grid.title')}
                resourceName="pagadorCieResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default PagadorCieGrid;
