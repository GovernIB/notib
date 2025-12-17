const translationCa = {
    menu: {
        home: "Inici",
        config: "Configuració",
        entitats: "Entitats",
        enviaments: "Enviaments",
    },
    page: {
        entitats: {
            grid: {
                title: "Entitats",
            },
            form: {
                titleCreate: "Crear entitat",
                titleUpdate: "Modificar entitat",
                tabs: {
                    dades: "Dades",
                    personalitzar: "Personalitzar",
                    tipusDocs: "Tipus doc.",
                    aplicacions: "Aplicacions",
                    permisos: "Permisos",
                },
                resourceNames: {
                    aplicacio: "aplicació",
                    permis: "permís",
                },
                tipusDocuments: {
                    enable: {
                        success: "Tipus de document activat",
                        error: "Error activant el tipus de document",
                    },
                    disable: {
                        success: "Tipus de document desactivat",
                        error: "Error desactivant el tipus de document",
                    },
                },
                permisos: {
                    tipus: "Tipus",
                    grantedAuthority: {
                        user: "Usuari",
                        role: "Rol",
                    },
                    usuariAllowed: "Usuari",
                    admEntitatAllowed: "Adm. entitat",
                    admLecturaAllowed: "Adm. lectura",
                    aplicacioAllowed: "Aplicació",
                }
            },
        },
        notFound: {
            title: "Pàgina no trobada",
            toHome: "Anar a l'inici",
        },
    },
    component: {
        HeaderThemeSelector: {
            light: "Clar",
            system: "Sistema",
            dark: "Fosc"
        },
        HeaderLanguageSelector: {
            languages: {
                ca: "Català",
                es: "Castellà",
            },
        },
        Offline: {
            message: "Sense connexió amb el servidor",
            retry: "Tornar a provar"
        },
        AclPermissionManager: {
            title: "Permisos",
            resourceTitle: "Permís",
        },
    },
};

export default translationCa;
