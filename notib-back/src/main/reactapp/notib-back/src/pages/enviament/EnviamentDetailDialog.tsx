import React from 'react';
import { useTranslation } from 'react-i18next';
import { useResourceApiService, useMuiContentDialog } from 'reactlib';
import CustomTabs from '../../components/CustomTabs';
import DataCard from '../../components/DataCard';
import { Box } from '@mui/material';

const toDataItem = (label: string, value: string) => {
    const item: Record<string, string> = {};
    item[label] = value;
    return item;
};

const EnviamentDetailDialogTabDades: React.FC<{ id: any }> = (props) => {
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
    const serveiTipusField = apiCurrentFields?.find((f) => f?.name === 'serveiTipus');
    //const estatField = apiCurrentFields?.find((f) => f.name === 'estat');
    const data =
        enviament && serveiTipusField
            ? [
                  { 'Identificador de la notificació': '4df434ab-c361-481b-8999-ded3d5b805c6' },
                  { "Referència de l'enviament": '33b0dcd5-a5b7-4622-aa13-b27b656294cd' },
                  { 'DEH NIF': '' },
                  { 'DEH procediment': '' },
                  { 'DEH obligada': 'No' },
                  toDataItem(
                      serveiTipusField.label,
                      serveiTipusField.options[enviament.serveiTipus]
                  ),
                  //toDataItem(estatField.label, estatField.options[enviament.estat]),
                  { Estat: 'Notificada' },
              ]
            : [];
    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
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
        </Box>
    );
};

const EnviamentDetailDialogTabNotifica: React.FC = () => {
    return <span>Notific@</span>;
};

const EnviamentDetailDialogTabRegistre: React.FC = () => {
    return <span>Registre</span>;
};

const EnviamentDetailDialogTabRegistreEsdev: React.FC = () => {
    return <span>Registre d'esdeveniments</span>;
};

const EnviamentDetailDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs
                tabs={[
                    t('page.enviament.detail.tab.dades'),
                    t('page.enviament.detail.tab.notifica'),
                    t('page.enviament.detail.tab.registre'),
                    t('page.enviament.detail.tab.registreEsdev'),
                ]}
                contents={[
                    <EnviamentDetailDialogTabDades id={id} />,
                    <EnviamentDetailDialogTabNotifica />,
                    <EnviamentDetailDialogTabRegistre />,
                    <EnviamentDetailDialogTabRegistreEsdev />,
                ]}
            />
        </Box>
    );
};

export const useEnviamentDetailDialog = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            t('page.enviament.detail.title'),
            <EnviamentDetailDialogContent id={id} />,
            undefined,
            {
                maxWidth: 'lg',
                fullWidth: true,
            }
        ).catch(() => null);
    };
    return {
        dialogComponent,
        onDetailClick: handleDetailButtonClick,
    };
};
