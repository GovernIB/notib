import React from 'react';
import { useTranslation } from 'react-i18next';
import { useResourceApiService } from 'reactlib';
import CustomTabs from '../../components/CustomTabs';
import { Box } from '@mui/material';
import EnviamentDetailTabDades from './EnviamentDetailTabDades';
import EnviamentDetailTabRegistreEsdev from './EnviamentDetailTabRegistreEsdev';
import EnviamentDetailTabNotifica from './EnviamentDetailTabNotifica';
import EnviamentDetailTabRegistre from './EnviamentDetailTabRegistre';
import EnviamentDetailTabEntregaPostal from './EnviamentDetailTabEntregaPostal';
import EnviamentDetailTabHistoric from './EnviamentDetailTabHistoric';
import EnviamentDetailTabStateMachine from './EnviamentDetailTabStateMachine';
import { useNotibContext } from '../../components/NotibContext';

const EnviamentDetailDialogContent: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const [enviament, setEnviament] = React.useState<any>();
    const { currentRole } = useNotibContext();
    const isRolActualAdministradorLectura = currentRole === 'NOT_ADMIN_LECTURA';
    const isVisibleHistoric =
        currentRole === 'NOT_ADMIN' ||
        currentRole === 'NOT_ADMIN_LECTURA' ||
        currentRole === 'NOT_ORGAN';
    const isVisibleStateMachine = currentRole === 'NOT_ADMIN';

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: apiCurrentFields,
    } = useResourceApiService('notificacioEnviamentResource');

    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(id, { perspectives: ['TITULAR', 'ENTREGA_POSTAL'] }).then(setEnviament);
        }
    }, [apiIsReady]);

    const tabs = [
        {
            id: 'tabDades',
            label: t('page.enviament.detail.tab.dades.title'),
            content: (
                <EnviamentDetailTabDades
                    enviament={enviament}
                    apiCurrentFields={apiCurrentFields}
                />
            ),
        },
        {
            id: 'tabNotifica',
            label: t('page.enviament.detail.tab.notifica.title'),
            content: (
                <EnviamentDetailTabNotifica
                    enviament={enviament}
                    apiCurrentFields={apiCurrentFields}
                    isRolActualAdministradorLectura={isRolActualAdministradorLectura}
                />
            ),
        },
        {
            id: 'tabRegistre',
            label: t('page.enviament.detail.tab.registre.title'),
            content: (
                <EnviamentDetailTabRegistre
                    enviament={enviament}
                    apiCurrentFields={apiCurrentFields}
                />
            ),
        },
        {
            id: 'tabEntregaPostal',
            label: t('page.enviament.detail.tab.entregaPostal.title'),
            content: (
                <EnviamentDetailTabEntregaPostal
                    enviament={enviament}
                    apiCurrentFields={apiCurrentFields}
                    isRolActualAdministradorLectura={isRolActualAdministradorLectura}
                />
            ),
            hidden: !enviament?.entregaPostalInfo,
        },
        {
            id: 'tabRegistreEsdev',
            label: t('page.enviament.detail.tab.registreEsdev.title'),
            content: <EnviamentDetailTabRegistreEsdev id={id} />,
        },
        {
            id: 'tabHistoric',
            label: t('page.enviament.detail.tab.historic.title'),
            content: <EnviamentDetailTabHistoric id={id} />,
            hidden: !isVisibleHistoric,
        },
        {
            id: 'tabStateMachine',
            label: t('page.enviament.detail.tab.stateMachine.title'),
            content: <EnviamentDetailTabStateMachine id={id} />,
            hidden: !isVisibleStateMachine,
        },
    ];

    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            <CustomTabs tabs={tabs} />
        </Box>
    );
};

export default EnviamentDetailDialogContent;
