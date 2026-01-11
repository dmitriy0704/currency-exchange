package dev.folomkin.backend.model;

public class CreateExchangeRatesDto {
    private String baseCode;
    private String targetCode;

    public CreateExchangeRatesDto() {
    }

    public CreateExchangeRatesDto(String baseCode, String targetCode) {
        this.baseCode = baseCode;
        this.targetCode = targetCode;
    }


    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }
}
