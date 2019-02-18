package es.caib.notib.plugin.arxiu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import com.sun.jersey.core.util.Base64;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Content;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FolderNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Metadata;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.SummaryInfoNode;
import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.EstadosElaboracion;
import es.caib.arxiudigital.apirest.constantes.EstadosExpediente;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.MetadatosExpediente;
import es.caib.arxiudigital.apirest.constantes.OrigenesContenido;
import es.caib.arxiudigital.apirest.constantes.TiposContenidosBinarios;
import es.caib.arxiudigital.apirest.constantes.TiposDocumentosENI;
import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;

public class ArxiuConversioHelper {

	public static FileNode expedientToFileNode(
			Expedient expedient,
			List<Metadata> metadadesPrevies,
			List<Aspectos> aspectesPrevis,
			String aplicacioCodi,
			boolean creacio) throws ArxiuException {
		FileNode node = new FileNode();
		node.setType(TiposObjetoSGD.EXPEDIENTE);
		node.setId(expedient.getIdentificador());
		node.setName(
				revisarContingutNom(expedient.getNom()));
		node.setMetadataCollection(
				toMetadataExpedient(
						expedient.getMetadades(),
						aplicacioCodi,
						metadadesPrevies));
		node.setAspects(generarAspectes(aspectesPrevis, creacio));
		return node;
	}

	public static Expedient fileNodeToExpedient(
			FileNode fileNode,
			String versio) throws ArxiuException {
		Expedient expedient = new Expedient();
		expedient.setIdentificador(fileNode.getId());
		expedient.setNom(fileNode.getName());
		expedient.setMetadades(
				toExpedientMetadades(fileNode.getMetadataCollection()));
		expedient.setContinguts(
				summaryInfoNodesToInformacioItems(fileNode.getChildObjects()));
		if (versio != null) {
			expedient.setVersio(versio);
		}
		return expedient;
	}

	public static List<ContingutArxiu> fileNodesToFileContingutArxiu(
			List<FileNode> fileNodeList) {
		if (fileNodeList == null) {
			return null;
		}
		List<ContingutArxiu> informacioItemList = new ArrayList<ContingutArxiu>();
		for (FileNode fileNode: fileNodeList) {
			informacioItemList.add(
					crearContingutArxiu(
							fileNode.getId(),
							fileNode.getName(),
							toContingutTipus(fileNode.getType()),
							null));
		}
		return informacioItemList;
	}

	public static DocumentNode documentToDocumentNode(
			Document document,
			String serieDocumental,
			List<Metadata> metadadesPrevies,
			List<Aspectos> aspectesPrevis,
			String aplicacioCodi,
			String csv,
			String csvDef,
			boolean creacio) throws ArxiuException {
		DocumentNode node = new DocumentNode();
		node.setId(document.getIdentificador());
		node.setName(
				revisarContingutNom(document.getNom()));
		node.setType(TiposObjetoSGD.DOCUMENTO);
		node.setBinaryContents(
				toBinaryContents(document));
		node.setMetadataCollection(
				toMetadataDocument(
						document.getMetadades(),
						serieDocumental,
						document.getFirmes(),
						aplicacioCodi,
						metadadesPrevies,
						csv,
						csvDef));
		node.setAspects(
				generarAspectes(aspectesPrevis, creacio));
		return node;
	}

	public static Document documentNodeToDocument(
			DocumentNode documentNode,
			String versio) throws ArxiuException {
		Document document = new Document();
		document.setIdentificador(documentNode.getId());
		document.setNom(documentNode.getName());
		document.setContingut(toDocumentContingut(documentNode.getBinaryContents()));
		document.setMetadades(toDocumentMetadades(documentNode.getMetadataCollection()));
		document.setFirmes(toDocumentFirmes(documentNode.getBinaryContents(), documentNode.getMetadataCollection()));
		if (documentNode.getAspects() != null) {
			boolean esborrany = documentNode.getAspects().contains(Aspectos.BORRADOR);
			if (esborrany) {
				document.setEstat(DocumentEstat.ESBORRANY);
			} else {
				document.setEstat(DocumentEstat.DEFINITIU);
			}
		}
		if (versio != null) {
			document.setVersio(versio);
		}
		return document;
	}

	public static List<ContingutArxiu> fileNodeToDocumentContingut(
			List<DocumentNode> documentNodeList) {
		List<ContingutArxiu> informacioItemList = new ArrayList<ContingutArxiu>();
		for (DocumentNode documentNode: documentNodeList) {
			informacioItemList.add(crearContingutArxiu(documentNode.getId(), documentNode.getName(),
					toContingutTipus(documentNode.getType()), null));
		}
		return informacioItemList;
	}

	public static FolderNode toFolderNode(
			String identificador,
			String nom) {
		FolderNode node = new FolderNode();
		node.setId(identificador);
		node.setName(
				revisarContingutNom(nom));
		node.setType(TiposObjetoSGD.DIRECTORIO);
		return node;
	}

	public static Carpeta folderNodeToCarpeta(FolderNode folderNode) {
		Carpeta carpeta = new Carpeta();
		carpeta.setIdentificador(folderNode.getId());
		carpeta.setNom(folderNode.getName());
		carpeta.setContinguts(summaryInfoNodesToInformacioItems(folderNode.getChildObjects()));
		return carpeta;
	}

	public static ContingutArxiu crearContingutArxiu(
			String identificador,
			String nom,
			ContingutTipus tipus,
			String versio) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		informacioItem.setVersio(versio);
		return informacioItem;
	}



	private static List<Metadata> toMetadataExpedient(
			ExpedientMetadades expedientMetadades,
			String aplicacioCodi,
			List<Metadata> metadadesPrevies) throws ArxiuException {
		List<Metadata> metadades = new ArrayList<Metadata>();
		if (metadadesPrevies != null) {
			metadades.addAll(metadadesPrevies);
		}
		addMetadata(
				metadades,
				MetadatosExpediente.CODIGO_APLICACION_TRAMITE,
				aplicacioCodi);
		if (expedientMetadades != null) {
			// TODO: La metadada eni:origen s'ha hagut d'afegir per evitar
			// un error de l'arxiu però aquesta metadada no hauria de ser
			// necessària per als expedients
			addMetadata(
					metadades,
					MetadatosExpediente.ORIGEN,
					OrigenesContenido.ADMINISTRACION);
			addMetadata(
					metadades,
					MetadatosExpediente.CODIGO_CLASIFICACION,
					expedientMetadades.getSerieDocumental());
			addMetadata(
					metadades,
					MetadatosExpediente.IDENTIFICADOR_PROCEDIMIENTO,
					expedientMetadades.getClassificacio());
			addMetadata(
					metadades,
					MetadatosExpediente.ESTADO_EXPEDIENTE,
					toEstadosExpediente(expedientMetadades.getEstat()));
			addMetadata(
					metadades,
					MetadatosExpediente.ORGANO,
					expedientMetadades.getOrgans());
			addMetadata(
					metadades,
					MetadatosExpediente.INTERESADOS,
					expedientMetadades.getInteressats());
			addMetadata(
					metadades,
					MetadatosExpediente.FECHA_INICIO,
					formatDateIso8601(expedientMetadades.getDataObertura()));
			if (expedientMetadades.getMetadadesAddicionals() != null) {
				for (String metadada : expedientMetadades.getMetadadesAddicionals().keySet()) {
					addMetadata(
							metadades,
							metadada,
							expedientMetadades.getMetadadesAddicionals().get(metadada));
				}
			}
		}
		return metadades;
	}

	private static void addMetadata(
			List<Metadata> metadades,
			String qname,
			Object value) {
		if (value != null) {
			boolean actualitzat = false;
			for (Metadata metadata: metadades) {
				if (metadata.getQname().equals(qname)) {
					metadata.setValue(value);
					actualitzat = true;
				}
			}
			if (!actualitzat) {
				Metadata metadata = new Metadata();
				metadata.setQname(qname);
				metadata.setValue(value);
				metadades.add(metadata);
			}
		}
	}

	private static List<Aspectos> generarAspectes(
			List<Aspectos> aspectesPrevis,
			boolean create) {
		List<Aspectos> aspectesCreats = null;
		if (aspectesPrevis != null && !aspectesPrevis.isEmpty()) {
			aspectesCreats = new ArrayList<Aspectos>();
			aspectesCreats.addAll(aspectesPrevis);
		}
		if (create && aspectesCreats == null) {
			aspectesCreats = new ArrayList<Aspectos>();
			aspectesCreats.add(Aspectos.INTEROPERABLE);
			aspectesCreats.add(Aspectos.TRANSFERIBLE);
		}
		return aspectesCreats;
	}

	@SuppressWarnings("unchecked")
	private static ExpedientMetadades toExpedientMetadades(
			List<Metadata> metadataList) throws ArxiuException {
		ExpedientMetadades metadades = new ExpedientMetadades();
		for (Metadata metadata: metadataList) {
			if (metadata.getValue() != null) {
				if (MetadatosDocumento.ID_ENI.equals(metadata.getQname())) {
					metadades.setIdentificador(metadata.getValue().toString());
				} else if ("eni:v_nti".equals(metadata.getQname())) {
					metadades.setVersioNti(metadata.getValue().toString());
				} else if (MetadatosExpediente.ORGANO.equals(metadata.getQname())) {
					Object preOrgan = metadata.getValue();
					if (preOrgan instanceof List<?>) {
						metadades.setOrgans((List<String>)preOrgan);
					} else if (preOrgan instanceof String) {
						List<String> organs = new ArrayList<String>();
						organs.add((String) preOrgan);
						metadades.setOrgans(organs);
					}
				} else if (MetadatosExpediente.FECHA_INICIO.equals(metadata.getQname())) {
					metadades.setDataObertura(
							parseDateIso8601(metadata.getValue().toString()));
				} else if (MetadatosExpediente.IDENTIFICADOR_PROCEDIMIENTO.equals(metadata.getQname())) {
					metadades.setClassificacio(metadata.getValue().toString());
				} else if (MetadatosExpediente.ESTADO_EXPEDIENTE.equals(metadata.getQname())) {
					if (metadata.getValue() instanceof EstadosExpediente) {
						metadades.setEstat(
								ExpedientEstat.toEnum(
										((EstadosExpediente)metadata.getValue()).getValue()));
					} else if (metadata.getValue() instanceof String) {
						metadades.setEstat(
								ExpedientEstat.toEnum(
										(String)metadata.getValue()));
					}
				} else if (MetadatosExpediente.INTERESADOS.equals(metadata.getQname())) {
					Object preInteressat = metadata.getValue();
					if (preInteressat instanceof List<?>) {
						metadades.setInteressats((List<String>)preInteressat);
					} else if (preInteressat instanceof String) {
						List<String> interessats = new ArrayList<String>();
						interessats.add((String) preInteressat);
						metadades.setInteressats(interessats);
					}
				} else if (MetadatosExpediente.CODIGO_CLASIFICACION.equals(metadata.getQname())) {
					metadades.setSerieDocumental(metadata.getValue().toString());
				} else {
					Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
					if (metadadesAddicionals == null) {
						metadadesAddicionals = new HashMap<String, Object>();
						metadades.setMetadadesAddicionals(metadadesAddicionals);
					}
					if (metadata.getValue() != null) {
						metadadesAddicionals.put(metadata.getQname(), metadata.getValue());
					}
				}
			}
		}
		return metadades;
	}

	private static List<ContingutArxiu> summaryInfoNodesToInformacioItems(
			List<SummaryInfoNode> summaryInfoNodes) {
		if (summaryInfoNodes == null) {
			return null;
		}
		List<ContingutArxiu> continguts = new ArrayList<ContingutArxiu>();
		for (SummaryInfoNode summaryInfoNode: summaryInfoNodes) {
			continguts.add(
					crearContingutArxiu(
							summaryInfoNode.getId(),
							summaryInfoNode.getName(),
							toContingutTipus(summaryInfoNode.getType()),
							null));
		}
		return continguts;
	}

	private static List<Metadata> toMetadataDocument(
			DocumentMetadades documentMetadades,
			String serieDocumental,
			List<Firma> firmes,
			String aplicacioCodi,
			List<Metadata> metadadesPrevies,
			String csv,
			String csvDef) throws ArxiuException {
		List<Metadata> metadades = new ArrayList<Metadata>();
		if (metadadesPrevies != null) {
			metadades.addAll(metadadesPrevies);
		}
		addMetadata(
				metadades,
				MetadatosExpediente.CODIGO_APLICACION_TRAMITE,
				aplicacioCodi);
		addMetadata(
				metadades,
				MetadatosDocumento.CODIGO_CLASIFICACION,
				serieDocumental);
		if (documentMetadades != null) {
			addMetadata(
					metadades,
					MetadatosDocumento.ORIGEN,
					toOrigenesContenido(documentMetadades.getOrigen()));
			addMetadata(
					metadades,
					MetadatosDocumento.FECHA_INICIO,
					formatDateIso8601(documentMetadades.getDataCaptura()));
			addMetadata(
					metadades,
					MetadatosDocumento.ESTADO_ELABORACION,
					toEstadosElaboracion(documentMetadades.getEstatElaboracio()));
			addMetadata(
					metadades,
					MetadatosDocumento.TIPO_DOC_ENI,
					toTiposDocumentosEni(documentMetadades.getTipusDocumental()));
			if (documentMetadades.getFormat() != null) {
				addMetadata(
						metadades,
						MetadatosDocumento.NOMBRE_FORMATO,
						documentMetadades.getFormat().toString());
			}
			if (documentMetadades.getExtensio() != null) {
				addMetadata(
						metadades,
						MetadatosDocumento.EXTENSION_FORMATO,
						documentMetadades.getExtensio().toString());
			}
			addMetadata(
					metadades,
					MetadatosDocumento.ORGANO,
					documentMetadades.getOrgans());
			if (documentMetadades.getMetadadesAddicionals() != null) {
				for (String clau : documentMetadades.getMetadadesAddicionals().keySet()) {
					addMetadata(
							metadades,
							clau,
							documentMetadades.getMetadadesAddicionals().get(clau));
				}
			}
		}
		boolean firmaCsvConfigurada = false;
		if (firmes != null) {
			boolean tipusFirmaConfigurat = false;
			for (Firma firma: firmes) {
				if (FirmaTipus.CSV.equals(firma.getTipus())) {
					if (firma.getContingut() != null) {
						addMetadata(
								metadades,
								MetadatosDocumento.CSV,
								new String(firma.getContingut()));
					}
					addMetadata(
							metadades,
							MetadatosDocumento.DEF_CSV,
							firma.getCsvRegulacio());
					firmaCsvConfigurada = true;
				} else if (!tipusFirmaConfigurat) {
					addMetadata(
							metadades,
							MetadatosDocumento.TIPO_FIRMA,
							firma.getTipus().toString());
					addMetadata(
							metadades,
							MetadatosDocumento.PERFIL_FIRMA,
							firma.getPerfil().toString());
					tipusFirmaConfigurat = true;
				}
			}
		}
		if (!firmaCsvConfigurada) {
			addMetadata(metadades, MetadatosDocumento.CSV, csv);
			addMetadata(metadades, MetadatosDocumento.DEF_CSV, csvDef);
		}
		return metadades;
	}

	private static List<Content> toBinaryContents(Document document) {
		List<Content> contents = null;
		if (document.getContingut() != null) {
			Content content = new Content();
			content.setBinaryType(TiposContenidosBinarios.CONTENT);
			content.setContent(new String(Base64.encode(document.getContingut().getContingut())));
			content.setEncoding("UTF-8");
			content.setMimetype(document.getContingut().getTipusMime());
			if (contents == null) {
				contents = new ArrayList<Content>();
			}
			contents.add(content);
		}
		if (document.getFirmes() != null) {
			for (Firma firma: document.getFirmes()) {
				if (!FirmaTipus.CSV.equals(firma.getTipus()) && !FirmaTipus.PADES.equals(firma.getTipus())) {
					Content content = new Content();
					content.setBinaryType(TiposContenidosBinarios.SIGNATURE);
					if (firma.getContingut() != null) {
						content.setContent(new String(Base64.encode(firma.getContingut())));
					}
					content.setEncoding("UTF-8");
					content.setMimetype(firma.getTipusMime());
					if (contents == null) {
						contents = new ArrayList<Content>();
					}
					contents.add(content);
				} else if (
						FirmaTipus.PADES.equals(firma.getTipus()) &&
						document.getContingut() == null &&
						firma.getContingut() != null) {
					// En el cas de les firmes PAdES es dona la possibilitat de que
					// el contingut del PDF firmat s'especifiqui a dins la firma o com
					// a contingut. En el cas de que s'especifiqui a dins la firma el
					// contingut haurà de ser null.
					Content content = new Content();
					content.setBinaryType(TiposContenidosBinarios.CONTENT);
					content.setContent(new String(Base64.encode(firma.getContingut())));
					content.setEncoding("UTF-8");
					content.setMimetype(firma.getTipusMime());
					if (contents == null) {
						contents = new ArrayList<Content>();
					}
					contents.add(content);
				}
			}
		}
		return contents;
	}

	private static DocumentContingut toDocumentContingut(List<Content> contents) {
		if (contents == null) {
			return null;
		}
		for (Content content: contents) {
			if (TiposContenidosBinarios.CONTENT.equals(content.getBinaryType())) {
				DocumentContingut contingut = new DocumentContingut();
				contingut.setContingut(Base64.decode(content.getContent()));
				contingut.setTamany(contingut.getContingut().length);
				contingut.setTipusMime(content.getMimetype());
				return contingut;
			}
		}
		return null;
	}

	private static List<Firma> toDocumentFirmes(
			List<Content> contents,
			List<Metadata> metadades) {
		List<Firma> firmes = null;
		String firmaTipus = null;
		String firmaPerfil = null;
		String firmaCsv = null;
		String firmaCsvRegulacio = null;
		for (Metadata metadada: metadades) {
			if (MetadatosDocumento.TIPO_FIRMA.equals(metadada.getQname())) {
				firmaTipus = (String)metadada.getValue();
			} else if (MetadatosDocumento.PERFIL_FIRMA.equals(metadada.getQname())) {
				firmaPerfil = (String)metadada.getValue();
			} else if (MetadatosDocumento.CSV.equals(metadada.getQname())) {
				firmaCsv = (String)metadada.getValue();
			} else if (MetadatosDocumento.DEF_CSV.equals(metadada.getQname())) {
				firmaCsvRegulacio = (String)metadada.getValue();
			}
		}
		if (firmaCsv != null) {
			Firma firma = new Firma();
			firma.setTipus(FirmaTipus.CSV);
			firma.setContingut(firmaCsv.getBytes());
			firma.setTamany(firma.getContingut().length);
			firma.setTipusMime("text/plain");
			firma.setCsvRegulacio(firmaCsvRegulacio);
			if (firmes == null) {
				firmes = new ArrayList<Firma>();
			}
			firmes.add(firma);
		}
		if (firmaTipus != null) {
			FirmaTipus firmaTipusEnum = FirmaTipus.toEnum(firmaTipus);
			Firma firma = new Firma();
			firma.setTipus(firmaTipusEnum);
			firma.setPerfil(FirmaPerfil.toEnum(firmaPerfil));
			if (contents != null) {
				for (Content content: contents) {
					if (TiposContenidosBinarios.SIGNATURE.equals(content.getBinaryType())) {
						firma.setContingut(Base64.decode(content.getContent()));
						firma.setTamany(firma.getContingut().length);
						firma.setTipusMime(content.getMimetype());
					} else if ((FirmaTipus.PADES.equals(firmaTipusEnum) || FirmaTipus.CADES_ATT.equals(firmaTipusEnum)) && TiposContenidosBinarios.CONTENT.equals(content.getBinaryType())) {
						firma.setContingut(Base64.decode(content.getContent()));
						firma.setTamany(firma.getContingut().length);
						firma.setTipusMime(content.getMimetype());
					}
				}
			}
			if (firmes == null) {
				firmes = new ArrayList<Firma>();
			}
			firmes.add(firma);
		}
		return firmes;
	}

	@SuppressWarnings("unchecked")
	private static DocumentMetadades toDocumentMetadades(List<Metadata> metadatas) throws ArxiuException {
		DocumentMetadades metadades = new DocumentMetadades();
		for (Metadata metadata: metadatas) {
			if (metadata.getValue() != null) {
				if (MetadatosDocumento.ID_ENI.equals(metadata.getQname())) {
					metadades.setIdentificador(metadata.getValue().toString());
				} else if ("eni:v_nti".equals(metadata.getQname())) {
					metadades.setVersioNti(metadata.getValue().toString());
				} else if (MetadatosDocumento.ORIGEN.equals(metadata.getQname())) {
					metadades.setOrigen(
							ContingutOrigen.toEnum(
									String.valueOf(metadata.getValue())));
				} else if (MetadatosDocumento.FECHA_INICIO.equals(metadata.getQname())) {
					metadades.setDataCaptura(parseDateIso8601(metadata.getValue().toString()));
				} else if (MetadatosDocumento.ESTADO_ELABORACION.equals(metadata.getQname())) {
					metadades.setEstatElaboracio(
							DocumentEstatElaboracio.toEnum(
									metadata.getValue().toString()));
				} else if (MetadatosDocumento.TIPO_DOC_ENI.equals(metadata.getQname())) {
					metadades.setTipusDocumental(
							DocumentTipus.toEnum(
									metadata.getValue().toString()));
				} else if (MetadatosDocumento.ORGANO.equals(metadata.getQname())) {
					Object preValor = metadata.getValue();
					if (preValor instanceof List<?>) {
						metadades.setOrgans((List<String>)metadata.getValue());
					} else {
						metadades.setOrgans(Arrays.asList((String)preValor));
					}
					break;
				} else if (MetadatosDocumento.NOMBRE_FORMATO.equals(metadata.getQname())) {
					metadades.setFormat(
							DocumentFormat.toEnum(metadata.getValue().toString()));
				} else if (MetadatosDocumento.EXTENSION_FORMATO.equals(metadata.getQname())) {
					metadades.setExtensio(
							DocumentExtensio.toEnum(metadata.getValue().toString()));
				//} else if (MetadatosDocumento.CSV.equals(metadata.getQname())) {
				//} else if (MetadatosDocumento.DEF_CSV.equals(metadata.getQname())) {
				} else {
					Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
					if (metadadesAddicionals == null) {
						metadadesAddicionals = new HashMap<String, Object>();
						metadades.setMetadadesAddicionals(metadadesAddicionals);
					}
					if (metadata.getValue() != null) {
						metadadesAddicionals.put(metadata.getQname(), metadata.getValue());
					}
				}
			}
		}
		return metadades;
	}

	private static EstadosExpediente toEstadosExpediente(
			ExpedientEstat estat) throws ArxiuException {
		if (estat == null)
			return null;
		switch (estat) {
		case OBERT:
			return EstadosExpediente.ABIERTO;
		case TANCAT:
			return EstadosExpediente.CERRADO;
		case INDEX_REMISSIO:
			return EstadosExpediente.INDICE_REMISION;
		default:
			throw new ArxiuException(
					"No s'ha pogut convertir el valor de l'enumeració ExpedientEstat (" + "valor=" + estat + ")");
		}
	}

	private static ContingutTipus toContingutTipus(TiposObjetoSGD tiposObjetoSGD) {
		switch (tiposObjetoSGD) {
		case DIRECTORIO:
			return ContingutTipus.CARPETA;
		case EXPEDIENTE:
			return ContingutTipus.EXPEDIENT;
		case DOCUMENTO:
			return ContingutTipus.DOCUMENT;
		case DOCUMENTO_MIGRADO:
			return ContingutTipus.DOCUMENT;
		default:
			return null;
		}
	}

	private static OrigenesContenido toOrigenesContenido(
			ContingutOrigen origen) throws ArxiuException {
		if (origen == null)
			return null;
		switch (origen) {
		case CIUTADA:
			return OrigenesContenido.CIUDADANO;
		case ADMINISTRACIO:
			return OrigenesContenido.ADMINISTRACION;
		default:
			throw new ArxiuException(
					"No s'ha pogut convertir el valor per l'enumeració Origen (" + "valor=" + origen + ")");
		}
	}

	private static EstadosElaboracion toEstadosElaboracion(DocumentEstatElaboracio estatElaboracio)
			throws ArxiuException {
		if (estatElaboracio == null)
			return null;
		switch (estatElaboracio) {
		case ORIGINAL:
			return EstadosElaboracion.ORIGINAL;
		case COPIA_CF:
			return EstadosElaboracion.COPIA_AUTENTICA_FORMATO;
		case COPIA_DP:
			return EstadosElaboracion.COPIA_AUTENTICA_PAPEL;
		case COPIA_PR:
			return EstadosElaboracion.COPIA_AUTENTICA_PARCIAL;
		case ALTRES:
			return EstadosElaboracion.OTROS;
		default:
			throw new ArxiuException("No s'ha pogut convertir el valor per l'enumeració ArxiuEstatElaboracio ("
					+ "valor=" + estatElaboracio + ")");
		}
	}

	private static TiposDocumentosENI toTiposDocumentosEni(DocumentTipus documentTipus) throws ArxiuException {
		if (documentTipus == null)
			return null;
		switch (documentTipus) {
		case RESOLUCIO:
			return TiposDocumentosENI.RESOLUCION;
		case ACORD:
			return TiposDocumentosENI.ACUERDO;
		case CONTRACTE:
			return TiposDocumentosENI.CONTRATO;
		case CONVENI:
			return TiposDocumentosENI.CONVENIO;
		case DECLARACIO:
			return TiposDocumentosENI.DECLARACION;
		case COMUNICACIO:
			return TiposDocumentosENI.COMUNICACION;
		case NOTIFICACIO:
			return TiposDocumentosENI.NOTIFICACION;
		case PUBLICACIO:
			return TiposDocumentosENI.PUBLICACION;
		case JUSTIFICANT_RECEPCIO:
			return TiposDocumentosENI.ACUSE_DE_RECIBO;
		case ACTA:
			return TiposDocumentosENI.ACTA;
		case CERTIFICAT:
			return TiposDocumentosENI.CERTIFICADO;
		case DILIGENCIA:
			return TiposDocumentosENI.DILIGENCIA;
		case INFORME:
			return TiposDocumentosENI.INFORME;
		case SOLICITUD:
			return TiposDocumentosENI.SOLICITUD;
		case DENUNCIA:
			return TiposDocumentosENI.DENUNCIA;
		case ALEGACIO:
			return TiposDocumentosENI.ALEGACION;
		case RECURS:
			return TiposDocumentosENI.RECURSOS;
		case COMUNICACIO_CIUTADA:
			return TiposDocumentosENI.COMUNICACION_CIUDADANO;
		case FACTURA:
			return TiposDocumentosENI.FACTURA;
		case ALTRES_INCAUTATS:
			return TiposDocumentosENI.OTROS_INCAUTADOS;
		case ALTRES:
			return TiposDocumentosENI.OTROS;
		default:
			throw new ArxiuException(
					"No s'ha pogut convertir el valor per l'enumeració ArxiuDocumentTipus (" +
					"valor=" + documentTipus + ")");
		}
	}

	private static String formatDateIso8601(Date date) {
		if (date == null) {
			return null;
		}
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		return df.format(date);
	}

	private static Date parseDateIso8601(String date) throws ArxiuException {
		if (date == null) {
			return null;
		}
		try {
			Calendar c = DatatypeConverter.parseDateTime(date);
			return c.getTime();
		} catch (IllegalArgumentException ex) {
			throw new ArxiuException("No s'ha pogut parsejar el valor de la data (valor=" + date + ")");
		}
	}

	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}

}