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
import Grups from './pages/Grups';
import OrganGrid from './pages/organ/OrganGrid';
import OrganForm from './pages/organ/OrganForm';
import PagadorCieGrid from './pages/pagadorCie/PagadorCieGrid';
import PagadorCieForm from './pages/pagadorCie/PagadorCieForm';
import PagadorsPostals from './pages/PagadorsPostals';
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
            <Route path="grups" element={<Grups />} />
            <Route path="organs">
                <Route index element={<OrganGrid />} />
                <Route path="form">
                    <Route index element={<OrganForm />} />
                    <Route path=":id" element={<OrganForm />} />
                </Route>
            </Route>
            <Route path="pagadorspostals" element={<PagadorsPostals />} />
            <Route path="pagadorscie">
                <Route index element={<PagadorCieGrid />} />
                <Route path="form">
                    <Route index element={<PagadorCieForm />} />
                    <Route path=":id" element={<PagadorCieForm />} />
                </Route>
            </Route>
            <Route path="propietats" element={<Propietats />} />
            <Route path="*" element={<NotFoundPage />} />
        </Routes>
    );
};

export default AppRoutes;
