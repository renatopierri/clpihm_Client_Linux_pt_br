package br.com.nets_nuts.clpihm;

/**
 * <pre>
 * Esta classe faz o cálculo do check sum do comando do CLP,
 * e adiciona os caracteres de controle necessários.
 * Para cálculo do check sum somente são contados os caracteres
 * do comando propriamente dito, sem o caractere "/".
 * O cálculo do checksum e feito pelo cálculo do módulo da soma 
 * dos caracteres ASCII da palavra de comando.
 * 
 * Descrição do cabeçalho
 *  
 * |0x07|0xLow|0xHigh|0x65|0x0|0xLow|0xHigh|0x2F|
 *    |     |    |      |         |     |    |
 *    |     |    |      |         |     |    +---- Comeco do comando (caractere '/')
 *    |     |    |      |         |     +--------- Tamanho do comando - byte alto
 *    |     |    |      |         +--------------- Tamanho do comando - byte baixo
 *    |     |    |      +------------------------- Modo ASCII 0x65, modo binário 0x66
 *    |     |    +-------------------------------- Nr transação - byte alto
 *    |     +------------------------------------- Nr transação - byte baixo
 *    +------------------------------------------- Não documentado. Bug a ser resolvido
 * </pre>
 * 
 * @author Renato de Pierri - renato.pierri@gmail.com
 * */
public class CalcCheckSum {

	private int rawCmdSize, valor, modulo, soma;
	private String comando_CLP, checksum, tamanhoCmd, tamLowByte, tamHiByte,
			trID, trLowByte, trHiByte;
	private String fim = new String(new byte[] { 0x0D });

	/** Construtor da classe */
	public CalcCheckSum() {
		// TODO Auto-generated constructor stub
	}

	/****************************************************************/

	/**
	 * <p>
	 * Este método calcula o checksum e constroi a palavra de comando.
	 * calcCheckSum recebe o comando e o nr da transação para cálculo do check
	 * sum e montagem do cabeçalho.
	 * </p>
	 * 
	 * @param rawCmd
	 *            String contendo o parâmetro a ser enviado para o CLP
	 * @param transaID
	 *            Inteiro contendo o número da transação
	 * @return String com o comando completo a ser enviado ao CLP
	 * */
	public String calcCheckSum(String rawCmd, int transaID) {

		soma = 0;
		rawCmdSize = rawCmd.length();
		for (int i = 0; i < rawCmdSize; i++) {
			valor = rawCmd.charAt(i);
			soma = soma + valor;
		}
		modulo = soma % 256;

		checksum = cSum(rawCmd);

		/**
		 * Fim do cálculo do checksum.
		 */

		/**
		 * Definindo o tamanho da palavra de comando, em hexadecimal (2 bytes)
		 * */
		rawCmdSize = rawCmdSize + 4;
		tamanhoCmd = (Integer.toHexString(rawCmdSize)).toUpperCase();
		while (tamanhoCmd.length() != 4) {
			tamanhoCmd = "0" + tamanhoCmd;

		}
		tamLowByte = "0x" + tamanhoCmd.substring(2, 4);
		tamHiByte = "0x" + tamanhoCmd.substring(0, 2);
		/**
		 * Fim da definição do tamanho da palavra de comando.
		 * */

		/**
		 * Convertendo o número da transação de decimal para hexadecimal (2
		 * bytes).
		 * */

		trID = intHex(transaID);
		trLowByte = "0x" + trID.substring(2, 4);
		trHiByte = "0x" + trID.substring(0, 2);
		/**
		 * Fim da conversão do número de transação de decimal para hexadecimal.
		 * */

		/**
		 * Montando o cabeçalho do comando do CLP
		 * */
		String cabecalho = new String(new byte[] { 0x07,
				Integer.decode(trLowByte).byteValue(),
				Integer.decode(trHiByte).byteValue(), 0x65, 0x00,
				Integer.decode(tamLowByte).byteValue(),
				Integer.decode(tamHiByte).byteValue() });

		/**
		 * Concatenando os elementos do comando do CLP:
		 * <p>
		 * 1- cabeçalho, 2- o início do comando, 3- o comando "/", 4- o check
		 * sum, 5- a finalização <CR>.
		 * </p>
		 **/

		comando_CLP = cabecalho + "/" + rawCmd + checksum + fim;
		return comando_CLP;
	}

	/****************************************************************/

	/**
	 * Converte o numero da transação de decimal para hexadecimal e preenche a
	 * direita com zeros.
	 * 
	 * @param transaID
	 *            Inteiro com o identificador da transação .
	 * @return String contendo o valor em hexa.
	 * */
	public String intHex(int transaID) {

		String trID = (Integer.toHexString(transaID)).toUpperCase();
		while (trID.length() != 4) {
			trID = "0" + trID;
		}
		return trID;
	}

	/****************************************************************/

	/**
	 * Calcula o checksum. cSum recebe o comando e faz o cálculo do checksum.
	 * 
	 * @param rawCmd
	 *            String contendo o parâmetro a ser calculado.
	 * @return - String contendo o valor do checksum (hexa).
	 * */
	public String cSum(String rawCmd) {
		int soma = 0;
		rawCmdSize = rawCmd.length();
		for (int i = 0; i < rawCmdSize; i++) {
			valor = rawCmd.charAt(i);
			soma = soma + valor;
		}
		modulo = soma % 256;
		checksum = (Integer.toHexString(modulo)).toUpperCase();
		if (checksum.length() == 1) {
			checksum = "0" + checksum;
		}
		return checksum;
	}

	/****************************************************************/
}
