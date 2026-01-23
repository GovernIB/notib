const translationCa = {
    menu: {
        home: "Inici",
        config: "Configuració",
        entitats: "Entitats",
        avisos: "Avisos",
        enviaments: "Enviaments",
        grups: "Grups",
        organsGestors: "Òrgans gestors",
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
                personalitzar: {
                    capsalera: "Capçalera",
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
        avisos: {
            grid: {
                title: "Avisos",
            },
            form: {
                titleCreate: "Crear avís",
                titleUpdate: "Modificar avís",
            },
        },
        grups: {
            grid: {
                title: "Grups",
            },
            form: {
                titleCreate: "Crear grup",
                titleUpdate: "Modificar grup",
            },
        },
        organs: {
            grid: {
                title: "Òrgans gestors"
            },
            form: {
                titleCreate: "Crear òrgan gestor",
                titleUpdate: "Modificar òrgan gestor",
                tabs: {
                    dades: "Dades",
                    permisos: "Permisos",
                },
                resourceNames: {
                    permis: "permís",
                },
                permisos: {
                    tipus: "Tipus",
                    grantedAuthority: {
                        user: "Usuari",
                        role: "Rol",
                    },
                    administrador: "Administrador",
                    consulta: "Consulta",
                    processar: "Processar",
                    gestio: "Gestió",
                    comuns: "ProcSer comuns",
                    notificacions: "Notificacions",
                    comunicacions: "Comunicacions",
                    sir: "Comunicacions SIR",
                    comSenseProc: "Comunicacions sense procediment",
                }
            }
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
        RoleSelector: {
            role: {
                NOT_SUPER: "Superadministrador",
                NOT_ADMIN: "Administrador"
            }
        }
    },
};

export default translationCa;
