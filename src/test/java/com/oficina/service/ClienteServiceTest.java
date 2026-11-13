package com.oficina.service;

import com.oficina.dto.ClienteRequestDTO;
import com.oficina.dto.ClienteResponseDTO;
import com.oficina.dto.VeiculoDTO;
import com.oficina.exception.CpfDuplicadoException;
import com.oficina.exception.CpfInvalidoException;
import com.oficina.exception.PlacaInvalidaException;
import com.oficina.model.Cliente;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        VeiculoDTO veiculoDTO = new VeiculoDTO(null, "ABC1D23", "Chevrolet Onix 1.0");
        dtoValido = new ClienteRequestDTO(
            "Carlos Silva",
            "52998224725", // CPF com dígitos verificadores matematicamente válidos
            "carlos.silva@email.com",
            "38000-000",
            veiculoDTO
        );
    }

    @Test
    @DisplayName("Deve cadastrar cliente e veículo com sucesso quando dados e regras forem válidos")
    void deveCadastrarClienteComSucesso() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(veiculoRepository.existsByPlaca(anyString())).thenReturn(false);

        Cliente clienteSalvo = new Cliente(dtoValido.nome(), dtoValido.cpf(), dtoValido.email(), dtoValido.cep());
        clienteSalvo.setId(1L);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponseDTO response = clienteService.cadastrarCliente(dtoValido);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Carlos Silva", response.nome());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar CpfDuplicadoException quando CPF já estiver cadastrado no repositório")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(CpfDuplicadoException.class, () -> {
            clienteService.cadastrarCliente(dtoValido);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException quando CPF tiver dígitos verificadores incorretos")
    void deveLancarExcecaoQuandoCpfInvalido() {
        ClienteRequestDTO dtoCpfInvalido = new ClienteRequestDTO(
            "Carlos Silva",
            "11122233344", // CPF inválido
            "carlos@email.com",
            "38000-000",
            dtoValido.veiculo()
        );

        assertThrows(CpfInvalidoException.class, () -> {
            clienteService.cadastrarCliente(dtoCpfInvalido);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar PlacaInvalidaException quando o formato da placa for inválido")
    void deveLancarExcecaoQuandoPlacaInvalida() {
        VeiculoDTO veiculoInvalido = new VeiculoDTO(null, "PLACA123", "Fiat Uno");
        ClienteRequestDTO dtoPlacaInvalida = new ClienteRequestDTO(
            "Carlos Silva",
            "52998224725",
            "carlos@email.com",
            "38000-000",
            veiculoInvalido
        );

        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);

        assertThrows(PlacaInvalidaException.class, () -> {
            clienteService.cadastrarCliente(dtoPlacaInvalida);
        });

        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}
