package com.oficina.service;

import com.oficina.dto.ClienteRequestDTO;
import com.oficina.dto.ClienteResponseDTO;
import com.oficina.exception.CpfDuplicadoException;
import com.oficina.exception.CpfInvalidoException;
import com.oficina.exception.PlacaInvalidaException;
import com.oficina.model.Cliente;
import com.oficina.model.Veiculo;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    public ClienteService(ClienteRepository clienteRepository, VeiculoRepository veiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO dto) {
        // Regra de Negócio 1: Validação algorítmica de CPF
        Cliente clienteTemporario = new Cliente(dto.nome(), dto.cpf(), dto.email(), dto.cep());
        if (!clienteTemporario.validarCpf()) {
            throw new CpfInvalidoException(dto.cpf());
        }

        // Regra de Negócio 2: Não permitir duplicidade de CPF no sistema
        String cpfLimpo = dto.cpf().replaceAll("\\D", "");
        if (clienteRepository.existsByCpf(dto.cpf()) || clienteRepository.existsByCpf(cpfLimpo)) {
            throw new CpfDuplicadoException(dto.cpf());
        }

        // Regra de Negócio 3: Validação da placa do veículo inicial
        Veiculo veiculo = new Veiculo(dto.veiculo().placa(), dto.veiculo().modelo());
        if (!veiculo.validarPlaca()) {
            throw new PlacaInvalidaException(dto.veiculo().placa());
        }

        // Regra de Negócio 4: Não permitir duplicidade de placa no sistema
        if (veiculoRepository.existsByPlaca(veiculo.getPlaca())) {
            throw new PlacaInvalidaException("A placa " + veiculo.getPlaca() + " já se encontra cadastrada em outro veículo.");
        }

        // Construção do agregado de domínio
        Cliente cliente = new Cliente(
            dto.nome().trim(),
            dto.cpf().trim(),
            dto.email().trim().toLowerCase(),
            dto.cep().trim()
        );
        cliente.adicionarVeiculo(veiculo);

        // Persistência isolada através do repositório
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Conversão para DTO de resposta (desacoplamento da entidade)
        return ClienteResponseDTO.fromEntity(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
            .stream()
            .map(ClienteResponseDTO::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para o ID: " + id));
        return ClienteResponseDTO.fromEntity(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para o CPF: " + cpf));
        return ClienteResponseDTO.fromEntity(cliente);
    }
}
