/**
 * 
 */

package test;

import com.example.visualcryptography.FloydSteinbergDither;
import com.example.visualcryptography.FloydSteinbergDither.Position;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author weiyao
 */
public class FloydSteinbergDitherTest extends TestCase {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link FloydSteinbergDither#plus_truncate_uchar(int, int)}.
     */
    @Test
    public void testPlus_truncate_uchar() {
        // common case
        assertEquals(48, FloydSteinbergDither.plus_truncate_uchar(15, 33));
        // test upper bound
        assertEquals(255, FloydSteinbergDither.plus_truncate_uchar(150, 150));
        // test lower bound
        assertEquals(0, FloydSteinbergDither.plus_truncate_uchar(150, -200));
    }

    /**
     * Test method for
     * {@link FloydSteinbergDither#diffuseError(java.awt.image.BufferedImage, FloydSteinbergDither.Position, int[])}
     * 测试点在左上角.
     */
    @Test
    public void testDiffuseError00() {
        Position pos = new Position(0, 0);
        int[] correct = {
                239,
                232,
                239,
                234,
                238,
                239
        };
        int[] result = rundiffuseError(pos);
        Assert.assertArrayEquals(result, correct);
    }

    /**
     * Test method for
     * {@link FloydSteinbergDither#diffuseError(java.awt.image.BufferedImage, FloydSteinbergDither.Position, int[])}
     * 测试点在右下角. 3*2 大小图片中 坐标为2,1
     */
    @Test
    public void testDiffuseError21() {
        Position pos = new Position(2, 1);
        int[] correct = {
                239,
                239,
                239,
                239,
                239,
                239
        };
        int[] result = rundiffuseError(pos);
        Assert.assertArrayEquals(result, correct);
    }

    /**
     * 测试点周围5个点都可扩散.
     */
    @Test
    public void testDiffuseErrorNormal() {
        Position pos = new Position(1, 0);
        int[] result = rundiffuseError(pos);
        int[] correct = {
                239,
                239,
                232,
                236,
                234,
                238
        };
        Assert.assertArrayEquals(result, correct);
    }

    /**
     * 运行diffuseError函数，并将blue通道的值返回为数组.
     * 
     * @param pos
     * @return
     */
    private int[] rundiffuseError(final Position pos) {
        FloydSteinbergDither fsd = new FloydSteinbergDither(FloydSteinbergDither.BINARY_PALETTE,
                BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(239, 239, 239));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        Color pixel = new Color(image.getRGB(0, 0));
        // System.out.println(pixel.getRed() + " " + pixel.getGreen() + " " +
        // pixel.getBlue());
        assertEquals(239, pixel.getRed());
        assertEquals(239, pixel.getGreen());
        assertEquals(239, pixel.getBlue());
        assertEquals(239, image.getRGB(0, 0) & 0xff);
        fsd.diffuseError(image, pos, new int[] {
                -16, -16, -16
        });

        int k = 0;
        int[] result = new int[6];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int pixels = image.getRGB(j, i);
                System.out.println(new Color(pixels).getBlue());
                result[k++] = (pixels & 0xff);
            }
        }
        g.dispose();
        return result;
    }
}
