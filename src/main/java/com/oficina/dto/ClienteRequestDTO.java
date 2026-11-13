package com.oficina.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClienteRequestDTO(
    @NotBlank(message = "O nome do cliente é obrigatório.")
    String nome,

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(
        regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$",
        message = "Formato de CPF inválido. Utilize o formato 000.000.000-00 ou 11 dígitos numéricos."
    )
    String cpf,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    String email,

    @NotBlank(message = "O CEP é obrigatório.")
    @Pattern(
        regexp = "^\\d{5}-?\\d{3}$",
        message = "Formato de CEP inválido. Utilize o formato 00000-000 ou 8 dígitos numéricos."
    )
    String cep,

    @NotNull(message = "Os dados do veículo são obrigatórios.")
    @Valid
    VeiculoDTO veiculo
) {
}
