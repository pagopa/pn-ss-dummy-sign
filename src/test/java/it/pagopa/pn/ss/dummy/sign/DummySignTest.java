package it.pagopa.pn.ss.dummy.sign;

import it.pagopa.pn.library.exceptions.PnSpapiPermanentErrorException;
import it.pagopa.pn.library.sign.pojo.PnSignDocumentResponse;
import it.pagopa.pn.ss.dummy.sign.pojo.SignatureFormat;
import it.pagopa.pn.ss.dummy.sign.service.PnDummySignServiceImpl;
import lombok.CustomLog;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@CustomLog
@SpringBootTest(classes = DummySignTestApplication.class)
public class DummySignTest {

    @SpyBean
    private PnDummySignServiceImpl service;

    private static byte[] pdfBytes;
    private static byte[] xmlBytes;

    @BeforeAll
    static void setup() {
        try {
            pdfBytes = FileUtils.readFileToByteArray(new File("src/test/resources/in/sample.pdf"));
            xmlBytes = FileUtils.readFileToByteArray(new File("src/test/resources/in/sample.xml"));
        } catch (IOException e) {
            log.error("Error reading file", e);
        }
    }

    @ParameterizedTest
    @MethodSource("getDummySignTestOkValues")
    @DisplayName("Test sign document with valid data with different signature formats")
    void dummySignTestOk(SignatureFormat signatureFormat, byte[] docBytes) {
        Mono<PnSignDocumentResponse> pnSignDocumentResponseMono = executeSign(service, docBytes, signatureFormat, false);
        assert pnSignDocumentResponseMono != null;
        StepVerifier.create(pnSignDocumentResponseMono)
                .assertNext(response -> {
                    log.info("Response: {}", response);
                    Assertions.assertNotNull(response);
                    Assertions.assertNotNull(response.getSignedDocument());
                    Assertions.assertTrue(response.getSignedDocument().length > 0);
                    Assertions.assertEquals(response.getSignedDocument(), docBytes);
                }).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("getDummySignTestKoValues")
    @DisplayName("Test sign document with invalid data in pdf and xml format")
    void dummySignTestKo(SignatureFormat format, byte[] docBytes) {
        Mono<PnSignDocumentResponse> pnSignDocumentResponseMono = executeSign(service, docBytes, format, false);
        assert pnSignDocumentResponseMono != null;
        StepVerifier.create(pnSignDocumentResponseMono)
                .expectErrorMatches(throwable -> throwable instanceof PnSpapiPermanentErrorException && throwable.getMessage().equals("fileBytes cannot be null or empty"))
                .verify();
    }

    public static Stream<Arguments> getDummySignTestKoValues() {
        byte[] emptyDocBytes = new byte[0];

        return Stream.of(
                Arguments.of(SignatureFormat.CADES, null),
                Arguments.of(SignatureFormat.XADES, null),
                Arguments.of(SignatureFormat.PADES, null),
                Arguments.of(SignatureFormat.CADES, emptyDocBytes),
                Arguments.of(SignatureFormat.XADES, emptyDocBytes),
                Arguments.of(SignatureFormat.PADES, emptyDocBytes)
        );
    }

    public static Stream<Arguments> getDummySignTestOkValues() {
        return Stream.of(
                Arguments.of(SignatureFormat.PADES, pdfBytes),
                Arguments.of(SignatureFormat.XADES, xmlBytes),
                Arguments.of(SignatureFormat.CADES, xmlBytes)
        );
    }

    private static Mono<PnSignDocumentResponse> executeSign(PnDummySignServiceImpl service, byte[] docBytes, SignatureFormat signatureFormat, boolean timestamping) {
        return switch (signatureFormat) {
            case PADES -> service.signPdfDocument(docBytes, timestamping);
            case XADES -> service.signXmlDocument(docBytes, timestamping);
            case CADES -> service.pkcs7Signature(docBytes, timestamping);
        };
    }
}