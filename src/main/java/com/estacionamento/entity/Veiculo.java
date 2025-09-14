package com.estacionamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 8)
    @NotBlank(message = "Placa é obrigatória")
    private String placa;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(nullable = false, length = 30)
    @NotBlank(message = "Cor é obrigatória")
    private String cor;

    @Column(name = "data_entrada", nullable = false)
    @NotNull(message = "Data de entrada é obrigatória")
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "valor_pago")
    private Double valorPago;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Veiculo() {
    }

    public Veiculo(String placa, String modelo, String cor) {
        this.placa = placa.toUpperCase();
        this.modelo = modelo;
        this.cor = cor;
        this.dataEntrada = LocalDateTime.now();
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa != null ? placa.toUpperCase() : null;
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

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Double getValorPago() {
        return valorPago;
    }

    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void processarSaida(Double valor) {
        if (this.dataSaida != null) {
            throw new IllegalStateException("Veículo já processou a saída");
        }
        this.dataSaida = LocalDateTime.now();
        this.valorPago = valor;
        this.ativo = false;
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "id=" + id +
                ", placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", cor='" + cor + '\'' +
                ", dataEntrada=" + dataEntrada +
                ", dataSaida=" + dataSaida +
                ", valorPago=" + valorPago +
                ", ativo=" + ativo +
                '}';
    }
}