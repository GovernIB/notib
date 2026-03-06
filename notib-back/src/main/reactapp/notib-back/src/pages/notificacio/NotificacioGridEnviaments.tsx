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
import { useResourceApiService, useMuiContentDialog } from 'reactlib';
import DataCard from '../../components/DataCard';
import CustomTabs from '../../components/CustomTabs';

const toDataItem = (label: string, value: string) => {
    const item: Record<string, string> = {};
    item[label] = value;
    return item;
};

const NotificacioDialogTabDades: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioEnviamentResource');
    const [enviament, setEnviament] = React.useState<any>();
    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id).then(setEnviament);
        }
    }, [apiIsReady]);
    const serveiTipusField = apiCurrentFields?.find((f) => f.name === 'serveiTipus');
    //const estatField = apiCurrentFields?.find((f) => f.name === 'estat');
    console.log('>>> enviament', enviament);
    console.log('>>> apiCurrentFields', apiCurrentFields);
    const data = serveiTipusField
        ? [
              { 'Identificador de la notificació': '4df434ab-c361-481b-8999-ded3d5b805c6' },
              { "Referència de l'enviament": '33b0dcd5-a5b7-4622-aa13-b27b656294cd' },
              { 'DEH NIF': '' },
              { 'DEH procediment': '' },
              { 'DEH obligada': 'No' },
              toDataItem(serveiTipusField.label, serveiTipusField.options[enviament.serveiTipus]),
              //toDataItem(estatField.label, estatField.options[enviament.estat]),
              { Estat: 'Notificada' },
          ]
        : [];
    return (
        <>
            <DataCard title="Dades de la notificació" data={data} sx={{ mb: 3 }} />
            <DataCard
                title="Dades de l'interessat"
                data={[
                    { NIF: '12345678Z' },
                    { Nom: 'Jaime' },
                    { Llinatges: 'Oleza' },
                    { 'Correu electrònic': 'joleza@dgtic.caib.es' },
                ]}
            />
        </>
    );
};

const NotificacioDialogTabNotifica: React.FC = () => {
    return <span>Notific@</span>;
};

const NotificacioDialogTabRegistre: React.FC = () => {
    return <span>Registre</span>;
};

const NotificacioDialogTabRegistreEsdev: React.FC = () => {
    return <span>Registre d'esdeveniments</span>;
};

const NotificacioGridEnviamentsDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    return (
        <CustomTabs
            tabs={['Dades', 'Notific@', 'Registre', "Registre d'esdeveniments"]}
            contents={[
                <NotificacioDialogTabDades id={id} />,
                <NotificacioDialogTabNotifica />,
                <NotificacioDialogTabRegistre />,
                <NotificacioDialogTabRegistreEsdev />,
            ]}
        />
    );
};

export const NotificacioGridEnviaments: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService(
        'notificacioEnviamentResource'
    );
    const [dialogShow, dialogComponent] = useMuiContentDialog();
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
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            "Detalls de l'enviament",
            <NotificacioGridEnviamentsDialogContent id={id} />,
            undefined,
            {
                maxWidth: 'lg',
                fullWidth: true,
            }
        ).catch(() => null);
    };
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
                                        onClick={() => handleDetailButtonClick(e.id)}
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
