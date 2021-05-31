/**
 * 
 */
package simdeVLIWLite;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author Iván Castilla
 *
 */
public class Code {
	private final ArrayList<Instruction> instructions;
	private final TreeMap<String, Integer> labels;
	
	/**
	 * 
	 */
	public Code() {
		instructions = new ArrayList<>();
		labels = new TreeMap<>();
	}

	public void addInstruction(Instruction inst) {
		instructions.add(inst);
	}
	
	public void addLabel(String label, int line) {
		labels.put(label, line);			
	}
	
	public void finishCode() {
		for (Instruction inst : instructions) {
			if (FunctionalUnit.JUMP.equals(inst.getOpcode().getFU())) {
				inst.getOp()[2] = labels.get(inst.getStrOp()[2]);
			}
		}
	}

	/**
	 * @return the n
	 */
	public int getNLines() {
		return instructions.size();
	}

	/**
	 * @return the instructions
	 */
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}

	/**
	 * @return the labels
	 */
	public TreeMap<String, Integer> getLabels() {
		return labels;
	}

	public static int processOperand(OperandType opType, SIMDELexer lexer, String[] strOps, int[] ops, int nop) {
		int opsCreated = 0;
		try {
			final Tokens token = lexer.yylex();
			final String text = lexer.yytext();
			switch(opType) {
			case ADDRESS:
				if (Tokens.LEXDIRECCION.equals(token)) {
					int[] aux = opType.assemble(text);
					ops[nop] = aux[0];
					ops[nop+1] = aux[1];
					opsCreated = 2;
					strOps[nop] = text;
					strOps[nop+1] = "";
				}
				break;
			case FPREGISTER:
				if (Tokens.LEXREGFP.equals(token)) {
					ops[nop] = opType.assemble(text)[0];
					strOps[nop] = text;
					opsCreated = 1;
				}
				break;
			case GPREGISTER:
				if (Tokens.LEXREGGP.equals(token)) {
					ops[nop] = opType.assemble(text)[0];
					strOps[nop] = text;
					opsCreated = 1;
				}
				break;
			case INMEDIATE:
				if (Tokens.LEXINMEDIATO.equals(token)) {
					ops[nop] = opType.assemble(text)[0];
					strOps[nop] = text;
					opsCreated = 1;
				}
				break;
			case LABEL:
				if (Tokens.LEXID.equals(token)) {
					ops[nop] = opType.assemble(text)[0];
					strOps[nop] = text;
					opsCreated = 1;
				}
				break;
			default:
				break;
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return opsCreated;		
	}
	
	public static Code loadCode(String fileName) {
		final Code code = new Code();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(fileName));
			SIMDELexer lexer = new SIMDELexer(buffer);
			int nInst = 0;
			int nBlock = -1;
			boolean newBlock = true;
			while (!lexer.yyatEOF()) {
				final Tokens token = lexer.yylex();
				final String text = lexer.yytext();
				if (token != null) {
					switch(token) {
					case LEXETIQUETA:
						code.addLabel(text.substring(0, text.length() - 1), nInst);
						newBlock = true;
						break;
					case LEXID:
						// Se procesa toda la instrucción, asumiendo que es correcta
						if (newBlock) {
							nBlock++;
						}
						final Opcode op = Opcode.valueOf(text);
						// Si es una instrucción de salto, dejamos indicado para que se cree un nuevo bloque básico en la siguiente
						newBlock = FunctionalUnit.JUMP.equals(op.getFU());
						String[] strOps = new String[3];
						int[] ops = new int[3];
						int nop = 0;
						for (OperandType opType : op.getOperands()) {
							nop += processOperand(opType, lexer, strOps, ops, nop);
						}
						code.addInstruction(new Instruction(nInst, nBlock, op, ops, text, strOps));
						nInst++;
						break;
					case LEXDIRECCION:
					case LEXINMEDIATO:
					case LEXNLINEAS:
					case LEXREGFP:
					case LEXREGGP:
					default:
						// Se ignoran
						break;
					
					}
				}
			}
			code.finishCode();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}

	public static void debugFile(String fileName) {
		final Code code = new Code();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(fileName));
			SIMDELexer lexer = new SIMDELexer(buffer);
			while (!lexer.yyatEOF()) {
				final Tokens token = lexer.yylex();
				System.out.println(token + ":" + lexer.yytext());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
