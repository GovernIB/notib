import { Routes, Route, Navigate} from 'react-router-dom';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid';
import ProcedimentForm from "./pages/procediment/ProcedimentForm";
import AvisGrid from './pages/avis/AvisGrid';
import AvisForm from './pages/avis/AvisForm';
import GrupGrid from './pages/grup/GrupGrid';
import GrupForm from './pages/grup/GrupForm';
import Enviaments from './pages/Enviaments';
import NotFoundPage from './pages/NotFound';

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<Navigate to="/entitats" replace />} />
            <Route path="entitats">
                <Route index element={<EntitatGrid />} />
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
            <Route index element={<Navigate to="/procediments" replace />} />
            <Route path="procediments">
                <Route index element={<ProcedimentGrid />} />
                <Route path="form">
                    <Route index element={<ProcedimentForm />} />
                    <Route path=":id" element={<ProcedimentForm />} />
                </Route>
            </Route>
            <Route index element={<Navigate to="/grups" replace />} />
            <Route path="grups">
                <Route index element={<GrupGrid />} />
                <Route path="form">
                    <Route index element={<GrupForm />} />
                    <Route path=":id" element={<GrupForm />} />
                </Route>
            </Route>
            <Route path="enviaments" element={<Enviaments />} />
            <Route path="*" element={<NotFoundPage />} />
        </Routes>
    );
};

export default AppRoutes;
