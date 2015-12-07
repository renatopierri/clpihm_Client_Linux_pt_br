package br.com.nets_nuts.clpihm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * Esta classe faz a leitura do arquivo que contém os comandos disponíveis.
 * Primeiro ela lê o arquivo e determina o número de comandos disponíveis.
 * Depois ela carrega os comandos dis poníveis e faz o retorno da função. Quando
 * der, talvez eu refaça isso usando RandomAccessFile.
 * 
 * <pre>
 * lComando[0][n] Mnemônico da operação.
 * lComando[1][n] Descrição da operação.
 * lComando[2][n] Grupo de comando (leitura, gravação, relógio...)
 * lComando[3][n] Quantidade de bytes por operação de leitura/escrita. Quando não aplicável o valor é 1.
 * 
 * Grupo de comandos pode assumir os seguintes valores: </pre>
 * <table summary="">
 *     <tr>
 *         <td>1</td>
 *         <td>Não utilizado</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>Leitura dos inteiros 16 e 32 bits</td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td>Leitura dos bits</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>Escreve inteiros 16 e 32 bits</td>
 *     </tr>
 *     <tr>
 *         <td>5</td>
 *         <td>Setar bits</td>
 *     </tr>
 *     <tr>
 *         <td>6</td>
 *         <td>Operações de relógio</td>
 *     </tr>
 * </table>
 *
 * 
 * 
 * @author Renato de Pierri - renato.pierri@gmail.com
 * */
public class TelaLogica {

	private String[][] lComando;

	/****************************************************************/

	/** Construtor da classe padrao */
	public TelaLogica() {

	}

	/****************************************************************/

	/**
	 * Classe listaComandos faz a leitura do arquivo de comandos de acordo com
	 * as opções de filtro.
	 * 
	 * @param filtro
	 *            : contém o índice dos comandos desejados
	 * @return - String[][] contém uma lista de comandos e descrições.
	 * */
	public String[][] listaComandos(int filtro) {
		int n = 0;
		int nrLinhas = 0;

		BufferedReader reader = null;
		/** Lendo o arquivo e determinando o numero de linhas */
		try {
			try {
				reader = new BufferedReader(new FileReader("comandos.csv"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String linha = null;
			try {
				while ((linha = reader.readLine()) != null) {
					String mix[] = StringUtils.split(linha, "|");
					if ((Integer.valueOf(mix[0]) == filtro) || (filtro == 0)) {
						nrLinhas++;
					}
				}
				lComando = new String[4][nrLinhas];
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Recarregando o arquivo e populando o array de strings lcomando
		 * */

		try {
			try {
				reader = new BufferedReader(new FileReader("comandos.csv"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String linha = null;
			try {
				n = 0;
				while ((linha = reader.readLine()) != null) {
					String mix[] = StringUtils.split(linha, "|");
					if ((Integer.valueOf(mix[0]) == filtro) || (filtro == 0)) {
						lComando[0][n] = mix[1];
						lComando[1][n] = mix[2];
						lComando[2][n] = mix[0];
						lComando[3][n] = mix[3];
						n++;
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lComando;
	}
	/****************************************************************/
}