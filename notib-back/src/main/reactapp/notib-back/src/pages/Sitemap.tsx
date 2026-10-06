import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Box, List, ListItem, ListItemText, Link, Divider, Icon, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useResourceApiContext, type MenuEntry } from 'reactlib';
import { getMenuEntries } from '../routeAccess';
import { filterMenuEntries } from '../components/BaseApp';
import { useNotibContext } from '../components/NotibContext';
import PageTitle from "../components/PageTitle.tsx";
// import { Helmet } from '@dr.pogodin/react-helmet';

const SitemapList: React.FC<{ entries: MenuEntry[] }> = ({ entries }) => (
    <List disablePadding>
        {entries.map((entry) => (
            <React.Fragment key={entry.id}>
                {entry.to ? (
                    <ListItem disablePadding>
                        <ListItemText
                            primary={
                                <Link
                                    component={RouterLink}
                                    to={entry.to}
                                    display="flex"
                                    alignItems="center"
                                    underline="hover">
                                    {entry.icon && <Icon sx={{ mr: 1 }}>{entry.icon}</Icon>}
                                    {entry.title}
                                </Link>
                            }
                        />
                    </ListItem>
                ) : (
                    <ListItem disablePadding sx={{ pt: 1 }}>
                        <Typography variant="subtitle1" component="h2" sx={{ fontWeight: 700 }}>
                            {entry.title}
                        </Typography>
                    </ListItem>
                )}
                {entry.children?.length ? (
                    <ListItem disablePadding sx={{ pl: 3 }}>
                        <SitemapList entries={entry.children} />
                    </ListItem>
                ) : null}
            </React.Fragment>
        ))}
    </List>
);

const Sitemap: React.FC = () => {
    const { t } = useTranslation();
    const { currentRole } = useNotibContext();
    const { indexState: apiIndex } = useResourceApiContext();

    // Mismo filtrado que el menú lateral: descarta entradas ocultas para el rol
    // y recursos no expuestos por la API. Sin esto el mapa enlaza a páginas prohibidas.
    const resourceNames = apiIndex?.links.getAll()?.map((l: any) => l.rel);
    const entries: MenuEntry[] = [
        ...(filterMenuEntries(getMenuEntries(currentRole, t), resourceNames) ?? []),
        { id: 'accessibilitat', title: t('page.accessibilitat.link'), icon: 'info', to: '/accessibilitat' },
    ];

    return (<>
        <PageTitle title={t('page.sitemap.title')}></PageTitle>
        <Box sx={{ maxWidth: 600, mx: 'auto', p: 3 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                {t('page.sitemap.title')}
            </Typography>
            <Typography variant="body1" color="text.secondary" gutterBottom>
                {t('page.sitemap.desc')}
            </Typography>
            <Divider sx={{ my: 2 }} />
            <SitemapList entries={entries} />
        </Box>
        </>
    );
};

export default Sitemap;
