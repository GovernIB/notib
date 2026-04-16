import React from 'react';
import { useTranslation } from 'react-i18next';
import CustomTabs from '../../components/CustomTabs.tsx';
import SistemaTab from './SistemaTab.tsx';
import FilsExecucioTab from './FilsExecucioTab.tsx';
import TasquesSegonPlaTab from './TasquesSegonPlaTab.tsx';

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
    return <CustomTabs tabs={tabs} />;
};

export default MonitorSistema;
