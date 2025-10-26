import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';

const NotFound = () => {
    const { t } = useTranslation();
    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100%',
            }}
        >
            <Typography variant="h2">{t('page.notFound')}</Typography>
        </Box>
    );
};

export default NotFound;
