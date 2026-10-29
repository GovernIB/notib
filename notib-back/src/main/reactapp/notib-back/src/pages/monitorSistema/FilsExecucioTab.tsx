import Box from '@mui/material/Box';
import StyledMuiGrid from '../../components/StyledMuiGrid';

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
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <StyledMuiGrid
                resourceName="threadInfoResource"
                columns={columnsFils}
                readOnly
                sortModel={sortModelFils}
            />
        </Box>
    );
};

export default FilsExecucioTab;
