package com.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.SecureRandom;
import java.util.Date;

public class Certificate {

    private String version;

    private int serialNumber;

    private String signAlgorithmIdentifier;

    private String issuerName;

    private String publicKeyInformation;

    private Date expirationDate;

    private String signature;

    public Certificate()
    {
        this.version = "3.0";
        this.serialNumber = 12345678;
        this.signAlgorithmIdentifier = "RSA";
        this.issuerName = "Server";
        this.publicKeyInformation = "public";
        this.expirationDate = new Date(2030,10,8);
        this.signature = "";
    }

    public Certificate(@JsonProperty("version") String version,
                       @JsonProperty("serialNumber") int serialNumber,
                       @JsonProperty("signAlgorithmIdentifier") String signAlgorithmIdentifier,
                       @JsonProperty("issuerName") String issuerName,
                       @JsonProperty("publicKeyInformation") String publicKeyInformation,
                       @JsonProperty("expirationDate") Date expirationDate,
                       @JsonProperty("signature") String signature)
    {
            this.version = version;
            this.serialNumber = serialNumber;
            this.signAlgorithmIdentifier = signAlgorithmIdentifier;
            this.issuerName = issuerName;
            this.publicKeyInformation = publicKeyInformation;
            this.expirationDate = expirationDate;
            this.signature = signature;
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getserialNumber()
    {
        return this.serialNumber;
    }

    public void getSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSignAlgorithmIdentifier()
    {
        return this.signAlgorithmIdentifier;
    }

    public void setSignAlgorithmIdentifier(String signAlgorithmIdentifier) {
        this.signAlgorithmIdentifier = signAlgorithmIdentifier;
    }
    public String getIssuerName()
    {
        return this.issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getPublicKeyInformation()
    {
        return this.publicKeyInformation;
    }

    public void setPublicKeyInformation(String publicKeyInformation) {
        this.publicKeyInformation = publicKeyInformation;
    }

    public Date getExpirationDate()
    {
        return this.expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSignature()
    {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }






}
