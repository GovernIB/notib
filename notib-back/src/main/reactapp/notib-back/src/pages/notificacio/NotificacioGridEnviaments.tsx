import React from 'react';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { useResourceApiService } from 'reactlib';
import { useEnviamentDetailDialog } from '../enviament/EnviamentDetailDialog';

export const NotificacioGridEnviaments: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService(
        'notificacioEnviamentResource'
    );
    const { dialogComponent, onDetailClick } = useEnviamentDetailDialog();
    const [enviaments, setEnviaments] = React.useState<any[]>();
    React.useEffect(() => {
        if (apiIsReady) {
            const args = {
                filter: 'notificacio.id:' + id,
                unpaged: true,
            };
            apiFind(args).then((response) => {
                setEnviaments(response.rows);
            });
        }
    }, [apiIsReady]);
    return (
        enviaments != null && (
            <TableContainer
                component={Paper}
                elevation={2}
                sx={{
                    mx: 2,
                    my: 2,
                    width: 'calc(100% - 32px)',
                }}
            >
                <Table size="small" aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.interessat')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.representant')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.estatPostal')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.estatTelematica')}
                            </TableCell>
                            <TableCell></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {enviaments.map((e) => (
                            <TableRow key={e.id}>
                                <TableCell component="th" scope="row">
                                    {e.titular.description}
                                </TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row" sx={{ width: '1px' }}>
                                    <Button
                                        variant="outlined"
                                        size="small"
                                        startIcon={<Icon>info</Icon>}
                                        onClick={() => onDetailClick(e.id)}
                                    >
                                        Detalls
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                {dialogComponent}
            </TableContainer>
        )
    );
};

export default NotificacioGridEnviaments;
