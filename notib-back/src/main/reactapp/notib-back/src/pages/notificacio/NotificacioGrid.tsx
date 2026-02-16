import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid } from 'reactlib';

const columns = [
    {
        field: 'registreData',
        flex: 1,
    },
    {
        field: 'concepte',
        flex: 3,
    },
    {
        field: 'estat',
        flex: 1,
    },
];

const NotificacioGrid = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default NotificacioGrid;
