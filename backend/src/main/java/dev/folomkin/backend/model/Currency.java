package dev.folomkin.backend.model;

import java.math.BigDecimal;

public class Currency {
        private Long id;
        private String name;
        private String code;
        private BigDecimal rub_rate;
        private String sign;


        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
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
