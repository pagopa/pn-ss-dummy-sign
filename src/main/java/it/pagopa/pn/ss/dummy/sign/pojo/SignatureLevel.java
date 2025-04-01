package it.pagopa.pn.ss.dummy.sign.pojo;


import lombok.Getter;

@Getter
public enum SignatureLevel {
    BASIC("BES"),
    TIMESTAMP("T");

    private final String value;

    SignatureLevel(String value) {
        this.value = value;
    }

}
