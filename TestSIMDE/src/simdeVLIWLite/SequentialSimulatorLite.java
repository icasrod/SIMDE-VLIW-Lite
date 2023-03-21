/**
 * 
 */
package simdeVLIWLite;

import java.io.IOException;
import java.util.Scanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;


/**
 * Una versi�n simple de simulador secuencial basado en SIMDE. Se trata de un port para java que simplemente ejecuta c�digos creados en SIMDE.
 * La aplicaci�n necesita un c�digo secuencial en pseudoMIPS de SIMDE (generalmente en un fichero .pla). Opcionalmente requiere una inicializaci�n 
 * de la memoria y registros (fichero .mem).
 * El simulador ejecuta el c�digo y devuelve el total de ciclos de ejecuci�n. 
 * Tambi�n permite a�adir un porcentaje de fallos de cach�, en cuyo caso lanza varias r�plicas y devuelve media, desviaci�n est�ndar, m�ximo y m�nimo...  
 * La aplicaci�n asume que el c�digo secuencial y su planificaci�n est�n correctamente construidas y no continen errores. 
 * @author Iv�n Castilla
 *
 */
public class SequentialSimulatorLite {

	/**
	 * Crea una configuraci�n de latencias de la  m�quina a partir de una lista de n�meros separados por comas. 
	 * @param latencies lista de n�meros separados por comas donde cada n�mero corresponde a la latencia de un tipo de unidad funcional, en el orden en que est�n definidas en {@link FunctionalUnit}
	 * @return
	 */
	private static int[] getLatencies(String latencies) throws ParameterException {
		final int[] configuration = new int[FunctionalUnit.values().length];
		final Scanner scan = new Scanner(latencies);
		scan.useDelimiter(",");
		for (int i = 0; i < FunctionalUnit.values().length; i++) {
			if (scan.hasNextInt()) {
				int lat = scan.nextInt();
				if (lat < 1) {
					scan.close();
					throw new ParameterException("ERROR: La latencia de una unidad funcional no puede ser menor que 1. Intentando asignar " + lat);
				}
				configuration[i] =  lat;
			}
			else
				// Por defecto
				configuration[i] =  FunctionalUnit.values()[i].getDefaultLatency();
		}
		scan.close();
		return configuration;
	}
	
	/**
	 * Lanza la aplicaci�n
	 * @param args Argumentos de la aplicaci�n, definidos en {@link Arguments}
	 */
	public static void main(String[] args) {
		try {
			final Arguments args1 = new Arguments();
			JCommander jc = JCommander.newBuilder()
					  .addObject(args1)
					  .build();
			jc.parse(args);
			
			if(args1.cacheMissRate < 0 || args1.cacheMissRate > 100)
				throw new ParameterException("ERROR: El porcentaje de fallos de cach� debe ser un n�mero entre 0 y 100. Usado: " + args1.cacheMissRate);
			int []latencies = getLatencies(args1.latencies);
			final SequentialMachine machine = new SequentialMachine(latencies, args1.cacheMissRate, args1.cacheMissPenalty);
			machine.setDebugMode(args1.debug);
			final Code code = Code.loadCode(args1.fileName);
			if (args1.debug)
				System.out.println(code);
			// Si no hay fallos de cach�, lanzo una �nica simulaci�n
			if (args1.cacheMissRate == 0) {
				if (args1.memFileName != null)
					machine.loadMemoryAndRegisters(args1.memFileName);
				if (args1.debug)
					machine.printMemoryAndRegisters();
				int cycles = machine.execute(code);
				if (args1.debug)
					machine.printMemoryAndRegisters();
				System.out.println("Total ciclos: " + cycles);
			}
			// En otro caso, lanzo tantas como indique el par�metro cacheMissSimul
			else {
				if(args1.cacheMissSimul < 1)
					throw new ParameterException("ERROR: El n�mero de r�plicas a lanzar cuando el porcentaje de fallos de cach� es mayor que 0 debe ser mayor que 0. Usado: " + args1.cacheMissSimul);
				final int[] results = new int[args1.cacheMissSimul];
				for (int i = 0; i < args1.cacheMissSimul; i++) {
					if (args1.debug)
						System.out.println("Simulando r�plica " + i);
					machine.reset();
					if (args1.memFileName != null) {
						machine.loadMemoryAndRegisters(args1.memFileName);
					}
					results[i] = machine.execute(code);
					if (args1.debug)
						machine.printMemoryAndRegisters();
				}
				double promedio = Statistics.average(results);
				double sd = Statistics.stdDev(results, promedio);
				double []ci = Statistics.normal95CI(promedio, sd, args1.cacheMissSimul);
				System.out.println("N�mero r�plicas: " + args1.cacheMissSimul);
				System.out.println("Ciclos Promedio (Desv. Est.): " + promedio + " (" + sd + ")");
				System.out.println("Ciclos IC95%: [" + ci[0] + ", " + ci[1] + "]");
				System.out.println("Ciclos [min-max]: [" + Statistics.min(results) + " - " + Statistics.max(results) + "]");
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		} catch (SIMDEException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Argumentos de la l�nea de comandos del programa
	 * @author Iv�n Castilla
	 *
	 */
	private static class Arguments {
		@Parameter(names ={"--source", "-s"}, description = "Nombre del fichero con el fuente (generalmente con extensi�n pla)", order = 1, required = true)
		private String fileName = null;
		@Parameter(names ={"--latencies", "-l"}, description = "Latencias de cada unidad funcional de la m�quina secuencial, expresado como una lista separadas por comas: <#SUMA_ENTERA,#MULT_ENTERA,#SUMA_FP,#MULT_FP,#MEMORIA,#SALTO>.", order = 2)
		private String latencies = "1,2,4,6,4,2";
		@Parameter(names ={"--mem", "-m"}, description = "Nombre del fichero de configuraci�n de memoria y registros", order = 3)
		private String memFileName = null;
		@Parameter(names ={"--debug", "-d"}, description = "Habilita el modo de debug", order = 4)
		private boolean debug = false;
		@Parameter(names ={"--cachemissrate", "-cmr"}, description = "Porcentaje de fallos de cach� (un n�mero entero entre 0 y 100)", order = 5)
		private int cacheMissRate = 0;
		@Parameter(names ={"--cachemisspenalty", "-cmp"}, description = "Latencia ADICIONAL cuando se produce un fall� de cach�", order = 5)
		private int cacheMissPenalty = 5;
		@Parameter(names ={"--cachemisssimul", "-cms"}, description = "N�mero de r�plicas a lanzar cuando se pone un porcentaje de fallos de cach� > 0", order = 5)
		private int cacheMissSimul = 20;
	}
	
}
