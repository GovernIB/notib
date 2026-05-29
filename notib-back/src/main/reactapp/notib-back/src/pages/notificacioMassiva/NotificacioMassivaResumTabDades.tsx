import { Box } from '@mui/material';
import { FieldsDataCard } from '../../components/DataCard';
import React from 'react';
import { useTranslation } from 'react-i18next';

interface PropsTabDades {
    notificacio: any;
    apiCurrentFields: any[] | undefined;
}


const NotificacioMassivaResumDialogTabDades: React.FC<PropsTabDades> = (props) => {

    const { notificacio, apiCurrentFields } = props;
    const { t } = useTranslation();

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            <FieldsDataCard
                title={t('page.notificacioMassiva.detall.dades.title')}
                rows={[
                    {
                        field: 'csvFilename',
                        label: t('page.notificacioMassiva.detall.dades.csvFilename')
                    },
                    {
                        field: 'zipFilename',
                        label: t('page.notificacioMassiva.detall.dades.zipFilename')
                    },
                    {
                        field: 'caducitat',
                        label: t('page.notificacioMassiva.detall.dades.caducitat')
                    },
                    {
                        field: 'createdDate',
                        label: t('page.notificacioMassiva.detall.dades.createdDate')
                    },
                    {
                        field: 'email',
                        label: t('page.notificacioMassiva.detall.dades.email')
                    },
                    {
                        field: 'createdBy',
                        label: t('page.notificacioMassiva.detall.dades.createdBy')
                    },
                ]}
                fields={apiCurrentFields}
                data={notificacio}
                sx={{ mb: 1 }}
            />
        </Box>
    );
};

export default NotificacioMassivaResumDialogTabDades;
