import React from 'react';
import { useAuthContext, useResourceApiContext, useResourceApiService } from 'reactlib';
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

const useSessionStorage = (...keyParts: any[]) => {
    const key = keyParts
        .map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p)))
        .join('|');
    const getValue = () => {
        return sessionStorage.getItem(key);
    };
    const setValue = (value: string | null) => {
        if (value != null) {
            sessionStorage.setItem(key, value);
        } else {
            sessionStorage.removeItem(key);
        }
    };
    return {
        getValue,
        setValue,
    };
};

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const {
        isReady: authIsReady,
        getUserId: authGetUserId,
        getToken: authGetToken,
    } = useAuthContext();
    const { setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('entitatResource');
    const [isReady, setIsReady] = React.useState<boolean>(false);
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentRole, setCurrentRole] = React.useState<string>();
    const [currentEntitat, setCurrentEntitat] = React.useState<any>();
    const { getValue: sessionGetValue, setValue: sessionSetValue } = useSessionStorage(
        currentUserId,
        'currentRole'
    );
    React.useEffect(() => {
        if (authIsReady) {
            const userId = authGetUserId();
            setCurrentUserId(userId);
            const token = authGetToken();
            if (token != null) {
                const tokenDecoded = decodeJwt(token);
                const realmRoles = tokenDecoded.realm_access?.roles?.filter((r: string) =>
                    r.startsWith(ROLE_PREFIX)
                );
                const filteredRoles = ALLOWED_ROLES.filter((a) => realmRoles.includes(a));
                setRolesAvailable(filteredRoles);
            }
        }
    }, [authIsReady]);
    React.useEffect(() => {
        if (currentUserId != null && rolesAvailable != null) {
            setIsReady(true);
        }
    }, [currentUserId, rolesAvailable]);
    React.useEffect(() => {
        if (isReady && currentRole == null) {
            const sessionValue = sessionGetValue();
            const isSessionValueInRolesAvailable =
                sessionValue != null && rolesAvailable?.includes(sessionValue);
            if (sessionValue != null && isSessionValueInRolesAvailable) {
                setCurrentRole(sessionValue);
            } else if (rolesAvailable?.length && currentRole == null) {
                setCurrentRole(rolesAvailable[0]);
            }
        }
    }, [isReady]);
    React.useEffect(() => {
        if (apiIsReady) {
            if (currentRole !== ROLE_SUPER) {
                apiFind({ unpaged: true }).then((response) => {
                    setEntitatsAvailable(response.rows);
                });
            } else {
                setEntitatsAvailable([]);
            }
        }
    }, [apiIsReady, currentRole]);
    React.useEffect(() => {
        if (isReady) {
            sessionSetValue(currentRole ?? null);
            if (currentRole) {
                apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
            }
        }
    }, [isReady, currentRole]);
    const contextValue = {
        rolesAvailable,
        entitatsAvailable,
        currentRole,
        setCurrentRole,
        currentEntitat,
        setCurrentEntitat,
    };
    return (
        <NotibContext.Provider value={contextValue}>{isReady && children}</NotibContext.Provider>
    );
};

export default NotibProvider;
