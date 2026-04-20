import { Box } from '@mui/material';
import CustomTabs from '../../components/CustomTabs';
import { useTranslation } from 'react-i18next';
import React from 'react';
import NotificacioDetailTabHistoric from './NotificacioDetailTabHistoric';
import NotificacioDetailTabRegistreEsdev from './NotificacioDetailTabRegistreEsdev';
import NotificacioDetailDialogTabDades from './NotificacioDetailTabDades';
import NotificacioDetailDialogTabAccions from './NotificacioDetailTabAccions';
import { useResourceApiService } from 'reactlib';

const NotificacioDetailDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioResource');
    const [notificacio, setNotificacio] = React.useState<any>();
    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id).then(setNotificacio);
        }
    }, [apiIsReady]);

    const tabs = [
        {
            id: 'tabDades',
            label: t('page.notificacio.detail.tab.dades'),
            content: (
                <NotificacioDetailDialogTabDades
                    id={id}
                    notificacio={notificacio}
                    apiCurrentFields={apiCurrentFields}
                />
            ),
        },
        {
            id: 'tabRegistreEsdev',
            label: t('page.notificacio.detail.tab.registreEsdev'),
            content: <NotificacioDetailTabRegistreEsdev id={id} />,
        },
        {
            id: 'tabAccions',
            label: t('page.notificacio.detail.tab.accions'),
            content: <NotificacioDetailDialogTabAccions />,
        },
        {
            id: 'tabHistoric',
            label: t('page.notificacio.detail.tab.historic'),
            content: <NotificacioDetailTabHistoric id={id} />,
        },
    ];

    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs tabs={tabs} />
        </Box>
    );
};

export default NotificacioDetailDialogContent;
