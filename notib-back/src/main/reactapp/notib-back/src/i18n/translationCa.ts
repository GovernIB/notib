const translationCa = {
    menu: {
        home: 'Inici',
        config: 'Configuració',
        entitats: 'Entitats',
        avisos: 'Avisos',
        propietats: 'Propietats',
        currentEntitat: 'Entitat actual',
        organsGestors: 'Òrgans gestors',
        procediments: 'Procediments',
        serveis: 'Serveis',
        grups: 'Grups',
        enviaments: 'Enviaments',
        pagadors: {
            cie: "Centres d'impressió i ensobrat",
            postal: 'Operadors postal'
        },
    },
    page: {
        home: {
            toolbar: {
                title: 'Benvinguts a NOTIB',
                subtitle: 'Aplicació per a gestionar i enviar les notificacions de la CAIB',
            },
        },
        entitats: {
            grid: {
                title: 'Entitats',
            },
            form: {
                titleCreate: 'Crear entitat',
                titleUpdate: 'Modificar entitat',
                tabs: {
                    dades: 'Dades',
                    personalitzar: 'Personalitzar',
                    tipusDocs: 'Tipus doc.',
                    aplicacions: 'Aplicacions',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    aplicacio: 'aplicació',
                    permis: 'permís',
                },
                personalitzar: {
                    capsalera: 'Capçalera',
                },
                tipusDocuments: {
                    tableColumn: {
                        tipusDoc: 'Tipus de document',
                        actiu: 'Actiu',
                    },
                    enable: {
                        success: 'Tipus de document activat',
                        error: 'Error activant el tipus de document',
                    },
                    disable: {
                        success: 'Tipus de document desactivat',
                        error: 'Error desactivant el tipus de document',
                    },
                },
                permisos: {
                    usuariAllowed: 'Usuari',
                    admEntitatAllowed: 'Adm. entitat',
                    admLecturaAllowed: 'Adm. lectura',
                    aplicacioAllowed: 'Aplicació',
                },
            },
        },
        avisos: {
            grid: {
                title: 'Avisos',
            },
            form: {
                titleCreate: 'Crear avís',
                titleUpdate: 'Modificar avís',
            },
        },
        grups: {
            grid: {
                title: 'Grups',
                popupResourceTitle: 'grup',
            },
        },
        organs: {
            grid: {
                title: 'Òrgans gestors',
                sync: {
                    title: 'Sincronització DIR3',
                    dialogTitle: 'Sincronització DIR3',
                    dialogButton: {
                        cancel: 'Cancel·lar',
                        query: 'Consultar canvis',
                        apply: 'Aplicar canvis',
                    },
                    success: 'Canvis aplicats amb èxit',
                },
            },
            form: {
                titleCreate: 'Crear òrgan gestor',
                titleUpdate: 'Modificar òrgan gestor',
                tabs: {
                    dades: 'Dades',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permís',
                },
                permisos: {
                    tipus: 'Tipus',
                    grantedAuthority: {
                        user: 'Usuari',
                        role: 'Rol',
                    },
                    administrador: 'Administrador',
                    consulta: 'Consulta',
                    processar: 'Processar',
                    gestio: 'Gestió',
                    comuns: 'ProcSer comuns',
                    notificacions: 'Notificacions',
                    comunicacions: 'Comunicacions',
                    sir: 'Comunicacions SIR',
                    comSenseProc: 'Comunicacions sense procediment',
                },
            },
        },
        propietats: {
            find: 'Cercar a les propietats',
            empty: 'Sense propietats',
            save: {
                success: 'Valor modificat correctament',
                error: 'Error modificant la propietat',
            },
        },
        propietatsConfiguracio: {
            grid: {
                title: 'Propietats configurables',
            },
            form: {
                titleCreate: 'Crear propietat configurable',
                titleUpdate: 'Modificar propietat configurable',
            },
        },
        procediments: {
            grid: {
                title: 'Procediments',
            },
            form: {
                titleCreate: 'Crear procediment',
                titleUpdate: 'Modificar procediment',
                tabs: {
                    dades: 'Dades',
                    grups: 'Grups',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permís',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grup',
                        actiu: 'Actiu',
                    },
                    enable: {
                        success: 'Grup activat',
                        error: 'Error activant el grup',
                    },
                    disable: {
                        success: 'Grup desactivat',
                        error: 'Error desactivant el grup',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    procesAllowed: 'Processament',
                    gestioAllowed: 'Gestió',
                    notificacioAllowed: 'Notificació',
                    comunicacioAllowed: 'Comunicació',
                    comunicacioSirAllowed: 'Comunicació SIR',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Serveis',
            },
            form: {
                titleCreate: 'Crear servei',
                titleUpdate: 'Modificar servei',
                tabs: {
                    dades: 'Dades',
                    grups: 'Grups',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permís',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grup',
                        actiu: 'Actiu',
                    },
                    enable: {
                        success: 'Grup activat',
                        error: 'Error activant el grup',
                    },
                    disable: {
                        success: 'Grup desactivat',
                        error: 'Error desactivant el grup',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    procesAllowed: 'Processament',
                    gestioAllowed: 'Gestió',
                    notificacioAllowed: 'Notificació',
                    comunicacioAllowed: 'Comunicació',
                    comunicacioSirAllowed: 'Comunicació SIR',
                },
            },
        },
        pagador: {
            cie: {
                grid: {
                    title: "Centres d'impressió i ensobrat"
                }
            },
            postal: {
                grid: {
                    title: "Operadors postals"
                },
                form: {
                    nom: "Nom de l'operador",
                    organGestor: "Organisme pagador",
                    contracteNum: "Número del contracte",
                    contracteDataVig: "Data vigència del contracte",
                    facturacioClientCodi: "Codi del client postal",
                    tabs: {
                        dades: 'Dades',
                        permisos: 'Permisos',
                    }
                }
            }
        },
        notFound: {
            title: 'Pàgina no trobada',
            toHome: "Anar a l'inici",
        }
    },
    component: {
        HeaderThemeSelector: {
            light: 'Clar',
            system: 'Sistema',
            dark: 'Fosc',
        },
        HeaderLanguageSelector: {
            languages: {
                ca: 'Català',
                es: 'Castellà',
            },
        },
        Offline: {
            message: 'Sense connexió amb el servidor',
            retry: 'Tornar a provar',
        },
        AclPermissionManager: {
            title: 'Permisos',
            resourceTitle: 'Permís',
        },
        RoleSelector: {
            role: {
                NOT_SUPER: 'Superadministrador',
                NOT_ADMIN: 'Administrador',
            },
        },
        PermissionGrid: {
            tipus: 'Tipus',
            grantedAuthority: {
                user: 'Usuari',
                role: 'Rol',
            },
        },
    },
};

export default translationCa;
