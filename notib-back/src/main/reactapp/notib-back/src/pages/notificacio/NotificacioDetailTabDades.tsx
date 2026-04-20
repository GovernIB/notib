import { Box, Button, Icon, Typography } from '@mui/material';
import { FieldsDataCard } from '../../components/DataCard';
import { useResourceApiService } from 'reactlib';
import React from 'react';
import { useTranslation } from 'react-i18next';

const NotificacioDetailDialogTabEnviaments: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        find: apiFind,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioEnviamentResource');
    const [enviaments, setEnviaments] = React.useState<any[]>();

    React.useEffect(() => {
        if (apiIsReady) {
            apiFind({ filter: 'notificacio.id:' + id, sorts: ['id'], unpaged: true }).then(
                (response) => {
                    console.log('>>> enviaments', response.rows);
                    setEnviaments(response.rows);
                }
            );
        }
    }, [apiIsReady]);

    return (
        <>
            {enviaments?.map((e, i) => (
                <FieldsDataCard
                    key="enviament-{i}"
                    title={t('page.notificacio.detail.enviaments.title') + ' ' + (i + 1)}
                    rows={[
                        { field: 'titular' },
                        { field: 'representant' },
                        { field: 'notificaEstat' },
                        { field: 'registre' },
                        { field: 'certificacio' },
                    ]}
                    fields={apiCurrentFields}
                    data={e}
                    sx={{ mb: 1 }}
                />
            ))}
        </>
    );
};

const NotificacioDetailDialogTabDocuments: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const { isReady: notificacioApiIsReady, getOne: notificacioApiGetOne } =
        useResourceApiService('notificacioResource');
    const {
        isReady: documentApiIsReady,
        find: documentApiFind,
        currentFields: documentApiCurrentFields,
    } = useResourceApiService('documentResource');
    const [documents, setDocuments] = React.useState<any[]>();

    React.useEffect(() => {
        if (notificacioApiIsReady && documentApiIsReady) {
            notificacioApiGetOne(id)
                .then((notificacio) => {
                    const documentIds = [
                        notificacio.document?.id,
                        notificacio.document2?.id,
                        notificacio.document3?.id,
                        notificacio.document4?.id,
                        notificacio.document5?.id,
                    ].filter((id) => id != null);
                    return documentIds;
                })
                .then((documentIds) => {
                    documentApiFind({
                        filter: 'id in (' + documentIds + ')',
                        sorts: ['id'],
                        unpaged: true,
                    }).then((response) => {
                        console.log('>>> documents', response.rows);
                        setDocuments(response.rows);
                    });
                });
        }
    }, [notificacioApiIsReady && documentApiIsReady]);

    return (
        <>
            {documents?.map((e, i) => (
                <FieldsDataCard
                    id="docment-{i}"
                    title={t('page.notificacio.detail.documents.title') + ' ' + (i + 1)}
                    rows={[{ field: 'titular' }]}
                    fields={documentApiCurrentFields}
                    data={e}
                    sx={{ mb: 1 }}
                />
            ))}
        </>
    );
};

const NotificacioDetailDialogTabDades: React.FC<{
    id: any;
    notificacio: any;
    apiCurrentFields: any[] | undefined;
}> = (props) => {
    const { id, notificacio, apiCurrentFields } = props;
    const { t } = useTranslation();
    const procedimentTipusField = apiCurrentFields?.find((f) => f?.name === 'procedimentTipus');

    return (
        <>
            <FieldsDataCard
                title={t('page.notificacio.detail.dades.title')}
                rows={[
                    {
                        field: 'organGestor',
                    },
                    {
                        field: 'procediment',
                        labelRenderer: () =>
                            procedimentTipusField.options[notificacio.procedimentTipus],
                    },
                    {
                        field: 'numExpedient',
                    },
                    {
                        field: 'concepte',
                    },
                    {
                        field: 'idioma',
                    },
                    {
                        field: 'createdBy',
                    },
                    {
                        field: 'createdDate',
                    },
                    {
                        field: 'enviadaDate',
                    },
                    {
                        field: 'caducitat',
                        formatOptions: { noTime: true },
                    },
                    {
                        field: 'retard',
                    },
                    {
                        field: 'estat',
                        valueRenderer: (_value: any, formattedValue: string) => {
                            return (
                                <>
                                    <Typography>
                                        <Icon
                                            sx={{
                                                position: 'relative',
                                                top: 2,
                                                mr: 0.6,
                                                fontSize: 16,
                                            }}
                                        >
                                            schedule
                                        </Icon>
                                        {formattedValue}
                                    </Typography>
                                </>
                            );
                        },
                    },
                ]}
                fields={apiCurrentFields}
                data={notificacio}
                sx={{ mb: 1 }}
            />
            <Box sx={{ textAlign: 'right' }}>
                <Button variant="outlined" startIcon={<Icon>file_download</Icon>}>
                    {t('page.notificacio.detail.dades.justificant')}
                </Button>
            </Box>
            <NotificacioDetailDialogTabDocuments id={id} />
            <NotificacioDetailDialogTabEnviaments id={id} />
        </>
    );
};

export default NotificacioDetailDialogTabDades;
