import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext, useResourceApiContext } from 'reactlib';
import {
    NotibContext,
    ROLE_PREFIX,
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_CONSULTA,
    ROLE_ORGAN,
    ROLE_USER,
} from './NotibContext';

const ALLOWED_ROLES = [
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_CONSULTA,
    ROLE_ORGAN,
    ROLE_USER,
].reverse();

const decodeJwt = (token: string) => {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const navigate = useNavigate();
    const { isReady: authIsReady, getToken: authGetToken } = useAuthContext();
    const { setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const [currentUserRealmRoles, setCurrentUserRealmRoles] = React.useState<string[]>();
    const [currentRole, setCurrentRole] = React.useState<string>();
    React.useEffect(() => {
        if (authIsReady) {
            const token = authGetToken();
            if (token != null) {
                const tokenDecoded = decodeJwt(token);
                const realmRoles = tokenDecoded.realm_access?.roles?.filter((r: string) =>
                    r.startsWith(ROLE_PREFIX)
                );
                const filteredRoles = ALLOWED_ROLES.filter((a) => realmRoles.includes(a));
                setCurrentUserRealmRoles(filteredRoles);
                if (filteredRoles.length > 0 && currentRole == null) {
                    setCurrentRole(filteredRoles[0]);
                }
            }
        }
    }, [authIsReady]);
    React.useEffect(() => {
        if (currentRole) {
            apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
            navigate('/', { replace: true });
        }
    }, [currentRole]);
    const contextValue = {
        currentUserRealmRoles,
        currentRole,
        setCurrentRole,
    };
    return <NotibContext.Provider value={contextValue}>{children}</NotibContext.Provider>;
};

export default NotibProvider;
