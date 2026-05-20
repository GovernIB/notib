const translationEs = {
    app: {
        loading: 'Iniciando NOTIB',
        noEntitat: 'Este usuario no tiene acceso a ningún entorno',
        menu: {
            home: 'Inicio',
            config: 'Configuración',
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
            callbacksError: "Notificaciones callback erroneas",
            enviamentMassiu: "Envíos masivos",
            nouEnviamentmassiu: "Nuevo envío masivo",
            consultaEnviamentmassiu: "Consulta envíos masivos",
            gestio: "Gestión",
            errorRegistre: "Notificación con error de registro",
            notificacioEsborrades: "Notificaciones borradas",
            callbackPendent: "Callback pendiente",
            accionsMassives: "Consulta acciones masivas",
            permisosUsuari: "Permisos de usuario",
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
                    anular: "Anular",
                    certificacio: "Certificación",
                    processat: "Marcar como processada",
                    justificantEnviament: "Justificante de envio",
                    ampliarTermini: "Ampliar termino",
                },
                procediment: 'Procedimiento',
                servei: 'Servicio',
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
                },
                tab: {
                    dades: 'Datos',
                    enviaments: 'Envios',
                    documents: 'Documentos',
                    registreEsdev: 'Registro de acontecimientos',
                    accions: 'Acciones',
                    historic: 'Histórico',
                },
                dades: {
                    title: 'Datos de la notificación',
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
                        },
                    },
                },
                accions: {
                    noAccions: "No hay acciones disponibles para este envio",
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
                        refrescar: 'Refrescar estado',
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
                        refrescarEstat: 'Refrescar estado',
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
                        descarregarCertificacio: 'Descarregar'
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
        },
        activemq: {
            grid: {
                title: "Monitor ActiveMQ"
            },
        },
        callbacksError: {
            grid: {
                title: "Notificaciones con error en el último callback "
            },
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
                NOT_ADMIN: 'Administrador',
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
        AccionsMassives: {
            labelBoto: "Acciones masivas",
            selectAll: "Seleccionar todo",
            deselectAll: "Desmarcar todo",
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
    comu: {
        netejarFiltre: 'Limpiar filtro',
        filtrar: 'Filtrar',
        obrirFiltreAvançat: 'Abrir filtro avanzado',
        tancarFiltreAvançat: 'Cerrar filtro avanzado',
    },
};

export default translationEs;
