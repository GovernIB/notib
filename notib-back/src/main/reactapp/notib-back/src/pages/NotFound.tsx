import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { BasePage } from 'reactlib';
import notFound from '../assets/not_found.svg';
import PageTitle from "../components/PageTitle.tsx";

const NotFound = () => {
    const { t } = useTranslation();
    return (<>
        <PageTitle title={'Not found'}></PageTitle>
        <BasePage expandHeight>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100%',
                }}>
                <img width="5%" alt="Not found" src={notFound} />
                <Typography variant="h4">{t('page.notFound.title')}</Typography>
                <Button variant="contained" component={Link} to="/" sx={{ mt: 2 }}>
                    {t('page.notFound.toHome')}
                </Button>
            </Box>
        </BasePage>
    </>);
};

export default NotFound;
