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

const NotificacioDetailDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const isVisibleHistoric =
        currentRole === 'NOT_ADMIN' ||
        currentRole === 'NOT_ADMIN_LECTURA' ||
        currentRole === 'NOT_ORGAN';
    // const isVisibleStateMachine = currentRole === 'NOT_ADMIN';

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioResource');
    const [notificacio, setNotificacio] = React.useState<any>();

    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id, {
                perspectives: [
                    'ENVIAMENTS_NOTIFICACIO',
                    'DOCUMENTS_NOTIFICACIO',
                    'OPERADORS_CIE_POSTAL',
                    'OPERADORS_GRUP',
                ],
            }).then(setNotificacio);
        }
    }, [apiIsReady, id]);
    const tabs = [
        {
            id: 'tabDades',
            label: t('page.notificacio.detail.tab.dades'),
            content: (
                <NotificacioDetailDialogTabDades
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
            id: 'tabAccions', // Linia 1063
            label: t('page.notificacio.detail.tab.accions'),
            content: <NotificacioDetailDialogTabAccions notificacio={notificacio} />,
            // hidden: notificacio.deleted, TODO: Linia 1058
        },
        {
            id: 'tabHistoric',
            label: t('page.notificacio.detail.tab.historic'),
            content: <NotificacioDetailTabHistoric id={id} />,
            hidden: !isVisibleHistoric,
        },
        // {
        //      TODO: Revisar JSP Linia 1250
        //     id: 'tabStateMachine',
        //     label: t('page.notificacio.detail.tab.stateMachine.title'),
        //     content: <NotificacioDetailTabStateMachine id={id} />,
        //     hidden: !isVisibleStateMachine,
        // },
    ];


    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs tabs={tabs} />
        </Box>
    );
};

export default NotificacioDetailDialogContent;
