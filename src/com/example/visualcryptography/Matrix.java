
package com.example.visualcryptography;

import java.util.Arrays;
import java.util.Random;

/**
 * visual cryptography中实现像素分配的基本矩阵的定义.
 * 
 * @author weiyao
 */
public class Matrix {
    private static final int COLUMN_COUNT = 4;
    public static final byte BLACK = 1;
    /**
     * white是数组的默认值.
     */
    public static final byte WHITE = 0;
    /**
     * 矩阵行的数量，即分发的图片的数量.
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
     * 拷贝构造函数.
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
            throw new FormatErrorException("图片数量错误");
        }
    }

    /**
     * 标志这个矩阵行叠加后是黑还是白.
     */
    private byte p;
    /**
     * 标志矩阵的各行是黑还是白.
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
     * 混淆列.
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
     * 生成基本矩阵.
     * 
     * @param p 合成色.
     * @param rows 行色值的数组.
     * @return 返回生成的基本矩阵.
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
     * 会操作原数组，生成一个合成色为黑色的矩阵.
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
     * 会操作原数组，生成一个合成色为白色的矩阵.
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
