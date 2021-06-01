/**
 * 
 */
package simdeVLIWLite;

import java.util.Scanner;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;


/**
 * @author Iv�n Castilla
 *
 */
public class VLIWSimulatorLite {

	/**
	 * Crea una configuraci�n de m�quina a partir de una lista de n�meros separados por comas. Asume que solo hay una de salto siempre.
	 * @param config lista de n�meros separados por comas donde cada n�mero corresponde a un tipo de unidad funcional, en el orden en que est�n definidas en {@link FunctionalUnit}
	 * @return
	 */
	private static TreeMap<FunctionalUnit, Integer> getConfiguration(String config) {
		final TreeMap<FunctionalUnit, Integer> configuration = new TreeMap<>();
		final Scanner scan = new Scanner(config);
		scan.useDelimiter(",");
		// Se asume que la de salto siempre es la �ltima
		for (int i = 0; i < FunctionalUnit.values().length - 1; i++) {
			if (scan.hasNextInt())
				configuration.put(FunctionalUnit.values()[i],  scan.nextInt());
			else
				// Por defecto, 1
				configuration.put(FunctionalUnit.values()[i],  1);
		}
		configuration.put(FunctionalUnit.JUMP,  1);		
		scan.close();
		return configuration;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final Arguments args1 = new Arguments();
			JCommander jc = JCommander.newBuilder()
					  .addObject(args1)
					  .build();
			jc.parse(args);
			
			final Code code = Code.loadCode(args1.fileName + ".pla");
			final TreeMap<FunctionalUnit, Integer> configuration = getConfiguration(args1.config);
			final VLIWCode vliwcode = VLIWCode.loadCode(configuration, code, args1.fileName + ".vliw");
			System.out.println(vliwcode);
			final VLIWMachine machine = new VLIWMachine(configuration);
			if (args1.memFileName != null)
				machine.loadMemoryAndRegisters(args1.memFileName);
			machine.setDebugMode(args1.debug);
			machine.printMemoryAndRegisters();
			int cycles = machine.execute(vliwcode);
			machine.printMemoryAndRegisters();
			System.out.println("Total ciclos: " + cycles);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}
	}

	
	private static class Arguments {
		@Parameter(names ={"--input", "-i"}, description = "Nombre de los ficheros de c�digo (si es X, deber�a existir un fichero X.pla y otro X.vliw", order = 1, required = true)
		private String fileName = "D://Mi unidad//Docencia//Arquitectura de computadores//SIMDE v1.4//Test//bucle";
		@Parameter(names ={"--config", "-c"}, description = "Configuraci�n de la m�quina VLIW, expresado como n�mero de UF de cada tipo, separadas por comas: <#SUMA_ENTERA,#MULT_ENTERA,#SUMA_FP,#MULT_FP,#MEMORIA>. Por defecto son dos de cada tipo. Siempre hay una �nica de salto.", order = 2)
		private String config = "2,2,2,2,2";
		@Parameter(names ={"--mem", "-m"}, description = "Nombre del fichero de configuraci�n de memoria y registros", order = 3)
		private String memFileName = null;
		@Parameter(names ={"--debug", "-d"}, description = "Habilita el modo de debug", order = 6)
		private boolean debug = false;
	}
	
}
