
package test;

import com.example.visualcryptography.FormatErrorException;
import com.example.visualcryptography.Matrix;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class MatrixTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testArraysDeepEquals() {
        // byte p = Matrix.BLACK;
        // byte[] params = {
        // 0, 0, 1, 1
        // };
        byte[][] output = {
                {
                        1, 0, 0, 0
                },
                {
                        0, 1, 0, 0,
                },
                {
                        0, 0, 1, 1,
                },
                {
                        1, 0, 0, 1
                }
        };
        byte[][] output2 = {
                {
                        1, 0, 0, 0
                },
                {
                        0, 1, 0, 0,
                },
                {
                        0, 0, 1, 1,
                },
                {
                        1, 0, 0, 1
                }
        };
        assertEquals(true, Arrays.deepEquals(output, output2));
        output2[0][0] = 0;
        assertEquals(false, Arrays.deepEquals(output, output2));
    }

    @Test
    public void testGenerateBasisMatrixcase1() throws FormatErrorException {
        byte p = Matrix.BLACK;
        byte[] rows = {
                0, 0, 1, 1
        };
        byte[][] output = {
                {
                        1, 0, 0, 0
                },
                {
                        0, 1, 0, 0,
                },
                {
                        0, 0, 1, 1,
                },
                {
                        1, 0, 0, 1
                }
        };
        Matrix outputMatrix = new Matrix(p, rows, output);
        assertEquals(outputMatrix, Matrix.generateBasisMatrix(p, rows));
    }

    @Test
    public void testGenerateBasisMatrixcase0() throws FormatErrorException {
        byte p = Matrix.WHITE;
        byte[] rows = {
                1, 0, 1, 0
        };
        byte[][] output = {
                {
                        1, 1, 0, 0
                },
                {
                        1, 0, 0, 0,
                },
                {
                        1, 1, 0, 0,
                },
                {
                        1, 0, 0, 0
                }
        };
        Matrix outputMatrix = new Matrix(p, rows, output);
        assertEquals(outputMatrix, Matrix.generateBasisMatrix(p, rows));
    }

    @Test
    public void testPermuteColumns() {
        byte p = Matrix.WHITE;
        byte[] rows = {
                1, 0, 1, 0
        };
        byte[][] input = {
                {
                        1, 1, 0, 0
                },
                {
                        1, 0, 0, 0,
                },
                {
                        1, 1, 0, 0,
                },
                {
                        1, 0, 0, 0
                }
        };
        byte[][] bak = {
                {
                        1, 1, 0, 0
                },
                {
                        1, 0, 0, 0,
                },
                {
                        1, 1, 0, 0,
                },
                {
                        1, 0, 0, 0
                }
        };
        Matrix inputMatrix = new Matrix(p, rows, input);
        /**
         * test it will not change the original matrix
         */
        Matrix outputMatirx2 = Matrix.permuteColumns(inputMatrix);
        assertEquals(inputMatrix, new Matrix(p, rows, bak));
        byte[][] trueResult = {
                {
                        1, 1, 0, 0
                },
                {
                        0, 1, 0, 0,
                },
                {
                        1, 1, 0, 0,
                },
                {
                        0, 1, 0, 0
                }

        };
        Matrix resultMatrix = new Matrix(p, rows, trueResult);
        // assertNEquals(resultMatrix, outputMatirx2);
    }
}
