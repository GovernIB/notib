import React from 'react';
import { useAuthContext, useResourceApiContext } from 'reactlib';
import { NotibContext } from './NotibContext';

export const ROLE_PREFIX = 'NOT_';
export const ROLE_SUPER = ROLE_PREFIX + 'SUPER';
export const ROLE_ADMIN = ROLE_PREFIX + 'ADMIN';
export const ROLE_ADMIN_CONSULTA = ROLE_PREFIX + 'ADMIN_CONSULTA';
export const ROLE_ORGAN = ROLE_PREFIX + 'ORGAN';
export const ROLE_USER = 'tothom';
const ALLOWED_ROLES = [ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_CONSULTA, ROLE_ORGAN, ROLE_USER];

const decodeJwt = (token: string) => {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
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
            }
        }
    }, [authIsReady]);
    React.useEffect(() => {
        if (currentRole) {
            apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
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
