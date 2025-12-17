import { Routes, Route, Navigate} from 'react-router-dom';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
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
            <Route path="enviaments" element={<Enviaments />} />
            <Route path="*" element={<NotFoundPage />} />
        </Routes>
    );
};

export default AppRoutes;
