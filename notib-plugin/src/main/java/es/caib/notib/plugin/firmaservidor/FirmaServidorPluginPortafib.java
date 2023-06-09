package es.caib.notib.plugin.firmaservidor;

import es.caib.notib.plugin.SistemaExternException;
import org.apache.commons.io.FileUtils;
import org.fundaciobit.plugins.signature.api.*;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.portafib.PortaFIBSignatureServerPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * Implementació del plugin de firma en servidor emprant PortaFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginPortafib implements FirmaServidorPlugin {

	private static final String PROPERTIES_BASE = "es.caib.notib.plugin.firmaservidor.portafib.";
	private static final String FIRMASERVIDOR_TMPDIR = "avacat_firmaservidor";

	private ISignatureServerPlugin plugin;
	private String tempDirPath;

	private final Properties properties;

	public FirmaServidorPluginPortafib(Properties properties) {

		super();
		plugin = new PortaFIBSignatureServerPlugin(PROPERTIES_BASE, properties);
		this.properties = properties;
		var tempDir = System.getProperty("java.io.tmpdir");
		final var base = new File(tempDir, FIRMASERVIDOR_TMPDIR);
		base.mkdirs();
		tempDirPath = base.getAbsolutePath();
	}

	@Override
	public byte[] firmar(String nom, String motiu, byte[] contingut, TipusFirma tipusFirma, String idioma) throws SistemaExternException {

		File sourceFile = null;
		File destFile = null;
		var uuid = UUID.randomUUID().toString();
		try {
			// Guarda el contingut en un arxiu temporal
			sourceFile = getArxiuTemporal(uuid, contingut);
			var sourcePath = sourceFile.getAbsolutePath();
			var destPath = sourcePath + "_PADES.pdf";
			String signType;
			int signMode;
			if (tipusFirma == TipusFirma.CADES) {
				signType = FileInfoSignature.SIGN_TYPE_CADES;
				signMode = FileInfoSignature.SIGN_MODE_EXPLICIT; // Detached
			} else if (tipusFirma == TipusFirma.XADES) {
				signType = FileInfoSignature.SIGN_TYPE_XADES;
				signMode = FileInfoSignature.SIGN_MODE_EXPLICIT; // Detached
			} else {
				// Per defecte es suposa el tipus de firma PAdES
				signType = FileInfoSignature.SIGN_TYPE_PADES;
				signMode = FileInfoSignature.SIGN_MODE_IMPLICIT; // Attached
			}
			var userRequiresTimeStamp = false;
			signFile(uuid, sourcePath, destPath, signType, signMode, motiu, idioma, userRequiresTimeStamp);
			destFile = new File(destPath);
			return FileUtils.readFileToByteArray(destFile);
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			// Esborra els arxius temporals
			if (sourceFile != null && sourceFile.exists()) {
				sourceFile.delete();
			}
			if (destFile != null && destFile.exists()) {
				destFile.delete();
			}
		}
	}

	private File getArxiuTemporal(String uuid, byte[] contingut) throws IOException {

		// Crea l'arxiu temporal
		var fitxerTmp = new File(tempDirPath, uuid + "_original");
		fitxerTmp.getParentFile().mkdirs();
		// Escriu el contingut al fitxer temporal
		FileUtils.writeByteArrayToFile(fitxerTmp, contingut);
		return fitxerTmp;
	}

	private void signFile(String uuid, String sourcePath, String destPath, String signType, int signMode, String reason, String language, boolean userRequiresTimeStamp) throws Exception {

		// Informació comú per a totes les signatures
		var filtreCertificats = "";
		var username = properties.getProperty(PROPERTIES_BASE + "username", null);
		String administrationID = null; // No te sentit en API Firma En Servidor
		var commonInfoSignature = new CommonInfoSignature(language, filtreCertificats, username, administrationID);
		var source = new File(sourcePath);
		var fileName = source.getName();
		var location = properties.getProperty(PROPERTIES_BASE + "location", "Palma");
		var signerEmail = properties.getProperty(PROPERTIES_BASE + "signerEmail", "suport@caib.es");
		var signNumber = 1;
		var signAlgorithm = FileInfoSignature.SIGN_ALGORITHM_SHA1;
		var signaturesTableLocation = FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT;
		PdfVisibleSignature pdfInfoSignature = null;
		/*
		 * IRubricGenerator rubricGenerator = null; if
		 * (FileInfoSignature.SIGN_TYPE_PADES.equals(signType) && rubricGenerator !=
		 * null) { signaturesTableLocation =
		 * FileInfoSignature.SIGNATURESTABLELOCATION_LASTPAGE; PdfRubricRectangle
		 * pdfRubricRectangle = new PdfRubricRectangle(106, 650, 555, 710);
		 * pdfInfoSignature = new PdfVisibleSignature(pdfRubricRectangle,
		 * rubricGenerator); }
		 */
		final ITimeStampGenerator timeStampGenerator = null;
		// Valors per defecte
		final SignaturesTableHeader signaturesTableHeader = null;
		final SecureVerificationCodeStampInfo csvStampInfo = null;
		var signId = "999";
		var fileInfo = new FileInfoSignature(signId, source, FileInfoSignature.PDF_MIME_TYPE, fileName, reason, location, signerEmail, signNumber,
				language, signType, signAlgorithm, signMode, signaturesTableLocation, signaturesTableHeader, pdfInfoSignature, csvStampInfo, userRequiresTimeStamp, timeStampGenerator);

		final var signaturesSetID = String.valueOf(System.currentTimeMillis());
		var signaturesSetRequest = new SignaturesSet(signaturesSetID + "_" + uuid, commonInfoSignature, new FileInfoSignature[] { fileInfo });
		// Signa el document
		String timestampUrlBase = null;
		var signaturesSetResponse = plugin.signDocuments(signaturesSetRequest, timestampUrlBase, null);
		var signaturesSetStatus = signaturesSetResponse.getStatusSignaturesSet();
		if (signaturesSetStatus.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
			// Error en el procés de firma
			var exceptionMessage = "Error en la firma de servidor: [" + signaturesSetStatus.getStatus() + "] " + signaturesSetStatus.getErrorMsg();
			if (signaturesSetStatus.getErrorException() != null) {
				throw new SistemaExternException(exceptionMessage, signaturesSetStatus.getErrorException());
			}
			throw new SistemaExternException(exceptionMessage);
		}
		var fis = signaturesSetResponse.getFileInfoSignatureArray()[0];
		var status = fis.getStatusSignature();
		if (status.getStatus() == StatusSignaturesSet.STATUS_FINAL_OK) {
			// Document firmat correctament
			status.getSignedData().renameTo(new File(destPath));
			return;
		}
		// Error en el document a firmar
		String exceptionMessage = "Error al firmar en servidor el document (status=" + status.getStatus() + "): " +
				status.getErrorMsg();
		if (signaturesSetStatus.getErrorException() != null) {
			throw new SistemaExternException(exceptionMessage, signaturesSetStatus.getErrorException());
		}
		throw new SistemaExternException(exceptionMessage);
	}
}
