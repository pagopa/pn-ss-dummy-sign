package it.pagopa.pn.ss.dummy.sign.service;

import it.pagopa.pn.ss.dummy.sign.pojo.SignatureLevel;
import it.pagopa.pn.library.exceptions.PnSpapiPermanentErrorException;
import it.pagopa.pn.library.sign.pojo.PnSignDocumentResponse;
import it.pagopa.pn.ss.dummy.sign.pojo.SignatureFormat;
import it.pagopa.pn.library.sign.service.PnSignService;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CustomLog
@NoArgsConstructor
@AllArgsConstructor
public class PnSignServiceImpl implements PnSignService {

    private String apiEndpoint;

    /**
     * Sign a PDF document with PAdES format
     * @param fileBytes the PDF document to sign
     * @param timestamping if true, the signature will be timestamped
     * @return a {@link PnSignDocumentResponse} with the signed document
     */
    @Override
    public Mono<PnSignDocumentResponse> signPdfDocument(byte[] fileBytes, Boolean timestamping) {
        return applySignature(SignatureFormat.PADES, timestamping, fileBytes);
    }

    /**
     * Sign an XML document with XAdES format
     * @param fileBytes the XML document to sign
     * @param timestamping if true, the signature will be timestamped
     * @return a {@link PnSignDocumentResponse} with the signed document
     */
    @Override
    public Mono<PnSignDocumentResponse> signXmlDocument(byte[] fileBytes, Boolean timestamping) {
        return applySignature(SignatureFormat.XADES, timestamping, fileBytes);
    }

    /**
     * Sign a generic document with CAdES format
     * @param fileBytes the document to sign
     * @param timestamping if true, the signature will be timestamped
     * @return a {@link PnSignDocumentResponse} with the signed document
     */
    @Override
    public Mono<PnSignDocumentResponse> pkcs7Signature(byte[] fileBytes, Boolean timestamping) {
        return applySignature(SignatureFormat.CADES, timestamping, fileBytes);
    }

    /**
     * Apply the signature to the document
     * @param format the signature format
     * @param timestamping if true, the signature will be timestamped
     * @param fileBytes the document to sign
     * @return a {@link PnSignDocumentResponse} with the signed document
     */
    private Mono<PnSignDocumentResponse> applySignature(String format, Boolean timestamping, byte[] fileBytes) {
        if(fileBytes == null || fileBytes.length == 0) {
            return Mono.error(new PnSpapiPermanentErrorException("fileBytes cannot be null or empty"));
        }
        String level = timestamping ? SignatureLevel.TIMESTAMP : SignatureLevel.BASIC;
        var requestId = UUID.randomUUID().toString();
        String message = "Dummy - Invoked [sign{}] request {} with params format={}, level={}, requestBody length: {} bytes.";
        log.info(message, format, requestId, format, level, fileBytes.length);
        return Mono.just(new PnSignDocumentResponse(fileBytes));
    }

}
