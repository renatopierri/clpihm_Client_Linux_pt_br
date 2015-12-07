package br.com.nets_nuts.clpihm;

import java.io.*;
import java.net.*;

import org.apache.commons.lang3.StringUtils;

import java.text.*;
import java.util.Date;

/**
 * Esta classe é um cliente TCP. Ela cria oo soquete de acordo com as
 * configurações de IP e Port e envia os comandos para o CLP.
 * 
 * @author Renato de Pierri renato.pierri@gmail.com
 * */
public class TCPClient {
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Date date = new Date();
	private String ip;
	private int port;
	private String checkReturn, checkSend;
	private Socket clientSocket;

	/****************************************************************/

	/** Contrutor padrão da classe. */
	public TCPClient() {
		// TODO Auto-generated constructor stub.
	}

	/****************************************************************/

	/**
	 * Este método envia os comandos para o CLP. Caso o CLP não responda em 5
	 * segundos, é retornada a mensagem de timeout, acusando provável erro na
	 * configuração do campo CLPID.
	 * 
	 * @param comando_CLP
	 *            String a ser enviada para o CLP.
	 * @return String com o status do comando executado.
	 * @throws Exception
	 *             e Timeout na conexão com o CLP remoto.
	 **/
	public String sendCLP(String comando_CLP) throws Exception {

		try {
			String statusCmdCLP;
			/** Abrindo a porta e os streans de entrada e saída */
			clientSocket = new Socket(ip, port);
			clientSocket.setSoTimeout(3000);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			/** Fim da abertura da porta e dos streans de entrada e saída */

			/** Enviando os dados para o CLP */
			outToServer.writeBytes(comando_CLP);

			/** Lendo a resposta do CLP */
			statusCmdCLP = inFromServer.readLine();
			clientSocket.close();

			/** Descartando caracteres nao imprimíveis */
			statusCmdCLP = StringUtils.right(statusCmdCLP,
					statusCmdCLP.length() - 6);
			System.out.println(dateFormat.format(date)
					+ " Comando enviado ao CLP: "
					+ StringUtils.right(comando_CLP, comando_CLP.length() - 7));

			/** Gerando log do resultado obtido */
			checkSend = StringUtils.substring(comando_CLP, 8, 12);
			checkReturn = StringUtils.substring(statusCmdCLP, 2, 6);

			if (StringUtils.equals(checkSend, checkReturn)) {
				System.out.println(dateFormat.format(date)
						+ " Resposta do CLP: " + statusCmdCLP);
			} else {
				System.out.println(dateFormat.format(date)
						+ " Resposta do CLP inconsistente: " + statusCmdCLP);
			}

			return statusCmdCLP;
		} catch (Exception e) {
			/** TODO Auto-generated catch block */
			e.printStackTrace();
			clientSocket.close();
			return "Erro de timeout. CLP ID pode estar errado";
		}
	}

	/****************************************************************/

	/**
	 * Este método serve para configurar o endereço de IP do CLP. <br>
	 * Deve ser enviada uma string no formato aaa.bbb.ccc.ddd:pppp<br>
	 * Onde:
	 * <ul>
	 * <li>aaa.bbb.ccc.ddd é o endereço IP do CLP</li>
	 * <li>pppp é o número da porta do CLP</li>
	 * </ul>
	 * Após a configuração é feito um teste de ping.
	 * 
	 * @param endIP
	 *            Endereço de IP a ser configurado na aplicação.
	 * @return boolean indicando se o teste de ping foi concluido com sucesso.
	 * */
	public boolean configIP(String endIP) {

		port = Integer.parseInt(StringUtils.substringAfter(endIP, ":"));
		ip = StringUtils.substringBefore(endIP, ":");
		return testaPing(ip);

	}

	/****************************************************************/

	/**
	 * <p>
	 * Este método faz o teste de ping no CLP destino.
	 * </p>
	 * <p>
	 * Fonte deste código:
	 * </p>
	 * Veja: <a href="http://www.inprose.com/en/content/icmp-ping-in-java">
	 * www.inprose.com/en/content/icmp-ping-in-java</a>
	 * 
	 * @param host
	 *            String contendo o endereço de IP ou hostname da máquina
	 *            destino.
	 * @return boolean indica que o teste de ping ocorreu com exito.
	 * */
	public static boolean testaPing(String host) {
		try {
			String cmd = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
				// Windows
				cmd = "ping -n 1 " + host;
			} else {
				// Linux
				cmd = "ping -c 1 " + host;
			}
			Process processo = Runtime.getRuntime().exec(cmd);
			processo.waitFor();
			if (processo.exitValue() == 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/****************************************************************/

}
