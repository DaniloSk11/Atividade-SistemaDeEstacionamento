package com.estacionamento.service;

import com.estacionamento.entity.Veiculo;
import com.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final int capacidadeMaxima;

    private static final double TAXA_MINIMA = 5.00;
    private static final double VALOR_POR_MINUTO_EXTRA = 0.25;
    private static final int MINUTOS_TAXA_MINIMA = 30;

    public VeiculoService(VeiculoRepository veiculoRepository,
                          @Value("${estacionamento.capacidade.maxima:10}") int capacidadeMaxima) {
        this.veiculoRepository = veiculoRepository;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Veiculo registrarEntrada(Veiculo veiculo) {
        if (!hayVagasDisponiveis()) {
            throw new RuntimeException("Estacionamento lotado! Capacidade máxima: " + capacidadeMaxima + " veículos.");
        }

        if (veiculoRepository.existsByPlacaAndAtivoTrue(veiculo.getPlaca())) {
            throw new RuntimeException("Veículo com placa " + veiculo.getPlaca() + " já está estacionado!");
        }

        veiculo.setDataEntrada(LocalDateTime.now());
        veiculo.setAtivo(true);
        veiculo.setDataSaida(null);
        veiculo.setValorPago(null);

        return veiculoRepository.save(veiculo);
    }

    public Veiculo registrarSaida(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + id));

        if (!veiculo.getAtivo()) {
            throw new RuntimeException("Veículo já processou a saída anteriormente!");
        }

        if (veiculo.getDataSaida() != null) {
            throw new RuntimeException("Data de saída já foi registrada para este veículo!");
        }

        double valorPago = calcularValor(veiculo.getDataEntrada(), LocalDateTime.now());

        veiculo.processarSaida(valorPago);

        return veiculoRepository.save(veiculo);
    }

    private double calcularValor(LocalDateTime entrada, LocalDateTime saida) {
        Duration duracao = Duration.between(entrada, saida);
        long minutosTotal = duracao.toMinutes();

        if (minutosTotal <= MINUTOS_TAXA_MINIMA) {
            return TAXA_MINIMA;
        } else {
            long minutosExtras = minutosTotal - MINUTOS_TAXA_MINIMA;
            return TAXA_MINIMA + (minutosExtras * VALOR_POR_MINUTO_EXTRA);
        }
    }

    private boolean hayVagasDisponiveis() {
        long veiculosAtivos = veiculoRepository.countVeiculosAtivos();
        return veiculosAtivos < capacidadeMaxima;
    }

    @Transactional(readOnly = true)
    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAllByOrderByDataEntradaDesc();
    }

    @Transactional(readOnly = true)
    public List<Veiculo> listarAtivos() {
        return veiculoRepository.findByAtivoTrueOrderByDataEntradaAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa.toUpperCase());
    }

    @Transactional(readOnly = true)
    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public EstacionamentoInfo getEstacionamentoInfo() {
        long veiculosAtivos = veiculoRepository.countVeiculosAtivos();
        long vagasDisponiveis = capacidadeMaxima - veiculosAtivos;

        return new EstacionamentoInfo(
                capacidadeMaxima,
                (int) veiculosAtivos,
                (int) vagasDisponiveis,
                vagasDisponiveis > 0
        );
    }

    public static class EstacionamentoInfo {
        private final int capacidadeTotal;
        private final int veiculosEstacionados;
        private final int vagasDisponiveis;
        private final boolean temVagas;

        public EstacionamentoInfo(int capacidadeTotal, int veiculosEstacionados,
                                  int vagasDisponiveis, boolean temVagas) {
            this.capacidadeTotal = capacidadeTotal;
            this.veiculosEstacionados = veiculosEstacionados;
            this.vagasDisponiveis = vagasDisponiveis;
            this.temVagas = temVagas;
        }

        public int getCapacidadeTotal() { return capacidadeTotal; }
        public int getVeiculosEstacionados() { return veiculosEstacionados; }
        public int getVagasDisponiveis() { return vagasDisponiveis; }
        public boolean isTemVagas() { return temVagas; }
    }
}