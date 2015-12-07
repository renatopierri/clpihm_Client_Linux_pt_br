package br.com.nets_nuts.clpihm;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.DateTime;
/**
 * Classe para acertar a hora.<br>
 * Esta classe recebe dois objetos SWT DateTime,um configurado no modo e
 * calendário e o outro configurado no modo relógio e faz o acerto de ambos de acordo com a string daHora.
 * 
 * @author Renato de Pierri
 * */
public class AcertaHora {
	private DateTime calendario, dateTimeCLP;

	public AcertaHora() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * * Classe para acertar a hora.<br>
	 * Esta classe recebe dois objetos SWT DateTime,um configurado no modo e
	 * calendário e o outro configurado no modo relógio e faz o acerto de ambos de acordo com a string daHora.
	 * 
	 * @param daHora
	 *            String contendo data e hora a ser populada nos controles
	 * @param cal
	 *            Objeto DateTime representando o calendário.
	 * @param dT
	 *            Objeto DateTime representando o relógio.
	 *
	 * */
	protected void acertaRelogioApp(String daHora, DateTime cal, DateTime dT) {
		dateTimeCLP = dT;
		calendario = cal;
		if (daHora.length() == 14) {
			dateTimeCLP.setTime(
					Integer.parseInt(StringUtils.substring(daHora, 4, 6)),
					Integer.parseInt(StringUtils.substring(daHora, 2, 4)),
					Integer.parseInt(StringUtils.substring(daHora, 0, 2)));
			calendario
					.setDate(Integer.parseInt(StringUtils.substring(daHora, 12,
							14)) + 2000, Integer.parseInt(StringUtils
							.substring(daHora, 10, 12)) - 1, Integer
							.parseInt(StringUtils.substring(daHora, 8, 10)));
		}
	}
}
