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
 * El código secuencial en pseudoMIPS
 * @author Iván Castilla
 *
 */
public class Code {
	/** Lista de instrucciones en el orden en que aparecen en el código */
	private final ArrayList<Instruction> instructions;
	/** Etiquetas que se usan en el código y línea de código a la que corresponden */
	private final TreeMap<String, Integer> labels;
	
	/**
	 * Crea un código vacío
	 */
	public Code() {
		instructions = new ArrayList<>();
		labels = new TreeMap<>();
	}

	/**
	 * Añade una instrucción al final de la lista de instrucciones 
	 * @param inst Instrucción a añadir
	 */
	public void addInstruction(Instruction inst) {
		instructions.add(inst);
	}
	
	/**
	 * Añade una etiqueta, junto con la línea de código a la que corresponde
	 * @param label Etiqueta de un salto
	 * @param line Línea de código a la que corresponde
	 */
	public void addLabel(String label, int line) {
		labels.put(label, line);			
	}
	
	/**
	 * Realiza los últimos ajustes del código, como repasar los valores de las etiquetas
	 */
	public void finishCode() {
		for (Instruction inst : instructions) {
			if (FunctionalUnit.JUMP.equals(inst.getOpcode().getFU())) {
				inst.getOp()[2] = labels.get(inst.getStrOp()[2]);
			}
		}
	}

	/**
	 * Devuelve el número de líneas (instrucciones) del código secuencial
	 * @return número de líneas (instrucciones) del código secuencial
	 */
	public int getNLines() {
		return instructions.size();
	}

	/**
	 * Devuelve la lista de instrucciones que conforman el código
	 * @return lista de instrucciones que conforman el código
	 */
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}

	/**
	 * Lee un operando del fichero con el fichero con el código secuencial y lo procesa para almacenarlo correctamente como operando "virtual". 
	 * De un único "lexema" del analizador léxico puede salir más de un operando en la implementación "virtual".
	 * @param opType El tipo de operando esperado según el opcode que se encontró
	 * @param lexer El analizador léxico usado para "parsear" el código
	 * @param strOps Un array que contendrá las cadenas de texto que representan los operandos
	 * @param ops Un array que contendrá los identificadores de los operandos (si son registros), valores constantes (direcciones o inmediatos), 
	 * o destinos de etiquetas 
	 * @param opOrder Orden que ocupa el operando actual en el conjunto de operandos que la instrucción que se está procesando
	 * @return El número de operandos "virtuales" creados
	 */
	public static int processOperand(OperandType opType, SIMDELexer lexer, String[] strOps, int[] ops, int opOrder) {
		int opsCreated = 0;
		try {
			final Tokens token = lexer.yylex();
			final String text = lexer.yytext();
			switch(opType) {
			case ADDRESS:
				if (Tokens.LEXDIRECCION.equals(token)) {
					int[] aux = opType.assemble(text);
					ops[opOrder] = aux[0];
					ops[opOrder+1] = aux[1];
					opsCreated = 2;
					strOps[opOrder] = text;
					strOps[opOrder+1] = "";
				}
				break;
			case FPREGISTER:
				if (Tokens.LEXREGFP.equals(token)) {
					ops[opOrder] = opType.assemble(text)[0];
					strOps[opOrder] = text;
					opsCreated = 1;
				}
				break;
			case GPREGISTER:
				if (Tokens.LEXREGGP.equals(token)) {
					ops[opOrder] = opType.assemble(text)[0];
					strOps[opOrder] = text;
					opsCreated = 1;
				}
				break;
			case INMEDIATE:
				if (Tokens.LEXINMEDIATO.equals(token)) {
					ops[opOrder] = opType.assemble(text)[0];
					strOps[opOrder] = text;
					opsCreated = 1;
				}
				break;
			case LABEL:
				if (Tokens.LEXID.equals(token)) {
					ops[opOrder] = opType.assemble(text)[0];
					strOps[opOrder] = text;
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
	
	/**
	 * Carga un código secuencial a partir de un fichero
	 * @param fileName Nombre del fichero que contiene el código secuencial
	 * @return El código secuencial creado
	 */
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
						code.addInstruction(new Instruction(nInst, nBlock, op, ops, strOps));
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

	/**
	 * Simplemente parsea el fichero y muestra los "tokens" que identifica 
	 * @param fileName Fichero con un código secuencial
	 */
	public static void debugFile(String fileName) {
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
