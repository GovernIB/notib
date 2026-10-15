import React from 'react';
import { useTranslation } from 'react-i18next';
import CustomTabs from '../../components/CustomTabs.tsx';
import SistemaTab from './SistemaTab.tsx';
import FilsExecucioTab from './FilsExecucioTab.tsx';
import TasquesSegonPlaTab from './TasquesSegonPlaTab.tsx';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';

const MonitorSistema: React.FC = () => {
    const { t } = useTranslation();
    const tabs = [
        {
            id: 'tabSistema',
            label: t('page.monitorSistema.tab.sistema.title'),
            content: <SistemaTab />,
        },
        {
            id: 'tabFilsExecucio',
            label: t('page.monitorSistema.tab.fils.title'),
            content: <FilsExecucioTab />,
        },
        {
            id: 'tabTasques',
            label: t('page.monitorSistema.tab.tasques.title'),
            content: <TasquesSegonPlaTab />,
        },
    ];
    return (<>
                <PageTitle title={t('page.integracio.detall.monitorSistema')}></PageTitle>
                <ScreenFrame icon={icons.monitorSistema} title={t('page.integracio.detall.monitorSistema')} padded>
                    <CustomTabs tabs={tabs} />
                </ScreenFrame>
            </>);
};

export default MonitorSistema;
