import React from 'react';
import { Navigate } from 'react-router-dom';
import { useNotibContext } from '../components/NotibContext';
import { getRoleHomePath, isExternalPath } from '../routeAccess';

// Redirigeix, en accedir a l'aplicació, a la pantalla inicial que correspon al rol actual de l'usuari.
const HomeRedirect: React.FC = () => {
    const { currentRole } = useNotibContext();
    const homePath = getRoleHomePath(currentRole);
    if (isExternalPath(homePath)) {
        window.location.replace(homePath);
        return null;
    }
    return <Navigate to={homePath} replace />;
};

export default HomeRedirect;
