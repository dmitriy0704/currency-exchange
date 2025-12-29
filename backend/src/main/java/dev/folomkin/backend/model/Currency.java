package dev.folomkin.backend.model;

import java.math.BigDecimal;

public class Currency {
        private Long id;
        private String full_name;
        private String code;
        private BigDecimal rub_rate;
        private String sign;


        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getFull_name() {
                return full_name;
        }

        public void setFull_name(String full_name) {
                this.full_name = full_name;
        }

        public String getCode() {
                return code;
        }

        public void setCode(String code) {
                this.code = code;
        }

        public BigDecimal getRub_rate() {
                return rub_rate;
        }

        public void setRub_rate(BigDecimal rub_rate) {
                this.rub_rate = rub_rate;
        }

        public String getSign() {
                return sign;
        }

        public void setSign(String sign) {
                this.sign = sign;
        }
}
