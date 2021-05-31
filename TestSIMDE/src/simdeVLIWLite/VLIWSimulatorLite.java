/**
 * 
 */
package simdeVLIWLite;

import java.util.Scanner;
import java.util.TreeMap;

/**
 * @author Iván Castilla
 *
 */
public class VLIWSimulatorLite {

	/**
	 * Crea una configuración de máquina a partir de una lista de números separados por comas
	 * @param config lista de números separados por comas donde cada número corresponde a un tipo de unidad funcional, en el orden en que están definidas en {@link FunctionalUnit}
	 * @return
	 */
	private static TreeMap<FunctionalUnit, Integer> getConfiguration(String config) {
		final TreeMap<FunctionalUnit, Integer> configuration = new TreeMap<>();
		final Scanner scan = new Scanner(config);
		scan.useDelimiter(",");
		for (FunctionalUnit fu : FunctionalUnit.values()) {
			configuration.put(fu,  scan.nextInt());
		}
		scan.close();
		return configuration;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean debug = true;
//		Code.debugFile("G://Mi unidad//Docencia//Arquitectura de computadores//SIMDE v1.4//Test//bucle.pla");
		Code code = Code.loadCode("G://Mi unidad//Docencia//Arquitectura de computadores//SIMDE v1.4//Test//bucle4.pla");
		TreeMap<FunctionalUnit, Integer> configuration = getConfiguration("2,2,2,2,2,1");
		VLIWCode vliwcode = VLIWCode.loadCode(configuration, code, "G://Mi unidad//Docencia//Arquitectura de computadores//SIMDE v1.4//Test//bucle4.vliw");
		System.out.println(vliwcode);
		VLIWMachine machine = new VLIWMachine(configuration);
		machine.loadMemoryAndRegisters("G://Mi unidad//Docencia//Arquitectura de computadores//SIMDE v1.4//Test//bucle2.mem");
		machine.setDebugMode(debug);
		machine.printMemoryAndRegisters();
		int cycles = machine.execute(vliwcode);
		machine.printMemoryAndRegisters();
		System.out.println("Total ciclos: " + cycles);
//		if (args.length < 2)
//			System.err.println("Please select a file with a VLIW code");
//		else {
//			VLIWSimulatorLite sim = new VLIWSimulatorLite();
//			sim.loadCode(args[1]);
//		}

	}

}
