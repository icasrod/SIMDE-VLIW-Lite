package samples.sort;
/**
 * @author icasrod
 *
 */
public class BidirectionalBubbleSort {
	
	/**
	 * Ordena un vector mediante el algoritmo "Bidirectional bubble sort" y devuelve el n�mero de ciclos que le llevar�a a la versi�n secuencial del c�digo equivalente en SIMDE
	 * @param lista Vector a ordenar
	 * @return N�mero de ciclos que le llevar�a esta ordenaci�n al c�digo equivalente en SIMDE
	 */
	private static int ordenar(double [] lista) {
		int izq, der, ultimo, n, cont;
		n = lista.length;
		izq = 0;
		der = n;
		ultimo = n-1;
		// ADDI	R1 R0 #41
		// LW	R2 -1(R1)
		// ADD	R2 R1 R2
		// ADDI	R3 R2 #-1
		cont = 7;
		do {
			cont += 1; // ADDI	R4 R2 #-1
			for (int i = der-1; i >= izq+1; i--) {
				// LW	R5 -1(R4)
				// LW	R6 (R4)
				// BGT	R6 R5 NOSWAP1				
				cont += 10;
				if (lista[i-1] > lista[i]) {
					double aux = lista[i-1];
					lista[i-1] = lista[i];
					lista[i] = aux;
					ultimo = i;
					printArray(lista);
					// SW	R6 -1(R4)
					// SW	R5 (R4)
					// ADDI	R3 R4 #0
					cont += 9;
				}
				// ADDI	R4 R4 #-1
				// BGT	R4 R1 IZQ
				cont += 3;
			}
			izq = ultimo;
			// ADDI	R1 R3 #0
			// ADDI	R4 R1 #1
			cont += 2;
			for (int i = izq+1; i <= der-1; i++) {
				// LW	R5 -1(R4)
				// LW	R6 (R4)
				// BGT	R6 R5 NOSWAP2
				cont += 10;
				if (lista[i-1] > lista[i]) {
					double aux = lista[i-1];
					lista[i-1] = lista[i];
					lista[i] = aux;
					ultimo = i;
					printArray(lista);
					// SW	R6 -1(R4)
					// SW	R5 (R4)
					// ADDI	R3 R4 #0
					cont += 9;
				}
				// ADDI	R4 R4 #1
				// BGT	R2 R4 DER
				cont += 3;
			}
			der = ultimo;
			// ADDI	R2 R3 #0
			// BNE	R1 R2 OUTER
			cont += 3;
		} while (der > izq);
		return cont;
	}

	
	/**
	 * Ordena un vector mediante el algoritmo "Bubble sort" y devuelve el n�mero de ciclos que le llevar�a a la versi�n secuencial del c�digo equivalente en SIMDE
	 * 	para i=1 hasta n
	 * 		para j=0 hasta n-i
	 * 			si lista[j] > lista[j+1] entonces
	 * 				intercambiar(lista[j], lista[j+1])
	 * 			fin si
	 * 		fin para
	 * 	fin para
	 * 
	 * @param lista Vector a ordenar
	 * @return N�mero de ciclos que le llevar�a esta ordenaci�n al c�digo equivalente en SIMDE
	 */
	private static int ordenarBubble(double [] lista) {
		// R1 <- i
		// ADDI	R1 R0 #42
		// R2 <- j
		// ADDI	R2 R0 #41
		// R4 <- n
		// LW	R4 -1(R2)
		// ADD	R4 R2 R4 
		int cont = 7;
		
		for (int i = 1; i < lista.length; i++) {
			// OUTER:
			// R7 <- (n - i)
			// SUB		R7 R4 R1
			// ADD		R7 R7 R2
			cont += 2;
			for (int j = 0; j < lista.length - i; j++) {
				// INNER:
				// Cargo R5 y R6 con lista[j] y lista[j+1]
				// LW		R5 (R2)
				// LW		R6 1(R2)
				// BGT		R6 R5 NOSWAP 
				cont += 10;
				if (lista[j] > lista[j+1]) {
					// Intercambio lista[j] y lista[j+1]
					// SW		R6 (R2)
					// SW		R5 1(R2)
					cont += 8;
					double aux = lista[j+1];
					lista[j+1] = lista[j];
					lista[j] = aux;
				}
				// NOSWAP: 
				//	ADDI	R2 R2 #1
				//	BNE		R2 R7 INNER
				cont += 3;
			}
			// ADDI	R1 R1 #1
			// ADDI	R2 R0 #41
			// BNE		R1 R4 OUTER
			cont += 4;
		}
		return cont;
	}

	private static void printArray(double []lista) {
		for (double val : lista)
			System.out.print(val + "\t");
		System.out.println();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double [] lista1 = new double[] {8, 7, 6, 5, 4, 3, 2, 1};
		double [] lista2 = new double[] {4, 3, 2, 1, 8, 7, 6, 5};
		double [] lista3 = new double[] {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		
		// Hay que cambiar esto para probar diferentes ejemplos
		double[] lista = lista3; 
		printArray(lista);
		int nCycles = ordenarBubble(lista);
		printArray(lista);
		System.out.println("#Cycles=" + nCycles);
	}

}
