import { Routes, Route, Navigate} from 'react-router-dom';
import Entitats from './pages/Entitats';
import Enviaments from './pages/Enviaments';
import NotFoundPage from './pages/NotFound';

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<Navigate to="/entitats" replace />} />
            <Route path="entitats" element={<Entitats />} />
            <Route path="enviaments" element={<Enviaments />} />
            <Route path="*" element={<NotFoundPage />} />
        </Routes>
    );
};

export default AppRoutes;
