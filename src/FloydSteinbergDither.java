/* The authors of this work have released all rights to it and placed it
in the public domain under the Creative Commons CC0 1.0 waiver
(http://creativecommons.org/publicdomain/zero/1.0/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Floyd-Steinberg_dithering_(Java)?oldid=12476
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//class RGBTriple {
//    public final byte[] channels;
//    public RGBTriple() { channels = new byte[3]; }
//    public RGBTriple(int R, int G, int B)
//    { channels = new byte[]{(byte)R, (byte)G, (byte)B}; }
//}

public class FloydSteinbergDither
{
    /**
     * 右移4位等于 除以16.
     */
    private static final int RIGHT_SHIF_BITS = 4;
    /**
     * 标志输出格式（位深度).
     */
    private final int outputFormat;
    /**
     * 色值的数组，定义输出图像含有的色值类型.
     */
    private final Color[] palette;

    public FloydSteinbergDither(final Color[] color, final int outputFormat) {
        this.palette = new Color[color.length];
        System.arraycopy(color, 0, this.palette, 0, color.length);
        this.outputFormat = outputFormat;
    }

    /**
     * 加色值
     * 
     * @param a
     * @param b
     * @return
     */
    private static int plus_truncate_uchar(final int a, final int b) {
        if ((a) + b < 0) {
            return 0;
        } else if ((a) + b > 255) {
            return 255;
        } else {
            return (a + b);
        }
    }

    private Color findNearestColor(final Color color) {
        int minDistanceSquared = 255 * 255 + 255 * 255 + 255 * 255 + 1;
        byte bestIndex = 1;
        for (byte i = 0; i < palette.length; i++) {

            int Rdiff = (color.getRed()) - (palette[i].getRed());
            int Gdiff = (color.getGreen()) - (palette[i].getGreen());
            int Bdiff = (color.getBlue()) - (palette[i].getBlue());
            int distanceSquared = Rdiff * Rdiff + Gdiff * Gdiff + Bdiff *
                    Bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        // if (color.getRed() < 125 || color.getGreen() < 125 || color.getBlue()
        // < 125)
        // {
        // bestIndex = 0;
        // } else {
        // int a = 0;
        // }
        return palette[bestIndex];
    }

    private int getWeight(final int i, final int j) {
        int ret = 0;
        if (i == 1 && j == 0) {
            ret = 7;
        } else if (i == -1 && j == 1) {
            ret = 3;
        } else if (i == 0 && j == 1) {
            ret = 5;
        } else if (i == 1 && j == 1) {
            ret = 1;
        }
        return ret;
    }

    /**
     * 用 Floyd–Steinberg dithering 算法转化图像.
     * 
     * @param image
     * @return 转化后的图像.
     */
    private BufferedImage floydSteinbergDither(final BufferedImage image)
    {
        // byte[][] result = new byte[image.length][image[0].length];
        BufferedImage imgCopy = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imgCopy.createGraphics();
        g.drawImage(image, 0, 0, null);
        int count = 0;
        int countW = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                Color oldPixel = new Color(imgCopy.getRGB(x, y));
                Color newPixel = findNearestColor(oldPixel);
                if (newPixel == Color.BLACK) {
                    count++;
                } else {
                    countW++;
                }
                imgCopy.setRGB(x, y, newPixel.getRGB());
                // g.setColor(dstPixel);
                // g.fillRect(x, y, 1, 1);
                int[] errors = new int[CHANNEL_COUNT];
                for (int k = 0; k < 2; k++) {
                    errors[k] = getChannel(oldPixel, k) - getChannel(newPixel, k);
                }
                diffuseError(imgCopy, new Position(x, y), errors);
            }
        }
        System.out.println("count " + count + "　" + countW);
        g.dispose();
        return imgCopy;
    }

    /**
     * 通道数量. r g b
     */
    private static final int CHANNEL_COUNT = 3;
    /**
     * 存储了要扩散误差的5个邻居的相对坐标.
     */
    private static final Position[] NEIGHBORS = {
            new Position(-1, 1),
            new Position(0, 1),
            new Position(1, 0),
            new Position(1, 1)
    };

    /**
     * 坐标类.
     * 
     * @author weiyao
     */
    private static class Position {
        /**
         * x coordinate.
         */
        private int x;

        public int getX() {
            return x;
        }

        public void setX(final int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(final int y) {
            this.y = y;
        }

        /**
         * y coordinate.
         */
        private int y;

        /**
         * 构造函数，初始化坐标值.
         * 
         * @param x x coordinate
         * @param y y coordinate
         */
        public Position(final int x, final int y) {
            setX(x);
            setY(y);
        }
    }

    /**
     * 扩散误差，将一个点的误差扩散到周围的五点,传入的img将被改变.
     * 
     * @param img 图像对象
     * @param position 正在处理的点的坐标
     * @param errors :r，g，b通道的误差数组.
     */
    protected void diffuseError(final BufferedImage img, final Position position,
            final int[] errors) {
        int x = position.getX();

        int y = position.getY();
        for (Position pos : NEIGHBORS) {
            int i = pos.getX();
            int j = pos.getY();
            if (!checkPos(position, pos, img.getWidth(), img.getHeight())) {
                continue;
            }
            int[] channels = new int[CHANNEL_COUNT];
            final int weight = getWeight(i, j);
            Color targetPixel = new Color(img.getRGB(x + i, y + j));
            for (int k = 0; k < CHANNEL_COUNT; k++) {
                channels[k] =
                        plus_truncate_uchar(getChannel(targetPixel, k),
                                (errors[k] * weight) >> RIGHT_SHIF_BITS);
            }
            try {
                Color pixelColor = new Color(channels[0], channels[1],
                        channels[2],
                        targetPixel.getAlpha());
                img.setRGB(x + i, y + j,
                        pixelColor.getRGB());
            } catch (IllegalArgumentException e) {
                System.out.println((channels[0]) + " " + channels[1]
                        +
                        " "
                        + channels[2]);
                throw e;
            }
        }
    }

    /**
     * 检查坐标是否合法.
     * 
     * @param base 原始坐标
     * @param offset 偏移坐标
     * @param width 图像宽度
     * @param height 图像高度
     * @return 坐标在图像的矩形之内返回true，否则返回false.
     */
    private boolean checkPos(final Position base, final Position offset, final int width,
            final int height) {
        int x = base.getX() + offset.getX();
        int y = base.getY() + offset.getY();

        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    /**
     * 将一幅图像按照指定的参数转换.
     * 
     * @param image
     * @return
     */
    public BufferedImage transform(final BufferedImage image) {
        BufferedImage imgTemp = floydSteinbergDither(image);
        if (imgTemp.getType() != outputFormat) {
            BufferedImage imgCopy = new BufferedImage(image.getWidth(), image.getHeight(),
                    outputFormat);
            Graphics2D g = imgCopy.createGraphics();
            g.drawImage(imgTemp, 0, 0, null);
            g.dispose();
            return imgCopy;
        } else {
            return imgTemp;
        }

    }

    /**
     * 根据索引得到颜色的各个通道.
     * 
     * @param color
     * @param index
     * @return
     */
    private int getChannel(final Color color, final int index) {
        int ret = 0;
        switch (index) {
            case 0:
                ret = color.getRed();
                break;
            case 1:
                ret = color.getGreen();
                break;
            case 2:
                ret = color.getBlue();
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * 二值图像的调色板.
     */
    public static final Color[] BINARY_PALETTE = {
            Color.BLACK,
            Color.WHITE
    };
    /**
     * 8色的调色板.
     */
    public static final Color[] THREE_BITS_PALETTE = {
            new Color(0, 0, 0),
            new Color(0, 0, 255),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(255, 0, 0),
            new Color(255, 0, 255),
            new Color(255, 255, 0),
            new Color(255, 255, 255)
    };

    public static void main(final String[] args) throws IOException {
        // RGBTriple[][] image = new RGBTriple[145][100];

        // InputStream raw_in = new BufferedInputStream(new
        // FileInputStream(args[0]));
        // for (int y = 0; y < image.length; y++) {
        // for (int x = 0; x < image[0].length; x++) {
        // image[y][x] = new RGBTriple();
        // raw_in.read(image[y][x].channels, 0, 3);
        // }
        // }
        // raw_in.close();

        // Color[] colors = {
        // new Color(149, 91, 110),
        // new Color(176, 116, 137),
        // new Color(17, 11, 15),
        // new Color(63, 47, 69),
        // new Color(93, 75, 112),
        // new Color(47, 62, 24),
        // new Color(76, 90, 55),
        // new Color(190, 212, 115),
        // new Color(160, 176, 87),
        // new Color(116, 120, 87),
        // new Color(245, 246, 225),
        // new Color(148, 146, 130),
        // new Color(200, 195, 180),
        // new Color(36, 32, 27),
        // new Color(87, 54, 45),
        // new Color(121, 72, 72)
        // };
        // Color colors[] = {
        // Color.BLACK,
        // Color.WHITE
        // };
        FloydSteinbergDither fsd = new FloydSteinbergDither(BINARY_PALETTE,
                BufferedImage.TYPE_BYTE_BINARY);
        // FloydSteinbergDither fsd = new
        // FloydSteinbergDither(THREE_BITS_PALETTE,
        // BufferedImage.TYPE_INT_RGB);
        // byte[][] result = floydSteinbergDither(image, palette);
        File sourceFile = new File("input.bmp");
        BufferedImage imgSrc = ImageIO.read(sourceFile);
        BufferedImage output = fsd.transform(imgSrc);
        saveImage(output);

    }

    /**
     * @param output
     * @throws IOException
     */
    public static void saveImage(final BufferedImage output) throws IOException {
        File outFile = new File("output.png");
        ImageIO.write(output, "png", outFile);
    }
}
