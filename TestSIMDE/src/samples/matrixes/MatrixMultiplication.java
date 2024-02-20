/**
 * 
 */
package samples.matrixes;

/**
 * @author Iván Castilla
 *
 */
public class MatrixMultiplication {

	/**
	 * 
	 */
	public MatrixMultiplication() {
		// TODO Auto-generated constructor stub
	}

	static int[][] multiply(int[][] A, int [][]B) {
		final int n = A.length;
		final int [][] C = new int[n][n];
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            C[i][j] = 0;
	            for (int k = 0; k < n; k++) {
	                C[i][j] += A[i][k] * B[k][j];
	            }
	        }
	    }
	    return C;
	}
	
	static void printMatrix(int [][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++)
				System.out.print(matrix[i][j] + "\t");
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		int [][]A = {{1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}};
		System.out.println("A");
		printMatrix(A);

		int [][]B = {{17, 18, 19, 20},
                {21, 22, 23, 24},
                {25, 26, 27, 28},
                {29, 30, 31, 32}};
		System.out.println("B");
		printMatrix(B);
		
		int [][]C = multiply(A, B);
		System.out.println("C");
		printMatrix(C);
	}
}
