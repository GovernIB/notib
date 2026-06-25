import { Box } from '@mui/material';
import CustomTabs from '../../components/CustomTabs';
import { useTranslation } from 'react-i18next';
import React from 'react';
import NotificacioDetailTabHistoric from './NotificacioDetailTabHistoric';
import NotificacioDetailTabRegistreEsdev from './NotificacioDetailTabRegistreEsdev';
import NotificacioDetailDialogTabDades from './NotificacioDetailTabDades';
import NotificacioDetailDialogTabAccions from './NotificacioDetailTabAccions';
import { useResourceApiService } from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';

const NotificacioDetailDialogContent: React.FC<{ id: any, notificacionsEsborrades: boolean }> = (props) => {

    const { id, notificacionsEsborrades } = props;
    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isVisibleHistoric= currentRole === 'NOT_ADMIN' || currentRole === 'NOT_ADMIN_LECTURA' || currentRole === 'NOT_ORGAN';
    const {isReady: apiIsReady, getOne: apiGetOne, currentFields: apiCurrentFields} = useResourceApiService('notificacioResource');
    const [notificacio, setNotificacio] = React.useState<any>();

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        apiGetOne(id, {
            perspectives: [
                'ENVIAMENTS_NOTIFICACIO',
                'DOCUMENTS_NOTIFICACIO',
                'OPERADORS_CIE_POSTAL',
                'OPERADORS_GRUP',
                'NOTIFICACIO_DETALL'
            ],
        }).then(setNotificacio);
    }, [apiIsReady, id]);
    const tabs = [
        {
            id: 'tabDades',
            label: t('page.notificacio.detail.tab.dades'),
            content: (<NotificacioDetailDialogTabDades notificacio={notificacio} apiCurrentFields={apiCurrentFields}/>),
        },
        {
            id: 'tabRegistreEsdev',
            label: t('page.notificacio.detail.tab.registreEsdev'),
            content: <NotificacioDetailTabRegistreEsdev id={id} />,
        },
        {
            id: 'tabAccions',
            label: t('page.notificacio.detail.tab.accions'),
            content: <NotificacioDetailDialogTabAccions notificacio={notificacio} />,
            hidden: notificacionsEsborrades
        },
        {
            id: 'tabHistoric',
            label: t('page.notificacio.detail.tab.historic'),
            content: <NotificacioDetailTabHistoric id={id} />,
            hidden: !isVisibleHistoric,
        }
    ];

    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs tabs={tabs} />
        </Box>
    );
};

export default NotificacioDetailDialogContent;
