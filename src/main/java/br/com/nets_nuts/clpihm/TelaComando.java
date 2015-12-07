package br.com.nets_nuts.clpihm;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.*;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Esta classe é o construtor de comandos para CLP Unitronics da linha Vision350
 * 
 * @author renato@gmail.com
 */
public class TelaComando {
	protected Shell shlConstrutorDeComandos;
	private DataBindingContext m_bindingContext;

	private Text descricaoComando, complemento, respostaCLP, endIP,
			txtccaddrcopl, txtHistResult;

	private static Text txtHistCmd;

	private Label lblTrIdHexa, txtTamanhoComando, comprTransaHexa,
			lblChkSumDec, lblChkSumHexa, lblEndInHexa, lblEndereco,
			lblTamanhoDoComando, comprTransaInt, lblCLPID, lblComplemento;

	private Button btnEnviar, btnIniciar, btnTodos, readBit, setBit, readInt,
			writeInt, rtc;

	private static Button btnRodarComandos;

	private Combo comandoCLP;

	private DateTime calendario, dateTimeCLP;

	private Spinner address, tamComando, transacaoId, tempoMs, clpID;

	private int filtro, cart = 0;

	private String comando, result, dataCLPS, horaCLPS, hrDtCLPS;

	private Testador tj = new Testador();
	private TCPClient tcpclient = new TCPClient();
	private TelaLogica tL = new TelaLogica();
	private AcertaHora ah = new AcertaHora();
	private CalcCheckSum cCS = new CalcCheckSum();

	private String[][] lComando = tL.listaComandos(filtro);

	/** Construtor da classe padrão */
	public TelaComando() {
	}

	/****************************************************************/

	/**
	 * Inicializando a aplicação.
	 * 
	 * @param args
	 *            Nao há parâmetros iniciais.
	 */
	public static void main(String[] args) {

		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {

					TelaComando window = new TelaComando();
					window.open();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/****************************************************************/

	/**
	 * Abre a janela.
	 */
	public void open() {
		Display display = Display.getDefault();

		createContents();

		shlConstrutorDeComandos.open();
		shlConstrutorDeComandos.layout();
		while (!shlConstrutorDeComandos.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/****************************************************************/

	/**
	 * Criando o conteúdo da janela.
	 */
	protected void createContents() {

		shlConstrutorDeComandos = new Shell();
		shlConstrutorDeComandos.setSize(1024, 600);
		shlConstrutorDeComandos.setText("Construtor de comandos");
		shlConstrutorDeComandos.setLayout(null);

		Label lblContrutorDeComandos = new Label(shlConstrutorDeComandos,
				SWT.NONE);
		lblContrutorDeComandos.setBounds(268, 0, 576, 25);
		lblContrutorDeComandos.setFont(SWTResourceManager.getFont("Ubuntu", 16,
				SWT.NORMAL));
		lblContrutorDeComandos.setAlignment(SWT.CENTER);
		lblContrutorDeComandos.setText("Construtor de comandos CLP Unitronics");

		Label label_3 = new Label(shlConstrutorDeComandos, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		label_3.setBounds(5, 35, 1014, 2);

		Label lblDecimal = new Label(shlConstrutorDeComandos, SWT.SHADOW_IN);
		lblDecimal.setBounds(289, 42, 54, 17);
		lblDecimal.setText("Decimal");

		Label lblNmeroDaTransacao = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblNmeroDaTransacao.setBounds(443, 42, 87, 17);
		lblNmeroDaTransacao.setText("Hexadecimal");

		calendario = new DateTime(shlConstrutorDeComandos, SWT.CALENDAR);
		calendario.setBounds(585, 38, 427, 176);
		calendario.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/**
				 * Apaga o campo complemento caso esteja lendo o calendario do
				 * CLP
				 */
				if (StringUtils.equals(comandoCLP.getText(), "RC")) {
					complemento.setText("");
				} else {
					complemento.setText(preparaHoraCLP() + preparaDataCLP());
				}
			}
		});
		preparaDataCLP();

		Label lblTransacaoId = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblTransacaoId.setBounds(58, 69, 135, 17);
		lblTransacaoId.setAlignment(SWT.RIGHT);
		lblTransacaoId.setText("Transação ID:");

		/**
		 * Este spinner registra o número da transação que está sendo enviada
		 * para o CLP. Ele é utilizado para identificar o nr do pacote enviado
		 * para e retornado do CLP
		 */
		transacaoId = new Spinner(shlConstrutorDeComandos, SWT.BORDER);
		transacaoId.setBounds(221, 64, 191, 27);
		transacaoId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String tempS = transacaoId.getText();
				if (StringUtils.equals(tempS, "")) {
					tempS = "0";
				}
				int tempI = Integer.parseInt(tempS);
				if (tempI > 65535) {
					tempI = 65535;
					transacaoId.setSelection(65535);
				}
				lblTrIdHexa.setText("0x" + cCS.intHex(tempI));
				tempI = txtccaddrcopl.getCharCount();
				comprTransaInt.setText(Integer.toString(tempI));
				comprTransaHexa.setText("0x" + cCS.intHex(tempI));
			}
		});
		transacaoId.setMaximum(65535);

		lblTrIdHexa = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblTrIdHexa.setBounds(457, 69, 60, 17);
		lblTrIdHexa.setText("0x0000");

		Label lblProtocolo = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblProtocolo.setBounds(121, 96, 72, 17);
		lblProtocolo.setText("Protocolo:");

		Label lblProtoInt = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblProtoInt.setBounds(304, 96, 24, 17);
		lblProtoInt.setText("101");

		Label lblProtoHexa = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblProtoHexa.setBounds(457, 96, 60, 17);
		lblProtoHexa.setText("0x65");

		Label lblComprimento = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblComprimento.setBounds(5, 118, 188, 17);
		lblComprimento.setText("Comprimento da transação:");

		comprTransaInt = new Label(shlConstrutorDeComandos, SWT.NONE);
		comprTransaInt.setBounds(281, 118, 70, 17);
		comprTransaInt.setAlignment(SWT.CENTER);
		comprTransaInt.setText("0");

		comprTransaHexa = new Label(shlConstrutorDeComandos, SWT.NONE);
		comprTransaHexa.setBounds(457, 118, 60, 17);
		comprTransaHexa.setText("0x0000");

		Label reguaHoriz1 = new Label(shlConstrutorDeComandos, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		reguaHoriz1.setBounds(5, 140, 582, 2);

		Label lblClpId = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblClpId.setBounds(146, 152, 47, 17);
		lblClpId.setText("CLP ID:");

		/**
		 * Este spinner armazena o número do CLP. Esse número é  utilizado pelos
		 * aplicativos da Unitronics para identificar o CLP O valor em decimal e
		 * convertido para hexadecimal em seguida
		 * */
		clpID = new Spinner(shlConstrutorDeComandos, SWT.BORDER);
		clpID.setBounds(221, 147, 191, 27);
		clpID.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String tempS = clpID.getText();
				if (StringUtils.equals(tempS, "")) {
					tempS = "0";
				}
				int tempI = Integer.parseInt(tempS);
				if (tempI > 255) {
					tempI = 255;
					clpID.setSelection(255);
				}
				tempS = cCS.intHex(tempI).substring(2, 4);
				lblCLPID.setText("0x" + tempS);
				atualizaCampos();
			}
		});
		clpID.setMaximum(255);

		lblCLPID = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblCLPID.setBounds(457, 152, 60, 17);
		lblCLPID.setText("0x00");

		Label reguaHoriz2 = new Label(shlConstrutorDeComandos, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		reguaHoriz2.setBounds(5, 179, 582, 2);

		/**
		 * Essa sequencia de radio buttons serve para filtrar a lista de
		 * operacões disponíveis no drop down list
		 */

		/** Botao btnTodos lista todos comandos disponíveis */
		btnTodos = new Button(shlConstrutorDeComandos, SWT.RADIO);
		btnTodos.setBounds(221, 190, 67, 24);
		btnTodos.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("Clicou no botao todos");
					filtro = 0;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("todos");
					atualizaCampos();
					break;
				}
			}
		});
		btnTodos.setText("Todos");
		btnTodos.setSelection(true);

		/** Botao readInt mostra os comandos para leitura de inteiros */
		readInt = new Button(shlConstrutorDeComandos, SWT.RADIO);
		readInt.setBounds(417, 190, 111, 24);
		readInt.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("clicou no botao Read Integer");
					filtro = 2;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("leitura");
					atualizaCampos();
					break;
				}
			}
		});
		readInt.setText("Read Integer");

		Label lblFiltroDosComandos = new Label(shlConstrutorDeComandos,
				SWT.NONE);
		lblFiltroDosComandos.setBounds(51, 226, 142, 17);
		lblFiltroDosComandos.setText("Filtro dos comandos:");

		/** Botao readBit mostra os comandos para leitura de bits */
		readBit = new Button(shlConstrutorDeComandos, SWT.RADIO);
		readBit.setBounds(221, 223, 140, 24);
		readBit.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("clicou no botao Read Bit");
					filtro = 3;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("leitura");
					atualizaCampos();
					break;
				}
			}
		});
		readBit.setText("Read Bits");

		/** Botao writeInt mostra comandos de escrita de nrs inteiros */
		writeInt = new Button(shlConstrutorDeComandos, SWT.RADIO);
		writeInt.setBounds(417, 223, 115, 24);
		writeInt.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("clicou no botao Write Integer");
					filtro = 4;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("todos");
					atualizaCampos();
					break;
				}
			}
		});
		writeInt.setText("Write Integer");

		Label lblHoraClp = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblHoraClp.setBounds(939, 220, 62, 17);
		lblHoraClp.setText("Hora CLP");

		/** Botao setBit mostra comandos para escrita de bits */
		setBit = new Button(shlConstrutorDeComandos, SWT.RADIO);
		setBit.setBounds(221, 253, 77, 24);
		setBit.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("clicou no botao Set Bit");
					filtro = 5;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("todos");
					atualizaCampos();
					break;
				}
			}
		});
		setBit.setText("Set Bits");

		/** Botao rtc mostra comandos para operar o relógio do CLP */
		rtc = new Button(shlConstrutorDeComandos, SWT.RADIO);
		rtc.setBounds(417, 253, 140, 24);
		rtc.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseUp:
					// System.out.println ("clicou no botao RTC");
					filtro = 6;
					lComando = tL.listaComandos(filtro);
					comandoCLP.setItems(lComando[0]);
					comandoCLP.select(0);
					descricaoComando.setText(lComando[1][0]);
					enableDisable("clock");
					atualizaCampos();
					break;
				}
			}
		});
		rtc.setText("Real Time Clock");

		dateTimeCLP = new DateTime(shlConstrutorDeComandos, SWT.TIME);
		dateTimeCLP.setBounds(886, 250, 128, 27);
		dateTimeCLP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/**
				 * Apaga o campo complemento caso esteja lendo o calendario do
				 * CLP
				 */
				if (StringUtils.equals(comandoCLP.getText(), "RC")) {
					complemento.setText("");
				} else {
					complemento.setText(preparaHoraCLP() + preparaDataCLP());
				}
				System.out.println("hora do CLP: " + horaCLPS);
			}
		});
		preparaHoraCLP();

		Label lblComando = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblComando.setBounds(123, 289, 70, 17);
		lblComando.setText("Operação:");

		/**
		 * Combo comandoCLP apresenta os comandos disponíveis para serem
		 * enviados ao CLP
		 */
		comandoCLP = new Combo(shlConstrutorDeComandos, SWT.NONE);
		comandoCLP.setBounds(221, 284, 191, 29);
		/** Preenchendo o drop down do comando com o primeiro item da lista */
		comandoCLP.setItems(lComando[0]);
		comandoCLP.select(0);
		comandoCLP.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					System.out.println("clicou no botao dropsdown");
					complemento.setText("");
					descricaoComando.setText(lComando[1][comandoCLP
							.getSelectionIndex()]);
					/**
					 * Configura para trabalhar com relógio se comandoCLP for
					 * "RC" ou "SC"
					 */
					if (StringUtils.equals(comandoCLP.getText(), "RC")
							|| StringUtils.equals(comandoCLP.getText(), "SC")) {
						enableDisable("clock");
					} else {
						enableDisable("todos");
					}
					atualizaCampos();
					break;
				}
			}
		});

		descricaoComando = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		descricaoComando.setBounds(417, 284, 597, 28);
		descricaoComando.setFont(SWTResourceManager.getFont("Ubuntu", 11,
				SWT.NORMAL));

		Label reguaHoroiz3 = new Label(shlConstrutorDeComandos, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		reguaHoroiz3.setBounds(5, 317, 1014, 2);
		lblEndereco = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblEndereco.setBounds(84, 329, 109, 17);
		lblEndereco.setText("Endereço inicial:");

		/**
		 * Spinner address serve para armazenar o endereço inicial da operação
		 * escolhida.
		 * */
		address = new Spinner(shlConstrutorDeComandos, SWT.BORDER);
		address.setBounds(221, 324, 191, 27);

		address.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String tempS = address.getText();
				if (StringUtils.equals(tempS, "")) {
					tempS = "0";
				}
				int tempI = Integer.parseInt(tempS);
				if (tempI > 65535) {
					tempI = 65535;
					address.setSelection(65535);
				}
				lblEndInHexa.setText("0x" + cCS.intHex(tempI));
				atualizaCampos();
			}
		});
		address.setMaximum(65535);

		lblEndInHexa = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblEndInHexa.setBounds(457, 329, 60, 17);
		lblEndInHexa.setText("0x0000");

		Label reguaVert = new Label(shlConstrutorDeComandos, SWT.SEPARATOR
				| SWT.VERTICAL);
		reguaVert.setBounds(585, 324, 2, 227);

		Label lblHistComando = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblHistComando.setBounds(617, 329, 155, 17);
		lblHistComando.setText("Histórico de comandos");

		Label lblHistRespostas = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblHistRespostas.setBounds(835, 329, 152, 17);
		lblHistRespostas.setText("Histórico de respostas");

		lblTamanhoDoComando = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblTamanhoDoComando.setBounds(41, 361, 152, 17);
		lblTamanhoDoComando.setText("Tamanho do comando:");

		/**
		 * tamComando armazena a informação de quantos bits, bytes ou inteiros
		 * de 16 ou 32 bits tem o comando. O campo complemento deve conter a
		 * mesma quantidade de informação apontada no campo tamComando.
		 * */
		tamComando = new Spinner(shlConstrutorDeComandos, SWT.BORDER);
		tamComando.setBounds(221, 356, 191, 27);
		tamComando.setMaximum(254);
		tamComando.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				System.out.println("mudou o valor");
				String tempS = tamComando.getText();
				if (StringUtils.equals(tempS, "")) {
					tempS = "0";
				}
				int tempI = Integer.parseInt(tempS);
				if (tempI > 255) {
					tempI = 255;
					tamComando.setSelection(255);
				}
				tempS = cCS.intHex(tempI).substring(2, 4);
				if (!StringUtils.equals(txtTamanhoComando.getText(), "----")) {
					txtTamanhoComando.setText("0x" + tempS);
				}
				atualizaCampos();
			}
		});

		txtTamanhoComando = new Label(shlConstrutorDeComandos, SWT.NONE);
		txtTamanhoComando.setBounds(457, 361, 60, 17);
		txtTamanhoComando.setText("0x00");

		txtHistCmd = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtHistCmd.setBounds(592, 356, 206, 161);
		txtHistCmd.setEditable(true);
		txtHistCmd.setBackground(SWTResourceManager.getColor(255, 255, 153));

		txtHistResult = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtHistResult.setBounds(803, 356, 211, 161);
		txtHistResult.setEditable(true);
		txtHistResult.setBackground(SWTResourceManager.getColor(255, 255, 153));

		lblComplemento = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblComplemento.setBounds(93, 397, 100, 20);
		lblComplemento.setText("Complemento:");

		/**
		 * O campo complemento deve conter os dados de acordo com o comando
		 * selecionado (bit, byte, word 16 ou 32 bits). Deve-se consultar manual
		 * do fabricante para informações sobre o formato dos dados nesse campo.
		 * */
		complemento = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		complemento.setBounds(221, 388, 359, 39);
		complemento.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				/**
				 * Testando a validade do campo complemento. Se o complemento
				 * tiver o tamanho adequeado, é feita a apresentação de seu
				 * tamanho.
				 * */

				int tc = tamComando.getSelection();
				int pal = Integer.parseInt(lComando[3][comandoCLP
						.getSelectionIndex()]);
				int car = complemento.getCharCount();
				if (car > cart) {
					if (car % pal == 0) {
						tc = car / pal;
						cart = car;
					} else {
						txtTamanhoComando.setText("----");
					}
				}
				if (car < cart) {
					tc = car / pal;
					cart = tc * pal;
					System.out.println("ap");
					txtTamanhoComando.setText("----");
				}
				if (car == cart) {
					String tempS = cCS.intHex(tc).substring(2, 4);
					txtTamanhoComando.setText("0x" + tempS);
				}
				tamComando.setSelection(tc);
				atualizaCampos();
			}
		});
		complemento.setFont(SWTResourceManager
				.getFont("Ubuntu", 11, SWT.NORMAL));
		;
		;

		Label lblCheckSum = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblCheckSum.setBounds(117, 432, 76, 17);
		lblCheckSum.setText("Check Sum:");

		/** O label lblChkSumDec recebe o checksum em decimal */
		lblChkSumDec = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblChkSumDec.setBounds(304, 432, 24, 17);
		lblChkSumDec.setText("128");

		/** O label lblChkSumHea recebe o checksum em hexadecimal */
		lblChkSumHexa = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblChkSumHexa.setBounds(457, 432, 60, 17);
		lblChkSumHexa.setText("0x80");

		Label lblComandoCompleto = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblComandoCompleto.setBounds(95, 455, 98, 17);
		lblComandoCompleto.setText("Comando CLP:");

		/**
		 * O campo txtccaddrcopl recebe o comando completo, incluindo o checksum
		 */
		txtccaddrcopl = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.V_SCROLL);
		txtccaddrcopl.setBounds(221, 454, 359, 19);
		txtccaddrcopl.setEditable(false);

		Label lblRetornoDoClp = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblRetornoDoClp.setBounds(84, 478, 109, 17);
		lblRetornoDoClp.setText("Retorno do CLP:");

		/**
		 * O campo respostaCLP recebe a resposta do CLP ao comando enviado.
		 */
		respostaCLP = new Text(shlConstrutorDeComandos, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		respostaCLP.setBounds(221, 478, 359, 39);
		respostaCLP.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {

			}
		});
		respostaCLP.setEditable(false);
		respostaCLP.setBackground(SWTResourceManager.getColor(255, 255, 153));
		respostaCLP.setFont(SWTResourceManager
				.getFont("Ubuntu", 11, SWT.NORMAL));

		Label lblEndereoDeIp = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblEndereoDeIp.setBounds(90, 528, 103, 17);
		lblEndereoDeIp.setText("Endereço de IP:");

		/**
		 * O campo endIP deve ser configurado com o endereço de IP do CLP. Por
		 * default ele é configurado com o endereço que estou utilizando nos
		 * testes (192.168.2.50:20256)
		 * */
		endIP = new Text(shlConstrutorDeComandos, SWT.BORDER);
		endIP.setBounds(221, 523, 191, 27);
		endIP.setText("192.168.2.50:20256");
		/**
		 * O botão enviar opera em dois estágios. No mouseDown é feito
		 * diagnóstico de ping. Caso funcione, é calculado o checksum senão, é
		 * enviada uma mensagem de ping timeout.
		 * 
		 * No evento mouseUp, faz-se o tratamento do comando se ele for de
		 * relógio, calcula o checksum e é enviado o comando para o CLP. Se o
		 * comando for de leitura do relógio, o retorno do comando é direcionado
		 * para atualização do calendário e relógio.
		 * */
		btnEnviar = new Button(shlConstrutorDeComandos, SWT.NONE);
		btnEnviar.setBounds(417, 522, 140, 29);
		btnEnviar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				boolean x;
				complemento.setText(StringUtils.upperCase(complemento.getText()));
				respostaCLP.setText("");
				if (StringUtils.equals(endIP.getText(), "")) {
					endIP.setText("192.168.2.50:20256");
				}
				x = tcpclient.configIP(endIP.getText());
				if (x == true) {
					respostaCLP.setText("Ping Ok!");
					comando = cCS.calcCheckSum(
							lblCLPID.getText().substring(2, 4)
									+ comandoCLP.getText()
									+ lblEndInHexa.getText().substring(2, 6)
									+ txtTamanhoComando.getText().substring(2,
											4) + complemento.getText(),
							Integer.parseInt(transacaoId.getText()));
				} else {
					respostaCLP
							.setText("Ping timeout. Comando não foi enviado");
				}
			}

			public void mouseUp(MouseEvent e) {
				String tempS;
				/** Montando a string para calcular o checksum */
				if (StringUtils.equals(comandoCLP.getText(), "SC")
						|| StringUtils.equals(comandoCLP.getText(), "RC")) {
					/**
					 * Se o complemento estiver vazio, ele é preenchido com a
					 * hora do sistema local
					 */
					if (StringUtils.equals(comandoCLP.getText(), "SC")
							&& StringUtils.equals(complemento.getText(), "")) {
						System.out.println("SC + complemento vazio");
						DateFormat df = new SimpleDateFormat(
								"yy:MM:dd:HH:mm:ss");
						Date date = new Date();
						String[] data = StringUtils.split(df.format(date), ':');
						complemento.setText(data[5] + data[4] + data[3]
								+ dSem(date) + data[2] + data[1] + data[0]);
					}
					tempS = lblCLPID.getText().substring(2, 4)
							+ comandoCLP.getText() + complemento.getText();
				} else {
					tempS = lblCLPID.getText().substring(2, 4)
							+ comandoCLP.getText()
							+ lblEndInHexa.getText().substring(2, 6)
							+ txtTamanhoComando.getText().substring(2, 4)
							+ complemento.getText();
				}
				/** Calculando o checksum */
				comando = cCS.calcCheckSum(tempS,
						Integer.parseInt(transacaoId.getText()));

				/** Direcionando o comando para ser empacotado e enviado na rede */
				try {
					result = tcpclient.sendCLP(comando);
					if (StringUtils.equals("Ping Ok!", respostaCLP.getText())) {
						respostaCLP.setText(result);
						txtHistCmd.setText("/" + tempS + "\n"
								+ txtHistCmd.getText());
						txtHistResult.setText(result + "\n"
								+ txtHistResult.getText());
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/**
				 * Direcionando o resultado da transação para atualizar os dados
				 * do relógio, caso seja um comando de leitura de relógio
				 * */
				if (StringUtils.equals(comandoCLP.getText(), "RC")) {
					ah.acertaRelogioApp(StringUtils.substring(respostaCLP
							.getText(), 6, respostaCLP.getText().length() - 2),
							calendario, dateTimeCLP);
				}
			}
		});
		btnEnviar.setText("Enviar");

		btnRodarComandos = new Button(shlConstrutorDeComandos, SWT.CHECK);
		btnRodarComandos.setBounds(592, 524, 61, 24);
		btnRodarComandos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (btnRodarComandos.getSelection() == true) {
					enableDisable("rodar_on");
				}
				if (btnRodarComandos.getSelection() == false) {
					enableDisable("rodar_off");
				}
			}
		});
		btnRodarComandos.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnRodarComandos.setText("Loop");

		tempoMs = new Spinner(shlConstrutorDeComandos, SWT.BORDER);
		tempoMs.setBounds(658, 523, 80, 27);
		tempoMs.setPageIncrement(100);
		tempoMs.setMaximum(100000);
		tempoMs.setMinimum(1);
		tempoMs.setSelection(500);

		Label lblTempoms = new Label(shlConstrutorDeComandos, SWT.NONE);
		lblTempoms.setBounds(803, 528, 79, 17);
		lblTempoms.setText("Tempo (ms)");

		btnIniciar = new Button(shlConstrutorDeComandos, SWT.NONE);
		btnIniciar.setBounds(934, 523, 80, 29);
		btnIniciar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

				tj.inicializa(txtHistCmd, txtHistResult, endIP, transacaoId,
						tempoMs, btnRodarComandos, btnIniciar, btnEnviar,
						calendario, dateTimeCLP);
				tj.roda();
			}
		});
		btnIniciar.setEnabled(false);
		btnIniciar.setText("Iniciar");
		m_bindingContext = initDataBindings();
		enableDisable("todos");

		descricaoComando.setText(lComando[1][0]);

		ToolBar toolBar = new ToolBar(shlConstrutorDeComandos, SWT.FLAT
				| SWT.RIGHT);
		toolBar.setBounds(5, 0, 197, 27);

		ToolItem tltmAboutSobre = new ToolItem(toolBar, SWT.NONE);
		tltmAboutSobre.setText("About / Sobre");

	}

	/****************************************************************/

	/**
	 * Método para atualização dos campos do formulário. lComando[2][n] indica
	 * se o comando é de leitura ou gravação. '2' ou '3' são comandos de
	 * leitura.
	 * 
	 * @return String contendo o comando atualizado.
	 * */
	protected String atualizaCampos() {
		String tempS;
		if (!StringUtils.contains(respostaCLP.getText(), "Erro")) {
			respostaCLP.setText("");
		}
		if (StringUtils.equals(comandoCLP.getText(), "SC")
				|| StringUtils.equals(comandoCLP.getText(), "RC")) {
			tempS = lblCLPID.getText().substring(2, 4) + comandoCLP.getText()
					+ complemento.getText();
			txtccaddrcopl.setText("/" + tempS);
			int tempI = txtccaddrcopl.getCharCount() + 3;
			comprTransaInt.setText(Integer.toString(tempI));
			comprTransaHexa.setText("0x" + cCS.intHex(tempI));
			tempS = cCS.cSum(tempS);
			lblChkSumHexa.setText("0x" + tempS);
			txtccaddrcopl.setText(txtccaddrcopl.getText() + tempS);
			tempI = Integer.parseInt(tempS, 16);
			tempS = Integer.toString(tempI);
			lblChkSumDec.setText(tempS);

		} else {
			if (StringUtils.equals(lComando[2][comandoCLP.getSelectionIndex()],
					"2")
					|| StringUtils.equals(
							lComando[2][comandoCLP.getSelectionIndex()], "3")) {
				System.out.println("leitura");
				tempS = lblCLPID.getText().substring(2, 4)
						+ comandoCLP.getText()
						+ lblEndInHexa.getText().substring(2, 6)
						+ txtTamanhoComando.getText().substring(2, 4);
				txtccaddrcopl.setText("/" + tempS);
				int tempI = txtccaddrcopl.getCharCount() + 3;
				comprTransaInt.setText(Integer.toString(tempI));
				comprTransaHexa.setText("0x" + cCS.intHex(tempI));
				tempS = cCS.cSum(tempS);
				lblChkSumHexa.setText("0x" + tempS);
				txtccaddrcopl.setText(txtccaddrcopl.getText() + tempS);
				tempI = Integer.parseInt(tempS, 16);
				tempS = Integer.toString(tempI);
				lblChkSumDec.setText(tempS);

			} else {
				tempS = lblCLPID.getText().substring(2, 4)
						+ comandoCLP.getText()
						+ lblEndInHexa.getText().substring(2, 6)
						+ txtTamanhoComando.getText().substring(2, 4)
						+ complemento.getText();
				txtccaddrcopl.setText("/" + tempS);
				int tempI = txtccaddrcopl.getCharCount() + 3;
				comprTransaInt.setText(Integer.toString(tempI));
				comprTransaHexa.setText("0x" + cCS.intHex(tempI));
				tempS = cCS.cSum(tempS);
				lblChkSumHexa.setText("0x" + tempS);
				txtccaddrcopl.setText(txtccaddrcopl.getText() + tempS);
				tempI = Integer.parseInt(tempS, 16);
				tempS = Integer.toString(tempI);
				lblChkSumDec.setText(tempS);
			}
		}
		return tempS;
	}

	/****************************************************************/

	/**
	 * Método para preparar a data do CLP a partir do valor selecionado no
	 * calendário. Note que o Java enumera os meses de '0' à '11' e o CLP
	 * Unitronics enumera os meses de '1' à '12'.
	 * 
	 * @return String contendo a data no formato do CLP.
	 * */
	private String preparaDataCLP() {
		/** Pegando a data do calendário */
		String diaS = StringUtils.leftPad(
				Integer.toString(calendario.getDay()), 2, '0');
		String mesS = StringUtils.leftPad(
				Integer.toString(calendario.getMonth() + 1), 2, '0');
		String anoS = Integer.toString(calendario.getYear());
		String data_info = diaS + "/" + mesS + "/" + anoS;
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date data = new Date();
		try {
			data = formato.parse(data_info);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String diaSemana = dSem(data);
		/** Concatenando a data */
		dataCLPS = diaSemana + diaS + mesS + StringUtils.substring(anoS, 2, 4);
		hrDtCLPS = horaCLPS + dataCLPS;
		System.out.println("Hora + Data CLP: " + hrDtCLPS + "\n" + "Hora CLP: "
				+ horaCLPS + "\n" + "Data CLP: " + dataCLPS);
		return dataCLPS;
	}

	/****************************************************************/

	/**
	 * Método para preparar a hora do CLP
	 * 
	 * @return String contendo a hora no formato do CLP.
	 * */
	private String preparaHoraCLP() {
		String hourS = StringUtils.leftPad(
				Integer.toString(dateTimeCLP.getHours()), 2, '0');
		String minS = StringUtils.leftPad(
				Integer.toString(dateTimeCLP.getMinutes()), 2, '0');
		String secS = StringUtils.leftPad(
				Integer.toString(dateTimeCLP.getSeconds()), 2, '0');
		horaCLPS = secS + minS + hourS;
		hrDtCLPS = horaCLPS + dataCLPS;
		System.out.println("Hora + Data CLP: " + hrDtCLPS + "\n" + "Hora CLP: "
				+ horaCLPS + "\n" + "Data CLP: " + dataCLPS);
		return horaCLPS;
	}

	/****************************************************************/

	/**
	 * Método para alternar a visibilidade dos campos de acordo com a operação
	 * selecionada.
	 * 
	 * @param chave
	 *            String para definir quais telas serão habilitadas.
	 * */
	private void enableDisable(String chave) {

		switch (chave) {
		case "clock":
			if (StringUtils.equals(comandoCLP.getText(), "RC")) {
				complemento.setVisible(false);
				lblComplemento.setVisible(false);
			} else {
				complemento.setVisible(true);
				lblComplemento.setVisible(true);
			}
			address.setSelection(0);
			address.setVisible(false);
			tamComando.setSelection(0);
			tamComando.setVisible(false);
			lblEndInHexa.setVisible(false);
			txtTamanhoComando.setVisible(false);
			lblEndereco.setVisible(false);
			lblTamanhoDoComando.setVisible(false);
			calendario.setEnabled(true);
			dateTimeCLP.setEnabled(true);
			atualizaCampos();
			break;

		case "todos":
			if (StringUtils.equals(lComando[2][comandoCLP.getSelectionIndex()],
					"2")
					|| StringUtils.equals(
							lComando[2][comandoCLP.getSelectionIndex()], "3")) {
				complemento.setVisible(false);
				lblComplemento.setVisible(false);
			} else {
				complemento.setVisible(true);
				lblComplemento.setVisible(true);
			}
			if (StringUtils.equals(lComando[2][comandoCLP.getSelectionIndex()],
					"4")
					|| StringUtils.equals(
							lComando[2][comandoCLP.getSelectionIndex()], "5")) {
				tamComando.setEnabled(false);
			} else {
				tamComando.setEnabled(true);
			}
			address.setSelection(0);
			address.setVisible(true);
			tamComando.setSelection(0);
			tamComando.setVisible(true);
			lblEndInHexa.setVisible(true);
			txtTamanhoComando.setVisible(true);
			lblEndereco.setVisible(true);
			lblTamanhoDoComando.setVisible(true);
			calendario.setEnabled(false);
			dateTimeCLP.setEnabled(false);
			break;
		case "leitura":
			complemento.setVisible(false);
			lblComplemento.setVisible(false);
			complemento.setText("");
			tamComando.setEnabled(true);
			address.setSelection(0);
			address.setVisible(true);
			tamComando.setSelection(0);
			tamComando.setVisible(true);
			lblEndInHexa.setVisible(true);
			txtTamanhoComando.setVisible(true);
			lblEndereco.setVisible(true);
			lblTamanhoDoComando.setVisible(true);
			calendario.setEnabled(false);
			dateTimeCLP.setEnabled(false);
			break;
		case "rodar_on":
			btnEnviar.setEnabled(false);
			btnIniciar.setEnabled(true);
			calendario.setEnabled(true);
			dateTimeCLP.setEnabled(true);
			transacaoId.setEnabled(false);
			clpID.setEnabled(false);
			btnTodos.setEnabled(false);
			readBit.setEnabled(false);
			setBit.setEnabled(false);
			readInt.setEnabled(false);
			writeInt.setEnabled(false);
			rtc.setEnabled(false);
			descricaoComando.setEnabled(false);
			comandoCLP.setEnabled(false);
			address.setEnabled(false);
			tamComando.setEnabled(false);
			complemento.setEnabled(false);
			txtccaddrcopl.setEnabled(false);
			respostaCLP.setEnabled(false);
			endIP.setEnabled(false);
			break;
		case "rodar_off":
			btnEnviar.setEnabled(true);
			btnIniciar.setEnabled(false);
			if (!StringUtils.equals(comandoCLP.getText(), "RC")
					&& !StringUtils.equals(comandoCLP.getText(), "SC")) {
				calendario.setEnabled(false);
				dateTimeCLP.setEnabled(false);
			}
			transacaoId.setEnabled(true);
			clpID.setEnabled(true);
			btnTodos.setEnabled(true);
			readBit.setEnabled(true);
			setBit.setEnabled(true);
			readInt.setEnabled(true);
			writeInt.setEnabled(true);
			rtc.setEnabled(true);
			descricaoComando.setEnabled(true);
			comandoCLP.setEnabled(true);
			address.setEnabled(true);
			tamComando.setEnabled(true);
			complemento.setEnabled(true);
			txtccaddrcopl.setEnabled(true);
			respostaCLP.setEnabled(true);
			endIP.setEnabled(true);
			break;

		default:
			complemento.setVisible(true);
			lblComplemento.setVisible(true);
			tamComando.setEnabled(true);
			address.setSelection(0);
			address.setVisible(true);
			tamComando.setSelection(0);
			tamComando.setVisible(true);
			lblEndInHexa.setVisible(true);
			txtTamanhoComando.setVisible(true);
			lblEndereco.setVisible(true);
			lblTamanhoDoComando.setVisible(true);
			calendario.setEnabled(false);
			dateTimeCLP.setEnabled(false);
			break;
		}
		return;
	}

	/****************************************************************/

	/**
	 * Método para definir o dia da semana.
	 * <p>
	 * 01 - Domingo 02 - Segunda 03 - Terça 04 - Quarta 05 - Quinta 06 - Sexta
	 * 07 - Sábado
	 * </p>
	 * 
	 * @param data
	 *            Recebe um objeto de data
	 * @return Retorna uma string contendo o número do dia na semana.
	 * */
	private String dSem(Date data) {
		/** Pegando o dia da semana */
		DateFormat formaDiaSemana = new SimpleDateFormat("u");
		String diaSemana = formaDiaSemana.format(data);
		/** Formatando o dia da semana de acordo com o padrão do CLP Unitronics */
		int nr = Integer.parseInt(diaSemana);
		if (nr == 7) {
			nr = nr - 6;
		} else {
			nr = nr + 1;
		}
		diaSemana = "0" + Integer.toString(nr);
		return diaSemana;
	}

	/****************************************************************/
	/**
	 * @return bindingContext
	 * */
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();

		return bindingContext;
	}

	/****************************************************************/

}
