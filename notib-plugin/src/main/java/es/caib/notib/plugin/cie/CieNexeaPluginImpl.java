package es.caib.notib.plugin.cie;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.cie.nexea.NotificaWsV2PortType;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.AltaRemesaEnvios;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Destinatarios;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Documento;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.EntregaPostal;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Envio;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Envios;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Opcion;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Opciones;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.OrganismoPagadorCIE;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.OrganismoPagadorPostal;
import es.caib.notib.plugin.cie.nexea.altaremesaenvios.Persona;
import es.caib.notib.plugin.cie.nexea.cancelarenvio.CancelarEnvio;
import es.caib.notib.plugin.cie.nexea.infoenvioligero.InfoEnvioLigero;
import es.caib.notib.plugin.utils.WsClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class CieNexeaPluginImpl extends AbstractSalutPlugin implements CiePlugin {

    private final Properties properties;
    private static final String NOTIB = "Notib";
    private static final String CIE = "CIE";

    public CieNexeaPluginImpl(Properties properties, boolean configuracioEspecifica) {

        this.properties = properties;
        this.configuracioEspecifica = configuracioEspecifica;
        urlPlugin = properties.getProperty("es.caib.notib.plugin.cie.url");
    }

    @Override
    public RespostaCie enviar(EnviamentCie notificacio) {

        try {
            long startTime = System.currentTimeMillis();
            var alta = generarAltaRemesaEnvios(notificacio);
            var r = getNotificaWs(notificacio.getEntregaCie().getApiKey()).altaRemesaEnvios(alta);
//            NotibLogger.getInstance
            var resultadoEnvios = r.getResultadoEnvios();
            if (resultadoEnvios == null || resultadoEnvios.getItem() == null || resultadoEnvios.getItem().isEmpty()) {
                return RespostaCie.builder().codiResposta(r.getCodigoRespuesta()).descripcioError(r.getDescripcionRespuesta()).build();
            }
            List<IdentificadorCie> ids = new ArrayList<>();
            boolean error;
            for (var e : r.getResultadoEnvios().getItem()) {
                error = Strings.isNullOrEmpty(r.getCodigoRespuesta());
                ids.add(IdentificadorCie.builder().identificador(e.getIdentificador()).error(error).nifTitular(e.getNifTitular()).build());
            }
            incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return RespostaCie.builder().identificadors(ids).codiResposta(r.getCodigoRespuesta()).descripcioError(r.getDescripcionRespuesta()).build();
        } catch (SOAPFaultException sfe) {
            log.error("[CieCorreosPluginImpl.enviar] Error en la crida SOAP ", sfe);
            var codi = sfe.getFault().getFaultCode();
            var desc = sfe.getFault().getFaultString();
            incrementarOperacioError();
            return RespostaCie.builder().codiResposta(codi).descripcioError(desc).build();
        } catch (Exception ex) {
            var desc = "Error inesperat al enviar a CIE la notificacio " + notificacio.getId() + " - " + ex.getMessage();
            log.error(desc, ex);
            incrementarOperacioError();
            return RespostaCie.builder().codiResposta(CIE).descripcioError(desc).build();
        }
    }

    private AltaRemesaEnvios generarAltaRemesaEnvios(EnviamentCie notificacio) throws Exception {

        var envios = new AltaRemesaEnvios();
        Integer retardPostal = null;
        try {
            envios.setCodigoOrganismoEmisor(notificacio.getEntregaCie().getOrganismeEmisorCodi());
            switch (notificacio.getEnviamentTipus()) {
                case COMUNICACIO:
                    envios.setTipoEnvio(new BigInteger("1"));
                    break;
                case NOTIFICACIO:
                    envios.setTipoEnvio(new BigInteger("2"));
                    break;
            }
            if (notificacio.getEnviamentDataProgramada() != null) {
                var formatter = new SimpleDateFormat("dd/MM/yyyy");
                var today = new Date();
                var todayWithZeroTime = formatter.parse(formatter.format(today));
                var dataProgramadaWithZeroTime = formatter.parse(formatter.format(notificacio.getEnviamentDataProgramada()));
                if (dataProgramadaWithZeroTime.after(todayWithZeroTime)) {
                    envios.setFechaEnvioProgramado(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
                }
            }
            envios.setConcepto(notificacio.getConcepte().replace('·', '.'));
            if (notificacio.getDescripcio() != null) {
                envios.setDescripcion(notificacio.getDescripcio().replace('·', '.'));
            }
            envios.setProcedimiento(notificacio.getProcedimentCodiNotib());
            var documento = new Documento();
            if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
                documento.setContenido(notificacio.getContingutDocument());
                var opcionesDocumento = new Opciones();
                var opcionNormalizado = new Opcion();
                opcionNormalizado.setTipo("normalizado");
                opcionNormalizado.setValue(notificacio.getDocument().isNormalitzat()  ? "si" : "no");
                opcionesDocumento.getOpcion().add(opcionNormalizado);
                var opcionGenerarCsv = new Opcion();
                opcionGenerarCsv.setTipo("generarCsv");
                opcionGenerarCsv.setValue("no");
                opcionesDocumento.getOpcion().add(opcionGenerarCsv);
                documento.setOpcionesDocumento(opcionesDocumento);
                if(notificacio.getContingutDocument() != null) {
                    String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(notificacio.getContingutDocument()).toCharArray()));
                    //Hash a enviar
                    documento.setHash(hash256);
                }
                envios.setDocumento(documento);
            } else if (notificacio.getDocument() != null && notificacio.getDocument().getCsv() != null) {
                var contingut = notificacio.getContingutDocument();
                documento.setContenido(contingut);
                if(contingut != null) {
                    String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
                    //Hash a enviar
                    documento.setHash(hash256);
                }
                Opciones opcionesDocumento = new Opciones();
                Opcion opcionNormalizado = new Opcion();
                opcionNormalizado.setTipo("normalizado");
                opcionNormalizado.setValue(
                        notificacio.getDocument().isNormalitzat()  ? "si" : "no");
                opcionesDocumento.getOpcion().add(opcionNormalizado);
                Opcion opcionGenerarCsv = new Opcion();
                opcionGenerarCsv.setTipo("generarCsv");
                opcionGenerarCsv.setValue("no");
                opcionesDocumento.getOpcion().add(opcionGenerarCsv);
                documento.setOpcionesDocumento(opcionesDocumento);
                envios.setDocumento(documento);
            } else if (notificacio.getDocument() != null && notificacio.getDocument().getUuid() != null) {
                var contingut = notificacio.getContingutDocument();
                documento.setContenido(contingut);
                if(contingut != null) {
                    var hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
                    //Hash a enviar
                    documento.setHash(hash256);
                }
                var opcionesDocumento = new Opciones();
                var opcionNormalizado = new Opcion();
                opcionNormalizado.setTipo("normalizado");
                opcionNormalizado.setValue(notificacio.getDocument().isNormalitzat()  ? "si" : "no");
                opcionesDocumento.getOpcion().add(opcionNormalizado);
                var opcionGenerarCsv = new Opcion();
                opcionGenerarCsv.setTipo("generarCsv");
                opcionGenerarCsv.setValue("no");
                opcionesDocumento.getOpcion().add(opcionGenerarCsv);
                documento.setOpcionesDocumento(opcionesDocumento);
                envios.setDocumento(documento);
            } else if(notificacio.getDocument() != null) {
                documento.setHash(notificacio.getDocument().getHash());
                if(notificacio.getDocument().getContingutBase64() != null) {
                    var contingut = notificacio.getDocument().getContingutBase64().getBytes();
                    documento.setContenido(contingut);
                    var hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
                    //Hash a enviar
                    documento.setHash(hash256);
                }
                var opcionesDocumento = new Opciones();
                var opcionNormalizado = new Opcion();
                opcionNormalizado.setTipo("normalizado");
                opcionNormalizado.setValue(notificacio.getDocument().isNormalitzat()  ? "si" : "no");
                opcionesDocumento.getOpcion().add(opcionNormalizado);
                var opcionGenerarCsv = new Opcion();
                opcionGenerarCsv.setTipo("generarCsv");
                opcionGenerarCsv.setValue("no");
                opcionesDocumento.getOpcion().add(opcionGenerarCsv);
                documento.setOpcionesDocumento(opcionesDocumento);
                envios.setDocumento(documento);
            }
            envios.setEnvios(generarEnvios(notificacio));
            var opcionesRemesa = new Opciones();
            if(notificacio.getRetard() != null) {
                retardPostal = notificacio.getRetard();
            } else if (notificacio.getProcediment() != null) {
                retardPostal = notificacio.getProcediment().getRetard();
            }

            if (retardPostal != null) {
                var opcionRetardo = new Opcion();
                opcionRetardo.setTipo("retardo");
                opcionRetardo.setValue(retardPostal.toString()); // número de días
                opcionesRemesa.getOpcion().add(opcionRetardo);
            }
            if (notificacio.getCaducitat() != null) {
                var opcionCaducidad = new Opcion();
                opcionCaducidad.setTipo("caducidad");
                var sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
                opcionCaducidad.setValue(sdfCaducitat.format(notificacio.getCaducitat())); // formato YYYY-MM-DD
                opcionesRemesa.getOpcion().add(opcionCaducidad);
            }
            envios.setOpcionesRemesa(opcionesRemesa);
        } catch (Exception ex) {
            var error = "Error generant la petició (notificacioId=" + notificacio.getId() + ")";
            log.error(error, ex);
            throw new Exception(ex);
        }
        return envios;
    }

    private Envios generarEnvios(EnviamentCie enviamentCie) throws DatatypeConfigurationException {

        var envios = new Envios();
        for (var enviament : enviamentCie.getEnviaments()) {
            if (enviament == null) {
                continue;
            }
            var envio = new Envio();
            envio.setReferenciaEmisor(enviament.getUuid());
            var titular = new Persona();
            var titularIncapacitat = false;
            if (enviament.getTitular().isIncapacitat() && enviament.getDestinataris() != null) {
                titular.setNif(enviament.getDestinataris().get(0).getNif());
                titular.setApellidos(concatenarLlinatges(enviament.getDestinataris().get(0).getLlinatge1(), enviament.getDestinataris().get(0).getLlinatge2()));
                titular.setTelefono(enviament.getDestinataris().get(0).getTelefon());
                titular.setEmail(enviament.getDestinataris().get(0).getEmail());
                if (enviament.getDestinataris().get(0).getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
                    titular.setRazonSocial(enviament.getDestinataris().get(0).getRaoSocial());
                } else {
                    titular.setNombre(enviament.getDestinataris().get(0).getNom());
                }
                titular.setCodigoDestino(enviament.getDestinataris().get(0).getDir3Codi());
                titularIncapacitat = true;
            } else {
                titular.setNif(InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getNif());
                titular.setApellidos(concatenarLlinatges(enviament.getTitular().getLlinatge1(), enviament.getTitular().getLlinatge2()));
                titular.setTelefono(enviament.getTitular().getTelefon());
//                titular.setEmail(InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getEmail());
                titular.setEmail(enviament.getTitular().getEmail());
                var interessatTipus = enviament.getTitular().getInteressatTipus();
                if (InteressatTipus.JURIDICA.equals(interessatTipus) || InteressatTipus.ADMINISTRACIO.equals(interessatTipus) ) {
                    titular.setRazonSocial(enviament.getTitular().getRaoSocial());
                } else {
                    titular.setNombre(enviament.getTitular().getNom());
                }
                titular.setCodigoDestino(enviament.getTitular().getDir3Codi());
            }
            envio.setTitular(titular);
            var destinatarios = new Destinatarios();
            var counter = 0;
            for (var destinatari : enviament.getDestinataris()) {
                if (Strings.isNullOrEmpty(destinatari.getNif()) || titularIncapacitat && counter == 0) {
                    continue;
                }
                counter++;
                var destinatario = new Persona();
                destinatario.setNif(destinatari.getNif());
                destinatario.setApellidos(concatenarLlinatges(destinatari.getLlinatge1(), destinatari.getLlinatge2()));
                destinatario.setTelefono(destinatari.getTelefon());
                destinatario.setEmail(destinatari.getEmail());
                if (destinatari.getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
                    destinatario.setRazonSocial(destinatari.getRaoSocial());
                } else {
                    destinatario.setNombre(destinatari.getNom());
                }
                destinatario.setCodigoDestino(destinatari.getDir3Codi());
                destinatarios.getDestinatario().add(destinatario);
            }
            if (!destinatarios.getDestinatario().isEmpty()) {
                envio.setDestinatarios(destinatarios);
            }

            if (enviament.getEntregaPostal() != null) {
                var entregaPostal = new EntregaPostal();
                var procedimentNotificacio = enviamentCie.getProcediment();
                if (procedimentNotificacio != null) {
                    var entregaCieEntity = enviamentCie.getEntregaCie();
                    if (entregaCieEntity != null) {
                        var pagadorCie = new OrganismoPagadorCIE();
                        pagadorCie.setCodigoDIR3CIE(entregaCieEntity.getOrganismePagadorCodi());
                        pagadorCie.setFechaVigenciaCIE(toXmlGregorianCalendar(entregaCieEntity.getContracteDataVig()));
                        entregaPostal.setOrganismoPagadorCIE(pagadorCie);
                    }
                    if (enviamentCie.getOperadorPostal() != null) {
                        var pagadorPostal = new OrganismoPagadorPostal();
                        pagadorPostal.setCodigoDIR3Postal(enviamentCie.getOperadorPostal().getOrganismePagadorCodi());
                        pagadorPostal.setCodClienteFacturacionPostal(enviamentCie.getOperadorPostal().getFacturacioClientCodi());
                        pagadorPostal.setNumContratoPostal(enviamentCie.getOperadorPostal().getContracteNum());
                        pagadorPostal.setFechaVigenciaPostal(toXmlGregorianCalendar(enviamentCie.getOperadorPostal().getContracteDataVig()));
                        entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
                    }
                }
                var entregaPostalDto = enviament.getEntregaPostal();
                if (entregaPostalDto.getDomiciliConcretTipus() != null) {
                    switch (entregaPostalDto.getDomiciliConcretTipus())  {
                        case NACIONAL:
                            entregaPostal.setTipoDomicilio(new BigInteger("1"));
                            break;
                        case ESTRANGER:
                            entregaPostal.setTipoDomicilio(new BigInteger("2"));
                            break;
                        case APARTAT_CORREUS:
                            entregaPostal.setTipoDomicilio(new BigInteger("3"));
                            break;
                        case SENSE_NORMALITZAR:
                            entregaPostal.setTipoDomicilio(new BigInteger("4"));
                            break;
                    }
                }
                if (!NotificaDomiciliConcretTipus.SENSE_NORMALITZAR.equals(entregaPostalDto.getDomiciliConcretTipus())) {
                    entregaPostal.setTipoVia(entregaPostalDto.getViaTipus() != null ? entregaPostalDto.getViaTipus().getVal() : null); //viaTipusToString(enviament.getDomiciliViaTipus()));
                    entregaPostal.setNombreVia(entregaPostalDto.getViaNom());
                    entregaPostal.setNumeroCasa(entregaPostalDto.getNumeroCasa());
                    entregaPostal.setPuntoKilometrico(entregaPostalDto.getPuntKm());
                    entregaPostal.setPortal(entregaPostalDto.getPortal());
                    entregaPostal.setPuerta(entregaPostalDto.getPorta());
                    entregaPostal.setEscalera(entregaPostalDto.getEscala());
                    entregaPostal.setPlanta(entregaPostalDto.getPlanta());
                    entregaPostal.setBloque(entregaPostalDto.getBloc());
                    entregaPostal.setComplemento(entregaPostalDto.getComplement());
                    entregaPostal.setCalificadorNumero(entregaPostalDto.getNumeroQualificador());
                    entregaPostal.setCodigoPostal(entregaPostalDto.getCodiPostal());
                    entregaPostal.setApartadoCorreos(entregaPostalDto.getApartatCorreus());
                    entregaPostal.setMunicipio(entregaPostalDto.getMunicipiCodi());
                    entregaPostal.setProvincia(entregaPostalDto.getProvincia());
                    entregaPostal.setPais(entregaPostalDto.getPaisCodi());
                    entregaPostal.setPoblacion(entregaPostalDto.getPoblacio());
                } else {
                    entregaPostal.setLinea1(entregaPostalDto.getLinea1());
                    entregaPostal.setLinea2(entregaPostalDto.getLinea2());
                    entregaPostal.setCodigoPostal(entregaPostalDto.getCodiPostal());
                    entregaPostal.setPais(entregaPostalDto.getPaisCodi());
                }
                if (entregaPostal.getPais() == null) {
                    entregaPostal.setPais("ES");
                }
                var opcionesCie = new Opciones();
                // TODO CAL AQUEST CODI???????????????????????????????????
//                if (entregaPostalDto.getDomiciliCie() != null) {
//                    var opcionCie = new Opcion();
//                    opcionCie.setTipo("cie");
//                    opcionCie.setValue(entregaPostalDto.getDomiciliCie().toString()); // identificador CIE
//                    opcionesCie.getOpcion().add(opcionCie);
//                }
                if (entregaPostalDto.getFormatSobre() != null) {
                    var opcionFormatoSobre = new Opcion();
                    opcionFormatoSobre.setTipo("formatoSobre");
                    opcionFormatoSobre.setValue(entregaPostalDto.getFormatSobre()); // americano, C5...
                    opcionesCie.getOpcion().add(opcionFormatoSobre);
                }
                if (entregaPostalDto.getFormatFulla() != null) {
                    var opcionFormatoHoja = new Opcion();
                    opcionFormatoHoja.setTipo("formatoHoja");
                    opcionFormatoHoja.setValue(entregaPostalDto.getFormatFulla()); // A4, A5...
                    opcionesCie.getOpcion().add(opcionFormatoHoja);
                }
                entregaPostal.setOpcionesCIE(opcionesCie);
                envio.setEntregaPostal(entregaPostal);
            }
            // TODO CAL AQUEST CODI???????????????????????????????????
//            if (enviament.getDehNif() != null && enviament.getDehProcedimentCodi() != null && enviament.getDehObligat() != null) {
//                var entregaDeh = new EntregaDEH();
//                entregaDeh.setObligado(enviament.getDehObligat());
//                entregaDeh.setCodigoProcedimiento(enviament.getDehProcedimentCodi());
//                envio.setEntregaDEH(entregaDeh);
//            }
            envios.getEnvio().add(envio);
        }
        return envios;
    }

    protected XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {

        if (date == null) {
            return null;
        }
        var sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
        var gc = new GregorianCalendar();
        sdfCaducitat.setCalendar(gc);
        gc.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),gc.get(Calendar.MONTH) + 1,gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
    }

    protected String concatenarLlinatges(String llinatge1, String llinatge2) {

        if (llinatge1 == null) {
            return null;
        }
        var llinatges = new StringBuilder();
        llinatges.append(llinatge1.trim());
        if (llinatge2 != null && !llinatge2.trim().isEmpty()) {
            llinatges.append(" ");
            llinatges.append(llinatge2);
        }
        return llinatges.toString();
    }

    @Override
    public RespostaCie cancelar(EnviamentCie enviament) {

        try {
            long startTime = System.currentTimeMillis();
            var cancelar = new CancelarEnvio();
            cancelar.setIdentificador(enviament.getIdentificador());
            var r = getNotificaWs(enviament.getEntregaCie().getApiKey()).cancelarEnvio(cancelar);
            List<IdentificadorCie> ids = new ArrayList<>();
            ids.add(IdentificadorCie.builder().identificador(r.getIdentificador()).build());
            incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return RespostaCie.builder().identificadors(ids).codiResposta(r.getCodigoRespuesta()).descripcioError(r.getDescripcionRespuesta()).build();
        } catch (SOAPFaultException sfe) {
            log.error("[CieCorreosPluginImpl.cancelar] Error en la crida SOAP ", sfe);
            var codi = sfe.getFault().getFaultCode();
            var desc = sfe.getFault().getFaultString();
            incrementarOperacioError();
            return RespostaCie.builder().codiResposta(codi).descripcioError(desc).build();
        } catch (Exception ex) {
            var desc = "Error al cancelar l'enviament CIE " + enviament.getIdentificador();
            log.error(desc, ex);
            incrementarOperacioError();
            return RespostaCie.builder().codiResposta(NOTIB).descripcioError(desc).build();
        }
    }

    @Override
    public InfoCie consultarEstat(EnviamentCie enviament) {

        try {
            long startTime = System.currentTimeMillis();
            var info = new InfoEnvioLigero();
            info.setIdentificador(enviament.getIdentificador());
            info.setNivelDetalle(BigInteger.valueOf(2));
            var r = getNotificaWs(enviament.getEntregaCie().getApiKey()).infoEnvioLigero(info);
            var estat = !Strings.isNullOrEmpty(r.getEstado()) ? CieEstat.valueOf(r.getEstado().toUpperCase()) : null;
            incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return InfoCie.builder().identificador(r.getIdentificador()).codiResposta(r.getCodigoRespuesta()).descripcioResposta(r.getDescripcionRespuesta()).codiEstat(estat).build();
        } catch (SOAPFaultException sfe) {
            log.error("[CieCorreosPluginImpl.cancelar] Error en la crida SOAP ", sfe);
            var codi = sfe.getFault().getFaultCode();
            var desc = sfe.getFault().getFaultString();
            incrementarOperacioError();
            return InfoCie.builder().identificador(enviament.getIdentificador()).codiResposta(codi).descripcioResposta(desc).build();
        } catch (Exception ex) {
            var desc = "Error al consultar l'enviament CIE " + enviament.getIdentificador() + ex.getMessage();
            log.error(desc, ex);
            incrementarOperacioError();
            return InfoCie.builder().identificador(enviament.getIdentificador()).codiResposta(NOTIB).descripcioResposta(desc).build();
        }
    }

    @Override
    public RespostaCie altaRemitent(RemitentCie remitent) {

//        try {
//            // TODO FALTA LA CRIDA
//            return RespostaCie.builder().identificador(r.getIdentificador()).codiError(r.getCodigoRespuesta()).descripcioError(r.getDescripcionRespuesta()).build();
//        } catch (SOAPFaultException sfe) {
//            log.error("[CieCorreosPluginImpl.cancelar] Error en la crida SOAP ", sfe);
//            var codi = sfe.getFault().getFaultCode();
//            var desc = sfe.getFault().getFaultString();
//            return RespostaCie.builder().identificador(remitent.getId()).codiError(codi).descripcioError(desc).build();
//        } catch (java.lang.Exception ex) {
//            var desc = "Error al crear el remitent " + remitent.getId();
//            log.error(desc, ex);
//            return RespostaCie.builder().identificador(remitent.getId()).codiError(NOTIB).descripcioError(desc).build();
//        }
        return null;
    }

    @Override
    public RespostaCie modificarRemitent(RemitentCie remitent) {

//        try {
//            // TODO FALTA LA CRIDA
//            return RespostaCie.builder().identificador(r.getIdentificador()).codiError(r.getCodigoRespuesta()).descripcioError(r.getDescripcionRespuesta()).build();
//        } catch (SOAPFaultException sfe) {
//            log.error("[CieCorreosPluginImpl.modificarRemitent] Error en la crida SOAP ", sfe);
//            var codi = sfe.getFault().getFaultCode();
//            var desc = sfe.getFault().getFaultString();
//            return RespostaCie.builder().identificador(remitent.getId()).codiError(codi).descripcioError(desc).build();
//        } catch (java.lang.Exception ex) {
//            var desc = "Error al modificar el remitent " + remitent.getId();
//            log.error(desc, ex);
//            return RespostaCie.builder().identificador(remitent.getId()).codiError(NOTIB).descripcioError(desc).build();
//        }
        return null;
    }

    @Override
    public Boolean borrarRemitent(String idRemitent) {

        try {
            // TODO FALTA LA CRIDA
        } catch (SOAPFaultException sfe) {
            log.error("[CieCorreosPluginImpl.borrarRemitent] Error en la crida SOAP ", sfe);
            var codi = sfe.getFault().getFaultCode();
            var desc = sfe.getFault().getFaultString();
            return false;
        } catch (Exception ex) {
            var desc = "Error al esborrar el remitent " + idRemitent;
            log.error(desc, ex);
            return false;
        }
        return null;
    }

    private NotificaWsV2PortType getNotificaWs(String apiKey) throws MalformedURLException, MalformedObjectNameException, InstanceNotFoundException, NamingException, RemoteException, CreateException {


         return new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
                getClass().getResource("https://nexea.es/serviciosweb_pre/NotificaWsV2Service?wsdl"),
                properties.get("es.caib.notib.plugin.cie.url").toString(),
//                new QName("https://nexea.es/serviciosweb_pre/NotificaWsV2Service","NotificaWsV2Service"),
                new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/","NotificaWsV2Service"),
                null,
                null,
                false,
                true,
                NotificaWsV2PortType.class,
                new ApiKeySOAPHandlerV2(apiKey));
    }

    private static class ApiKeySOAPHandlerV2 implements SOAPHandler<SOAPMessageContext> {
        private final String apiKey;

        public ApiKeySOAPHandlerV2(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public boolean handleMessage(SOAPMessageContext context) {

            var outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (!outboundProperty) {
                return true;
            }
            try {
                var envelope = context.getMessage().getSOAPPart().getEnvelope();
                var factory = SOAPFactory.newInstance();
                /*SOAPElement apiKeyElement = factory.createElement("apiKey");*/
                var apiKeyElement = factory.createElement(new QName("https://nexea.es/serviciosweb_pre/NotificaWsV2Service","apiKey"));
                apiKeyElement.addTextNode(apiKey);
                var header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }
                header.addChildElement(apiKeyElement);
                context.getMessage().saveChanges();
            } catch (SOAPException ex) {
                log.error("No s'ha pogut afegir l'API key a la petició SOAP per Notifica", ex);
            }
            return true;
        }

        @Override
        public boolean handleFault(SOAPMessageContext context) {
            return false;
        }

        @Override
        public void close(MessageContext context) {
            // close
        }

        @Override
        public Set<QName> getHeaders() {
            return new TreeSet<>();
        }
    }
}


