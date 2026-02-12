import { useTranslation } from 'react-i18next';
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
