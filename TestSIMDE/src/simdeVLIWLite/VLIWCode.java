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
 * @author Iván Castilla
 *
 */
public class VLIWCode {
	private final ArrayList<LongInstruction> longInstructions;
	private final TreeMap<FunctionalUnit, Integer> configuration;
	private final LongInstruction NOPLongInstruction;
	
	/**
	 * 
	 */
	public VLIWCode(TreeMap<FunctionalUnit, Integer> configuration) {
		longInstructions = new ArrayList<>();
		this.configuration = configuration;
		NOPLongInstruction = new LongInstruction(configuration);
	}
	
	public LongInstruction addLongInstruction() {
		LongInstruction longInst = new LongInstruction(configuration); 
		longInstructions.add(longInst);
		return longInst;
	}
	
	public LongInstruction getInstruction(int index) {
		if (index >= longInstructions.size())
			return NOPLongInstruction;
		return longInstructions.get(index);
	}
	
	public boolean isHalt(int index) {
		return (index >= longInstructions.size());
	}
	
	public static VLIWCode loadCode(TreeMap<FunctionalUnit, Integer> configuration, Code code, String fileName) {
		final File vliwFile = new File(fileName);
		final VLIWCode vliwcode = new VLIWCode(configuration);
		Scanner scan;
		try {
			scan = new Scanner(vliwFile);
			int n = scan.nextInt();
			for (int i = 0; i < n; i++) {
				final LongInstruction longInst = vliwcode.addLongInstruction();
				int noper = scan.nextInt();
		        for (int j = 0; j < noper; j++) {
		        	int ind = scan.nextInt();
		        	int tipo = scan.nextInt();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
