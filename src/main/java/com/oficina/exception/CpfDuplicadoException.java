package com.oficina.exception;

public class CpfDuplicadoException extends RuntimeException {

    public CpfDuplicadoException(String cpf) {
        super(String.format("Já existe um cliente cadastrado com o CPF: %s", cpf));
    }
}
