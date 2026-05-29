import { Box } from '@mui/material';
import React from 'react';
import CustomTabs from "../../components/CustomTabs.tsx";
import {useTranslation} from "react-i18next";
import NotificacioMassivaResumDialogTabDades from "./NotificacioMassivaResumTabDades.tsx";
import {useResourceApiService} from "reactlib";
import NotificacioMassivaResumDialogTabResum from "./NotificacioMassivaResumTabResum.tsx";


const NotificacioMassivaResumDialogContent: React.FC<{ id: any }> = (props) => {

    const { id } = props;
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioMassivaResource');
    const [notificacio, setNotificacio] = React.useState<any>();

    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id, {perspectives: ['RESUM_NOTIFACIO_MASSIVA'],}).then(setNotificacio);
        }
    }, [apiIsReady, id]);

    const tabs = [
        {
            id: 'tabDades',
            label: t('page.notificacioMassiva.detall.dades.title'),
            content: (<NotificacioMassivaResumDialogTabDades notificacio={notificacio} apiCurrentFields={apiCurrentFields}/>),
        },
        {
            id: 'tabResum',
            label: t('page.notificacioMassiva.detall.resum.title'),
            content: <NotificacioMassivaResumDialogTabResum notificacio={notificacio}/>,
        }
    ];
    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs tabs={tabs} />
        </Box>
    );
};
export default NotificacioMassivaResumDialogContent;

