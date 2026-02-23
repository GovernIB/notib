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
        pagadorsPostals: 'Operadors postals',
        pagadorsCie: "Centres d'impressió i ensobrat",
        notificacions: 'Notificacions',
        integracions: 'Integracions',
    },
    comu: {
        netejarFiltre: 'Netejar filtre',
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
        pagadorPostal: {
            grid: {
                title: 'Operadors postals',
                popupResourceTitle: 'operador postal',
            },
        },
        pagadorCie: {
            grid: {
                title: "Centres d'impressió i ensobrat",
            },
            form: {
                titleCreate: "Crear centre d'impressió i ensobrat",
                titleUpdate: "Modificar centre d'impressió i ensobrat",
                tabs: {
                    dades: 'Dades',
                    fulles: 'Formats de fulla',
                    sobres: 'Formats de sobre',
                },
            },
        },
        notificacio: {
            grid: {
                title: 'Notificacions',
                new: {
                    title: 'Nou enviament',
                    NOTIFICACIO: 'Notificació',
                    COMUNICACIO: 'Comunicació',
                    SIR: 'Comunicació SIR',
                },
            },
            form: {
                title: {
                    NOTIFICACIO: {
                        create: 'Crear notificació',
                        update: 'Modificar notificació',
                    },
                    COMUNICACIO: {
                        create: 'Crear comunicació',
                        update: 'Modificar comunicació',
                    },
                    SIR: {
                        create: 'Crear comunicació SIR',
                        update: 'Modificar comunicació SIR',
                    },
                },
                tabs: {
                    remesa: 'Informació de la remesa',
                    enviaments: 'Enviaments',
                    documents: 'Documents adjunts',
                },
                enviaments: {
                    title: 'Enviament',
                    add: 'Afegir enviament',
                    remove: 'Eliminar enviament',
                },
                interessats: {
                    interessat: 'Interessat (titular a Notifica)',
                    representant: 'Representant (destinatari a Notifica)',
                    add: 'Afegir representant',
                    remove: 'Eliminar representant',
                    nifLabel: {
                        FISICA: 'NIF/NIE/identificador EIDAS',
                        JURIDICA: 'CIF/identificador EIDAS',
                        ADMINISTRACIO: 'NIF',
                        FISICA_SENSE_NIF: 'Número de document',
                    },
                },
                documents: {
                    title: 'Adjunt',
                    add: 'Afegir adjunt',
                    remove: 'Eliminar adjunt',
                    helperText: {
                        attachment:
                            'La mida màxima del document és de 10 Mb. Els formats admesos són PDF i ZIP.',
                        normalitzat:
                            "Està la primera pàgina del document preparada per l'ensobrat?",
                    },
                },
                camps: {
                    procediment: 'Procediment',
                    servei: 'Servei',
                },
            },
        },
        integracions: {
            grid: {
                title: 'Monitor de integraciones',
            },
        },
        notFound: {
            title: 'Pàgina no trobada',
            toHome: "Anar a l'inici",
        },
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
        GridToolbarButton: {
            add: 'Afegir',
            refresh: 'Refrescar',
        },
    },
};

export default translationCa;
