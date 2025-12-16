const translationEn = {
    menu: {
        home: "Home",
        config: "Settings",
        entitats: "Entities",
        enviaments: "Deliveries",
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
                    aplicacions: "Applications",
                    permisos: "Permissions",
                },
                resourceNames: {
                    aplicacio: "application",
                    permis: "permission",
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
