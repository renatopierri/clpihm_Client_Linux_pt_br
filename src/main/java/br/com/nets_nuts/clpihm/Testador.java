/**
 * 
 */
package br.com.nets_nuts.clpihm;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.*;

/**
 * @author renato
 *
 */
public class Testador extends Thread {
	private AcertaHora ah = new AcertaHora();
	private TCPClient tcpclient = new TCPClient();
	private CalcCheckSum cCS = new CalcCheckSum();
	private String comando, result;
	private Text txtHistCmd, txtHistResult, endIP;
	private Spinner transacaoId, tempoMs;
	private Button btnRodarComandos, btnIniciar, btnEnviar;
	private Boolean loop;
	private int sleepThread;
	private DateTime calendario, dateTimeCLP;

	/****************************************************************/

	/**
	 * Construtor padrão da classe.
	 */
	public Testador() {
		// TODO Auto-generated constructor stub
	}

	/****************************************************************/

	/**
	 * Inicializa parâmetros do testador.
	 * 
	 * @param tHC
	 *            Objeto Texto que contém a lista de comandos a serem enviados
	 *            ao CLP
	 * @param tHR
	 *            Objeto Texto que receberá o retorno dos comandos enviados ao
	 *            CLP
	 * @param IP
	 *            Objeto Text contendo o IP e porta do CLP
	 * @param trId
	 *            Objeto Spinner contendo o número da transação a ser enviada ao
	 *            CLP
	 * @param tMs
	 *            Objeto Spinner contendo a temporização do envio de comandos ao
	 *            CLP
	 * @param bR
	 *            Objeto Button estilo checkbox que controla o loop e habilita o
	 *            envio de comandos ao CLP
	 * @param bI
	 *            Objeto Button que inicia o loop de envio de comandos ao CLP
	 * @param bE
	 *            Objeto Button do construtor de comandos que envia comandos ao
	 *            CLP
	 * @param cal
	 *            Objeto DateTime estilo calendário
	 * @param dCLP
	 *            Objeto DateTime estilo relógio
	 * */
	public void inicializa(Text tHC, Text tHR, Text IP, Spinner trId,
			Spinner tMs, Button bR, Button bI, Button bE, DateTime cal,
			DateTime dCLP) {
		txtHistCmd = tHC;
		txtHistResult = tHR;
		endIP = IP;
		transacaoId = trId;
		tempoMs = tMs;
		btnRodarComandos = bR;
		btnIniciar = bI;
		btnEnviar = bE;
		loop = btnRodarComandos.getSelection();
		sleepThread = tempoMs.getSelection();
		calendario = cal;
		dateTimeCLP = dCLP;
	}

	/****************************************************************/

	/**
	 * Inicia a thread que envia os comandos ao CLP
	 * */
	public void roda() {
		new Thread(new Runnable() {
			public void run() {
				while (loop) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							enviaComandos();
						}
					});
				}
			}
		}).start();
	}

	/****************************************************************/

	/**
	 * Envia comandos ao CLP. Este método extrai cada item do histórico de
	 * comandos, calcula o checksum, monta o comando e envia ao CLP.
	 * */
	public void enviaComandos() {
		loop = btnRodarComandos.getSelection();
		sleepThread = tempoMs.getSelection();
		txtHistResult.setText("");
		tcpclient.configIP(endIP.getText());
		int inicio, fim;
		String cmdPuro;
		inicio = 0;
		fim = 1;
		/**
		 * Passa todo o texto para maiúsculo, estrai os comandos e calcula o
		 * número de comandos
		 */
		txtHistCmd.setText(StringUtils.upperCase(txtHistCmd.getText()));
		String v = txtHistCmd.getText();
		String values[] = StringUtils.split(v, "\n");
		int tamArray = values.length;
		int i = 0;
		if (!btnRodarComandos.getSelection()) {
			i = tamArray;
		}
		/** A thread só existe por conta desse while */
		while (i < tamArray) {
			if (!btnRodarComandos.getSelection()) {
				i = tamArray - 1;
			}
			if (i == 0) {
				fim = fim + values[i].length();
			} else {
				fim = fim + values[i].length() + 1;
			}
			/**
			 * Aqui eu seleciono o item, só para ficar bonito na tela do
			 * usuário. Não faço nada de útil com isso
			 */
			txtHistCmd.setSelection(inicio, fim);
			cmdPuro = StringUtils.remove(values[i], "/");
			Display.getCurrent().getActiveShell().redraw();
			Display.getCurrent().getActiveShell().update();
			Display.getCurrent().getActiveShell().layout();
			/** Montando o comando */
			if (cmdPuro.length() > 3) {
				comando = cCS.calcCheckSum(cmdPuro,
						Integer.parseInt(transacaoId.getText()));
				/** Enviando para o TCP client e pegando o retorno */
				try {
					result = tcpclient.sendCLP(comando);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/**
				 * Se for comando de relógio, faz-se a atualização do calendário
				 * e relógio.
				 */
				if (StringUtils.contains(result, "RC")) {
					ah.acertaRelogioApp(StringUtils.substring(result, 6,
							result.length() - 2), calendario, dateTimeCLP);
					Display.getCurrent().getActiveShell().redraw();
					Display.getCurrent().getActiveShell().update();
					Display.getCurrent().getActiveShell().layout();
				}

				if (StringUtils.equals(txtHistResult.getText(), "")) {
					txtHistResult.setText(result);
				} else {
					txtHistResult.setText(txtHistResult.getText() + "\n"
							+ result);
				}
				result = "";
			}
			inicio = fim;
			try {
				Thread.sleep(Long.parseLong(tempoMs.getText()), 0);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		System.out.println("linhas no widget" + tamArray);
	}
}
