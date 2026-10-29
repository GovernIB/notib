import React from 'react';
import { useTranslation } from 'react-i18next';
import type { Theme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import SistemaTab, { useSistemAction } from './SistemaTab.tsx';
import FilsExecucioTab from './FilsExecucioTab.tsx';
import TasquesSegonPlaTab from './TasquesSegonPlaTab.tsx';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';

const MonitorSistema: React.FC = () => {
    const { t } = useTranslation();
    const [tab, setTab] = React.useState<string>('tabSistema');
    const { system, apiSystem, apiIsReady } = useSistemAction();

    React.useEffect(() => {
        if (apiIsReady) {
            apiSystem();
        }
    }, [apiIsReady]);

    const tabs = [
        {
            value: 'tabSistema',
            label: t('page.monitorSistema.tab.sistema.title'),
            content: <SistemaTab system={system} />,
        },
        {
            value: 'tabFilsExecucio',
            label: t('page.monitorSistema.tab.fils.title'),
            content: <FilsExecucioTab />,
        },
        {
            value: 'tabTasques',
            label: t('page.monitorSistema.tab.tasques.title'),
            content: <TasquesSegonPlaTab />,
        },
    ];
    return (<>
                <PageTitle title={t('page.monitorSistema.title')}></PageTitle>
                <ScreenFrame
                    icon={icons.monitorSistema}
                    title={t('page.monitorSistema.title')}
                    actions={
                        tab === 'tabSistema' ? (
                            <IconButton
                                title={t('component.GridToolbarButton.refresh')}
                                onClick={apiSystem}
                                sx={{ color: (theme: Theme) => theme.palette.primary.contrastText }}
                            >
                                <Icon>{icons.refresca}</Icon>
                            </IconButton>
                        ) : undefined
                    }
                >
                    <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                        <Tabs value={tab} onChange={(_event, value) => setTab(value)}>
                            {tabs.map(({ value, label }) => (
                                <Tab key={value} value={value} label={label} sx={{ textTransform: 'none', px: 3 }} />
                            ))}
                        </Tabs>
                    </Box>
                    <Box sx={{ flex: '1 1 auto', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
                        {tabs.find((item) => item.value === tab)?.content}
                    </Box>
                </ScreenFrame>
            </>);
};

export default MonitorSistema;
