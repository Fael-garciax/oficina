package com.oficina.exception;

public class PlacaInvalidaException extends RuntimeException {

    public PlacaInvalidaException(String placa) {
        super(String.format("A placa informada não atende aos padrões válidos (Mercosul ou padrão antigo): %s", placa));
    }
}
