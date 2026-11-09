import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import HomeRedirect from './pages/HomeRedirect';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid';
import ServeiGrid from './pages/servei/ServeiGrid';
import AvisGrid from './pages/avis/AvisGrid';
import Grups from './pages/grup/Grups.tsx';
import OrganGrid from './pages/organ/OrganGrid';
import OrganForm from './pages/organ/OrganForm';
import PagadorCieGrid from './pages/pagadorCie/PagadorCieGrid';
import NotificacioGrid from './pages/notificacio/NotificacioGrid';
import NotificacioForm from './pages/notificacio/NotificacioForm';
import EnviamentGrid from './pages/enviament/EnviamentGrid';
import NotFoundPage from './pages/NotFound';
import Propietats from './pages/propietats/Propietats';
import MonitorIntegracioGrid from './pages/integracio/MonitorIntegracioGrid';
import CacheGrid from './pages/cache/CacheGrid';
import ActiveMqGrid from './pages/activeMq/ActiveMqGrid';
import MonitorSistema from './pages/monitorSistema/MonitorSistema.tsx';
import NotificacioMassivaGrid from "./pages/notificacioMassiva/NotificacioMassivaGrid.tsx";
import NotificacioMassivaForm from "./pages/notificacioMassiva/NotificacioMassivaForm.tsx";
import AccioMassivaGrid from "./pages/accioMassiva/AccioMassivaGrid.tsx";
import CallbackPendentsGrid from "./pages/callbacks/CallbackPendentsGrid.tsx";
import Metriques from "./pages/metriques/Metriques.tsx";
import PermisosUsuariGrid from "./pages/usuaris/PermisosUsuariGrid.tsx";
import PagadorsPostalsGrid from "./pages/pagadorPostal/PagadorsPostalsGrid.tsx";
import Accessibilitat from "./pages/Accesibilitat.tsx";
import Sitemap from "./pages/Sitemap.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <HomeRedirect />,
                },
                {
                    element: <ProtectedRoute pantalla="home" />,
                    children: [
                        { path: 'home', element: <Home /> },
                    ],
                },
                {
                    path: 'entitats',
                    children: [
                        {
                            element: <ProtectedRoute pantalla="entitats" />,
                            children: [
                                { index: true, element: <EntitatGrid /> },
                                {
                                    path: 'form',
                                    children: [
                                        { index: true, element: <EntitatForm /> },
                                        { path: ':id', element: <EntitatForm /> },
                                    ],
                                },
                            ],
                        },
                        {
                            element: <ProtectedRoute pantalla="entitatActual" />,
                            children: [
                                { path: 'current', element: <EntitatForm /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="propietats" />,
                    children: [
                        { path: 'propietats', element: <Propietats /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="avisos" />,
                    children: [
                        {
                            path: 'avisos',
                            element: <AvisGrid />,
                            handle: { titol: 'page.avisos.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="procediments" />,
                    children: [
                        {
                            path: 'procediments',
                            children: [
                                { index: true, element: <ProcedimentGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="serveis" />,
                    children: [
                        {
                            path: 'serveis',
                            children: [
                                { index: true, element: <ServeiGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="grups" />,
                    children: [
                        { path: 'grups', element: <Grups /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="organs" />,
                    children: [
                        {
                            path: 'organs',
                            children: [
                                { index: true, element: <OrganGrid /> },
                                {
                                    path: 'form',
                                    children: [
                                        { index: true, element: <OrganForm /> },
                                        { path: ':id', element: <OrganForm /> },
                                    ],
                                },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="pagadorspostal" />,
                    children: [
                        {
                            path: 'pagadorspostal',
                            children: [
                                { index: true, element: <PagadorsPostalsGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="pagadorscie" />,
                    children: [
                        {
                            path: 'pagadorscie',
                            children: [
                                { index: true, element: <PagadorCieGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="notificacions" />,
                    children: [
                        {
                            path: 'notificacions',
                            children: [
                                { index: true, element: <NotificacioGrid key="notificacio-notificacions" notificacionsEsborrades={false} notificacionsErrorRegistre={false} /> },
                                {
                                    path: 'form',
                                    children: [
                                        { index: true, element: <NotificacioForm /> },
                                        { path: ':id', element: <NotificacioForm /> },
                                    ],
                                },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="notificacionsEsborrades" />,
                    children: [
                        { path: 'notificacionsEsborrades', element: <NotificacioGrid key="notificacio-esborrades" notificacionsEsborrades={true} notificacionsErrorRegistre={false} /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="notificacionsErrorRegistre" />,
                    children: [
                        { path: 'notificacionsErrorRegistre', element: <NotificacioGrid key="notificacio-errorRegistre" notificacionsErrorRegistre={true} notificacionsEsborrades={false} /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="notificacionsCallbackError" />,
                    children: [
                        { path: 'notificacionsCallbackError', element: <NotificacioGrid key="notificacio-callbackError" notificacionsErrorRegistre={false} notificacionsEsborrades={false} notificacionsCallbackError={true} /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="enviaments" />,
                    children: [
                        { path: 'enviaments', element: <EnviamentGrid /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="notificacioMassiva" />,
                    children: [
                        {
                            path: 'notificacio/massiva',
                            children: [
                                { index: true, element: <NotificacioMassivaGrid /> },
                                {
                                    path: 'form',
                                    children: [
                                        { index: true, element: <NotificacioMassivaForm /> },
                                    ],
                                },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="accionsMassives" />,
                    children: [
                        { path: 'accions/massives', element: <AccioMassivaGrid /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="integracions" />,
                    children: [
                        {
                            path: 'integracions',
                            children: [
                                { index: true, element: <MonitorIntegracioGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="caches" />,
                    children: [
                        { path: 'caches', element: <CacheGrid /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="metriques" />,
                    children: [
                        { path: 'metriques', element: <Metriques /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="activemq" />,
                    children: [
                        {
                            path: 'activemq',
                            children: [
                                { index: true, element: <ActiveMqGrid /> },
                            ],
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="callbacks" />,
                    children: [
                        { path: 'callbacks', element: <CallbackPendentsGrid /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="permisosUsuari" />,
                    children: [
                        { path: 'permisosUsuari', element: <PermisosUsuariGrid /> },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="monitorSistema" />,
                    children: [
                        {
                            path: 'monitorSistema',
                            children: [
                                { index: true, element: <MonitorSistema /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'sitemap',
                    element: <Sitemap />,
                },
                {
                    path: 'accessibilitat',
                    element: <Accessibilitat />,
                },
                {
                    path: '*',
                    element: <NotFoundPage />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);
