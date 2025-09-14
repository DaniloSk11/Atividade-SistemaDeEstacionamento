package com.estacionamento.repository;

import com.estacionamento.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    boolean existsByPlacaAndAtivoTrue(String placa);

    List<Veiculo> findByAtivoTrue();

    @Query("SELECT COUNT(v) FROM Veiculo v WHERE v.ativo = true")
    long countVeiculosAtivos();

    List<Veiculo> findByModeloContainingIgnoreCase(String modelo);

    List<Veiculo> findByCorContainingIgnoreCase(String cor);

    List<Veiculo> findAllByOrderByDataEntradaDesc();

    List<Veiculo> findByAtivoTrueOrderByDataEntradaAsc();

    @Query("SELECT v.ativo FROM Veiculo v WHERE v.id = :id")
    Optional<Boolean> isVeiculoAtivo(@Param("id") Long id);
}