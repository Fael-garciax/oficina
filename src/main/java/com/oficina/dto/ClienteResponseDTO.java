package com.oficina.dto;

import com.oficina.model.Cliente;
import java.util.List;

public record ClienteResponseDTO(
    Long id,
    String nome,
    String cpf,
    String email,
    String cep,
    List<VeiculoDTO> veiculos
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        if (cliente == null) return null;

        List<VeiculoDTO> veiculosDto = cliente.getVeiculos() != null
            ? cliente.getVeiculos().stream().map(VeiculoDTO::fromEntity).toList()
            : List.of();

        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getCpf(),
            cliente.getEmail(),
            cliente.getCep(),
            veiculosDto
        );
    }
}
