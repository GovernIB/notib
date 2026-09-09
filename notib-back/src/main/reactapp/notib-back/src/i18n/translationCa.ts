const translationCa = {
    app: {
        loading: 'Iniciant NOTIB',
        noEntitat: 'Aquest usuari no te accés a cap entitat',
        sensePermisos: 'Aquest usuari no té cap permís per accedir a l\'aplicació',
        menu: {
            home: 'Inici',
            config: 'Configuració',
            monitoritza: 'Monitoritza',
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
            activemq: 'Monitor ActiveMQ',
            notificacionsCallbacksError: 'Notificacions callback erronies',
            monitorSistema: 'Monitor de sistema',
            enviamentMassiu: "Enviaments massius",
            nouEnviamentmassiu: "Nou enviament massiu",
            consultaEnviamentmassiu: "Consulta enviaments massius",
            gestio: "Gestió",
            errorRegistre: "Remeses amb error de registre",
            notificacioEsborrades: "Remeses esborrades",
            callbackPendent: "Callback pendents",
            accionsMassives: "Consulta accions massives",
            permisosUsuari: "Permisos d'usuari",
            metriques: "Mètriques",
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
                llibre: {
                    refrescar: "Refrescar llibre",
                    error: "No s'ha trobat cap llibre per al codi dir3 especificat"
                }

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
                viewSwitch: 'Vista en arbre',
                groupColumn: 'Òrgan gestor',
                popupDialogTitle: 'òrgan gestor',
                sync: {
                    title: 'Sincronització DIR3',
                    dialogTitle: 'Sincronització DIR3',
                    dialogButton: {
                        cancel: 'Cancel·lar',
                        sincronitzar: 'Sincronitzar',
                        descarregarJson: 'Descarregar òrgans JSON',
                        descarregarJsonError: "S'ha produït un error descarregant el JSON, consulti els logs",
                        descarregarPdf: 'Descarrega PDF',
                        creacions: "Nous",
                        modificacions: "Canvis en atributs",
                        substitucions: "Substitucions",
                        extincions: "Extingides",
                        fusions: "Fusions",
                        divisions: "Divisions",
                        senseCanvis: "Sense canvis",
                    },
                    success: 'Canvis aplicats amb èxit',
                    oficines: {
                        title: "Actualitza oficines",
                        success: "Oficines actualizades correctament",
                        error: "S'ha produit un error consultant les oficines, consulti els logs",
                        cancel: 'Cancelar',
                        actualitzar: "Actualitzar"
                    }
                },
                syncCombined: {
                    title: 'Actualitzar òrgans i procediments',
                    dialogTitle: 'Actualitzar òrgans i procediments',
                    nota: "Se sincronitzaran òrgans, permisos, procediments, serveis i oficines SIR.",
                    cancelar: 'Cancel·lar',
                    sincronitzar: 'Sincronitzar',
                    success: 'Actualització completada correctament',
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
                    consultaTooltip:
                        'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan emissor).',
                    processar: 'Processar',
                    processarTooltip:
                        'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan emissor) en un estat final com a processades, sempre que hagin estat creades des de la mateixa aplicació, i no via API REST.',
                    gestio: 'Gestió',
                    gestioTooltip:
                        "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR creades amb aquest òrgan o un òrgan fill com a òrgan).",
                    comuns: 'Procediments i serveis comuns',
                    comunsTooltip:
                        'Permet realitzar notificacions i comunicacions (SIR i no SIR) sobre tots els procediments i serveis comuns amb aquest òrgan o un òrgan fill com a òrgan emissor, sempre que el procediment o servei no requereixi permís directe.',
                    notificacions: 'Notificacions',
                    notificacionsTooltip:
                        'Permet realitzar notificacions sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    comunicacions: 'Comunicacions',
                    comunicacionsTooltip:
                        'Permet realitzar comunicacions sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    sir: 'Comunicacions SIR',
                    sirTooltip:
                        'Permet realitzar comunicacions SIR sobre tots els procediments i serveis que pertanyen a aquest òrgan o un òrgan fill, sempre que el procediment o servei no requereixi permís directe.',
                    comSenseProc: 'Comunicacions sense procediment',
                    comSenseProcTooltip:
                        'Permet realitzar comunicacions (SIR i no SIR) sense procediment ni servei amb aquest òrgan o un òrgan fill com a òrgan emissor, sempre que el procediment o servei no requereixi permís directe.',
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
            entitats: {
                empty: 'Sense propietats per entitat configurades',
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
                sync: {
                    title: "Actualitzar procediments",
                    confirmacio: "Vols actualitzar els procediments amb la informació de ROLSAC?",
                    actualitzar: "Actualitzar",
                    cancelar: "Cancelar",
                    success: "Procediments actualitzats correctament",
                    error: "Error actualitzant els procediments",
                },
                netejarCache: {
                    title: "Neteja memòria cache",
                    success: "Memòria cache netejada correctament",
                    error: "Error netejant la memòria cache"
                },
                accions: {
                    activar: {
                        title: "Activa",
                        success: "El procediment s'ha activat correctament",
                        error: "Error activant el procediment",
                    },
                    desactivar: {
                        title: "Desactiva",
                        success: "El procediment s'ha desactivat correctament",
                        error: "Error desactivant el procediment",
                    },
                    actualitzar: {
                        title: "Actualitza procediment",
                        success: "El procediment s'ha actualitzat correctament",
                        error: "Error actualitzant el procediment",
                    },
                    syncManual: {
                        title: "Sincronització manual",
                        success: "El procediment s'ha marcat per a actualitzar-se de forma manual",
                        error: "Error marcant el procediment per a sincronització manual",
                    },
                    syncAuto: {
                        title: "Sincronització automàtica",
                        success: "El procediment s'ha marcat per a actualitzar-se de forma automàtica",
                        error: "Error marcant el procediment per a sincronització automàtica",
                    },
                },
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
                    consultaAllowedTooltip:
                        'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei).',
                    procesAllowed: 'Processar',
                    procesAllowedTooltip:
                        'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei) en un estat final com a processades, sempre que hagin estat creades de la mateixa aplicació i no via API REST.',
                    gestioAllowed: 'Gestió',
                    gestioAllowedTooltip:
                        "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR amb aquest procediment o servei).",
                    notificacioAllowed: 'Notificacions',
                    notificacioAllowedTooltip:
                        'Permet realitzar notificacions amb aquest procediment o servei.',
                    comunicacioAllowed: 'Comunicacions',
                    comunicacioAllowedTooltip:
                        'Permet realitzar comunicacions amb aquest procediment o servei.',
                    comunicacioSirAllowed: 'Comunicacions SIR',
                    comunicacioSirAllowedTooltip:
                        'Permet realitzar comunicacions SIR amb aquest procediment o servei.',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Serveis',
                sync: {
                    title: "Actualizar serveis",
                    actualitzar: "Actualizar",
                    cancelar: "Cancelar",
                    success: "Serveis actualizados correctamente",
                    error: "Error actualizando los serveis",
                },
                netejarCache: {
                    title: "Limpia memoria cache",
                    success: "Memoria cache limpiada correctamente",
                    error: "Error limpiando la memoria cache"
                }
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
                    consultaAllowedTooltip:
                        'Dona permís per a consultar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei).',
                    procesAllowed: 'Processar',
                    procesAllowedTooltip:
                        'Permet marcar les notificacions i comunicacions (SIR i no SIR creades amb aquest procediment o servei) en un estat final com a processades, sempre que hagin estat creades de la mateixa aplicació i no via API REST.',
                    gestioAllowed: 'Gestió',
                    gestioAllowedTooltip:
                        "Dona accés a la pestanya d'accions del detall de les notificacions i comunicacions (SIR i no SIR amb aquest procediment o servei).",
                    notificacioAllowed: 'Notificacions',
                    notificacioAllowedTooltip:
                        'Permet realitzar notificacions amb aquest procediment o servei.',
                    comunicacioAllowed: 'Comunicacions',
                    comunicacioAllowedTooltip:
                        'Permet realitzar comunicacions amb aquest procediment o servei.',
                    comunicacioSirAllowed: 'Comunicacions SIR',
                    comunicacioSirAllowedTooltip:
                        'Permet realitzar comunicacions SIR amb aquest procediment o servei.',
                },
            },
        },
        pagadorPostal: {
            grid: {
                title: 'Operadors postals',
                popupResourceTitle: 'operador postal',
            },
            form: {
                titleCreate: "Crear operador postal",
                titleUpdate: "Modificar operador postal",
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
                title: 'Consulta de remeses',
                detall: 'Detall',
                new: {
                    title: 'Nou enviament',
                    NOTIFICACIO: 'Notificació',
                    COMUNICACIO: 'Comunicació',
                    SIR: 'Comunicació SIR',
                },
                enviament: {
                    column: {
                        interessat: 'Interessat',
                        representant: {
                            title: 'Representant',
                            senseRepresentant: "Sense representant"
                        },
                        estatPostal: {
                            title: "Estat d'entrega postal",
                            senseCie: "Sense entrega postal",
                            cieNotifica: "Entrega postal gestionada per Notifica"
                        },
                        estatTelematica: "Estat d'entrega telemàtica",
                    },
                    accions: 'Accions',
                },
                column: {
                    detalls: 'Detalls',
                    mostrar: 'Mostrar enviaments',
                    ocultar: 'Ocultar enviaments',
                },
                accions: {
                    documentEnviat: "Document enviat",
                    anular: {
                        botoTitle: "Anular",
                        modalTitle: "Anul·lació",
                        noReposta: "Error inesperat al anular",
                        noExecutades: "Identificadors d'enviaments no executats: ",
                        respostesError: "Enviaments amb error de anul·lació: ",
                        ok: "Anul·lació realitzada correctament"
                    },
                    certificacio: "Certificacio",
                    processat: "Marcar com a processat",
                    processatTitle: "Marcar com a processat",
                    justificantEnviament: "Justificant d'enviament",
                    ampliarTermini: {
                        botoTitle: "Ampliar termini",
                        modalTitle: "Ampliació de termini",
                        noReposta: "Error inesperat ampliant termini",
                        noExecutades: "Identificadors d'enviaments no executats: ",
                        respostaError: "Error al ampliar el termini: ",
                        ok: "Ampliació de termini realitzada correctament"
                    },
                    editar: "Editar",
                    esborrar: {
                        title: "Esborrar",
                        ok: "La remesa s'ha esborrat correctament"
                    },
                },
                procediment: 'Procediment',
                servei: 'Servei',
                notificacionsEsborrades: {
                    title: ' esborrades',
                    recuperar: "Recuperar"
                },
                notificacionsErrorRegistre: {
                    title: " amb error de registre",
                },
                notificacionsCallbackError: {
                    title: " amb error a l'últim callback",
                }
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
                    procedimentComuns: 'Procediments comuns',
                    procedimentOrgans: "Procediments d'òrgans gestors",
                    serveiComuns: 'Serveis comuns',
                    serveiOrgans: "Serveis d'òrgans gestors",
                },
            },
            detail: {
                title: {
                    notificacio: 'Detalls de la notificació',
                    comunicacio: 'Detalls de la comunicació',
                    sir: 'Detalls de la comunicació SIR',
                    erroRegistre: "Detalls de la excepció"
                },
                errorRegistre: {
                    data: "Data",
                    tipus: "Tipus",
                    error: "Error descripció"
                },
                errorCallback: {
                    data: "Data",
                    tipus: "Tipus",
                    error: "Error descripció"
                },
                tab: {
                    dades: 'Dades',
                    enviaments: 'Enviaments',
                    documents: 'Documents',
                    registreEsdev: "Registre d'esdeveniments",
                    accions: 'Accions',
                    historic: 'Històric',
                },
                dades: {
                    title: 'Dades de la notificació',
                    refrescar: "Refrescar la columna estat",
                    anulada: "La remesa té un o més enviaments anul·lats",
                    errorCanviEstat: "Hi ha hagut error notificant al client del canvi d'estat...",
                    justificant: 'Justificant',
                    grup: {
                        title: 'Grup',
                        codi: "Codi",
                        nom: "Nom",
                    },
                    documents: {
                        title: 'Document',
                        nom: "Nom arxiu",
                        normalitzat: "Normalitzat",
                        csv: "Generar CSV",
                        descarregarDocument: "Document enviat",
                    },
                    pagadorPostal: {
                        title: "Dades del pagador postal",
                        organismePagadorNom: "Organisme pagador",
                        contracteNum: "Número de contracte",
                        facturacioClientCodi: "Codi del client",
                        contracteDataVig: "Data de vigència del contracte",
                    },
                    pagadorCie: {
                        title: "Dades del pagador CIE",
                        organismeEmissor: "Organisme emissor",
                        vigencia: "Data de vigència del contracte",
                    },
                    enviaments: {
                        title: 'Enviament',
                        interessat: "Interessat",
                        destinataris: "Representants",
                        senseDestinataris: "Sense destinataris",
                        estat: "Estat",
                        registre: {
                            title: "Registre",
                            registreNumeroFormatat: "Núm. registre",
                            registreData: "Data registre",
                            registreEstat: "Estat registre",
                            sirRecepcioData: "Data recepció SIR",
                            registreMotiu: "Motiu",
                            sirRegDestiData: "Data registre SIR",
                            registreOficinaNom: "Oficina",
                            registreLlibreNom: "Llibre",
                            noRegistrat: "No registrat",
                            certificacio: "Certificació",
                            noCertificacio: "Sena certificació",
                            notificaCertificacioData: "Data",
                            notificaCertificacioMime: "Tipus MIME",
                            notificaCertificacioOrigen: "Origen",
                            notificaCertificacioMetadades: "Metadades",
                            notificaCertificacioCsv: "CSV",
                            notificaCertificacioTipus: "Tipus",
                            notificaCertificacioArxiuTipus: "Tipus d'arxiu",
                            notificaCertificacioNumSeguiment: "Núm. seguiment",
                            certificacioNom: 'Document'
                        },
                    },
                },
                accions: {
                    success: "Acció enviada a executar correctament",
                    error: "Error al enviar a executar l'acció. Consultar la pipella d'esdeveniments per més informació",
                    noAccions: "No hi ha accions disponibles per a aquest enviament",
                    enviarCallback: "Envia canvi estat al client",
                    enviarEntregaPostal: "Reenviar la entrega postal",
                    enviarEntregaPostalButton: 'Reenvia la entrega postal',

                    registrar: {
                        title: "Registrar notificació pendent",
                        button: "Registra",
                        noReposta: "Error inesperat al executar la acció de registrar",
                        noExecutades: "Identificadors de remeses no executades: ",
                        repostesError: "Remeses amb error al enviar a registrar: ",
                        ok: "La remesa s'ha enviat a registrar"
                    },
                    enviarNotifica: {
                        title: "Envia notificació registrada a Notific@",
                        button: "Envia"
                    },
                    reactivarEstatNotifica: {
                        title: 'Reactivar estat de Notific@',
                        button: 'Reactivar estat'
                    },
                    reactivarSir: {
                        title: 'Reactivar consulta SIR',
                        button: 'Reactiva'
                    },
                    reactivarErrors: {
                        title: 'Reactiva enviaments amb error',
                        button: 'Reactiva'
                    },
                    reenviarErrors: {
                        title: 'Reenviar enviaments amb error',
                        button: 'Reenvia'
                    }
                },
            },
        },
        enviament: {
            grid: {
                title: 'Consulta d\'enviaments',
                detalls: 'Detalls',
                remesa: 'Remesa',
                anular: 'Anul·lar',
                ampliarTermini: 'Ampliar termini',
            },
            detail: {
                title: "Detalls de l'enviament",
                tab: {
                    dades: {
                        title: 'Dades',
                        enviaments: "Dades de l'enviament",
                        interessat: 'Dades del interessat',
                        titularInfo: {
                            nif: 'Nif',
                            nom: 'Nom',
                            llinatges: 'Llinatges',
                            telefon: 'Telèfon',
                            email: 'Email',
                        },
                    },
                    notifica: {
                        title: 'Notific@',
                        datat: 'Datat',
                        certificacio: 'Certificació',
                        noEnviat: 'Aquest enviament encara no ha estat enviat',
                        notificacioNoEnviat: 'Aquesta notificació no ha estat enviada a Notific@',
                        refrescarEstat: {
                            titleButton: 'Refrescar estat',
                            success: "Estat de l'enviament refrescat amb èxit",
                            error: "Error refrescant l'estat de l'enviament",
                        },
                    },
                    registre: {
                        title: 'Registre',
                        dadesRegistre: 'Dades del registre',
                        noEnviada: 'Aquesta notificació no ha estat enviada al registre',
                    },
                    entregaPostal: {
                        title: 'Entrega postal',
                        cieEstat: 'Estat',
                        cieEstatPendent:
                            "L'entrega postal no ha estat enviada o s'ha enviat amb errors. Veure pestanya d'events per més informació",
                        cieEstatData: 'Estat data',
                        cieId: 'Identificador Notifica',
                        cieDatatErrorDescripcio: 'Estat descripció',
                        cieDatatOrigen: 'Origen',
                        cieDatatReceptorNif: 'Receptor NIF',
                        cieDatatReceptorNom: 'Receptor nom',
                        cieDatatNumSeguiment: 'Núm. seguiment',
                        cieErroni:
                            "L'entrega CIE no es podrà donar d'alta ja que dona errors al crear-la al CIE",
                        refrescarEstat: {
                            titleButton: 'Refresca estat',
                            success: "Estat de l'enviament postal refrescat amb èxit",
                            error: "Error refrescant l'estat de l'enviament postal",
                        },
                        cancelar: {
                            titleButton: 'Cancelar',
                            success: "Enviament postal enviat a cancelar",
                            error: "Error cancelant l'enviament postal",
                        },
                        cancelarEntregaPostal: 'Cancelar entrega postal',
                        certificacio: 'Certificació',
                        cieCertificacioData: 'Data',
                        cieCertificacioMime: 'Tipus MIME',
                        cieCertificacioOrigen: 'Origen',
                        cieCertificacioMetadades: 'Metadades',
                        cieCertificacioCsv: 'CSV',
                        cieCertificacioTipus: 'Tipus',
                        cieCertificacioArxiuTipus: "Tipus d'arxiu",
                        cieCertificacioNumSeguiment: 'Núm. seguiment',
                        cieCertificacioArxiuNom: 'Document',
                        descarregarCertificacio: 'Descarregar'
                    },
                    registreEsdev: {
                        title: "Registre d'esdeveniments",
                        estatError: 'Esdeveniment processat amb error',
                        estatSuccess: 'Esdeveniment processat amb èxit',
                    },
                    historic: {
                        title: 'Històric',
                    },
                    stateMachine: {
                        title: 'State Machine',
                        missatge:
                            'Atenció! No alterar els estats de la state machine ni enviar events si no es coneixen les repercusions que poden tenir aquetes accions.' +
                            'En cas de dubte consultar abans de fer res. En qualsevol cas nomès fer-ho si la remesa tè un estat incoherent.',
                        estatOrigen: 'Estat origen',
                        event: 'Event',
                        taulaEstatsTitle: 'Operacions que es realitzen al enviar events',
                        descarregarDiagrama: 'Descarregar diagrama',
                        accioResultant: {
                            header: 'Acció resultant',
                            registrar: 'Registrat',
                            resetIntentsRegistre: 'Reseteja intents registre',
                            reintentarRegistre: 'Reintenta registre',
                            errorRegistre: 'Error de registre',
                            registreOk: 'Registre ok',
                            enviarNotifica: 'Envia a Notific@',
                            notificaOk: 'Notific@ ok',
                            notificaError: 'Error de Notific@',
                            reintentNotifica: 'Reintenta enviament a Notific@',
                            finalitzaRemesa: 'Finalitza remesa',
                            resetIntentsNotifica: 'Reseteja intents de Notific@',
                            consultaEstatEnviament: 'Consulta estat enviament',
                            consultaEstatEnviamentOk: 'Consulta estat enviament ok',
                            consultaEstatEnviamentError: 'Consulta estat enviament error',
                            reintentarConsultaEstatEnviament: 'Reintenta consulta estat enviament',
                            consultaEnviamentSir: 'Consulta enviament SIR',
                            consultaEnviamentSirOk: 'Consulta SIR ok',
                            consultaEnviamentSirError: 'Consulta SIR error',
                            reintentarConsultaEnviamentSir: 'Reintenta consulta SIR',
                            finalitzarComunicacioSir: 'Finalitza comunicació SIR',
                        },
                    },
                },
            },
        },
        accioMassiva : {
            grid: {
                title: 'Accions Massives',
                tipus: 'Tipus',
                createdDate: 'Data creació',
                dataInici: 'Data inici',
                dataFi: 'Data fi',
                createdBy: 'Codi usuari',
                okErrorPendent: 'Ok/Error/Pendent',
                progres: 'Progrés',
                mostarElements: 'Mostrar elements de l\'acció massiva',
                ocultarElements: 'Ocultar elements de l\'acció massiva',
                elements: {
                    titol: 'Elements de l\'acció massiva',
                    referencia: 'Referencia',
                    data: 'Data',
                    estat: 'Estat',
                    errorDesc: 'Error descripció',
                    estatFinalitzat: 'Finalitzat',
                    estatError: 'Error',
                    estatPendent: 'Pendent'
                }
            },
            accions: {
                labelBoto: 'Accions massives',
                selectAll: "Seleccionar tot",
                deselectAll: "Desmarcar tot",
                executant: "Processant acció massiva",
                noExecutades: "Identificadors no executables: ",
                respostesError: "Identificadors amb error: ",
                marcarProcessades: {
                    label: "Marcar com a processades",
                    tooltip: "Marca les remeses que es troben en un estat final com a processades",
                    ok: "La acció marcar com a processat s'ha enviat a executar correctament"
                },
                reintentarRegistre: {
                    label: "Reintentar registre",
                    tooltip: "Reseteja els intents de registre",
                    ok: "La acció de reintentar registre s'ha enviat a executar correctament"
                },
                actualitzarEstat: {
                    label: "Actualitzar l'estat",
                    tooltip: "Actualitza el estat (SIR i Notifica) de les remeses que no se troben en un estat final",
                    ok: "L'acció d'actualitzar l'estat s'ha enviat a executar"
                },
                reenviarAmbError: {
                    label: "Tornar a enviar les que han donat error",
                    tooltip: "Torna a enviar les remeses que han donat error durant el procés de registre o d'enviament a Notifica i han esgotat el nombre màxim d'intents",
                    ok: "L'acció de reenviar amb error s'ha executat correctament"
                },
                esborrar: {
                    label: "Esborrar",
                    tooltip: "Marca les remeses com a esborrades i no les mostra en el llistat",
                    ok: "L'acció de esborrar s'ha realitzat correctament"
                },
                exportarFullCalcul: {
                    label: "Exporta a full de càlcul",
                    tooltip: "Exporta el llistat de remeses a un fitxer de full de càlcul en format ods",
                    ok: "La exportació s'ha realitzat amb èxit"
                },
                justificantEnviament: {
                    label: "Descarrega justificants d'enviament",
                    tooltip: "Descarrega el justificant d'enviament de les remeses seleccionades",
                    ok: "Fitxer ZIP de justificants generat amb èxito"
                },
                certificacioRecepcio: {
                    label: "Descarrega certificats de recepció",
                    tooltip: "Descarrega la certificació de recepció de les remeses seleccionades"
                },
                anular: {
                    label: "Anul·lar",
                    tooltip: "Anul·la les remeses enviades a Notific@ que no hagin estat notificades",
                    ok: "La acció anul·lar s'ha enviat a executar correctament"
                },
                ampliarTermini: {
                    label: "Ampliar termini",
                    tooltip: "Amplia el termini de caducitat de les remeses seleccionades",
                    ok: "La acció ampliar termini s'ha enviat a executar correctament"
                },
                reactivarCanviEstat: {
                    label: "Torna a activar les consultes de canvi d'estat",
                    tooltip: "Torna a activar les consultes de canvi d'estat a Notific@ o SIR si s'han esgotat el nombre màxim de reintents. Aquesta accio torna el contador d'intents a zero fins que es torni a arribar al número màxim d'intents",
                    ok: "La acció reactivar la consulta de canvi d'estat s'ha enviat a executar correctament"
                },
                reactivarCallbacks: {
                    label: "Torna a activar l'enviament de callbacks",
                    tooltip: "Torna a activar l'enviament de callbacks a aplicacions si s'ha esgotat el nombre màxim d'intents màxim. Aquesta acció posa el comptador d'intents a zero fins que es torni a arribar al nombre màxim d'intents",
                    ok: "La acció reactivar callbacks s'ha enviat a executar correctament"
                },
                notificacionsMovil: {
                    label: "Envia notificacions mòvil",
                    tooltip: "Envia notificacions de canvi d'estat a dispositius mòvils (PUSH)",
                    ok: "La acció d'enviar notifiacions mòvils s'ha enviat a executar correctament"
                },

            },
        },
        notificacioMassiva: {
            grid: {
                title: 'Consulta d\'enviaments massius',
                createdDate: 'Creada el',
                csvFilename: 'Notifiacions',
                zipFilename: 'Documents',
                csvTooltip: 'Descarregar fitxer CSV',
                zipTooltip: 'Descarregar document ZIP',
                estatValidacio: 'Validació CSV',
                estatProces: 'Estat',
                createdBy: 'Creada per',
                llegenda: {
                    numProcessats: "Num.enviaments processats",
                    numErronis: "Num.enviaments erronis",
                    numCancelats: "Num.enviaments cancelats",
                },
                accions : {
                    resum: 'Resum',
                    descarregarResum: 'Descarregar resum',
                    errorsValidacio: 'Errors validació',
                    errorsExecucio: 'Errors execució',
                    posposar: {
                        title: 'Posposa',
                        success: "Notificació massiva posposada amb èxit",
                        error: "Error posposant la notificació massiva"
                    },
                    reactivar: {
                        title: 'Reactiva',
                        success: "Notificació massiva reactivada amb èxit",
                        error: "Error reactivant la notificació massiva"
                    },
                    mostrarRemeses: {
                        label: 'Mostra remeses',
                        msg1: 'Enviament massiu del',
                        msg2: 'Usuari'
                    }
                },
                estats: {
                    PENDENT: 'Pendent',
                    EN_PROCES: 'En procés',
                    EN_PROCES_AMB_ERRORS: 'En procés amb errors',
                    FINALITZAT: 'Finalitzat',
                    FINALITZAT_AMB_ERRORS: 'Finalitzat amb errors',
                    ERRONIA: 'Errònia',
                    CANCELADA: 'Cancelada',
                    FINALITZAT_PARCIAL: 'Finalitzat parcial'
                }
            },
            detall: {
                title: 'Detall de la notificació massiva',
                dades: {
                    title: 'Dades',
                    csvFilename: 'Fitxer CSV amb els enviaments',
                    zipFilename: 'Fitxer ZIP amb els documents adjunts',
                    caducitat: 'Caducitat',
                    createdDate: 'Creada el',
                    email: 'Email',
                    createdBy: 'Creada per',
                },
                resum: {
                    title: 'Resum',
                    enviamentTipus: 'Tipus d\'enviament',
                    codiDir3UnidadRemisora: 'Unitat remisora',
                    concepto: 'Concepte',
                    descripcio: 'Descripció',
                    prioridadServicio: 'Prioritat servei',
                    nomComplert: 'Interessat',
                    errores: 'Errors validació',
                    errorsExecucio: 'Errors execució',
                    cancelada: 'Cancelada',
                    interssatSenseNif: " - Interessat sense NIF",
                    noRows: "Sense files"
                }
            },
            form: {
                title: 'Crear enviament massiu',
                indicacions: {
                    title: 'Indicacions per a complimentar el fitxer de dades:',
                    indicacio1: 'El fitxer de dades ha de ser un document de text pla (CSV) amb els camps separats per punts i comes. Els fitxers amb separadors diferents fallaran (per exemple ",").',
                    indicacio2: 'La primera fila ha de contenir la capçalera de les columnes'
                },
                csvFieldLabel: 'Fitxer CSV amb els enviaments',
                csvFieldText: 'Només s\'admet format CSV. La mida màxima del document és de 2 Mb. El nombre màxim de notificacions és de 999.',
                zipFieldLabel: 'Fitxer ZIP amb els documents adjunts',
                zipFieldText: 'Només s\'admet format ZIP. La mida màxima del document és de 15 Mb.',
                caducitat: 'Caducitat',
                email: 'Email d\'avís quan finalitzi la càrrega',
                codisEntregaPostal: 'Descarrega codis entrega postal',
                modelCsv: 'Descarrega model de dades CSV'
            }
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
                title: "Cache d'aplicacions",
            },
            accions: {
                buidarMassiuOk: "Caches buidades correctament",
                buidarOk: "Cache buidada correctament",
                buidar: "Buidar cache",
                buidarMassiu: "Buidar caches"
            }
        },
        activemq: {
            grid: {
                title: 'Monitor ActiveMQ',
                missatges: "Missatges",
                buidar: "Buidar",
                buidarOk: "La cua s'ha buidat correctament"
            },
            detail: {
                title: "Missatges cua ",
                esborrar: "Esborrar",
                esborrarOk: "Missatge esborrat correctament",
                esborrarError: "Error esborrant el missatge"
            },
            descargarJobScheduler: "JobScheduler JSON"
        },
        callbacks: {
            pendents: {
                grid: {
                    title: "Callbacks pendents",
                    accions: {
                        enviar: {
                            title: "Enviar"
                        },
                        pausar: {
                            title: "Pausar"
                        },
                        activar: {
                            title: "Activar"
                        },
                        esborrar: {
                            title: "Esborrar"
                        }
                    }
                },
                accionsMassives: {
                    enviarPendents: {
                        label: "Enviar",
                        ok: "La acció d'enviar els callbacks s'ha enviat a executar correctament"
                    },
                    pausarPendents: {
                        label: "Pausar",
                        ok: "La acció de pausar els callbacks s'ha enviat a executar correctament"
                    },
                    activarPendents: {
                        label: "Activar",
                        ok: "La acció d'activar els callbacks s'ha enviat a executar correctament"
                    },
                    esborrarPendents: {
                        label: "Esborrar",
                        ok: "La acció d'esborrar els callbacks s'ha enviat a executar correctament"
                    },
                }
            },
            error: {
                grid: {
                    title: "Notificacions amb error a l'últim callback",
                },
                accionsMassives: {
                    reenviar: {
                        label: "Reintenta callback",
                        ok: "S'han reenviat els callbacks amb errror"
                    }
                }
            }
        },
        monitorSistema: {
            tab: {
                sistema: {
                    title: 'Sistema',
                    sistemaOperatiu: 'Sistema operatiu',
                    arquitectura: 'Arquitectura',
                    processadors: 'Processadors',
                    jbossVersion: 'Versió de Jboss',
                    applicationServerInfo: "Informació del servidor d'aplicacions",
                    tempsFuncionant: 'Temps funcionant',
                    jvmMemory: 'Màquina virtual de Java',
                    disksUsage: 'Disc i CPU',
                },
                fils: {
                    title: "Fils d'execució",
                },
                tasques: {
                    title: 'Tasques en segon pla',
                    restart: 'Reiniciar',
                    restartOk: 'Reiniciat correctament',
                    restartSelect: 'Reiniciar seleccionades',
                    restartSelectOk: 'Reiniciades correctament',
                },
            },
        },
        metriques: {
            title: "Mètriques",
            llegenda: {
                title: "Llegenda",
                numExecTempsMig: "Número d'execucions x Temps mig d'una execució (ms)",
                tempsMigExcecuio: "Temps mig d'una execució (ms)",
                tempsMaxim: "Temps màxim (ms)"
            },
            excecucions: "execucions",
            generics: "Genèrics",
            frequencia: "Freqüència",
            mitjana: "Mitjana",
            duracio: "Duració",
            percentils: "Percentils",
            exportaJson: "Exporta",
            importaJson: "Importa"
        },
        usuaris: {
            permisos: {
                grid: {
                    title: "Permisos d'usuari",
                    organsPermisDirecte: "Òrgans amb permís directe",
                    organsPermisHeredat: "Òrgans amb permisos heredats del pare",
                    procedimentPermisDirecte: "Procediments amb permís directe",
                    procedimentPermisOrgan: "Procediments amb permís per òrgan",
                    columnes: {
                        nom: "Nom",
                        organGestor: "Òrgan gestor",
                        tipus: "Tipus",
                        principal: "Principal",
                    }
                }
            }
        },
        notFound: {
            title: 'Pàgina no trobada',
            toHome: "Anar a l'inici",
        },
    },
    component: {
        AppFormFieldReference: {
            mostrarTots: 'Mostrar tots...',
        },
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
                NOT_ADMIN: 'Administrador Entitat',
                NOT_ADMIN_LECTURA: "Administrador (lectura)",
                NOT_ORGAN: "Administrador d'òrgan",
                NOT_APL: 'Aplicació',
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
        UserProfile: {
            perfil: "Perfil de l'usuari",
            auto: 'Automàtic',
            dades: 'Dades',
            correu: 'Notificacions per correu',
            general: 'Configuració general',
            tema: 'Aparença',
        },
        SwitchInterface: {
            classica: 'Anar a la versió clàssica',
        },
        FormDropzoneField: {
            arrosegar: 'Arrossega el fitxer aquí',
            amollar: 'Amolla el fitxer ara ...',
            validacio: "L'arxiu no és vàlid",
        },
        ButtonDetailExpandColapse: {
            expandAll: "Expandir tots",
            collapseAll: "Contreure tots",
        },
        Dir3SearchInput: {
            search: "Cercar",
            dialog: {
                title: "Consulta d'administracions públiques a DIR3",
                netejar: "Netejar",
                noCif: "Sense CIF",
                noSir: "Sense SIR",
                viaValib: "Via Valib",
            }
        },
    },
    hook: {
        useDataGrid: {
            treeData: {
                collapseAll: 'Contreure tot',
                expandAll: 'Expandir tot',
            },
        },
    },
    utils: {
        estatConfig: {
            ESTAT_ENUM_MAP: {
                PENDENT: "Pendent",
                ENVIADA: "Enviada",
                REGISTRADA: "Registrada",
                FINALITZADA: "Finalitzada",
                PROCESSADA: "Processada",
                EXPIRADA: "Expirada",
                NOTIFICADA: "Notificada",
                REBUTJADA: "Rebutjada",
                ENVIAT_SIR: "Enviada SIR",
                ENVIADA_AMB_ERRORS: "Enviada amb errors",
                FINALITZADA_AMB_ERRORS: "Finalitzada amb errors",
                ENVIANT: "Enviant",
                OFICI_ACCEPTAT: "Ofici acceptat",
                REBUTJADA_SIR: "Rebutjada SIR",
                ANULADA: "Anul·lada",
            },
            NOTIFICACIO_REGISTRE_ESTAT_ENUM_MAP: {
                VALID: "Vàlid",
                RESERVA: "Reserva",
                PENDENT: "Pendent",
                OFICI_EXTERN: "Ofici extern",
                OFICI_INTERN: "Ofici intern",
                OFICI_ACCEPTAT: "Ofici acceptat",
                DISTRIBUIT: "Distribuït",
                ANULAT: "Anul·lat",
                RECTIFICAT: "Rectificat",
                REBUTJAT: "Rebutjat",
                REENVIAT: "Reenviant",
                DISTRIBUINT: "Distribuint",
                OFICI_SIR: "Ofici SIR",
                ENVIAT_NOTIFICAR: "Enviat notificar",
            },
            ENVIAMENT_ESTAT_MAP: {
                NOTIB_PENDENT: "Pendent d'enviar",
                NOTIB_ENVIADA: "Enviada",
                ABSENT: "Absent",
                ADRESA_INCORRECTA: "Direcció incorrecta",
                DESCONEGUT: "Desconegut",
                ENVIADA_CI: "Enviat al centre d'impressió",
                ENVIADA_DEH: "Enviat a la DEH",
                ENVIAMENT_PROGRAMAT: "Enviament programat",
                ENTREGADA_OP: "Entregada a l'operador postal",
                ERROR_ENTREGA: "Error en l'enviament",
                EXPIRADA: "Expirada",
                EXTRAVIADA: "Extraviada",
                MORT: "Llegit",
                LLEGIDA: "Mort",
                NOTIFICADA: "Notificada",
                PENDENT: "Pendent",
                PENDENT_ENVIAMENT: "Pendent d'enviar",
                PENDENT_SEU: "Pendent de compareixença",
                PENDENT_CIE: "Pendent d'entrega a CIE",
                PENDENT_DEH: "Pendent d'entrega a DEH",
                REBUTJADA: "Rebutjada",
                SENSE_INFORMACIO: "Sense informació",
                FINALITZADA: "Anul·lada",
                ENVIADA: "Enviada SIR",
                REGISTRADA: "Enviada amb errors",
                PROCESSADA: "Finalitzada amb errors",
                ANULADA: "Registrada",
                ENVIAT_SIR: "Enviada",
                ENVIADA_AMB_ERRORS: "Finalitzat",
                FINALITZADA_AMB_ERRORS: "Processat",
            },
        },
    },
    comu: {
        netejarFiltre: 'Netejar filtre',
        filtrar: 'Filtrar',
        obrirFiltreAvançat: 'Obrir filtre avançat',
        tancarFiltreAvançat: 'Tancar filtre avançat',
        guardar: 'Desa',
        cancelar: 'Cancel·la',
        organGestorNoVigent: 'Òrgan no vigent',
    },
};

export default translationCa;
