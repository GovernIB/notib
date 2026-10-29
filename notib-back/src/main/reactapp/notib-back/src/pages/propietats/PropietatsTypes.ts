/** Referència (id i descripció) a un altre recurs, tal com la torna l'API. */
export interface PropietatReferencia {
    id: string;
    description?: string;
}

/**
 * Propietat configurable (`configResource`). `value` és el del fitxer del servidor per a les
 * propietats d'aquest fitxer i el de la BD per a les editables. Els identificadors es converteixen a
 * text en carregar-les (l'API els retorna numèrics), perquè l'arbre de grups els necessita així.
 */
export interface PropietatItem {
    id: string;
    key: string;
    value?: string | null;
    description: string;
    jbossProperty: boolean;
    /** Es pot configurar un valor diferent per a cada entitat (veure `PropietatsDialog`). */
    configurable: boolean;
    position: number;
    configGroup: PropietatReferencia;
    configTypeCode: string;
    /** Quan el tipus és una enumeració, la llista de valors vàlids separats per coma. */
    configTypeValue?: string | null;
}

/** Grup de propietats (`configGroupResource`). */
export interface PropietatGrup {
    id: string;
    key: string;
    description: string;
    position: number;
    parent?: PropietatReferencia | null;
}
