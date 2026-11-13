package com.oficina.exception;

public class CpfInvalidoException extends RuntimeException {

    public CpfInvalidoException(String cpf) {
        super(String.format("O CPF informado é inválido pelos critérios de validação: %s", cpf));
    }
}
