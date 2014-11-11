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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class RGBTriple {
    public final byte[] channels;

    public RGBTriple() {
        channels = new byte[3];
    }

    public RGBTriple(final int R, final int G, final int B)
    {
        channels = new byte[] {
                (byte) R, (byte) G, (byte) B
        };
    }
}

public class FloydSteinbergDither
{
    private static byte plus_truncate_uchar(final byte a, final int b) {
        if ((a & 0xff) + b < 0) {
            return 0;
        } else if ((a & 0xff) + b > 255) {
            return (byte) 255;
        } else {
            return (byte) (a + b);
        }
    }

    private static byte findNearestColor(final RGBTriple color, final RGBTriple[] palette) {
        int minDistanceSquared = 255 * 255 + 255 * 255 + 255 * 255 + 1;
        byte bestIndex = 0;
        for (byte i = 0; i < palette.length; i++) {
            int Rdiff = (color.channels[0] & 0xff) - (palette[i].channels[0] & 0xff);
            int Gdiff = (color.channels[1] & 0xff) - (palette[i].channels[1] & 0xff);
            int Bdiff = (color.channels[2] & 0xff) - (palette[i].channels[2] & 0xff);
            int distanceSquared = Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static byte[][] floydSteinbergDither(final RGBTriple[][] image, final RGBTriple[] palette)
    {
        byte[][] result = new byte[image.length][image[0].length];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[y].length; x++) {
                RGBTriple currentPixel = image[y][x];
                byte index = findNearestColor(currentPixel, palette);
                result[y][x] = index;

                for (int i = 0; i < 3; i++)
                {
                    int error = (currentPixel.channels[i] & 0xff)
                            - (palette[index].channels[i] & 0xff);
                    if (x + 1 < image[0].length) {
                        image[y + 0][x + 1].channels[i] =
                                plus_truncate_uchar(image[y + 0][x + 1].channels[i],
                                        (error * 7) >> 4);
                    }
                    if (y + 1 < image.length) {
                        if (x - 1 > 0) {
                            image[y + 1][x - 1].channels[i] =
                                    plus_truncate_uchar(image[y + 1][x - 1].channels[i],
                                            (error * 3) >> 4);
                        }
                        image[y + 1][x + 0].channels[i] =
                                plus_truncate_uchar(image[y + 1][x + 0].channels[i],
                                        (error * 5) >> 4);
                        if (x + 1 < image[0].length) {
                            image[y + 1][x + 1].channels[i] =
                                    plus_truncate_uchar(image[y + 1][x + 1].channels[i],
                                            (error * 1) >> 4);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void main(final String[] args) throws IOException {
        // RGBTriple[][] image = new RGBTriple[145][100];
        //
        // InputStream raw_in = new BufferedInputStream(new
        // FileInputStream(args[0]));
        // for (int y = 0; y < image.length; y++) {
        // for (int x = 0; x < image[0].length; x++) {
        // image[y][x] = new RGBTriple();
        // raw_in.read(image[y][x].channels, 0, 3);
        // }
        // }
        BufferedImage inputImg = ImageIO.read(new File("input.bmp"));
        RGBTriple[][] image = new RGBTriple[inputImg.getHeight()][inputImg.getWidth()];
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                Color color = new Color(inputImg.getRGB(x, y));
                image[y][x] = new RGBTriple(color.getRed(), color.getGreen(), color.getBlue());
                // raw_in.read(image[y][x].channels, 0, 3);
            }
        }

        // raw_in.close();

        RGBTriple[] palette = {
                new RGBTriple(149, 91, 110),
                new RGBTriple(176, 116, 137),
                new RGBTriple(17, 11, 15),
                new RGBTriple(63, 47, 69),
                new RGBTriple(93, 75, 112),
                new RGBTriple(47, 62, 24),
                new RGBTriple(76, 90, 55),
                new RGBTriple(190, 212, 115),
                new RGBTriple(160, 176, 87),
                new RGBTriple(116, 120, 87),
                new RGBTriple(245, 246, 225),
                new RGBTriple(148, 146, 130),
                new RGBTriple(200, 195, 180),
                new RGBTriple(36, 32, 27),
                new RGBTriple(87, 54, 45),
                new RGBTriple(121, 72, 72)
        };
        BufferedImage output = new BufferedImage(inputImg.getWidth(), inputImg.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        byte[][] result = floydSteinbergDither(image, palette);
        for (int y = 0; y < result.length; y++) {
            for (int x = 0; x < result[0].length; x++) {
                RGBTriple rt = palette[result[y][x]];
                Color color = new Color(rt.channels[0] & 0xff, rt.channels[1] & 0xff,
                        rt.channels[2] & 0xff, 255);
                output.setRGB(x, y, color.getRed());
                // image[y][x] = new RGBTriple(color.getRed(), color.getGreen(),
                // color.getBlue());
                // raw_in.read(image[y][x].channels, 0, 3);
            }
        }
        // OutputStream raw_out = new BufferedOutputStream(new
        // FileOutputStream(args[1]));
        // for (int y = 0; y < result.length; y++) {
        // for (int x = 0; x < result[y].length; x++) {
        // raw_out.write(palette[result[y][x]].channels, 0, 3);
        // }
        // }
        ImageIO.write(output, "png", new File("output.png"));
        // raw_out.close();
    }
}
