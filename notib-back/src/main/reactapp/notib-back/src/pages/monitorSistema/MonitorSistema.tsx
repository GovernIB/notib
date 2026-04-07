import React from 'react';
import { useTranslation } from 'react-i18next';
import CustomTabs from '../../components/CustomTabs.tsx';
import SistemaTab from './SistemaTab.tsx';
import FilsExecucioTab from './FilsExecucioTab.tsx';
import TasquesSegonPlaTab from './TasquesSegonPlaTab.tsx';

const MonitorSistema: React.FC = () => {
    const { t } = useTranslation();
    return (
        <CustomTabs
            tabs={[
                t('page.monitorSistema.tab.sistema.title'),
                t('page.monitorSistema.tab.fils.title'),
                t('page.monitorSistema.tab.tasques.title'),
            ]}
            contents={[<SistemaTab />, <FilsExecucioTab />, <TasquesSegonPlaTab />]}
        />
    );
};

export default MonitorSistema;
