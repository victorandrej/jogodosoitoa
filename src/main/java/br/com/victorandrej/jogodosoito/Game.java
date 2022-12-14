package br.com.victorandrej.jogodosoito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.victorandrej.jogodosoito.componentes.Peca;
import br.com.victorandrej.jogodosoito.componentes.Posicao;
import br.com.victorandrej.jogodosoito.componentes.Tabuleiro;
import br.com.victorandrej.jogodosoito.interfaces.Desenhador;
import br.com.victorandrej.jogodosoito.interfaces.Entrada;

public class Game {

	private Runnable quandoGanhar;
	private Desenhador desenhador;
	private Entrada entrada;
	private Tabuleiro tabuleiro;
	private int espacos;
	private boolean sair;

	public Game(Tabuleiro tabuleiro, int espacos, Desenhador desenhador, Entrada entrada) {
		this.tabuleiro = tabuleiro;
		this.espacos = espacos;
		this.desenhador = desenhador;
		this.entrada = entrada;
	}

	public void quandoGanharListener(Runnable quandoGanhar) {
		this.quandoGanhar = quandoGanhar;
	}

	public void resetar() {
		this.sair = false;
		tabuleiro = new Tabuleiro(tabuleiro.getAltura(), tabuleiro.getLargura());
	}

	public void start() {
	
		this.gerarPecas().forEach(tabuleiro::adicionarPeca);

		while (true) {
			desenhador.desenhar(this.tabuleiro);

			if (estaGanho() && quandoGanhar != null)
				quandoGanhar.run();

			if (sair)
				break;

			entrada.entrar(this);

			if (sair)
				break;
		}
	}

	public Tabuleiro getTabuleiro() {
		return this.tabuleiro;
	}

	public void sair() {
		this.sair = true;
	}

	private boolean estaGanho() {
		Peca pecaAnterior = null;
		List<Peca> pecasOrdenadas = Stream.of(this.tabuleiro.getPecas())
				.sorted((p1, p2) -> p1.getNumero() < p2.getNumero() ? -1 : 1).collect(Collectors.toList());

		for (Peca peca : pecasOrdenadas) {
			if ((pecaAnterior != null && pecaAnterior.getPosicao().estaNaFrente(peca.getPosicao()))
					|| Stream.of(tabuleiro.posicoesDisponiveis(peca)).anyMatch(p -> peca.getPosicao().estaNaFrente(p)))
				return false;

			pecaAnterior = peca;
		}

		return true;
	}

	private Set<Peca> gerarPecas() {
		Set<Peca> pecas = new HashSet<Peca>();
		List<Integer> numeros = new ArrayList<>();

		for (int i = 1; i <= (tabuleiro.getAltura() * tabuleiro.getLargura()) - espacos; i++) {
			numeros.add(i);
		}

		Collections.shuffle(numeros);
		int contador = 0;

		for (int i = 0; i < tabuleiro.getAltura(); i++) {
			for (int j = 0; j < tabuleiro.getLargura(); j++) {
				if (contador >= ((tabuleiro.getAltura() * tabuleiro.getLargura()) - espacos))
					break;

				pecas.add(new Peca(numeros.get(numeros.size() - 1), new Posicao(j, i)));
				numeros.remove(numeros.size() - 1);
				contador++;
			}

		}

		return pecas;
	}

}
