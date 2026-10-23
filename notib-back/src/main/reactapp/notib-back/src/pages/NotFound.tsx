import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import notFound from '../assets/not_found.svg';
import PageTitle from "../components/PageTitle.tsx";
import ScreenFrame from '../components/ScreenFrame';
import { icons } from '../utils/icons';

const NotFound = () => {
    const { t } = useTranslation();
    return (<>
        <PageTitle title={'Not found'}></PageTitle>
        <ScreenFrame icon={icons.noTrobat} title={t('page.notFound.title')}>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100%',
                }}>
                <img width="5%" alt="Not found" src={notFound} />
                <Button variant="contained" component={Link} to="/" sx={{ mt: 2 }}>
                    {t('page.notFound.toHome')}
                </Button>
            </Box>
        </ScreenFrame>
    </>);
};

export default NotFound;
