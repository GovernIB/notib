import React from 'react';
import { useTranslation } from 'react-i18next';
import { useResourceApiService, useMuiContentDialog } from 'reactlib';
import CustomTabs from '../../components/CustomTabs';
import DataCard from '../../components/DataCard';

const toDataItem = (label: string, value: string) => {
    const item: Record<string, string> = {};
    item[label] = value;
    return item;
};

const NotificacioDetailDialogTabDades: React.FC<{ id: any }> = (props) => {
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
        <>
            <DataCard title="Dades de la notificació" data={data} sx={{ mb: 3 }} />
        </>
    );
};

const NotificacioDetailDialogTabEnviaments: React.FC = () => {
    return <span>Enviaments</span>;
};

const NotificacioDetailDialogTabDocuments: React.FC = () => {
    return <span>Documents</span>;
};

const NotificacioDetailDialogTabRegistreEsdev: React.FC = () => {
    return <span>Registre d'esdeveniments</span>;
};

const NotificacioDetailDialogTabAccions: React.FC = () => {
    return <span>Accions</span>;
};

const NotificacioDetailDialogTabHistoric: React.FC = () => {
    return <span>Històric</span>;
};

const NotificacioDetailDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    return (
        <CustomTabs
            tabs={[
                t('page.notificacio.detail.tab.dades'),
                t('page.notificacio.detail.tab.enviaments'),
                t('page.notificacio.detail.tab.documents'),
                t('page.notificacio.detail.tab.registreEsdev'),
                t('page.notificacio.detail.tab.accions'),
                t('page.notificacio.detail.tab.historic'),
            ]}
            contents={[
                <NotificacioDetailDialogTabDades id={id} />,
                <NotificacioDetailDialogTabEnviaments />,
                <NotificacioDetailDialogTabDocuments />,
                <NotificacioDetailDialogTabRegistreEsdev />,
                <NotificacioDetailDialogTabAccions />,
                <NotificacioDetailDialogTabHistoric />,
            ]}
        />
    );
};

export const useNotificacioDetailDialog = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            t('page.notificacio.detail.title.notificacio'),
            <NotificacioDetailDialogContent id={id} />,
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
