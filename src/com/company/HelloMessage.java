package com.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;

public class HelloMessage {

    private String version;

    private String clientRandom;

    private String sessionId;

    private String cipherSuite;

    private String compressionMethod;

    public HelloMessage()
    {
        this.version = "3.0";
        this.clientRandom =  new SecureRandom().toString();
        this.sessionId = "2734";
        this.cipherSuite = "DES";
        this.compressionMethod = "SHA1";
    }

    public HelloMessage(@JsonProperty("version") String version,
                        @JsonProperty("clientRandom") String clientRandom,
                        @JsonProperty("sessionId") String sessionId,
                        @JsonProperty("cipherSuite") String cipherSuite,
                        @JsonProperty("compressionMethod") String compressionMethod)
    {
        this.version = version;
        this.clientRandom = clientRandom;
        this.sessionId = sessionId;
        this.cipherSuite = cipherSuite;
        this.compressionMethod = compressionMethod;
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClientRandom()
    {
        return this.clientRandom;
    }

    public void setClientRandom(String clientRandom) {
        this.clientRandom = clientRandom;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCipherSuite()
    {
        return this.cipherSuite;
    }

    public void setCipherSuite(String cipherSuite) {
        this.cipherSuite = cipherSuite;
    }

    public String getCompressionMethod()
    {
        return this.compressionMethod;
    }

    public void setCompressionMethod(String compressionMethod) {
        this.compressionMethod = compressionMethod;
    }


}
