
package com.example.visualcryptography;

import java.util.Arrays;
import java.util.Random;

/**
 * visual cryptography��ʵ�����ط���Ļ�������Ķ���.
 * 
 * @author weiyao
 */
public class Matrix {
    private static final int COLUMN_COUNT = 4;
    public static final byte BLACK = 1;
    /**
     * white�������Ĭ��ֵ.
     */
    public static final byte WHITE = 0;
    /**
     * �����е����������ַ���ͼƬ������.
     */
    private static final int ROW_COUNT = VisualCryptography.ROW_COUNT;

    /**
     * @param p
     * @param rows
     * @param data
     */
    public Matrix(final byte p, final byte[] rows,
            final byte[][] data) {
        setP(p);
        this.rows = new byte[data.length];
        System.arraycopy(rows, 0, this.rows, 0, rows.length);
        matrix = new byte[data.length][COLUMN_COUNT];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, matrix[i], 0, COLUMN_COUNT);
        }

    }

    /**
     * �������캯��.
     * 
     * @param input
     */
    public Matrix(final Matrix input) {
        this(input.getP(), input.getRows(), input.getMatrix());

    }

    // public static int getRowCount() {
    // return rowCount;
    // }

    // public static void setRowCount(final int rowCount) throws
    // FormatErrorException {
    // checkRowCount(rowCount);
    // Matrix.rowCount = rowCount;
    // }

    /**
     * @param rowCount
     * @throws FormatErrorException
     */
    private static void checkRowCount(final int rowCount) throws FormatErrorException {
        if (rowCount < COLUMN_COUNT) {
            throw new FormatErrorException("ͼƬ��������");
        }
    }

    /**
     * ��־��������е��Ӻ��Ǻڻ��ǰ�.
     */
    private byte p;
    /**
     * ��־����ĸ����Ǻڻ��ǰ�.
     */
    private byte[] rows;
    private byte[][] matrix;

    public byte getP() {
        return p;
    }

    public byte[] getRows() {
        return rows;
    }

    public void setP(final byte p) {
        this.p = p;
    }

    public byte[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(final byte[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * ������.
     * 
     * @param inputMatrix
     * @return
     * @throws FormatErrorException
     */
    public static Matrix permuteColumns(final Matrix inputMatrix) {
        byte[][] matrix = inputMatrix.getMatrix();
        byte[][] output = new byte[matrix.length][COLUMN_COUNT];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, output[i], 0, COLUMN_COUNT);
        }
        Random random = new Random();
        int next;
        // int permute[] = {
        // 1, 1, 3, 3
        // };
        for (int j = 0; j < COLUMN_COUNT; j++) {
            next = random.nextInt(COLUMN_COUNT);
            // next = permute[j];
            if (next != j) {
                for (int i = 0; i < matrix.length; i++) {
                    output[i][j] = matrix[i][next];
                    output[i][next] = matrix[i][j];
                }
            }
        }

        return new Matrix(inputMatrix.getP(), inputMatrix.getRows(), output);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(matrix);
        result = prime * result + p;
        result = prime * result + Arrays.hashCode(rows);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Matrix other = (Matrix) obj;
        if (!Arrays.deepEquals(matrix, other.matrix)) {
            return false;
        }
        if (p != other.p) {
            return false;
        }
        if (!Arrays.equals(rows, other.rows)) {
            return false;
        }
        return true;
    }

    /**
     * ���ɻ�������.
     * 
     * @param p �ϳ�ɫ.
     * @param rows ��ɫֵ������.
     * @return �������ɵĻ�������.
     * @throws FormatErrorException
     */
    public static Matrix generateBasisMatrix(final byte p, final byte[] rows)
            throws FormatErrorException {
        checkRowCount(rows.length);
        byte[][] output;
        // = new byte[rows.length][COLUMN_COUNT];
        if (p == BLACK) {
            output = createBlackMatrix(rows);
        } else {
            output = createWhiteMatrix(rows);
        }
        return new Matrix(p, rows, output);
    }

    /**
     * �����ԭ���飬����һ���ϳ�ɫΪ��ɫ�ľ���.
     * 
     * @param output
     * @param p2
     * @param rows
     */
    private static byte[][] createBlackMatrix(final byte[] rows) {
        byte[][] output =
                new byte[rows.length][COLUMN_COUNT];
        for (int i = 0; i < rows.length; i++) {
            output[i][i % COLUMN_COUNT] = BLACK;
            if (rows[i] == BLACK) {
                output[i][(i + 1) % COLUMN_COUNT] = BLACK;
            }
        }
        return output;

    }

    /**
     * �����ԭ���飬����һ���ϳ�ɫΪ��ɫ�ľ���.
     * 
     * @param output
     * @param p2
     * @param rows
     */
    private static byte[][] createWhiteMatrix(final byte[] rows) {
        byte[][] output =
                new byte[rows.length][COLUMN_COUNT];
        for (int i = 0; i < rows.length; i++) {
            output[i][0] = BLACK;
            if (rows[i] == BLACK) {
                output[i][1] = BLACK;
            }
        }
        return output;
    }
}
