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
    private static byte plus_truncate_uchar(final int a, final int b) {
        if ((a & 0xff) + b < 0) {
            return 0;
        } else if ((a & 0xff) + b > 255) {
            return (byte) 255;
        } else {
            return (byte) (a & 0xff + b);
        }
    }

    private Color findNearestColor(final Color color) {
        int minDistanceSquared = 255 * 255 + 255 * 255 + 255 * 255 + 1;
        byte bestIndex = 1;
        for (byte i = 0; i < palette.length; i++) {

            int Rdiff = (color.getRed() & 0xff) - (palette[i].getRed() &
                    0xff);
            int Gdiff = (color.getGreen() & 0xff) - (palette[i].getGreen() &
                    0xff);
            int Bdiff = (color.getBlue() & 0xff) - (palette[i].getBlue() &
                    0xff);
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

                Color currentPixel = new Color(imgCopy.getRGB(x, y));
                Color dstPixel = findNearestColor(currentPixel);
                if (dstPixel == Color.BLACK) {
                    count++;
                } else {
                    countW++;
                }
                imgCopy.setRGB(x, y, dstPixel.getRGB());
                // g.setColor(dstPixel);
                // g.fillRect(x, y, 1, 1);
                for (int i = -1; i < 2 && x + i < image.getWidth() && x + i
                        >= 0; i++) {
                    for (int j = 0; j < 2 && y + j < image.getHeight(); j++) {
                        if (i == -1 && j == 0) {
                            continue;
                        }
                        if (i == 0 && j == 0) {
                            continue;
                        }
                        byte channels[] = new byte[3];
                        final int weight = getWeight(i, j);
                        Color targetPixel = new Color(imgCopy.getRGB(x + i, y + j));
                        for (int k = 0; k < 3; k++) {
                            int error = (getChannel(currentPixel, k))
                                    - getChannel(dstPixel, k);
                            channels[k] =
                                    plus_truncate_uchar(getChannel(targetPixel, k),
                                            (error * weight) >> 4);
                        }
                        try {
                            Color pixelColor = new Color(channels[0] & 0xff, channels[1]
                                    & 0xff,
                                    channels[2] & 0xff,
                                    targetPixel.getAlpha());
                            // g.setColor(pixelColor);
                            // g.drawRect(x + i, y + j, 1, 1);
                            imgCopy.setRGB(x + i, y + j,
                                    pixelColor.getRGB());
                        } catch (IllegalArgumentException e) {
                            System.out.println((channels[0] & 0xff) + " " + channels[1] +
                                    " "
                                    + channels[2]);
                            throw e;
                        }

                    }
                }
            }
        }
        System.out.println("count " + count + "　" + countW);
        g.dispose();
        return imgCopy;
    }

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

        Color[] colors = {
                new Color(149, 91, 110),
                new Color(176, 116, 137),
                new Color(17, 11, 15),
                new Color(63, 47, 69),
                new Color(93, 75, 112),
                new Color(47, 62, 24),
                new Color(76, 90, 55),
                new Color(190, 212, 115),
                new Color(160, 176, 87),
                new Color(116, 120, 87),
                new Color(245, 246, 225),
                new Color(148, 146, 130),
                new Color(200, 195, 180),
                new Color(36, 32, 27),
                new Color(87, 54, 45),
                new Color(121, 72, 72)
        };
        // Color colors[] = {
        // Color.BLACK,
        // Color.WHITE
        // };
        FloydSteinbergDither fsd = new FloydSteinbergDither(colors, BufferedImage.TYPE_INT_ARGB);
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
