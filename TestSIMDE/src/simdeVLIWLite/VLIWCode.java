/**
 * 
 */
package simdeVLIWLite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import simdeVLIWLite.LongInstruction.LongInstructionOperation;

/**
 * C�digo de instrucciones largas de la m�quina simulada
 * @author Iv�n Castilla
 *
 */
public class VLIWCode {
	/** Lista ordenada de las instrucciones largas */
	private final ArrayList<LongInstruction> longInstructions;
	/** N�mero de unidades funcionales de cada tipo que incluye la m�quina simulada */
	private final TreeMap<FunctionalUnit, Integer> configuration;
	/** Una instrucci�n larga nula */
	private final LongInstruction NOPLongInstruction;

	/**
	 * Crea un c�digo de instrucciones largas con la configuraci�n de unidades funcionales indicada
	 * @param configuration N�mero de unidades funcionales de cada tipo que incluye la m�quina simulada
	 */
	public VLIWCode(TreeMap<FunctionalUnit, Integer> configuration) {
		longInstructions = new ArrayList<>();
		this.configuration = configuration;
		NOPLongInstruction = new LongInstruction(configuration);
	}
	
	/**
	 * Crea y a�ade una instrucci�n larga al final de la lista de instrucciones largas
	 * @return La instrucci�n larga creada
	 */
	public LongInstruction addLongInstruction() {
		LongInstruction longInst = new LongInstruction(configuration); 
		longInstructions.add(longInst);
		return longInst;
	}
	
	/**
	 * Devuelve la instrucci�n larga que ocupa la posici�n "index"
	 * @param index Posici�n en el c�digo de instrucciones largas
	 * @return Instrucci�n larga que ocupa la posici�n "index"
	 */
	public LongInstruction getInstruction(int index) {
		if (index >= longInstructions.size())
			return NOPLongInstruction;
		return longInstructions.get(index);
	}
	
	/**
	 * Devuelve verdadero si es una instrucci�n larga de parada de ejecuci�n.
	 * En la implementaci�n actual, esto no es estrictamente una instrucci�n de parada, sino un indicador de que ya no quedan m�s instrucciones por ejecutar 
	 * @param index Posici�n en el c�digo de instrucciones largas
	 * @return Verdadero si es una instrucci�n larga de parada de ejecuci�n.
	 */
	public boolean isHalt(int index) {
		return (index >= longInstructions.size());
	}
	
	/**
	 * Carga el c�digo de instrucciones largas desde un fichero definido previamente con la versi�n de escritorio de SIMDE o SIMDEWeb 
	 * @param configuration N�mero de unidades funcionales de cada tipo que incluye la m�quina simulada
	 * @param code C�digo secuencial original
	 * @param fileName Nombre del fichero que contiene la planificaci�n de las instrucciones largas
	 * @return C�digo de instrucciones largas creado
	 */
	public static VLIWCode loadCode(TreeMap<FunctionalUnit, Integer> configuration, Code code, String fileName) throws FileNotFoundException {
		final File vliwFile = new File(fileName);
		final VLIWCode vliwcode = new VLIWCode(configuration);
		Scanner scan;
		scan = new Scanner(vliwFile);
		int n = scan.nextInt();
		for (int i = 0; i < n; i++) {
			final LongInstruction longInst = vliwcode.addLongInstruction();
			int noper = scan.nextInt();
	        for (int j = 0; j < noper; j++) {
	        	int ind = scan.nextInt();
	        	// Se ignora el tipo: Se asume que es correcto
	        	scan.nextInt();
	        	int idFU = scan.nextInt();
	        	int pred = scan.nextInt();

	            final Instruction inst = code.getInstructions().get(ind);
	            if (FunctionalUnit.JUMP.equals(inst.getOpcode().getFU())) {
	            	int destination = scan.nextInt();
		            int predTrue = scan.nextInt();
		            int predFalse = scan.nextInt();
	            	longInst.addJumpInstruction(inst, pred, predTrue, predFalse, destination);
	            }
	            else
	            	longInst.addInstruction(inst, idFU, pred);
	        }
		}
		scan.close();
		return vliwcode;		
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (FunctionalUnit fu : FunctionalUnit.values()) {
			for (int i = 0; i < configuration.get(fu); i++) {
				str.append(fu + "_" + i + "\t");
			}
		}
		str.append(System.lineSeparator());
		for (LongInstruction longInst : longInstructions) {
			for (FunctionalUnit fu : FunctionalUnit.values()) {
				for (int i = 0; i < configuration.get(fu); i++) {
					LongInstructionOperation oper = longInst.getOperation(fu, i);
					str.append(LongInstruction.NOP.equals(oper) ? "_\t" : (oper.getInstruction().getId() + "\t"));
				}
			}			
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
}
