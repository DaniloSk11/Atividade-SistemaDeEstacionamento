package com.estacionamento;

import com.estacionamento.entity.Veiculo;
import com.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EstacionamentoApplication implements CommandLineRunner {

	@Autowired
	private VeiculoService veiculoService;

	public static void main(String[] args) {
		SpringApplication.run(EstacionamentoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Sistema de Controle de Estacionamento ===");
		System.out.println("API disponível em: http://localhost:8080/api/veiculos");
		System.out.println("Console H2 disponível em: http://localhost:8080/h2-console");
		System.out.println("JDBC URL: jdbc:h2:file:./data/db-api");
		System.out.println("Username: sa");
		System.out.println("Password: (vazio)");

		System.out.println("\n=== EXECUTANDO TESTES AUTOMÁTICOS ===");
		executarTestes();
	}

	private void executarTestes() {
		try {
			System.out.println("\n1. REGISTRANDO ENTRADAS:");

			Veiculo v1 = new Veiculo("ABC1234", "Honda Civic", "Branco");
			Veiculo entrada1 = veiculoService.registrarEntrada(v1);
			System.out.println("Entrada registrada: " + entrada1.getPlaca() + " - ID: " + entrada1.getId());

			Veiculo v2 = new Veiculo("DEF5678", "Toyota Corolla", "Prata");
			Veiculo entrada2 = veiculoService.registrarEntrada(v2);
			System.out.println("Entrada registrada: " + entrada2.getPlaca() + " - ID: " + entrada2.getId());

			Veiculo v3 = new Veiculo("GHI9012", "Volkswagen Gol", "Azul");
			Veiculo entrada3 = veiculoService.registrarEntrada(v3);
			System.out.println("Entrada registrada: " + entrada3.getPlaca() + " - ID: " + entrada3.getId());

			System.out.println("\n2. LISTANDO VEÍCULOS ATIVOS:");
			veiculoService.listarAtivos().forEach(v ->
					System.out.println("ID: " + v.getId() + " | Placa: " + v.getPlaca() + " | Modelo: " + v.getModelo())
			);

			System.out.println("\n3. INFORMAÇÕES DO ESTACIONAMENTO:");
			var info = veiculoService.getEstacionamentoInfo();
			System.out.println("Capacidade Total: " + info.getCapacidadeTotal());
			System.out.println("Veículos Estacionados: " + info.getVeiculosEstacionados());
			System.out.println("Vagas Disponíveis: " + info.getVagasDisponiveis());
			System.out.println("Status: " + (info.isTemVagas() ? "HÁ VAGAS" : "LOTADO"));

			System.out.println("\n4. AGUARDANDO 3 SEGUNDOS PARA SIMULAR TEMPO...");
			Thread.sleep(3000);

			System.out.println("\n5. REGISTRANDO SAÍDA:");
			Veiculo saida1 = veiculoService.registrarSaida(entrada1.getId());
			System.out.println("Saída registrada para: " + saida1.getPlaca());
			System.out.println("Valor a pagar: R$ " + String.format("%.2f", saida1.getValorPago()));

			System.out.println("\n6. TENTANDO ENTRADA DUPLICADA (deve falhar):");
			try {
				Veiculo duplicado = new Veiculo("DEF5678", "Outro modelo", "Outra cor");
				veiculoService.registrarEntrada(duplicado);
				System.out.println("ERRO: Não deveria ter permitido!");
			} catch (Exception e) {
				System.out.println("Erro esperado: " + e.getMessage());
			}

			System.out.println("\n7. BUSCANDO POR PLACA:");
			veiculoService.buscarPorPlaca("GHI9012").ifPresentOrElse(
					v -> System.out.println("Encontrado: " + v.getPlaca() + " - Ativo: " + v.getAtivo()),
					() -> System.out.println("Não encontrado")
			);

			System.out.println("\n8. LISTANDO TODOS OS REGISTROS:");
			veiculoService.listarTodos().forEach(v ->
					System.out.printf("ID: %d | Placa: %s | Ativo: %s | Valor pago: %s%n",
							v.getId(), v.getPlaca(), v.getAtivo() ? "SIM" : "NÃO",
							v.getValorPago() != null ? "R$ " + String.format("%.2f", v.getValorPago()) : "---")
			);

			System.out.println("\n9. TESTANDO CAPACIDADE MÁXIMA:");
			try {
				for (int i = 1; i <= 15; i++) {
					String placa = String.format("TST%04d", i);
					Veiculo teste = new Veiculo(placa, "Teste " + i, "Cor" + i);
					veiculoService.registrarEntrada(teste);
					System.out.println("Teste " + i + " registrado: " + placa);
				}
			} catch (Exception e) {
				System.out.println("Limite atingido: " + e.getMessage());
				var infoFinal = veiculoService.getEstacionamentoInfo();
				System.out.println("Situação final: " + infoFinal.getVeiculosEstacionados() + "/" + infoFinal.getCapacidadeTotal());
			}

			System.out.println("\n=== TESTES CONCLUÍDOS COM SUCESSO ===");

		} catch (Exception e) {
			System.out.println("Erro durante os testes: " + e.getMessage());
			e.printStackTrace();
		}
	}
}