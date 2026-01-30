package dev.folomkin.backend.model;

public class CreateExchangeRatesRequestDto {
    private String baseCode;
    private String targetCode;

    public CreateExchangeRatesRequestDto() {
    }

    public CreateExchangeRatesRequestDto(String baseCode, String targetCode) {
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
