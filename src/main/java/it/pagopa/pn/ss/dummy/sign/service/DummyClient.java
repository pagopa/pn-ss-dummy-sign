package it.pagopa.pn.ss.dummy.sign.service;

import it.pagopa.pn.library.sign.pojo.PnSignDocumentResponse;
import lombok.CustomLog;
import reactor.core.publisher.Mono;

@CustomLog
public class DummyClient {

    private DummyClient() {
        throw new IllegalStateException("Utility class");
    }


    public static Mono<PnSignDocumentResponse> sign(String apiEndpoint,
                                                    String requestId,
                                                    byte[] data,
                                                    String format,
                                                    String level){
        log.info("Dummy - sign invoked with requestId: {}, format: {}, level: {}, api endpoint (will be ignored): {}", requestId, format, level, apiEndpoint);
        return Mono.just(new PnSignDocumentResponse(data));
    }

}
