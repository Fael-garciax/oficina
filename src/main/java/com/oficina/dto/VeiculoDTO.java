package com.oficina.dto;

import com.oficina.model.Veiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VeiculoDTO(
    Long id,

    @NotBlank(message = "A placa do veículo é obrigatória.")
    @Pattern(
        regexp = "^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$|^[A-Za-z]{3}-[0-9]{4}$",
        message = "Placa inválida. Utilize o formato Mercosul (ex: ABC1D23) ou antigo (ex: ABC-1234)."
    )
    String placa,

    @NotBlank(message = "O modelo do veículo é obrigatório.")
    String modelo
) {
    public static VeiculoDTO fromEntity(Veiculo veiculo) {
        if (veiculo == null) return null;
        return new VeiculoDTO(
            veiculo.getId(),
            veiculo.getPlaca(),
            veiculo.getModelo()
        );
    }
}
