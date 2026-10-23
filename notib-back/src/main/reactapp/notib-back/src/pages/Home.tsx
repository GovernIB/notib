import React from 'react';
import { useTranslation } from 'react-i18next';
import Typography from '@mui/material/Typography';
import PageTitle from "../components/PageTitle.tsx";
import ScreenFrame from '../components/ScreenFrame';
import { icons } from '../utils/icons';

const Home: React.FC = () => {
    const { t } = useTranslation();
    return (<>
        <PageTitle title={'Home'}></PageTitle>
        <ScreenFrame icon={icons.inici} title={t('page.home.toolbar.title')} fitContent>
            <Typography variant="body2" color="text.secondary">
                {t('page.home.toolbar.subtitle')} (v{import.meta.env.VITE_APP_VERSION})
            </Typography>
        </ScreenFrame>
    </>);
};

export default Home;
