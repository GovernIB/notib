const translationEs = {
    app: {
        loading: 'Iniciando NOTIB',
        noEntitat: 'Este usuario no tiene acceso a ninguna entidad',
        menu: {
            home: 'Inicio',
            config: 'Configuración',
            monitoritza: 'Monitoriza',
            entitats: 'Entidades',
            avisos: 'Avisos',
            propietats: 'Propiedades',
            currentEntitat: 'Entidad actual',
            organsGestors: 'Órganos gestores',
            procediments: 'Procediments',
            serveis: 'Serveis',
            grups: 'Grupos',
            enviaments: 'Envios',
            pagadorsPostals: 'Operadores postales',
            pagadorsCie: 'Centros de impresión y ensobrado',
            notificacions: 'Remesas',
            integracions: 'Integraciones',
            cache: "Cache de aplicaciones",
            activemq: "Monitor ActiveMQ",
            notificacionsCallbacksError: "Notificaciones callback erroneas",
            enviamentMassiu: "Envíos masivos",
            nouEnviamentmassiu: "Nuevo envío masivo",
            consultaEnviamentmassiu: "Consulta envíos masivos",
            gestio: "Gestión",
            errorRegistre: "Remesas con error de registro",
            notificacioEsborrades: "Remesas eliminadas",
            callbackPendent: "Callback pendientes",
            accionsMassives: "Consulta acciones masivas",
            permisosUsuari: "Permisos de usuario",
            metriques: "Métricas"
        },
    },
    page: {
        home: {
            toolbar: {
                title: 'Bienvenidos a NOTIB',
                subtitle: 'Aplicación para gestionar y enviar las notificaciones de la CAIB',
            },
        },
        entitats: {
            grid: {
                title: 'Entidades',
            },
            form: {
                titleCreate: 'Crear entidad',
                titleUpdate: 'Modificar entidad',
                tabs: {
                    dades: 'Datos',
                    personalitzar: 'Personalizar',
                    tipusDocs: 'Tipos doc.',
                    aplicacions: 'Aplicaciones',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    aplicacio: 'aplicación',
                    permis: 'permiso',
                },
                personalitzar: {
                    capsalera: 'Cabecera',
                },
                tipusDocuments: {
                    tableColumn: {
                        tipusDoc: 'Tipo de documento',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Tipo de documento activado',
                        error: 'Error al activar el tipo de documento',
                    },
                    disable: {
                        success: 'Tipo de documento desactivado',
                        error: 'Error al desactivar el tipo de documento',
                    },
                },
                permisos: {
                    usuariAllowed: 'Usuario',
                    admEntitatAllowed: 'Adm. entidad',
                    admLecturaAllowed: 'Adm. lectura',
                    aplicacioAllowed: 'Aplicación',
                },
                llibre: {
                    refrescar: "Refrescar libro",
                    error: "No se ha encontrado ningún libro con el código dir3 especificado"
                }
            },
        },
        avisos: {
            grid: {
                title: 'Avisos',
            },
            form: {
                titleCreate: 'Crear aviso',
                titleUpdate: 'Modificar aviso',
            },
        },
        grups: {
            grid: {
                title: 'Grupos',
                popupResourceTitle: 'grupo',
            },
        },
        organs: {
            grid: {
                title: 'Órganos gestores',
                viewSwitch: 'Vista en árbol',
                groupColumn: 'Órgano gestor',
                popupDialogTitle: 'órgano gestor',
                sync: {
                    title: 'Sincronización DIR3',
                    dialogTitle: 'Sincronización DIR3',
                    dialogButton: {
                        cancel: 'Cancelar',
                        query: 'Consultar cambios',
                        apply: 'Aplicar cambios',
                        creacions: "Creaciones",
                        modificacions: "Modificaciones",
                        substitucions: "Sustituciones",
                        extincions: "Extinciones",
                        fusions: "Fusiones",
                        divisions: "Divisiones",
                        aplicarCanvis: "Haga clic al botón de aplicar para hacer efectivos los cambios.",
                        senseCanvis: "Sin cambios",
                    },
                    success: 'Cambios aplicados con éxito',
                },
            },
            form: {
                titleCreate: 'Crear órgano gestor',
                titleUpdate: 'Modificar órgano gestor',
                tabs: {
                    dades: 'Datos',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permiso',
                },
                permisos: {
                    tipus: 'Tipos',
                    grantedAuthority: {
                        user: 'Usuario',
                        role: 'Rol',
                    },
                    administrador: 'Administrador',
                    administradorTooltip: 'Asigna el perfil de administrador de órgano.',
                    consulta: 'Consulta',
                    consultaTooltip: 'Da permiso para consultar las notificaciones y comunicaciones (SIR y no SIR creadas con este órgano o un órgano hijo como órgano emisor).',
                    processar: 'Procesar',
                    processarTooltip: 'Permite marcar las notificaciones y comunicaciones (SIR y no SIR creadas con este órgano o un órgano hijo como órgano emisor) en un estado final como procesadas, siempre que hayan sido creadas desde la misma aplicación, y no vía API REST.',
                    gestio: 'Gestión',
                    gestioTooltip: 'Da acceso a la pestaña de acciones del detalle de las notificaciones y comunicaciones (SIR y no SIR creadas con este órgano o un órgano hijo como órgano).',
                    comuns: 'Procedimientos y servicios comunes',
                    comunsTooltip: 'Permite realizar notificaciones y comunicaciones (SIR y no SIR) sobre todos los procedimientos y servicios comunes con este órgano o un órgano hijo como órgano emisor, siempre que el procedimiento o servicio no requiera permiso directo.',
                    notificacions: 'Notificaciones',
                    notificacionsTooltip: 'Permite realizar notificaciones sobre todos los procedimientos y servicios que pertenecen a este órgano o un órgano hijo, siempre que el procedimiento o servicio no requiera permiso directo.',
                    comunicacions: 'Comunicaciones',
                    comunicacionsTooltip: 'Permite realizar comunicaciones sobre todos los procedimientos y servicios que pertenecen a este órgano o un órgano hijo, siempre que el procedimiento o servicio no requiera permiso directo.',
                    sir: 'Comunicaciones SIR',
                    sirTooltip: 'Permite realizar comunicaciones SIR sobre todos los procedimientos y servicios que pertenecen a este órgano o un órgano hijo, siempre que el procedimiento o servicio no requiera permiso directo.',
                    comSenseProc: 'Comunicaciones sin procedimiento',
                    comSenseProcTooltip: 'Permite realizar comunicaciones (SIR y no SIR) sin procedimiento ni servicio con este órgano o un órgano hijo como órgano emisor, siempre que el procedimiento o servicio no requiera permiso directo.',
                },
            },
        },
        propietats: {
            find: 'Buscar en las propiedades',
            empty: 'Sin propiedades',
            revert: 'Deshacer cambios',
            save: {
                success: 'Valor modificado correctamente',
                error: 'Error modificando la propiedad',
            },
        },
        propietatsConfiguracio: {
            grid: {
                title: 'Propiedades configurables',
            },
            form: {
                titleCreate: 'Crear propiedad configurable',
                titleUpdate: 'Modificar propiedad configurable',
            },
        },
        procediment: {
            grid: {
                title: 'Procedimientos',
            },
            form: {
                titleCreate: 'Crear procedimiento',
                titleUpdate: 'Modificar procedimiento',
                tabs: {
                    dades: 'Datos',
                    grups: 'Grupos',
                    permisos: 'Permisos',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grupo',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Grupo activado',
                        error: 'Error activando el grupo',
                    },
                    disable: {
                        success: 'Grupo desactivado',
                        error: 'Error desactivando el grupo',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    consultaAllowedTooltip: 'Da permiso para consultar las notificaciones y comunicaciones (SIR y no SIR creadas con este procedimiento o servicio).',
                    procesAllowed: 'Procesar',
                    procesAllowedTooltip: 'Permite marcar las notificaciones y comunicaciones (SIR y no SIR creadas con este procedimiento o servicio) en un estado final como procesadas, siempre que hayan sido creadas de la misma aplicación y no vía API RREST.',
                    gestioAllowed: 'Gestión',
                    gestioAllowedTooltip: 'Da acceso a la pestaña de acciones del detalle de las notificaciones y comunicaciones (SIR y no SIR con este procedimiento o servicio).',
                    notificacioAllowed: 'Notificaciones',
                    notificacioAllowedTooltip: 'Permite realizar notificaciones con este procedimiento o servicio.',
                    comunicacioAllowed: 'Comunicaciones',
                    comunicacioAllowedTooltip: 'Permite realizar comunicaciones con este procedimiento o servicio.',
                    comunicacioSirAllowed: 'Comunicaciones SIR',
                    comunicacioSirAllowedTooltip: 'Permite realizar comunicaciones SIR con este procedimiento o servicio.',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Servicios',
            },
            form: {
                titleCreate: 'Crear servicio',
                titleUpdate: 'Modificar servicio',
                tabs: {
                    dades: 'Datos',
                    grups: 'Grupos',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permiso',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grupo',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Grupo activado',
                        error: 'Error activando el grupo',
                    },
                    disable: {
                        success: 'Grupo desactivado',
                        error: 'Error desactivando el grupo',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    consultaAllowedTooltip: 'Da permiso para consultar las notificaciones y comunicaciones (SIR y no SIR creadas con este procedimiento o servicio).',
                    procesAllowed: 'Procesar',
                    procesAllowedTooltip: 'Permite marcar las notificaciones y comunicaciones (SIR y no SIR creadas con este procedimiento o servicio) en un estado final como procesadas, siempre que hayan sido creadas de la misma aplicación y no vía API RREST.',
                    gestioAllowed: 'Gestión',
                    gestioAllowedTooltip: 'Da acceso a la pestaña de acciones del detalle de las notificaciones y comunicaciones (SIR y no SIR con este procedimiento o servicio).',
                    notificacioAllowed: 'Notificaciones',
                    notificacioAllowedTooltip: 'Permite realizar notificaciones con este procedimiento o servicio.',
                    comunicacioAllowed: 'Comunicaciones',
                    comunicacioAllowedTooltip: 'Permite realizar comunicaciones con este procedimiento o servicio.',
                    comunicacioSirAllowed: 'Comunicaciones SIR',
                    comunicacioSirAllowedTooltip: 'Permite realizar comunicaciones SIR con este procedimiento o servicio.',
                },
            },
        },
        pagadorPostal: {
            grid: {
                title: 'Operadores postales',
                popupResourceTitle: 'operador postal',
            },
            form: {
                titleCreate: "Crear operador postal",
                titleUpdate: "Modificar operador postal",
            },
        },
        pagadorCie: {
            grid: {
                title: 'Centros de impresión y ensobrado',
            },
            form: {
                titleCreate: 'Crear centro de impresión y ensobrado',
                titleUpdate: 'Modificar centro de impresión y ensobrado',
                tabs: {
                    dades: 'Datos',
                    fulles: 'Formatos de hoja',
                    sobres: 'Formatos de sobre',
                },
            },
        },
        notificacio: {
            grid: {
                title: 'Remesas',
                detall: 'Detalle',
                new: {
                    title: 'Nuevo envio',
                    NOTIFICACIO: 'Notificación',
                    COMUNICACIO: 'Comunicación',
                    SIR: 'Comunicación SIR',
                },
                enviament: {
                    column: {
                        interessat: 'Interesado',
                        representant: 'Representante',
                        estatPostal: 'Estado de entrega postal',
                        estatTelematica: 'Estado de entrega telemática',
                    },
                    accions: 'Acciones',
                },
                column: {
                    detalls: 'Detalles',
                    mostrar: 'Mostrar envios',
                    ocultar: 'Ocultar envios',
                },
                accions: {
                    documentEnviat: "Documento enviado",
                    anular: {
                        botoTitle: "Anular",
                        modalTitle: "Anulación",
                        noReposta: "Error inesperado al anular",
                        noExecutades: "Identificadores de envíos no ejecutados: ",
                        respostesError: "Envíos con error de anulación: ",
                        ok: "Anulación realizada correctamente"
                    },
                    certificacio: "Certificación",
                    processat: "Marcar como processada",
                    processatTitle: "Marcar como procesada",
                    justificantEnviament: "Justificante de envio",
                    ampliarTermini: {
                        botoTitle: "Ampliar plazo",
                        modalTitle: "Ampliación de plazo",
                        noReposta: "Error inesperando ampliando plazo",
                        noExecutades: "Identificadores de envíos no ejecutados: ",
                        ok: "Ampliación de plazo realizada correctamente"
                    },
                    registrar: {
                        title: "Registrar notificación pendente",
                        button: "Registra",
                        noReposta: "Error inesperado al ejecutar la acción de registrar",
                        noExecutades: "Identificadores de remeses no ejecutadas: ",
                        repostesError: "Remeses con error al enviar a registrar: ",
                        ok: "La remesa se ha enviado a registrar",
                    },
                    editar: "Editar",
                    esborrar: {
                        title: "Eliminar",
                        ok: "La remesa se ha eliminado correctamente"
                    },
                },
                procediment: 'Procedimiento',
                servei: 'Servicio',
                notificacionsEsborrades: {
                    title: " eliminadas",
                    recuperar: "Recuperar"
                },
                notificacionsErrorRegistre: {
                    title: " con error de registro",
                },
                notificacionsCallbackError: {
                    title: " amb error en el último l'últim callback",
                }
            },
            form: {
                title: {
                    NOTIFICACIO: {
                        create: 'Crear notificación',
                        update: 'Modificar notificación',
                    },
                    COMUNICACIO: {
                        create: 'Crear comunicación',
                        update: 'Modificar comunicación',
                    },
                    SIR: {
                        create: 'Crear comunicación SIR',
                        update: 'Modificar comunicación SIR',
                    },
                },
                tabs: {
                    remesa: 'Información de la remesa',
                    enviaments: 'Envios',
                    documents: 'Documentos adjuntos',
                },
                enviaments: {
                    title: 'Envio',
                    add: 'Añadir envio',
                    remove: 'Eliminar envio',
                },
                interessats: {
                    interessat: 'Interesado (titular en Notifica)',
                    representant: 'Representante (destinatario en Notifica)',
                    add: 'Añadir representante',
                    remove: 'Eliminar representante',
                    nifLabel: {
                        FISICA: 'NIF/NIE/identificador EIDAS',
                        JURIDICA: 'CIF/identificador EIDAS',
                        ADMINISTRACIO: 'NIF',
                        FISICA_SENSE_NIF: 'Número de documento',
                    },
                },
                documents: {
                    title: 'Adjunto',
                    add: 'Añadir adjunto',
                    remove: 'Eliminar adjunto',
                    helperText: {
                        attachment: {
                            noSir: 'El tamaño máximo del documento es de 10 MB. Los formatos admitidos son PDF y ZIP.',
                            sir: 'El tamaño máximo del documento es de 10 MB. Máximo de 15 MB entre todos los documentos. Los formatos admitidos son JPG, JPEG, ODT, ODP, ODS, ODG, DOCX, XLSX, PPTX, PDF, PNG, RTF, SVG, TIFF, TXT, XML y XSIG.',
                        },
                        normalitzat:
                            'Está la primera página del documento preparada para el ensobrado?',
                    },
                },
            },
            detail: {
                title: {
                    notificacio: 'Detalles de la notificación',
                    comunicacio: 'Detalles de la comunicación',
                    sir: 'Detalles de la comunicación SIR',
                    erroRegistre: "Detalles de la excepción"
                },
                tab: {
                    dades: 'Datos',
                    enviaments: 'Envios',
                    documents: 'Documentos',
                    registreEsdev: 'Registro de acontecimientos',
                    accions: 'Acciones',
                    historic: 'Histórico',
                },
                errorRegistre: {
                    data: "Fecha",
                    tipus: "Tipo",
                    error: "Error descripción"
                },
                errorCallback: {
                    data: "Fecha",
                    tipus: "Tipo",
                    error: "Error descripción"
                },
                dades: {
                    title: 'Datos de la notificación',
                    refrescar: "Refrescar la columna estado",
                    anulada: "La remesa tiene uno o más envíos anulados",
                    errorCanviEstat: "Ha habido error notificando al cliente del cambio de estado...",
                    justificant: 'Justificante',
                    grup: {
                        title: 'Grupo',
                        codi: "Codigo",
                        nom: "Nombre",
                    },
                    documents: {
                        title: 'Documento',
                        nom: "Nombre archivo",
                        normalitzat: "Normalizado",
                        csv: "Generar CSV",
                        descarregarDocument: "Document enviado",
                    },
                    pagadorPostal: {
                        title: "Datos del pagador postal",
                        organismePagadorNom: "Organismo pagador",
                        contracteNum: "Número de contrato",
                        facturacioClientCodi: "Código del cliente",
                        contracteDataVig: "Fecha de vigencia del contrato",
                    },
                    pagadorCie: {
                        title: "Datos del pagador CIE",
                        organismeEmissor: "Organismo emisor",
                        vigencia: "Fecha de vigencia del contrato",
                    },
                    enviaments: {
                        title: 'Envio',
                        interessat: "Interesado",
                        destinataris: "Representantes",
                        senseDestinataris: "Sin destinatarios",
                        estat: "Estado",
                        registre: {
                            title: "Registro",
                            registreNumeroFormatat: "Núm. registro",
                            registreData: "Fecha registro",
                            registreEstat: "Estado registro",
                            sirRecepcioData: "Fecha recepción SIR",
                            registreMotiu: "Motivo",
                            sirRegDestiData: "Fecha registro SIR",
                            registreOficinaNom: "Oficina",
                            registreLlibreNom: "Libro",
                            noRegistrat: "No registrado",
                            certificacio: "Certificación",
                            noCertificacio: "Sin certificación",
                            notificaCertificacioData: "Fecha",
                            notificaCertificacioMime: "Tipo MIME",
                            notificaCertificacioOrigen: "Origen",
                            notificaCertificacioMetadades: "Metadatos",
                            notificaCertificacioCsv: "CSV",
                            notificaCertificacioTipus: "Tipos",
                            notificaCertificacioArxiuTipus: "Tipos de archivo",
                            notificaCertificacioNumSeguiment: "Núm. seguimiento",
                            certificacioNom: 'Documento'
                        },
                    },
                },
                accions: {
                    success: "Acción enviada a ejecutar correctamente",
                    error: "Error al enviar a ejecutar la acción. Consultar la pestaña de eventos per má información",
                    noAccions: "No hay acciones disponibles para este envio",
                    enviarCallback: 'Envia canvio de estado al cliente',
                    enviarEntregaPostal: 'Reenviar la entrega postal',
                    enviarEntregaPostalButton: 'Reenvía la entrega postal',
                    registrar: {
                        title: "Registrar notificación pendiente",
                        button: "Registra",
                        noReposta: "Error inesperado al ejectuar la acción de registrar",
                        noExecutades: "Identificadores de remesas no ejecutadas: ",
                        repostesError: "Remesas con error al enviar a registrar: ",
                        ok: "La remesa s'ha enviado a registrar"
                    },
                    enviarNotifica: {
                        title: "Envía notificación registrada a Notific@",
                        button: "Envía"
                    },
                    reactivarEstatNotifica: {
                        title: 'Reactivar estado de Notific@',
                        button: 'Reactivar estado'
                    },
                    reactivarSir: {
                        title: 'Reactivar consulta SIR',
                        button: 'Reactiva'
                    },
                    reactivarErrors: {
                        title: 'Reactiva envíos con error',
                        button: 'Reactiva'
                    },
                    reenviaErrors: {
                        title: 'Reenvía envíos con error',
                        button: 'Reenvía'
                    }
                },
            },
        },
        enviament: {
            grid: {
                title: 'Envios',
                detalls: 'Detalles',
                remesa: 'Remesa',
                anular: 'Anular',
                ampliarTermini: 'Ampliar terminio'
            },
            detail: {
                title: 'Detalles del envio',
                tab: {
                    dades: {
                        title: 'Datos',
                        enviaments: 'Datos del envio',
                        interessat: 'Datos del interesado',
                        titularInfo: {
                            nif: "Nif",
                            nom: "Nombre",
                            llinatges: "Apellidos",
                            telefon: "Teléfono",
                            email: "Email",
                        },
                    },
                    notifica: {
                        title: 'Notific@',
                        datat: 'Datado',
                        certificacio: 'Certificación',
                        noEnviat: 'Este envío todavía no ha sido enviado',
                        notificacioNoEnviat: 'Esta notificación no ha sido enviada a Notific@',
                        refrescarEstat: {
                            titleButton: 'Refrescar estado',
                            success: "Estado del envío refrescado con éxito",
                            error: "Error refrescando el estado del envío",
                        },
                    },
                    registre: {
                        title: 'Registro',
                        dadesRegistre: 'Datos del registro',
                        noEnviada: "Esta notificación no ha sido enviada al registro",
                    },
                    entregaPostal: {
                        title: 'Entrega postal',
                        cieEstat: "Estado",
                        cieEstatPendent: "La entrega postal no ha sido enviada o se ha enviado con errores. Ver pestaña de eventos para más información",
                        cieEstatData: "Estado fecha",
                        cieId: "Identificador Notifica",
                        cieDatatErrorDescripcio: "Estado descripción",
                        cieDatatOrigen: "Origen",
                        cieDatatReceptorNif: "Receptor NIF",
                        cieDatatReceptorNom: "Receptor nombre",
                        cieDatatNumSeguiment: "Núm. seguimiento",
                        cieErroni: "L'entrega CIE no es podrà donar d'alta ja que dona errors al crear-la al CIE",
                        refrescarEstat: {
                            titleButton: 'Refrescar estado',
                            success: "Estado del envío postal refrescado con éxito",
                            error: "Error refrescando el estado del envío postal",
                        },
                        cancelar: {
                            titleButton: 'Cancelar',
                            success: "Envío postal enviado a cancelar con éxito",
                            error: "Error cancelando el envío postal",
                        },
                        cancelarEntregaPostal: "Cancelar entrega postal",
                        certificacio: "Certificación",
                        cieCertificacioData: "Data",
                        cieCertificacioMime: "Tipo MIME",
                        cieCertificacioOrigen: "Origen",
                        cieCertificacioMetadades: "Metadatos",
                        cieCertificacioCsv: "CSV",
                        cieCertificacioTipus: "Tipos",
                        cieCertificacioArxiuTipus: "Tipos de archivo",
                        cieCertificacioNumSeguiment: "Núm. seguimiento",
                        cieCertificacioArxiuNom: "Documento",
                        descarregarCertificacio: 'Descargar',
                    },
                    registreEsdev: {
                        title: 'Registro de acontecimientos',
                        estatError: "Evento procesado con error",
                        estatSuccess: "Evento procesado con éxito",
                    },
                    historic: {
                        title: "Histórico",
                    },
                    stateMachine: {
                        title: "State Machine",
                        estatOrigen: "Estado origen",
                        event: "Evento",
                        taulaEstatsTitle: "Operacions que es realitzen al enviar events",
                        descarregarDiagrama: "Descargar diagrama",
                        missatge:"Atención! No alterar los estados de la state machine ni enviar eventos sin conocer las repercusiones que pueden tener estas acciones. " +
                            "En caso de duda consultar antes de hacer nada. En cualquier caso solo hacerlo si la remesa se encuentra en un estado incoherente.",
                        accioResultant: {
                            header: "Acción resultante",
                            registrar: "Registrado",
                            resetIntentsRegistre: "Resetea intentos registro",
                            reintentarRegistre: "Reintenta registro",
                            errorRegistre: "Error de registro",
                            registreOk: "Registro ok",
                            enviarNotifica: "Envia a Notific@",
                            notificaOk: "Notific@ ok",
                            notificaError: "Error de Notific@",
                            reintentNotifica: "Reintenta envío a Notific@",
                            finalitzaRemesa: "Finaliza remesa",
                            resetIntentsNotifica: "Resetea intentos de Notific@",
                            consultaEstatEnviament: "Consulta estado envío",
                            consultaEstatEnviamentOk: "Consulta estado envío ok",
                            consultaEstatEnviamentError: "Consulta estat envío error",
                            reintentarConsultaEstatEnviament: "Reintenta consulta estado envío",
                            consultaEnviamentSir: "Consulta envío SIR",
                            consultaEnviamentSirOk: "Consulta SIR ok",
                            consultaEnviamentSirError: "Consulta SIR error",
                            reintentarConsultaEnviamentSir: "Reintenta consulta SIR",
                            finalitzarComunicacioSir: "Finaliza comunicación SIR"
                        }
                    },
                },
            },
        },
        accioMassiva : {
            grid: {
                title: 'Acciones Masivas',
                tipus: 'Tipo',
                createdDate: 'Fecha creación',
                dataInici: 'Fecha inicio',
                dataFi: 'Fecha fin',
                createdBy: 'Código usuario',
                okErrorPendent: 'Ok/Error/Pendiente',
                progres: 'Progreso',
                elements: {
                    itol: 'Elemento de la acción masiva',
                    referencia: 'Referencia',
                    data: 'Fecha',
                    estat: 'Estado',
                    errorDesc: 'Error descripción',
                    estatFinalitzat: 'Finalitzado',
                    estatError: 'Error',
                    estatPendent: 'Pendiente'
                }
            },
            accions: {
                labelBoto: "Acciones masivas",
                selectAll: "Seleccionar todo",
                deselectAll: "Desmarcar todo",
                executant: "Processant acció massiva",
                noExecutades: "Identificadores no ejecutables: ",
                respostesError: "Identificadores con error: ",
                marcarProcessades: {
                    label: "Marca com procesadas",
                    tooltip: "Marca las remesas que se encuentran en un estado final como procesadas",
                    ok: "La acción de ampliar plazo se ha mandado a ejectuar correctamente"
                },
                reintentarRegistre: {
                    label: "Reintentar registro",
                    tooltip: "Reseta los intentos de registro",
                    ok: "La acción de reintentar registro se ha mandado a ejecutar correctamente"
                },
                actualitzarEstat: {
                    label: "Actualiza el estado",
                    tooltip: "Actualitza el estado (SIR y Notific@) de les remesas que no se encuentran en un estado final",
                    ok: "La acción de actualitzar el estado se manado a ejecutar"
                },
                reenviarAmbError: {
                    label: "Vuelve a enviar las que han dado error",
                    tooltip: "Vuelve a enviar las remesas que han dado error durante el proceso de registro o de envio a Notifica y han agotado el número máximo de intentos",
                    ok: "La acción reenviar con error se ejecutado correctamente",
                },
                esborrar: {
                    label: "Elimina",
                    tooltip: "Marca la remesa como elimianda y no la muestra en el listado",
                    ok: "La acción de eliminar se ha realizado correctamente"
                },
                exportarFullCalcul: {
                    label: "Exporta a hoja de cálculo",
                    tooltip: "Exporta el listado de remesas a un archivo de hoja de cálculo en format ods",
                    ok: "La exportación se ha realizado con éxito"
                },
                justificantEnviament: {
                    label: "Descarga justificantes de envío",
                    tooltip: "Descarga el justificante de envío de las remesas seleccionadas",
                    ok: "Fichero ZIP de justificantes generado con éxito"
                },
                certificacioRecepcio: {
                    label: "Descarga certificaciones de recepción",
                    tooltip: "Descarga la certificación de recepción de las remesas seleccionadas"
                },
                anular: {
                    label: "Anular",
                    tooltip: "Anula las remesas enviadas a Notfic@ que no hayan sido notificadas",
                    ok: "La acción de anular se ha mandado a ejectuar correctamente"
                },
                ampliarTermini: {
                    label: "Ampliar plazo",
                    tooltip: "Amplia el plazo de cad-ucidad de las remesas seleccionadas",
                    ok: "La acción de ampliar plazo se ha mandado a ejectuar correctamente"
                },
                reactivarCanviEstat: {
                    label: "Vuelve a activar las consultas de cambio de estado",
                    tooltip: "Vuelve a activar las consultas de cambio de estado a Notific@ o a SIR si se han egotado el nombre máximo de intentos. Esta acción vuelve a poner el contador de intentos a cero hasta que se llegue, de nuevo, al número máximo de intentos",
                    ok: "La acción de reactivar la consulta de cambio de estado se ha mandado a ejectuar correctamente"
                },
                reactivarCallbacks: {
                    label: "Vuelve a activar el envio de callbacks",
                    tooltip: "Vuelve a activar el envio de callbacks a aplicaciones si se ha agotado el número máximo de intentos máximo. Esta acción vuelve a poner el contador de intentos a cero hasta que se llegue, de nuevo, al número máximo de intentos",
                    ok: "La acción de reactivar los callbacks se ha mandado a ejectuar correctamente"
                },
                notificacionsMovil: {
                    label: "Envia notificaciones móbil",
                    tooltip: "Envia notificaciones de cambio de estado a dispositivos móbiles (PUSH)",
                    ok: "La acción de enviar notifiaciones móbil se ha mandado a ejectuar correctamente"
                },
            },
        },
        notificacioMassiva: {
            grid: {
                title: 'Consulta de envíos masivos',
                createdDate: 'Creada el',
                csvFilename: 'Notifiaciones',
                zipFilename: 'Documentos',
                csvTooltip: 'Descarregar fitxer CSV',
                zipTooltip: 'Descarregar document ZIP',
                estatValidacio: 'Validación CSV',
                estatProces: 'Estado',
                createdBy: 'Creada por',
                llegenda: {
                    numProcessats: "Num.envíos procesados",
                    numErronis: "Num.envíos erronios",
                    numCancelats: "Num.envíos cancelados",
                },
                accions : {
                    resum: 'Resumen',
                    descarregarResum: 'Descargar resumen',
                    errorsValidacio: 'Errores validación',
                    errorsExecucio: 'Errores execución',
                    posposar: 'Pospone',
                    reactivar: 'Reactiva',
                    mostrarRemeses: {
                        label: 'Muestra remesas',
                        msg1: 'Envío masivo del',
                        msg2: 'Usuario'
                    }
                },
                estats: {
                    PENDENT: 'Pendiente',
                    EN_PROCES: 'En procso',
                    EN_PROCES_AMB_ERRORS: 'En procos con errores',
                    FINALITZAT: 'Finalitzat',
                    FINALITZAT_AMB_ERRORS: 'Finalizado con errores',
                    ERRONIA: 'Errónia',
                    CANCELADA: 'Cancelada',
                    FINALITZAT_PARCIAL: 'Finalizado parcial'
                },
            },
            detall: {
                title: 'Detalle de la notificación masiva',
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
                    enviamentTipus: 'Tipo de envío',
                    codiDir3UnidadRemisora: 'Unidad remisora',
                    concepto: 'Concepto',
                    descripcio: 'Descripción',
                    prioridadServicio: 'Prioriad servicio',
                    nomComplert: 'Interesado',
                    errores: 'Errores validación',
                    errorsExecucio: 'Errores ejecución',
                    cancelada: 'Cancelada',
                    interssatSenseNif: " - Interessat sense NIF",
                    noRows: "Sin filas"
                }
            },
            form: {
                title: "Crear envío masivo",
                indicacions: {
                    title: 'Indicaciones para la cumplimentación del fichero de datos:',
                    indicacio1: 'El archivo de datos debe ser un fichero de texto plano (CSV) con los campos separados por puntos y comas. Los ficheros cuyos separadores sean diferentes fallarán (por ejemplo ",").',
                    indicacio2: 'La primera fila debe contener el encabezado de las columnas.'
                },
                csvFieldLabel: 'Fichero CSV con los envíos',
                csvFieldText: 'Solo se admite formato CSV. El tamaño máximo del document es de 2 Mb. El número máximo de notificaciones es de 999.',
                zipFieldLabel: 'Fichero ZIP con los documentos adjuntos',
                zipFieldText: 'Solo se admite formato ZIP. El tamaño máximo del document es de 15 Mb.',
                caducitat: 'Caducidad',
                email: 'Email de aviso cuando finalice la carga'
            }
        },
        integracio: {
            grid: {
                title: 'Monitor de integraciones',
            },
            detall: {
                title: 'Detalle del monitor de integración',
                descripcio: 'Descripción:',
                data: 'Fecha:',
                tipus: 'Tipo:',
                tipusEnum: {
                    enviament: 'Envío',
                    recepcio: 'Recepción',
                    processar: 'Procesar',
                },
                estat: 'Estado:',
                estatEnum: {
                    ok: 'Correcto',
                    warn: 'Alerta',
                    error: 'Error',
                },
                parametres: 'Parámetros',
                tootlipCopiarParametres: 'Copiar parámetros',
                tootlipCopiarError: 'Copiar error',
                tooltipCopiat: 'Copiado',
                error: 'Error',
                errorDescripcio: 'Descripción del error:',
                excepcioMessage: 'Mensaje de excepción:',
                excepcioStacktrace: 'Stacktrace',
                monitorSistema: "Monitor de sistema"
            },
        },
        cache: {
            grid: {
                title: "Cache de aplicaciones"
            },
            accions: {
                buidarMassiuOk: "Caches vaciadas correctamente",
                buidarOk: "Cache vaciada correctamente",
                buidar: "Vaciar cache",
                buidarMassiu: "Vaciar caches"
            }
        },
        activemq: {
            grid: {
                title: "Monitor ActiveMQ",
                missatges: "Mensajes",
                buidar: "Vaciar",
                buidarOk: "La cola se ha vaciado correctamente"
            },
            detail: {
                title: "Mensajes cola ",
                esborrar: "Eliminar",
                esborrarOk: "Mensaje eliminado correctamente",
                esborrarError: "Error eliminando el eensaje"
            },
            descargarJobScheduler: "JobScheduler JSON"

        },
        callbacks: {
            pendents: {
                grid: {
                    title: "Callbacks pendientes",
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
                            title: "Eliminar"
                        }
                    },
                    accionsMassives: {
                        enviarPendents: {
                            label: "Enviar",
                            ok: "La acción de enviar los callbacks se ha enviado a ejecutar correctamente"
                        },
                        pausarPendents: {
                            label: "Pausar",
                            ok: "La acción de pausar los callbacks se ha enviado a ejecutar correctamente"
                        },
                        activarPendents: {
                            label: "Activar",
                            ok: "La acción de activar los callbacks se ha enviado a ejecutar correctamente"
                        },
                        esborrarPendents: {
                            label: "Eliminar",
                            ok: "La acción de eliminar los callbacks se ha enviado a ejecutar correctamente"
                        },
                    }
                },
            },
            error: {
                grid: {
                    title: "Notificaciones con error en el último callback",
                },
                accionsMassives: {
                    reenviar: {
                        label: "Reintenta callback",
                        ok: "Se han reenviado los callbacks con error"
                    }
                }
            }
        },
        monitorSistema :{
            tab : {
                sistema: {
                    title: "Sistema",
                    sistemaOperatiu: "Sistema operativo",
                    arquitectura: "Arquitectura",
                    processadors: "Procesadores",
                    jbossVersion: "Versión de Jboss",
                    applicationServerInfo: "Información del servidor de aplicaciones",
                    tempsFuncionant: "Tiempo funcionando",
                    jvmMemory: "Máquina virtual de Java",
                    disksUsage: "Disco y CPU",
                },
                fils: {
                    title: "Hilos de ejecución",
                },
                tasques: {
                    title: "Tareas en segundo plano",
                    restart: "Reiniciar",
                    restartOk: "Reiniciado correctamente",
                    restartSelect: "Reiniciar seleccionadas",
                    restartSelectOk: "Reiniciadas correctamente",
                },
            }
        },
        metriques: {
            title: "Métricas",
            llegenda: {
                title: "Leyenda",
                numExecTempsMig: "Número de ejecuciones x Tiempo medio de una ejecución (ms)",
                tempsMigExcecuio: "Tiempo medio de una ejecución (ms)",
                tempsMaxim: "Tiempo máximo (ms)"
            },
            excecucions: "ejecuciones",
            generics: "Genéricos",
            frequencia: "Frequéncia",
            mitjana: "Mediana",
            duracio: "Duración",
            percentils: "Percentils",
            exportaJson: "Exporta",
            importaJson: "Importa"
        },
        usuaris: {
            permisos: {
                grid: {
                    title: "Permisos d'usuari",
                    organsPermisDirecte: "Órganos con permiso directo",
                    organsPermisHeredat: "Órganos con los permisos heredados del padre",
                    procedimentPermisDirecte: "Procedimientos con permiso directo",
                    procedimentPermisOrgan: "Procedimientos con permiso por órgano",
                    columnes: {
                        nom: "Nombre",
                        organGestor: "Órgano gestor",
                        tipus: "Tipo",
                        principal: "Principal",
                    }
                }
            }
        },
        notFound: {
            title: 'Página no encontrada',
            toHome: 'Ir al inicio',
        },
    },
    component: {
        HeaderThemeSelector: {
            light: 'Claro',
            system: 'Sistema',
            dark: 'Oscuro',
        },
        HeaderLanguageSelector: {
            languages: {
                ca: 'Catalán',
                es: 'Castellano',
            },
        },
        Offline: {
            message: 'Sin conexión con el servidor',
            retry: 'Volver a intentar',
        },
        AclPermissionManager: {
            title: 'Permisos',
            resourceTitle: 'Permiso',
        },
        RoleSelector: {
            role: {
                NOT_SUPER: 'Superadministrador',
                NOT_ADMIN: 'Administrador Entitat',
                NOT_ADMIN_LECTURA: "Administrador (lectura)",
                NOT_ORGAN: 'Administrador de órgano',
                tothom: 'Usuario',
            },
        },
        PermissionGrid: {
            popupTitle: 'Permiso',
            tipus: 'Tipo',
            grantedAuthority: {
                user: 'Usuario',
                role: 'Rol',
            },
        },
        GridToolbarButton: {
            add: 'Añadir',
            refresh: 'Refrescar',
        },
        UserProfile: {
            perfil: "Perfil del usuario",
            auto: "Automático",
        },
        FormDropzoneField: {
            arrosegar:  "Arrastra el fichero aquí",
            amollar: "Suelta el fichero ahora ...",
            validacio: "El archivo no es válido",
        },
        ButtonDetailExpandColapse: {
            expandAll: "Expandir todo",
            collapseAll: "Contraer todo",
        },
        Dir3SearchInput: {
            search: "Buscar",
            dialog: {
                title: "Consulta de administraciones públicas a DIR3",
                netejar: "Limpiar",
                noCif: "Sin CIF",
                noSir: "Sin SIR",
                viaValib: "Vía VALIB",
            }
        },
    },
    hook: {
        useDataGrid: {
            treeData: {
                collapseAll: 'Contraer todo',
                expandAll: 'Expandir todo',
            },
        },
    },
    utils: {
        estatConfig: {
            ESTAT_ENUM_MAP: {
                PENDENT: "Pendiente",
                ENVIADA: "Enviada",
                REGISTRADA: "Registrada",
                FINALITZADA: "Finalizada",
                PROCESSADA: "Procesada",
                EXPIRADA: "Expirada",
                NOTIFICADA: "Notificada",
                REBUTJADA: "Rechazada",
                ENVIAT_SIR: "Enviada SIR",
                ENVIADA_AMB_ERRORS: "Enviada con errores",
                FINALITZADA_AMB_ERRORS: "Finalizada con errores",
                ENVIANT: "Enviando",
                OFICI_ACCEPTAT: "Oficio aceptado",
                REBUTJADA_SIR: "Rechazada SIR",
                ANULADA: "Anulada",
            },
            NOTIFICACIO_REGISTRE_ESTAT_ENUM_MAP: {
                VALID: "Válido",
                RESERVA: "Reserva",
                PENDENT: "Pendiente",
                OFICI_EXTERN: "Oficio externo",
                OFICI_INTERN: "Oficio interno",
                OFICI_ACCEPTAT: "Oficio aceptado",
                DISTRIBUIT: "Distribuido",
                ANULAT: "Anulado",
                RECTIFICAT: "Rectificado",
                REBUTJAT: "Rechazado",
                REENVIAT: "Reenviando",
                DISTRIBUINT: "Distribuyendo",
                OFICI_SIR: "Oficio SIR",
                ENVIAT_NOTIFICAR: "Enviado notificar",
            },
            ENVIAMENT_ESTAT_MAP: {
                NOTIB_PENDENT: "Pendiente de enviar",
                NOTIB_ENVIADA: "Enviada",
                ABSENT: "Ausente",
                ADRESA_INCORRECTA: "Dirección incorrecta",
                DESCONEGUT: "Desconocido",
                ENVIADA_CI: "Enviado en el centro de impresión",
                ENVIADA_DEH: "Enviado a la DEH",
                ENVIAMENT_PROGRAMAT: "Envío programado",
                ENTREGADA_OP: "Entregada al operador postal",
                ERROR_ENTREGA: "Error en el envío",
                EXPIRADA: "Expirada",
                EXTRAVIADA: "Extraviada",
                MORT: "Leído",
                LLEGIDA: "Muerto",
                NOTIFICADA: "Notificada",
                PENDENT: "Pendiente",
                PENDENT_ENVIAMENT: "Pendiente de enviar",
                PENDENT_SEU: "Pendiente de comparecencia",
                PENDENT_CIE: "Pendiente de entrega a CIE",
                PENDENT_DEH: "Pendiente de entrega a DEH",
                REBUTJADA: "Rechazada",
                SENSE_INFORMACIO: "Sin información",
                FINALITZADA: "Anulada",
                ENVIADA: "Enviada SIR",
                REGISTRADA: "Enviada con errores",
                PROCESSADA: "Finalizada con errores",
                ANULADA: "Registrada",
                ENVIAT_SIR: "Enviada",
                ENVIADA_AMB_ERRORS: "Finalizado",
                FINALITZADA_AMB_ERRORS: "Procesado",
            },
        },
    },
    comu: {
        netejarFiltre: 'Limpiar filtro',
        filtrar: 'Filtrar',
        obrirFiltreAvançat: 'Abrir filtro avanzado',
        tancarFiltreAvançat: 'Cerrar filtro avanzado',
        guardar: 'Guarda',
        cancelar: 'Cancela'
    },
};

export default translationEs;
