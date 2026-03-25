const translationCa = {
    app: {
        noEntitat: 'Aquest usuari no te accés a cap entorn',
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
            notificacions: 'Remeses',
            integracions: 'Integracions',
            cache: "Cache d'aplicacions",
            activemq: "Monitor ActiveMQ",
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
                    administradorTooltip: "Assigna el perfil d'administrador d'òrgan.",
                    consulta: 'Consulta',
                    consultaTooltip: 'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan emissor).',
                    processar: 'Processar',
                    processarTooltip: 'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan emissor) en un estat final com a processades, sempre que hagin estat creades des de la mateixa aplicació, i no via API REST.',
                    gestio: 'Gestió',
                    gestioTooltip: "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan).",
                    comuns: 'Procediments i serveis comuns',
                    comunsTooltip: 'Permet realitzar notificacions i comunicacions (SIR i no SIR) sobre tots els procediments i serveis comuns amb aquest òrgan o un òrgan fill com a òrgan emissor, sempre que el procediment o servei no requereixi permís directe.',
                    notificacions: 'Notificacions',
                    notificacionsTooltip: 'Permet realitzar notificacions sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    comunicacions: 'Comunicacions',
                    comunicacionsTooltip: 'Permet realitzar comunicacions sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    sir: 'Comunicacions SIR',
                    sirTooltip: 'Permet realitzar comunicacions SIR sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    comSenseProc: 'Comunicacions sense procediment',
                    comSenseProcTooltip: 'Permet realitzar comunicacions (SIR i no SIR) sense procediment ni servei amb aquest òrgan o un òrgan fill com a òrgan emissor, sempre que el procediment o servei no requereixi permís directe.',
                },
            },
        },
        propietats: {
            find: 'Cercar a les propietats',
            empty: 'Sense propietats',
            revert: 'Desfer canvis',
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
                    consultaAllowedTooltip: 'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei).',
                    procesAllowed: 'Processar',
                    procesAllowedTooltip: 'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei) en un estat final com a processades, sempre que hagin estat creades de la mateixa aplicació i no via API REST.',
                    gestioAllowed: 'Gestió',
                    gestioAllowedTooltip: "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR amb aquest procediment o servei).",
                    notificacioAllowed: 'Notificacions',
                    notificacioAllowedTooltip: 'Permet realitzar notificacions amb aquest procediment o servei.',
                    comunicacioAllowed: 'Comunicacions',
                    comunicacioAllowedTooltip: 'Permet realitzar comunicacions amb aquest procediment o servei.',
                    comunicacioSirAllowed: 'Comunicacions SIR',
                    comunicacioSirAllowedTooltip: 'Permet realitzar comunicacions SIR amb aquest procediment o servei.',
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
                    consultaAllowedTooltip: 'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei).',
                    procesAllowed: 'Processar',
                    procesAllowedTooltip: 'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei) en un estat final com a processades, sempre que hagin estat creades de la mateixa aplicació i no via API REST.',
                    gestioAllowed: 'Gestió',
                    gestioAllowedTooltip: "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR amb aquest procediment o servei).",
                    notificacioAllowed: 'Notificacions',
                    notificacioAllowedTooltip: 'Permet realitzar notificacions amb aquest procediment o servei.',
                    comunicacioAllowed: 'Comunicacions',
                    comunicacioAllowedTooltip: 'Permet realitzar comunicacions amb aquest procediment o servei.',
                    comunicacioSirAllowed: 'Comunicacions SIR',
                    comunicacioSirAllowedTooltip: 'Permet realitzar comunicacions SIR amb aquest procediment o servei.',
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
                title: 'Remeses',
                new: {
                    title: 'Nou enviament',
                    NOTIFICACIO: 'Notificació',
                    COMUNICACIO: 'Comunicació',
                    SIR: 'Comunicació SIR',
                },
                enviament: {
                    column: {
                        interessat: 'Interessat',
                        representant: 'Representant',
                        estatPostal: "Estat d'entrega postal",
                        estatTelematica: "Estat d'entrega telemàtica",
                    },
                    detalls: 'Detalls',
                },
                column: {
                    detalls: 'Detalls',
                    desplegar: 'Desplegar enviaments',
                },
                procediment: 'Procediment',
                servei: 'Servei',
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
                        attachment: {
                            noSir: 'La mida màxima del document és de 10 MB. Els formats admesos són PDF i ZIP.',
                            sir: 'La mida màxima del document és de 10 MB. Màxim de 15 MB entre tots els documents. Els formats admesos són JPG, JPEG, ODT, ODP, ODS, ODG, DOCX, XLSX, PPTX, PDF, PNG, RTF, SVG, TIFF, TXT, XML i XSIG.',
                        },
                        normalitzat:
                            "Està la primera pàgina del document preparada per l'ensobrat?",
                    },
                },
                camps: {
                    procediment: 'Procediment',
                    servei: 'Servei',
                },
            },
            detail: {
                title: {
                    notificacio: 'Detalls de la notificació',
                    comunicacio: 'Detalls de la comunicació',
                    sir: 'Detalls de la comunicació SIR',
                },
                tab: {
                    dades: 'Dades',
                    enviaments: 'Enviaments',
                    documents: 'Documents',
                    registreEsdev: "Registre d'esdeveniments",
                    accions: 'Accions',
                    historic: 'Històric',
                },
            },
        },
        enviament: {
            grid: {
                title: 'Enviaments',
                detalls: 'Detalls',
            },
            detail: {
                title: "Detalls de l'enviament",
                tab: {
                    dades: 'Dades',
                    notifica: 'Notific@',
                    registre: 'Registre',
                    registreEsdev: "Registre d'esdeveniments",
                },
            },
        },
        integracio: {
            grid: {
                title: 'Monitor de integraciones',
            },
            detall: {
                title: "Detall del monitor d'integració",
                descripcio: 'Descripció:',
                data: 'Data:',
                tipus: 'Tipus:',
                tipusEnum: {
                    enviament: 'Enviament',
                    recepcio: 'Recepció',
                    processar: 'Processar',
                },
                estat: 'Estat:',
                estatEnum: {
                    ok: 'Correcte',
                    warn: 'Alerta',
                    error: 'Error',
                },
                parametres: 'Paràmetres',
                tooltipCopiarParametres: 'Copiar paràmetres',
                tooltipCopiarError: 'Copiar error',
                tooltipCopiat: 'Copiat',
                error: 'Error',
                errorDescripcio: "Descripció de l'error:",
                excepcioMessage: "Missatge d'excepció:",
                excepcioStacktrace: 'Stacktrace',
            },
        },
        cache: {
            grid: {
                title: "Cache d'aplicacions"
            },
        },
        activeMq: {
            grid: {
                title: "Monitor ActiveMQ"
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
                tothom: 'Usuari',
            },
        },
        PermissionGrid: {
            popupTitle: 'Permís',
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
        UserProfileFormDialog: {
            perfil: "Perfil de l'usuari",
        },
    },
    comu: {
        netejarFiltre: 'Netejar filtre',
    },
};

export default translationCa;
