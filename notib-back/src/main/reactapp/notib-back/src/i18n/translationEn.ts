const translationEn = {
    menu: {
        home: "Home",
        config: "Settings",
        entitats: "Entities",
        avisos: "Advices",
        enviaments: "Deliveries",
        grups: "Groups",
    },
    page: {
        entitats: {
            grid: {
                title: "Entities",
            },
            form: {
                titleCreate: "Create entity",
                titleUpdate: "Update entity",
                tabs: {
                    dades: "Data",
                    personalitzar: "Customize",
                    tipusDocs: "Doc. types",
                    aplicacions: "Applications",
                    permisos: "Permissions",
                },
                resourceNames: {
                    aplicacio: "application",
                    permis: "permission",
                },
                personalitzar: {
                    capsalera: "Header",
                },
                tipusDocuments: {
                    enable: {
                        success: "Document type enabled",
                        error: "Error enabling document type",
                    },
                    disable: {
                        success: "Document type disabled",
                        error: "Error disabling document type",
                    },
                },
                permisos: {
                    tipus: "Type",
                    grantedAuthority: {
                        user: "User",
                        role: "Role",
                    },
                    usuariAllowed: "User",
                    admEntitatAllowed: "Adm. entity",
                    admLecturaAllowed: "Adm. read",
                    aplicacioAllowed: "Application",
                }
            },
        },
        avisos: {
            grid: {
                title: "Advices",
            },
            form: {
                titleCreate: "Create advice",
                titleUpdate: "Update advice",
            },
        },
        grups: {
            grid: {
                title: "Group",
            },
            form: {
                titleCreate: "Create group",
                titleUpdate: "Update group",
            },
        },
        notFound: {
            title: "Page not found",
            toHome: "Go to home",
        },
    },
    component: {
        HeaderThemeSelector: {
            light: "Light",
            system: "System",
            dark: "Dark"
        },
        HeaderLanguageSelector: {
            languages: {
                ca: "Catalan",
                es: "Spanish",
            },
        },
        Offline: {
            message: "Server connection lost",
            retry: "Retry"
        },
        AclPermissionManager: {
            title: "Permissions",
            resourceTitle: "Permission",
        },
    },
};

export default translationEn;
