import { GridPage, MuiDataGrid } from 'reactlib';

const FilsExecucioTab: React.FC = () => {
    const columnsFils = [
        {
            field: 'threadName',
            flex: 5,
        },
        {
            field: 'tiempoCPU',
            flex: 1,
        },
        {
            field: 'threadState',
            flex: 1,
        },
        {
            field: 'waitedTime',
            flex: 1,
        },
        {
            field: 'blockedTime',
            flex: 1,
        },
    ];

    const sortModelFils: any[] = [{ field: 'threadId', sort: 'asc' }];

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=""
                resourceName="threadInfoResource"
                columns={columnsFils}
                readOnly
                sortModel={sortModelFils}
            />
        </GridPage>
    );
};

export default FilsExecucioTab;
