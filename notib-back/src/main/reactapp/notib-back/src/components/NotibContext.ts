import { createContext, useContext } from 'react';

export const ROLE_PREFIX = 'NOT_';
export const ROLE_SUPER = ROLE_PREFIX + 'SUPER';
export const ROLE_ADMIN = ROLE_PREFIX + 'ADMIN';
export const ROLE_ADMIN_CONSULTA = ROLE_PREFIX + 'ADMIN_CONSULTA';
export const ROLE_ORGAN = ROLE_PREFIX + 'ORGAN';
export const ROLE_USER = 'tothom';

export type NotibContextType = {
    rolesAvailable?: string[];
    entitatsAvailable?: any[];
    currentRole?: string;
    setCurrentRole: (currentRole: string | undefined) => void;
    currentEntitatId?: any;
    setCurrentEntitatId: (currentRole: any | undefined) => void;
};

export const NotibContext = createContext<NotibContextType | undefined>(undefined);

export const useNotibContext = () => {
    const context = useContext(NotibContext);
    if (context === undefined) {
        throw new Error('useNotibContext must be used within a ResourceApiProvider');
    }
    return context;
};
