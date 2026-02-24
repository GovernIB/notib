import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid';
import ProcedimentForm from './pages/procediment/ProcedimentForm';
import ServeiGrid from './pages/servei/ServeiGrid';
import ServeiForm from './pages/servei/ServeiForm';
import AvisGrid from './pages/avis/AvisGrid';
import AvisForm from './pages/avis/AvisForm';
import Grups from './pages/Grups';
import OrganGrid from './pages/organ/OrganGrid';
import OrganForm from './pages/organ/OrganForm';
import PagadorCieGrid from './pages/pagadorCie/PagadorCieGrid';
import PagadorCieForm from './pages/pagadorCie/PagadorCieForm';
import PagadorsPostals from './pages/PagadorsPostals';
import NotificacioGrid from './pages/notificacio/NotificacioGrid';
import NotificacioForm from './pages/notificacio/NotificacioForm';
import NotFoundPage from './pages/NotFound';
import Propietats from './pages/propietat/Propietats';
import MonitorIntegracioGrid from './pages/Integracions/MonitorIntegracioGrid';
import MonitorIntegracioParamDetail from './pages/Integracions/MonitorIntegracioParamDetail';

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/home" replace />,
                },
                {
                    path: 'home',
                    element: <Home />,
                },
                {
                    path: 'entitats',
                    children: [
                        { index: true, element: <EntitatGrid /> },
                        { path: 'current', element: <EntitatForm /> },
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
                    path: 'propietats',
                    element: <Propietats />,
                },
                {
                    path: 'avisos',
                    children: [
                        { index: true, element: <AvisGrid /> },
                        {
                            path: 'form',
                            children: [
                                { index: true, element: <AvisForm /> },
                                { path: ':id', element: <AvisForm /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'procediments',
                    children: [
                        { index: true, element: <ProcedimentGrid /> },
                        {
                            path: 'form',
                            children: [
                                { index: true, element: <ProcedimentForm /> },
                                { path: ':id', element: <ProcedimentForm /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'serveis',
                    children: [
                        { index: true, element: <ServeiGrid /> },
                        {
                            path: 'form',
                            children: [
                                { index: true, element: <ServeiForm /> },
                                { path: ':id', element: <ServeiForm /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'grups',
                    element: <Grups />,
                },
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
                {
                    path: 'pagadorspostals',
                    element: <PagadorsPostals />,
                },
                {
                    path: 'pagadorscie',
                    children: [
                        { index: true, element: <PagadorCieGrid /> },
                        {
                            path: 'form',
                            children: [
                                { index: true, element: <PagadorCieForm /> },
                                { path: ':id', element: <PagadorCieForm /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'notificacions',
                    children: [
                        { index: true, element: <NotificacioGrid /> },
                        {
                            path: 'form',
                            children: [
                                { index: true, element: <NotificacioForm /> },
                                { path: ':id', element: <NotificacioForm /> },
                            ],
                        },
                    ],
                },
                {
                    path: 'integracions',
                    children: [
                        { index: true, element: <MonitorIntegracioGrid /> },
                        {
                            path: 'detail',
                            children: [ { path: ':id', element: <MonitorIntegracioParamDetail /> }]
                        }
                    ],
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
