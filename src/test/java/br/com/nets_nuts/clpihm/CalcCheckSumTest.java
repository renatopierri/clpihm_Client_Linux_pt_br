package br.com.nets_nuts.clpihm;

import junit.framework.TestCase;

public class CalcCheckSumTest extends TestCase {

	public CalcCheckSumTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testCalcCheckSum(){
/**
 * Esse teste assegura que o checksum seja validado.
 * Para tal é enviado uma palavra de comando do CLP
 * (00RA00000C) junto com o número da transação (12784).
 * A variável 'resultado' retorna a palavra com o checksum
 * calculado, que é comparada com uma palavra padrão.
 * */		
		String resultado ="";
		CalcCheckSum calcSum = new CalcCheckSum();
		resultado = calcSum.calcCheckSum("00RA00000C",12874);
		
		String cabecalho = new String (new byte[] {0x07,0x4A,0x32,0x65,0x00,0x0e,0x00});
		String fim = new String (new byte[] {0x0D});
		
		assertEquals(true, resultado.equals(cabecalho + "/00RA00000C26" + fim));
		
	}

}
