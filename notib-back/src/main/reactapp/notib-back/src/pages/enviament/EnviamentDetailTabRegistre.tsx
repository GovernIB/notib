import { Alert, Box } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { FieldsDataCard } from '../../components/DataCard';

const EnviamentDetailTabRegistre: React.FC<{
    enviament: any;
    apiCurrentFields: any[] | undefined;
}> = (props) => {
    const { enviament, apiCurrentFields } = props;
    const { t } = useTranslation();

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            {!enviament.registreNumeroFormatat ? (
                <Alert severity="warning" sx={{ mb: 1, mt: 2 }}>
                    {t('page.enviament.detail.tab.registre.noEnviada')}
                </Alert>
            ) : (
                <FieldsDataCard
                    title={t('page.enviament.detail.tab.registre.dadesRegistre')}
                    rows={[
                        {
                            field: 'registreNumeroFormatat',
                            alwaysVisible: true,
                        },
                        {
                            field: 'registreData',
                            alwaysVisible: true,
                        },
                        {
                            field: 'registreEstat',
                        },
                        {
                            field: 'registreMotiu',
                        },
                    ]}
                    fields={apiCurrentFields}
                    data={enviament}
                    sx={{ mb: 1 }}
                />
            )}
        </Box>
    );
};

export default EnviamentDetailTabRegistre;
