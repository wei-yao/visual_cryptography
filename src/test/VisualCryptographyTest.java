
package test;

import com.example.visualcryptography.VisualCryptography;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VisualCryptographyTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCalPos() {
        byte[] params = {
                1, 0, 1, 0, 1
        };
        Assert.assertEquals(21, VisualCryptography.calPos(params));
        byte[] params2 = {
                1, 1, 1, 1, 1
        };
        Assert.assertEquals(31, VisualCryptography.calPos(params2));
    }

    @Test
    public void testExtractParameters() {
        int input = 20;
        byte[] output = {
                1, 0, 1, 0, 0
        };
        Assert.assertArrayEquals(output, VisualCryptography.extractParameters(5, input));
    }
}
