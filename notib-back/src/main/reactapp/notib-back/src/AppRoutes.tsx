import { Routes, Route, Navigate} from 'react-router-dom';
import Entitats, { EntitatForm } from './pages/Entitats';
import Enviaments from './pages/Enviaments';
import NotFoundPage from './pages/NotFound';

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<Navigate to="/entitats" replace />} />
            <Route path="entitats" element={<Entitats />} />
            <Route path="entitats">
                <Route index element={<Entitats />} />
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
