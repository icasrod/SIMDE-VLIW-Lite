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
 * El c�digo secuencial en pseudoMIPS
 * @author Iv�n Castilla
 *
 */
public class Code {
	/** Lista de instrucciones en el orden en que aparecen en el c�digo */
	private final ArrayList<Instruction> instructions;
	/** Etiquetas que se usan en el c�digo y l�nea de c�digo a la que corresponden */
	private final TreeMap<String, Integer> labels;
	
	/**
	 * Crea un c�digo vac�o
	 */
	public Code() {
		instructions = new ArrayList<>();
		labels = new TreeMap<>();
	}

	/**
	 * A�ade una instrucci�n al final de la lista de instrucciones 
	 * @param inst Instrucci�n a a�adir
	 */
	public void addInstruction(Instruction inst) {
		instructions.add(inst);
	}
	
	/**
	 * A�ade una etiqueta, junto con la l�nea de c�digo a la que corresponde
	 * @param label Etiqueta de un salto
	 * @param line L�nea de c�digo a la que corresponde
	 */
	public void addLabel(String label, int line) {
		labels.put(label, line);			
	}
	
	/**
	 * Realiza los �ltimos ajustes del c�digo, como repasar los valores de las etiquetas
	 */
	public void finishCode() {
		for (Instruction inst : instructions) {
			if (FunctionalUnit.JUMP.equals(inst.getOpcode().getFU())) {
				inst.getOp()[2] = labels.get(inst.getStrOp()[2]);
			}
		}
	}

	/**
	 * Devuelve el n�mero de l�neas (instrucciones) del c�digo secuencial
	 * @return n�mero de l�neas (instrucciones) del c�digo secuencial
	 */
	public int getNLines() {
		return instructions.size();
	}

	/**
	 * Devuelve la lista de instrucciones que conforman el c�digo
	 * @return lista de instrucciones que conforman el c�digo
	 */
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}

	/**
	 * Lee un operando del fichero con el fichero con el c�digo secuencial y lo procesa para almacenarlo correctamente como operando "virtual". 
	 * De un �nico "lexema" del analizador l�xico puede salir m�s de un operando en la implementaci�n "virtual".
	 * @param opType El tipo de operando esperado seg�n el opcode que se encontr�
	 * @param lexer El analizador l�xico usado para "parsear" el c�digo
	 * @param strOps Un array que contendr� las cadenas de texto que representan los operandos
	 * @param ops Un array que contendr� los identificadores de los operandos (si son registros), valores constantes (direcciones o inmediatos), 
	 * o destinos de etiquetas 
	 * @param opOrder Orden que ocupa el operando actual en el conjunto de operandos que la instrucci�n que se est� procesando
	 * @return El n�mero de operandos "virtuales" creados
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
	 * Carga un c�digo secuencial a partir de un fichero
	 * @param fileName Nombre del fichero que contiene el c�digo secuencial
	 * @return El c�digo secuencial creado
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
						// Se procesa toda la instrucci�n, asumiendo que es correcta
						if (newBlock) {
							nBlock++;
						}
						final Opcode op = Opcode.valueOf(text);
						// Si es una instrucci�n de salto, dejamos indicado para que se cree un nuevo bloque b�sico en la siguiente
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
	 * @param fileName Fichero con un c�digo secuencial
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
