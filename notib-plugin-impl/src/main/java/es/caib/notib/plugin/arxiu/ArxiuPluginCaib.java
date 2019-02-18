package es.caib.notib.plugin.arxiu;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocClassification;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentId;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Metadata;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.TargetNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.VersionNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamDispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CloseFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CopyDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CopyFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateChildFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDraftDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.DispatchDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExportFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GenerateDocCSVResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetENIDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ReopenFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchFilesResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFinalDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CloseFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CopyDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CopyFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.DispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ExportFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GenerateDocCSV;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetENIDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFileVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ReopenFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchDocs;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchFiles;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFinalDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFolder;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.Servicios;
import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;
import es.caib.notib.plugin.arxiu.ArxiuCaibClient.GeneradorParam;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ArxiuValidacioException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentRepositori;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class ArxiuPluginCaib implements IArxiuPlugin  {

	private static final String ARXIUCAIB_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "caib.";
	private static final int NUM_PAGINES_RESULTAT_CERCA = 100;
	private static final String VERSIO_INICIAL_CONTINGUT = "1.0";
	private static final String JERSEY_TIMEOUT_CONNECT = "10000";
	private static final String JERSEY_TIMEOUT_READ = "60000";

	private static final String QUERY_TYPE_ENI_EXPEDIENTE = "\"eni:expediente\"";
	private static final String QUERY_TYPE_ENI_DOCUMENTO = "\"eni:documento\"";
	private static final String QUERY_TYPE_GDIB_DOCUMENTO_MIGRADO = "\"gdib:documentoMigrado\"";

	private ArxiuCaibClient arxiuClient;
	private Client versioImprimibleClient;

	public ArxiuPluginCaib() {
		super();
	}

	@Override
	public ContingutArxiu expedientCrear(
			final Expedient expedient) throws ArxiuException {
		String metode = Servicios.CREATE_FILE;
		try {
			CreateFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateFile.class,
					new GeneradorParam<ParamCreateFile>() {
						@Override
						public ParamCreateFile generar() {
							ParamCreateFile param = new ParamCreateFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null,
											null,
											null,
											true));
							param.setRetrieveNode(Boolean.TRUE.toString());
							return param;
						}
					},
					ParamCreateFile.class,
					CreateFileResult.class);
			Expedient expedientCreat = ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getCreateFileResult().getResParam(),
					VERSIO_INICIAL_CONTINGUT);
			return crearContingutArxiu(
					expedientCreat.getIdentificador(), 
					expedientCreat.getNom(),
					ContingutTipus.EXPEDIENT,
					expedientCreat.getVersio());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu expedientModificar(
			final Expedient expedient) throws ArxiuException {
		String metode = null;
		try {
			/*metode = Servicios.GET_FILE;
			final GetFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(expedient.getIdentificador());
							return param;
						}
					},
					ParamNodeId.class,
					GetFileResult.class);*/
			metode = Servicios.SET_FILE;
			getArxiuClient().generarEnviarPeticio(
					metode,
					SetFile.class,
					new GeneradorParam<ParamSetFile>() {
						@Override
						public ParamSetFile generar() {
							ParamSetFile param = new ParamSetFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null, //resposta.getGetFileResult().getResParam().getMetadataCollection(),
											null, //resposta.getGetFileResult().getResParam().getAspects(),
											null,
											false));
							return param;
						}
					},
					ParamSetFile.class,
					SetFileResult.class);
			String versio = expedientDarreraVersio(
					expedient.getIdentificador());
			return crearContingutArxiu(
					expedient.getIdentificador(), 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					versio);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void expedientEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_FILE;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveFileResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Expedient expedientDetalls(
			final String identificador,
			final String versio) throws ArxiuException {
		String metode = Servicios.GET_FILE;
		try {
			String versioResposta = null;
			if (versio == null) {
				versioResposta = expedientDarreraVersio(identificador);
			} else {
				versioResposta = versio;
			}
			GetFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							String identificadorAmbVersio;
							if (versio != null) {
								identificadorAmbVersio = versio + "@" + identificador;
							} else {
								identificadorAmbVersio = identificador;
							}
							param.setNodeId(identificadorAmbVersio);
							return param;
						}
					},
					ParamNodeId.class,
					GetFileResult.class);
			return ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getGetFileResult().getResParam(),
					versioResposta);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ConsultaResultat expedientConsulta(
			final List<ConsultaFiltre> filtres,
			final Integer pagina,
			final Integer itemsPerPagina) throws ArxiuException {
		String metode = Servicios.SEARCH_FILE;
		try {
			List<ContingutArxiu> resultatConsulta = new ArrayList<ContingutArxiu>();
			List<ContingutArxiu> continguts = null;
			while (continguts == null || continguts.size() == NUM_PAGINES_RESULTAT_CERCA) {
				SearchFilesResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						SearchFiles.class,
						new GeneradorParam<ParamSearch>() {
							@Override
							public ParamSearch generar() {
								ParamSearch param = new ParamSearch();
								String query = generarConsulta(
										QUERY_TYPE_ENI_EXPEDIENTE,
										filtres);
								param.setQuery(query);
								param.setPageNumber(pagina);
								return param;
							}
						},
						ParamSearch.class,
						SearchFilesResult.class);
				List<FileNode> files = new ArrayList<FileNode>();
				if (	resposta.getSearchFilesResult().getResParam() != null &&
						resposta.getSearchFilesResult().getResParam().getFiles() != null) {
					files = resposta.getSearchFilesResult().getResParam().getFiles();
				}
				continguts = ArxiuConversioHelper.fileNodesToFileContingutArxiu(files);
				resultatConsulta.addAll(continguts);
			}
			int fromIndex = pagina.intValue() * itemsPerPagina.intValue();
			int toIndex = (pagina.intValue() + 1) * itemsPerPagina.intValue();
			if (toIndex > resultatConsulta.size()) toIndex = resultatConsulta.size();
			return new ConsultaResultat(
					itemsPerPagina,
					new Integer(resultatConsulta.size() / itemsPerPagina.intValue()),
					new Integer(resultatConsulta.size()),
					pagina,
					resultatConsulta.size() == 0 ? new ArrayList<ContingutArxiu>() : resultatConsulta.subList(fromIndex, toIndex));
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(
			final Expedient expedient, 
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_CHILD_FILE;
		try {
			CreateChildFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateChildFile.class,
					new GeneradorParam<ParamCreateChildFile>() {
						@Override
						public ParamCreateChildFile generar() {
							ParamCreateChildFile param = new ParamCreateChildFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null,
											null,
											null,
											true));
							param.setRetrieveNode(Boolean.TRUE.toString());
							param.setParent(identificadorPare);
							return param;
						}
					},
					ParamCreateChildFile.class,
					CreateChildFileResult.class);
			Expedient expedientCreat = ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getCreateChildFileResult().getResParam(),
					VERSIO_INICIAL_CONTINGUT);
			return crearContingutArxiu(
					expedientCreat.getIdentificador(), 
					expedientCreat.getNom(),
					ContingutTipus.EXPEDIENT,
					expedientCreat.getVersio());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public List<ContingutArxiu> expedientVersions(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_VERSION_FILE;
		try {
			return expedientVersionsComu(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void expedientTancar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.CLOSE_FILE;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					CloseFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					CloseFileResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void expedientReobrir(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REOPEN_FILE;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					ReopenFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					ReopenFileResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public String expedientExportarEni(
			final String identificador) throws ArxiuException {
		String metode = Servicios.EXPORT_FILE;
		try {
			ExportFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					ExportFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					ExportFileResult.class);
			String exportBase64 = resposta.getExportFileResult().getResParam();
			if (exportBase64 != null) {
				return new String(
						Base64.decodeBase64(exportBase64));
			} else {
				return null;
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu documentCrear(
			final Document document,
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_DOC;
		try {
			comprovarAbsenciaMetadadaCsv(document.getMetadades());
			comprovarFirma(document);
			final String serieDocumental = findSerieDocumentalExpedientPare(
					document,
					identificadorPare);
			Document creat = null;
			if (DocumentEstat.ESBORRANY.equals(document.getEstat())) {
				metode = Servicios.CREATE_DRAFT;
				CreateDraftDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						CreateDraftDocument.class,
						new GeneradorParam<ParamCreateDraftDocument>() {
							@Override
							public ParamCreateDraftDocument generar() {
								ParamCreateDraftDocument param = new ParamCreateDraftDocument();
								param.setParent(identificadorPare);
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												serieDocumental,
												null,
												null,
												null,
												null,
												null,
												true));
								param.setRetrieveNode(Boolean.TRUE.toString());
								return param;
							}
						},
						ParamCreateDraftDocument.class,
						CreateDraftDocumentResult.class);
				creat = ArxiuConversioHelper.documentNodeToDocument(
						resposta.getCreateDraftDocumentResult().getResParam(),
						VERSIO_INICIAL_CONTINGUT);
			} else if (DocumentEstat.DEFINITIU.equals(document.getEstat())) {
				metode = Servicios.GENERATE_CSV;
				GenerateDocCSVResult respostaCsv = getArxiuClient().generarEnviarPeticio(
						metode,
						GenerateDocCSV.class,
						null,
						Object.class,
						GenerateDocCSVResult.class);
				final String csv = respostaCsv.getGenerateDocCSVResult().getResParam();
				metode = Servicios.CREATE_DOC;
				CreateDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						CreateDocument.class,
						new GeneradorParam<ParamCreateDocument>() {
							@Override
							public ParamCreateDocument generar() {
								ParamCreateDocument param = new ParamCreateDocument();
								param.setParent(identificadorPare);
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												serieDocumental,
												null,
												null,
												null,
												csv,
												getPropertyDefinicioCsv(),
												true));
								param.setRetrieveNode(Boolean.TRUE.toString());
								return param;
							}
						},
						ParamCreateDocument.class,
						CreateDocumentResult.class);
				creat = ArxiuConversioHelper.documentNodeToDocument(
						resposta.getCreateDocumentResult().getResParam(),
						VERSIO_INICIAL_CONTINGUT);
			} else {
				throw new ArxiuValidacioException(
						"No s'ha emplenat l'estat del document o el document no conte un estat reconegut (ESBORRANY o DEFINITIU)");
			}
			return crearContingutArxiu(
					creat.getIdentificador(), 
					creat.getNom(),
					ContingutTipus.DOCUMENT,
					creat.getVersio());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu documentModificar(
			final Document document) throws ArxiuException {
		String metode = null;
		try {
			comprovarAbsenciaMetadadaCsv(document.getMetadades());
			comprovarFirma(document);
			if (DocumentEstat.DEFINITIU.equals(document.getEstat())) {
				if (document.getContingut() != null) {
					throw new ArxiuValidacioException(
							"No és possible marcar el document com a definitiu si es vol modificar el seu contingut.");
				}
				metode = Servicios.GENERATE_CSV;
				GenerateDocCSVResult respostaCsv = getArxiuClient().generarEnviarPeticio(
						metode,
						GenerateDocCSV.class,
						null,
						Object.class,
						GenerateDocCSVResult.class);
				final String csv = respostaCsv.getGenerateDocCSVResult().getResParam();
				metode = Servicios.SET_FINAL_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						SetFinalDocument.class,
						new GeneradorParam<ParamSetDocument>() {
							@Override
							public ParamSetDocument generar() {
								ParamSetDocument param = new ParamSetDocument();
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												null,
												null, //resposta.getGetDocumentResult().getResParam().getMetadataCollection(),
												null, //resposta.getGetDocumentResult().getResParam().getAspects(),
												null,
												csv,
												getPropertyDefinicioCsv(),
												false));
								return param;
							}
						},
						ParamSetDocument.class,
						SetFinalDocumentResult.class);
			} else {
				metode = Servicios.SET_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						SetDocument.class,
						new GeneradorParam<ParamSetDocument>() {
							@Override
							public ParamSetDocument generar() {
								ParamSetDocument param = new ParamSetDocument();
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												null,
												null, //resposta.getGetDocumentResult().getResParam().getMetadataCollection(),
												null, //resposta.getGetDocumentResult().getResParam().getAspects(),
												null,
												null,
												null,
												false));
								return param;
							}
						},
						ParamSetDocument.class,
						SetDocumentResult.class);
			}
			String versio = documentDarreraVersio(
					document.getIdentificador());
			return crearContingutArxiu(
					document.getIdentificador(), 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					versio);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void documentEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_DOC;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveDocument.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveDocumentResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Document documentDetalls(
			final String identificador,
			final String versio,
			final boolean ambContingut) throws ArxiuException {
		String metode = Servicios.GET_DOC;
		try {
			String versioResposta = null;
			if (versio == null) {
				versioResposta = documentDarreraVersio(identificador);
			} else {
				versioResposta = versio;
			}
			GetDocumentResult resposta = getDocumentResult(
					identificador,
					versio,
					ambContingut);
			return ArxiuConversioHelper.documentNodeToDocument(
					resposta.getGetDocumentResult().getResParam(),
					versioResposta);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ConsultaResultat documentConsulta(
			final List<ConsultaFiltre> filtres,
			final Integer pagina,
			final Integer itemsPerPagina,
			final DocumentRepositori repositori) throws ArxiuException {
		String metode = Servicios.SEARCH_DOC;
		try {
			List<ContingutArxiu> resultatConsulta = new ArrayList<ContingutArxiu>();
			List<ContingutArxiu> continguts = null;
			while (continguts == null || continguts.size() == NUM_PAGINES_RESULTAT_CERCA) {
				final String query;
				if (repositori == null || DocumentRepositori.ENI_DOCUMENTO.equals(repositori)) {
					query = generarConsulta(
							QUERY_TYPE_ENI_DOCUMENTO,
							filtres);
				} else if (DocumentRepositori.GDIB_DOCUMENTO_MIGRADO.equals(repositori)) {
					query = generarConsulta(
							QUERY_TYPE_GDIB_DOCUMENTO_MIGRADO,
							filtres);
				} else {
					throw new ArxiuException(
							"S'ha produit un error cridant el mètode " + metode + ":" +
							"Repositori de documents desconegut (" + repositori + ")");
				}
				SearchDocsResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						SearchDocs.class,
						new GeneradorParam<ParamSearch>() {
							@Override
							public ParamSearch generar() {
								ParamSearch param = new ParamSearch();
								param.setQuery(query);
								param.setPageNumber(pagina);
								return param;
							}
						},
						ParamSearch.class,
						SearchDocsResult.class);
				List<DocumentNode> documents = new ArrayList<DocumentNode>();
				if (	resposta.getSearchDocumentsResult().getResParam() != null &&
						resposta.getSearchDocumentsResult().getResParam().getDocuments() != null) {
					documents = resposta.getSearchDocumentsResult().getResParam().getDocuments();
				}
				continguts = ArxiuConversioHelper.fileNodeToDocumentContingut(documents);
				resultatConsulta.addAll(continguts);
			}
			int fromIndex = pagina.intValue() * itemsPerPagina.intValue();
			int toIndex = (pagina.intValue() + 1) * itemsPerPagina.intValue();
			if (toIndex > resultatConsulta.size()) toIndex = resultatConsulta.size();
			return new ConsultaResultat(
					itemsPerPagina,
					new Integer(resultatConsulta.size() / itemsPerPagina.intValue()),
					new Integer(resultatConsulta.size()),
					pagina,
					resultatConsulta.size() == 0 ? new ArrayList<ContingutArxiu>() : resultatConsulta.subList(fromIndex, toIndex));
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public List<ContingutArxiu> documentVersions(
			String identificador) throws ArxiuException {
		String metode = Servicios.GET_VERSION_DOC;
		try {
			return documentVersionsComu(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu documentCopiar(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		String metode = Servicios.COPY_DOC;
		try {
			CopyDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CopyDocument.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					CopyDocumentResult.class);
			resposta.getCopyDocumentResult().getResParam();
			return crearContingutArxiu(
					resposta.getCopyDocumentResult().getResParam(), 
					null,
					ContingutTipus.DOCUMENT,
					VERSIO_INICIAL_CONTINGUT);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void documentMoure(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		String metode = null;
		try {
			GetDocumentResult resposta = getDocumentResult(
					identificador,
					null,
					false);
			Document documentOrigen = ArxiuConversioHelper.documentNodeToDocument(
					resposta.getGetDocumentResult().getResParam(),
					null);
			final String serieDocumentalOrigen = findSerieDocumentalExpedientPare(
					documentOrigen,
					null);
			final String serieDocumentalDesti = findSerieDocumentalExpedientPare(
					null,
					identificadorDesti);
			if (serieDocumentalOrigen.equals(serieDocumentalDesti)) {
				metode = Servicios.MOVE_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						MoveDocument.class,
						new GeneradorParam<ParamNodeID_TargetParent>() {
							@Override
							public ParamNodeID_TargetParent generar() {
								ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
								param.setNodeId(identificador);
								param.setTargetParent(identificadorDesti);
								return param;
							}
						},
						ParamNodeID_TargetParent.class,
						MoveDocumentResult.class);
			} else {
				metode = Servicios.DISPATCH_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						DispatchDocument.class,
						new GeneradorParam<ParamDispatchDocument>() {
							@Override
							public ParamDispatchDocument generar() {
								ParamDispatchDocument param = new ParamDispatchDocument();
								param.setSourceNodeId(identificador);
								TargetNode targetNode = new TargetNode();
								targetNode.setId(identificadorDesti);
								targetNode.setTargetType("ttttttargetType");
								DocClassification docClassification = new DocClassification();
								docClassification.setSerie(serieDocumentalDesti);
								//docClassification.setType(type);
								targetNode.setDocClassification(docClassification);
								param.setTargetNode(targetNode);
								return param;
							}
						},
						ParamDispatchDocument.class,
						DispatchDocumentResult.class);
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public String documentExportarEni(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_ENIDOC;
		try {
			GetENIDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetENIDocument.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					GetENIDocumentResult.class);
			String exportBase64 = resposta.getGetENIDocResult().getResParam();
			if (exportBase64 != null) {
				return new String(
						Base64.decodeBase64(exportBase64));
			} else {
				return null;
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public DocumentContingut documentImprimible(
			final String identificador) throws ArxiuException {
		/*
		 * Les URLs de consulta son les següents:
		 *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 *   https://intranet.caib.es/concsv/rest/printable/CSV?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 * A on:
		 *   - CSV és el CSV del document a consultar [OBLIGATORI]
		 *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
		 *   - METADADA_1 és la primera metadada [OPCIONAL]
		 *   - METADADA_2 és la segona metadada [OPCIONAL]
		 *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
		 * Només es obligatori informa la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
		 */
		try {
			InputStream is = generarVersioImprimible(
					identificador,
					null, // metadada 1
					null, // metadada 2
					null); // marca d'aigua
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document",
					ex);
		}
		
	}

	@Override
	public ContingutArxiu carpetaCrear(
			final Carpeta carpeta,
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_FOLDER;
		try {
			CreateFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateFolder.class,
					new GeneradorParam<ParamCreateFolder>() {
						@Override
						public ParamCreateFolder generar() {
							ParamCreateFolder param = new ParamCreateFolder();
							param.setParent(identificadorPare);
							param.setFolder(
									ArxiuConversioHelper.toFolderNode(
											null,
											carpeta.getNom()));
							param.setRetrieveNode(Boolean.TRUE.toString());
							param.setRetrieveNode(Boolean.TRUE.toString());
							return param;
						}
					},
					ParamCreateFolder.class,
					CreateFolderResult.class);
			Carpeta carpetaCreada = ArxiuConversioHelper.folderNodeToCarpeta(
					resposta.getCreateFolderResult().getResParam());
			return crearContingutArxiu(
					carpetaCreada.getIdentificador(), 
					carpetaCreada.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaModificar(
			final Carpeta carpeta) throws ArxiuException {
		String metode = Servicios.SET_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					SetFolder.class,
					new GeneradorParam<ParamSetFolder>() {
						@Override
						public ParamSetFolder generar() {
							ParamSetFolder param = new ParamSetFolder();
							param.setFolder(ArxiuConversioHelper.toFolderNode(
									carpeta.getIdentificador(),
									carpeta.getNom()));
							return param;
						}
					},
					ParamSetFolder.class,
					SetFolderResult.class);
			return crearContingutArxiu(
					carpeta.getIdentificador(),
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void carpetaEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveFolder.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveFolderResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Carpeta carpetaDetalls(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_FOLDER;
		try {
			GetFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFolder.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					GetFolderResult.class);
			return ArxiuConversioHelper.folderNodeToCarpeta(
					resposta.getGetFolderResult().getResParam());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaCopiar(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		//String metode = Servicios.COPY_FOLDER;
		String metode = "/services/copyFolder";
		try {
			CopyFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CopyFolder.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					CopyFolderResult.class);
			return crearContingutArxiu(
					resposta.getCopyFolderResult().getResParam(), 
					null,
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void carpetaMoure(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		String metode = Servicios.MOVE_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					MoveFolder.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					MoveFolderResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public boolean suportaVersionatExpedient() {
		return true;
	}

	@Override
	public boolean suportaVersionatDocument() {
		return true;
	}

	@Override
	public boolean suportaVersionatCarpeta() {
		return false;
	}

	@Override
	public boolean suportaMetadadesNti() {
		return true;
	}

	@Override
	public boolean generaIdentificadorNti() {
		return true;
	}



	private ContingutArxiu crearContingutArxiu(
			String identificador, 
			String nom,
			ContingutTipus tipus,
			String versio) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		return informacioItem;
	}

	private List<ContingutArxiu> expedientVersionsComu(
			final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = Servicios.GET_VERSION_FILE;
		GetFileVersionListResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				GetFileVersionList.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						param.setNodeId(identificador);
						return param;
					}
				},
				ParamNodeId.class,
				GetFileVersionListResult.class);
		List<VersionNode> versions = resposta.getGetFileVersionListResult().getResParam();
		Collections.sort(
				versions,
				new Comparator<VersionNode>() {
					public int compare(VersionNode vn1, VersionNode vn2) {
						return vn1.getDate().compareTo(vn2.getDate());
					}
				});
		List<ContingutArxiu> continguts = new ArrayList<ContingutArxiu>();
		for (VersionNode versio: versions) {
			continguts.add(
					ArxiuConversioHelper.crearContingutArxiu(
							identificador,
							null,
							ContingutTipus.DOCUMENT,
							String.valueOf(versio.getId())));
		}
		return continguts;
	}
	private String expedientDarreraVersio(
			String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String darreraVersio = null;
		List<ContingutArxiu> versions = expedientVersionsComu(
				identificador);
		if (versions != null && !versions.isEmpty()) {
			darreraVersio = versions.get(versions.size() - 1).getVersio();
		}
		return darreraVersio;
	}

	private List<ContingutArxiu> documentVersionsComu(
			final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = Servicios.GET_VERSION_DOC;
		GetDocVersionListResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				GetDocVersionList.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						param.setNodeId(identificador);
						return param;
					}
				},
				ParamNodeId.class,
				GetDocVersionListResult.class);
		List<VersionNode> versions = resposta.getGetDocVersionListResult().getResParam();
		Collections.sort(
				versions,
				new Comparator<VersionNode>() {
					public int compare(VersionNode vn1, VersionNode vn2) {
						return vn1.getDate().compareTo(vn2.getDate());
					}
				});
		List<ContingutArxiu> continguts  = new ArrayList<ContingutArxiu>();
		for (VersionNode versio: versions) {
			continguts.add(
					ArxiuConversioHelper.crearContingutArxiu(
							identificador,
							null,
							ContingutTipus.DOCUMENT,
							String.valueOf(versio.getId())));
		}
		return continguts;
	}
	private String documentDarreraVersio(
			String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String darreraVersio = null;
		List<ContingutArxiu> versions = documentVersionsComu(
				identificador);
		if (versions != null && !versions.isEmpty()) {
			darreraVersio = versions.get(versions.size() - 1).getVersio();
		}
		return darreraVersio;
	}

	private String findSerieDocumentalExpedientPare(
			Document document,
			String identificadorPare) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		if (document != null && document.getMetadades() != null && document.getMetadades().getMetadadesAddicionals() != null) {
			Map<String, Object> metadadesAddicionals = document.getMetadades().getMetadadesAddicionals();
			Object serieDocumental = metadadesAddicionals.get(MetadatosDocumento.CODIGO_CLASIFICACION);
			if (serieDocumental != null) {
				return (String)serieDocumental;
			}
		}
		GetFileResult expedientPare = findExpedientPare(identificadorPare);
		List<Metadata> metadatas = expedientPare.getGetFileResult().getResParam().getMetadataCollection();
		String serieDocumental = null;
		for (Metadata metadata: metadatas) {
			if (MetadatosDocumento.CODIGO_CLASIFICACION.equals(metadata.getQname())) {
				serieDocumental = (String)metadata.getValue();
				break;
			}
		}
		if (serieDocumental != null) {
			return serieDocumental;
		} else {
			throw new ArxiuValidacioException(
					"No s'ha pogut trobar la sèrie documental de l'expedient pare (identificadorPare=" + identificadorPare + ").");
		}
	}

	private GetFileResult findExpedientPare(
			String identificadorPare) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String identificadorActual = identificadorPare;
		GetFileResult getFileResult = null;
		TiposObjetoSGD tipoObjeto;
		do {
			tipoObjeto = null;
			String identificador = null;
			try {
				getFileResult = getFileResult(
						identificadorActual,
						null);
				if (	getFileResult != null && 
						getFileResult.getGetFileResult() != null &&
						getFileResult.getGetFileResult().getResParam() != null) {
					tipoObjeto = getFileResult.getGetFileResult().getResParam().getType();
					identificador = getFileResult.getGetFileResult().getResParam().getId();
				}
			} catch (Exception ex) {
				GetFolderResult getFolderResult = getFolderResult(
						identificadorActual);
				if (	getFolderResult != null && 
						getFolderResult.getGetFolderResult() != null &&
						getFolderResult.getGetFolderResult().getResParam() != null) {
					tipoObjeto = getFolderResult.getGetFolderResult().getResParam().getType();
					identificador = getFolderResult.getGetFolderResult().getResParam().getId();
				}
			}
			if (tipoObjeto == null) {
				throw new ArxiuValidacioException(
						"No s'ha pogut trobar l'expedient superior per al contingut amb identificador " + identificadorPare + ". " +
						"No s'ha pogut trobar el tipus d'objecte per al contingut amb identificador " + identificadorActual + ".");
			}
			identificadorActual = identificador;
		} while (!TiposObjetoSGD.EXPEDIENTE.equals(tipoObjeto));
		return getFileResult;
	}

	private GetFileResult getFileResult(
			final String identificador,
			final String versio) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = Servicios.GET_FILE;
		return getArxiuClient().generarEnviarPeticio(
				metode,
				GetFile.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						String identificadorAmbVersio;
						if (versio != null) {
							identificadorAmbVersio = versio + "@" + identificador;
						} else {
							identificadorAmbVersio = identificador;
						}
						param.setNodeId(identificadorAmbVersio);
						return param;
					}
				},
				ParamNodeId.class,
				GetFileResult.class);
	}

	private GetDocumentResult getDocumentResult(
			final String identificador,
			final String versio,
			final boolean ambContingut) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = Servicios.GET_DOC;
		return getArxiuClient().generarEnviarPeticio(
				metode,
				GetDocument.class,
				new GeneradorParam<ParamGetDocument>() {
					@Override
					public ParamGetDocument generar() {
						ParamGetDocument param = new ParamGetDocument();
						DocumentId documentId = new DocumentId();
						String identificadorAmbVersio;
						if (versio != null) {
							identificadorAmbVersio = versio + "@" + identificador;
						} else {
							identificadorAmbVersio = identificador;
						}
						documentId.setNodeId(identificadorAmbVersio);
						param.setDocumentId(documentId);
						param.setContent(new Boolean(ambContingut).toString());
						return param;
					}
				},
				ParamGetDocument.class,
				GetDocumentResult.class);
	}

	private GetFolderResult getFolderResult(
			final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = Servicios.GET_FOLDER;
		return getArxiuClient().generarEnviarPeticio(
				metode,
				GetFolder.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						param.setNodeId(identificador);
						return param;
					}
				},
				ParamNodeId.class,
				GetFolderResult.class);
	}

	private void comprovarAbsenciaMetadadaCsv(
			DocumentMetadades metadades) {
		if (metadades != null && metadades.getMetadadesAddicionals() != null) {
			if (metadades.getMetadadesAddicionals().containsKey(MetadatosDocumento.CSV)) {
				throw new ArxiuValidacioException(
						"No és possible especificar la metadada " + MetadatosDocumento.CSV + " per als documents. " +
						"Aquesta metadada la gestiona l'arxiu de forma automàtica.");
			}
			if (metadades.getMetadadesAddicionals().containsKey(MetadatosDocumento.DEF_CSV)) {
				throw new ArxiuValidacioException(
						"No és possible especificar la metadada " + MetadatosDocumento.DEF_CSV + " per als documents. " +
						"Aquesta metadada la gestiona l'arxiu de forma automàtica.");
			}
		}
	}

	private void comprovarFirma(
			final Document document) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		if (document.getFirmes() != null && !document.getFirmes().isEmpty()) {
			FirmaTipus firmaTipus = null;
			for (Firma firma: document.getFirmes()) {
				if (firmaTipus == null) {
					firmaTipus = firma.getTipus();
				} else {
					if (!firmaTipus.equals(firma.getTipus())) {
						throw new ArxiuValidacioException(
								"El document no pot contenir firmes de diferents tipus");
					}
				}
			}
			boolean formatComprovat = false;
			if (document.getMetadades() != null && document.getMetadades().getFormat() != null) {
				if (FirmaTipus.PADES.equals(firmaTipus)) {
					formatComprovat = true;
					if (!DocumentFormat.PDF.equals(document.getMetadades().getFormat())) {
						throw new ArxiuValidacioException(
								"Un document que no te el format PDF (" + document.getMetadades().getFormat() + ") no pot tenir una firma de tipus PADES");
					}
					if (document.getFirmes().size() > 1) {
						throw new ArxiuValidacioException(
								"Només es pot especificar una firma de tipus PAdES");
					}
					Firma firma = document.getFirmes().get(0);
					if (document.getContingut() != null && firma.getContingut() != null) {
						throw new ArxiuValidacioException(
								"Al firmar un document amb firma PAdES no es pot especificar a la vegada el contingut del document i el contingut de la firma");
					}
				}
			}
			if (document.getIdentificador() != null) {
				String metode = Servicios.GET_DOC;
				GetDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						GetDocument.class,
						new GeneradorParam<ParamGetDocument>() {
							@Override
							public ParamGetDocument generar() {
								ParamGetDocument param = new ParamGetDocument();
								DocumentId documentId = new DocumentId();
								documentId.setNodeId(document.getIdentificador());
								param.setDocumentId(documentId);
								param.setContent(new Boolean(false).toString());
								return param;
							}
						},
						ParamGetDocument.class,
						GetDocumentResult.class);
				Document documentResposta = ArxiuConversioHelper.documentNodeToDocument(
						resposta.getGetDocumentResult().getResParam(),
						null);
				if (firmaTipus != null && documentResposta.getFirmes() != null && !documentResposta.getFirmes().isEmpty()) {
					Firma primeraFirma = documentResposta.getFirmes().get(0);
					if (!firmaTipus.equals(primeraFirma.getTipus())) {
						throw new ArxiuValidacioException(
								"El document de l'arxiu ja està firmat i el tipus de firma especificat (" + firmaTipus + ") no coicideix amb l'existent a l'arxiu (" + primeraFirma.getTipus() + ")");
					}
				}
				if (!formatComprovat && documentResposta.getMetadades() != null && documentResposta.getMetadades().getFormat() != null) {
					if (FirmaTipus.PADES.equals(firmaTipus)) {
						if (!DocumentFormat.PDF.equals(documentResposta.getMetadades().getFormat())) {
							throw new ArxiuValidacioException(
									"Un document que no te el format PDF (" + documentResposta.getMetadades().getFormat() + ") no pot tenir una firma de tipus PADES");
						}
					}
				}
			}
		}
	}
	
	private InputStream generarVersioImprimible(
			String identificador,
			String metadada1,
			String metadada2,
			String marcaAigua) throws IOException {
		String url = getPropertyConversioImprimibleUrlUuid();
		WebResource webResource;
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClient().
					resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClient().
					resource(url + "/" + identificador);
		}
		if (metadada1 != null) {
			webResource.queryParam("metadata1", metadada1);
		}
		if (metadada2 != null) {
			webResource.queryParam("metadata2", metadada2);
		}
		if (marcaAigua != null) {
			webResource.queryParam("watermark", marcaAigua);
		}
		return webResource.get(InputStream.class);
	}

	
	public static String generarConsulta(
			String queryType,
			List<ConsultaFiltre> filtres) throws ArxiuException {
		StringBuilder query = new StringBuilder();
		query.append("+TYPE:");
		query.append(queryType);
		query.append(" ");
		for (int i = 0; i < filtres.size(); i++) {
			ConsultaFiltre filtre = filtres.get(i);
			String metadada = filtre.getMetadada();
			String valor1 = filtre.getValorOperacio1();
			String valor2 = filtre.getValorOperacio2();
			String metadadaPerFiltre = metadada.replace(":", "\\:");
			if (filtre.getOperacio() != null) {
				query.append("+@");
				switch(filtre.getOperacio()) {
					case IGUAL:
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case CONTE:
						query.append(metadadaPerFiltre);
						query.append(":*" + valor1 + "*");
						break;
					case MAJOR:
						query.append(metadadaPerFiltre);
						query.append(":[" + valor1 + " TO *] -");
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case MENOR:
						query.append(metadadaPerFiltre);
						query.append(":[* TO " + valor1 + "] -");
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case ENTRE:
						query.append(metadadaPerFiltre);
						query.append(":[" + valor1 + " TO " + valor2 + "]");
						break;
				}
				if (i < filtres.size()-1) {
					query.append(" AND ");
				}
			} else {
				throw new ArxiuException("No s'ha definit cap operació pel filtre de la metadada " + metadada);
			}
		}
		return query.toString();
	}

	private ArxiuCaibClient getArxiuClient() {
		if (arxiuClient == null) {
			arxiuClient = new ArxiuCaibClient(
					getPropertyBaseUrl(),
					getPropertyAplicacioCodi(),
					getPropertyUsuari(),
					getPropertyContrasenya(),
					getPropertyTimeoutConnect(),
					getPropertyTimeoutRead());
		}
		return arxiuClient;
	}
	private Client getVersioImprimibleClient() {
		if (versioImprimibleClient == null) {
			versioImprimibleClient = Client.create();
			versioImprimibleClient.setConnectTimeout(
					getPropertyTimeoutConnect());
			versioImprimibleClient.setReadTimeout(
					getPropertyTimeoutRead());
			String usuari = getPropertyConversioImprimibleUsuari();
			String contrasenya = getPropertyConversioImprimibleContrasenya();
			if (usuari != null) {
				versioImprimibleClient.addFilter(
						new HTTPBasicAuthFilter(usuari, contrasenya));
			}
		}
		return versioImprimibleClient;
	}

	private String getPropertyBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "base.url");
	}
	private String getPropertyAplicacioCodi() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "aplicacio.codi");
	}
	private String getPropertyUsuari() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "usuari");
	}
	private String getPropertyContrasenya() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "contrasenya");
	}
	private String getPropertyDefinicioCsv() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "csv.definicio");
	}
	private String getPropertyConversioImprimibleUrlCsv() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.csv");
	}
	private String getPropertyConversioImprimibleUrlUuid() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.uuid");
	}
	private String getPropertyConversioImprimibleUsuari() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari");
	}
	private String getPropertyConversioImprimibleContrasenya() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya");
	}
	private int getPropertyTimeoutConnect() {
		String timeout = PropertiesHelper.getProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.connect",
				JERSEY_TIMEOUT_CONNECT);
		return Integer.parseInt(timeout);
	}
	private int getPropertyTimeoutRead() {
		String timeout = PropertiesHelper.getProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.read",
				JERSEY_TIMEOUT_READ);
		return Integer.parseInt(timeout);
	}

}