import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import NotFoundPage from './pages/NotFound';
import Enviaments from './pages/Enviaments';

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<HomePage />} />
            <Route path="*" element={<NotFoundPage />} />
            <Route path="entitats" element={<Enviaments />} />
            <Route path="enviaments" element={<Enviaments />} />
        </Routes>
    );
};

export default AppRoutes;
