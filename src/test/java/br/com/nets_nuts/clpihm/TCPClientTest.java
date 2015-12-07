package br.com.nets_nuts.clpihm;

import org.apache.commons.lang3.StringUtils;

import junit.framework.TestCase;

public class TCPClientTest extends TestCase {
	private String checkSend,checkReturn;

	public TCPClientTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTCPClient(){
		String result;
		String comando;
		TCPClient tcpclient = new TCPClient();
		try {
			
	/**
	 * Este teste pega uma palavra válida de comando, faz o cálculo
	 * do checksum e envia para o CLP acionando a classe TCPClient.
	 * 
	 * Estes comandos...
	 * comando = ccsum.calcCheckSum("00SA00000C101010101010",12874);
	 * comando = ccsum.calcCheckSum("00SA00000C010101010101",12874);
	 * comando = ccsum.calcCheckSum("00RA00000C",12874);
	 * comando = ccsum.calcCheckSum("00RC",12874);
	 * 
	 * Após o cálculo do checksum (ccsum), assumem esses valores:
	 * tcpclient.sendCLP("/00SA00000C1010101010106D");
	 * tcpclient.sendCLP("/00SA00000C0101010101016D");
	 * tcpclient.sendCLP("/00RA00000C26");
	 * tcpclient.sendCLP("/00RCF5");
	 * */

			
			CalcCheckSum ccsum = new CalcCheckSum();
			comando = ccsum.calcCheckSum("00SA00000C101010101010",12874);
			
			result = tcpclient.sendCLP(comando);
			System.out.println("Resultado retornado pela classe TCPClient: " + result );
			
			checkSend = StringUtils.substring(comando,8,12);
			checkReturn = StringUtils.substring(result,2,6);	
	
			assertEquals(checkReturn, checkSend);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals(true, false);
		}
		
	}
}
