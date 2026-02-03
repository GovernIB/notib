import { Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid';
import ProcedimentForm from './pages/procediment/ProcedimentForm';
import ServeiGrid from './pages/servei/ServeiGrid';
import ServeiForm from './pages/servei/ServeiForm';
import AvisGrid from './pages/avis/AvisGrid';
import AvisForm from './pages/avis/AvisForm';
import GrupGrid from './pages/grup/GrupGrid';
import GrupForm from './pages/grup/GrupForm';
import OrganGrid from './pages/organ/OrganGrid';
import OrganForm from './pages/organ/OrganForm';
import Enviaments from './pages/Enviaments';
import NotFoundPage from './pages/NotFound';
import Propietats from './pages/propietat/Propietats';

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<Navigate to="/home" replace />} />
            <Route path="home">
                <Route index element={<Home />} />
            </Route>
            <Route path="entitats">
                <Route index element={<EntitatGrid />} />
                <Route path="current" element={<EntitatForm />} />
                <Route path="form">
                    <Route index element={<EntitatForm />} />
                    <Route path=":id" element={<EntitatForm />} />
                </Route>
            </Route>
            <Route path="avisos">
                <Route index element={<AvisGrid />} />
                <Route path="form">
                    <Route index element={<AvisForm />} />
                    <Route path=":id" element={<AvisForm />} />
                </Route>
            </Route>
            <Route path="procediments">
                <Route index element={<ProcedimentGrid />} />
                <Route path="form">
                    <Route index element={<ProcedimentForm />} />
                    <Route path=":id" element={<ProcedimentForm />} />
                </Route>
            </Route>
            <Route path="serveis">
                <Route index element={<ServeiGrid />} />
                <Route path="form">
                    <Route index element={<ServeiForm />} />
                    <Route path=":id" element={<ServeiForm />} />
                </Route>
            </Route>
            <Route path="grups">
                <Route index element={<GrupGrid />} />
                <Route path="form">
                    <Route index element={<GrupForm />} />
                    <Route path=":id" element={<GrupForm />} />
                </Route>
            </Route>
            <Route path="organs">
                <Route index element={<OrganGrid />} />
                <Route path="form">
                    <Route index element={<OrganForm />} />
                    <Route path=":id" element={<OrganForm />} />
                </Route>
            </Route>
            <Route path="propietats" element={<Propietats />} />
            <Route path="enviaments" element={<Enviaments />} />
            <Route path="*" element={<NotFoundPage />} />
        </Routes>
    );
};

export default AppRoutes;
