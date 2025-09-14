package com.estacionamento.controller;

import com.estacionamento.entity.Veiculo;
import com.estacionamento.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@Valid @RequestBody VeiculoEntradaDTO entradaDTO) {
        try {
            Veiculo veiculo = new Veiculo(entradaDTO.getPlaca(), entradaDTO.getModelo(), entradaDTO.getCor());
            Veiculo veiculoSalvo = veiculoService.registrarEntrada(veiculo);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Entrada registrada com sucesso!", veiculoSalvo));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        }
    }

    @PutMapping("/saida/{id}")
    public ResponseEntity<?> registrarSaida(@PathVariable Long id) {
        try {
            Veiculo veiculo = veiculoService.registrarSaida(id);

            return ResponseEntity.ok(new ApiResponse("Saída registrada com sucesso! Valor a pagar: R$ " +
                    String.format("%.2f", veiculo.getValorPago()), veiculo));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        List<Veiculo> veiculos = veiculoService.listarTodos();
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Veiculo>> listarAtivos() {
        List<Veiculo> veiculosAtivos = veiculoService.listarAtivos();
        return ResponseEntity.ok(veiculosAtivos);
    }

    @GetMapping("/{placa}")
    public ResponseEntity<?> buscarPorPlaca(@PathVariable String placa) {
        Optional<Veiculo> veiculo = veiculoService.buscarPorPlaca(placa);

        if (veiculo.isPresent()) {
            return ResponseEntity.ok(veiculo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/info/estacionamento")
    public ResponseEntity<VeiculoService.EstacionamentoInfo> getInfoEstacionamento() {
        VeiculoService.EstacionamentoInfo info = veiculoService.getEstacionamentoInfo();
        return ResponseEntity.ok(info);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Erro interno: " + e.getMessage(), null));
    }

    public static class VeiculoEntradaDTO {
        private String placa;
        private String modelo;
        private String cor;

        public VeiculoEntradaDTO() {}

        public VeiculoEntradaDTO(String placa, String modelo, String cor) {
            this.placa = placa;
            this.modelo = modelo;
            this.cor = cor;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public String getCor() {
            return cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }
    }

    public static class ApiResponse {
        private String mensagem;
        private Object dados;

        public ApiResponse(String mensagem, Object dados) {
            this.mensagem = mensagem;
            this.dados = dados;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public Object getDados() {
            return dados;
        }

        public void setDados(Object dados) {
            this.dados = dados;
        }
    }
}