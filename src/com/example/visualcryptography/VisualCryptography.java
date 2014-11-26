
package com.example.visualcryptography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 实现progressive visualcryptography的主程序.
 * 
 * @author weiyao
 */
public class VisualCryptography {
    /**
     * 生成矩阵的列数.
     */
    public static final int COLUMN_COUNT = 4;
    /**
     * 原图片与四张隐写图片 的最小张数.
     */
    private static final int MIN_IMAGE_COUNT = COLUMN_COUNT + 1;
    /**
     * 最大图片数量.
     */
    private static final int MAX_IMAGE_COUNT = 8 * Integer.BYTES;
    // private BufferedImage originImage;
    /**
     * 原图片与载体图片数组.
     */
    private BufferedImage[] images;
    /**
     * 载体图片的数量.
     */
    private int carrierCount;

    // private File[] files;
    /**
     * 构造函数是传入一个图像的file 数组，第一个元素是要共享的图片，其他的图片是载体图片.
     * 
     * @param files
     * @throws FormatErrorException
     * @throws IOException
     */
    public VisualCryptography(final File[] files) throws FormatErrorException, IOException {
        if (files.length < MIN_IMAGE_COUNT) {
            throw new FormatErrorException("file lenght is less than " + MIN_IMAGE_COUNT);
        } else if (files.length > MAX_IMAGE_COUNT) {
            throw new FormatErrorException("file lenght is larger than " + MAX_IMAGE_COUNT);
        }
        carrierCount = files.length;
        images = new BufferedImage[files.length];
        readImages(files);
        // System.arraycopy(files, 0, this.files, 0, files.length);
    }

    private VisualCryptography() {

    }

    /**
     * 读取图片.
     * 
     * @param files
     * @throws IOException
     * @throws FormatErrorException
     */
    private void readImages(final File[] files) throws IOException, FormatErrorException {
        // originImage = ImageIO.read(files[0]);
        // if (!checkType(originImage.getType())) {
        // throw new FormatErrorException("type mismatch");
        // }
        for (int i = 0; i < files.length; i++) {
            images[i] = ImageIO.read(files[i]);
            if (images[i].getWidth() != images[0].getWidth()
                    || images[i].getHeight() != images[0].getHeight()) {
                throw new FormatErrorException("file size mismatch");
            }
            if (!checkType(images[i].getType())) {
                throw new FormatErrorException("type mismatch");
            }
        }
    }

    private boolean checkType(final int type) {
        return true;
        // return (type == BufferedImage.TYPE_BYTE_GRAY);
    }

    static BufferedImage deepCopy(final BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * 视觉密码的主处理程序. 返回载有秘密的载体图片文件对象数组.
     * 
     * @throws IOException
     * @throws FormatErrorException
     */
    public File[] process() throws IOException, FormatErrorException {
        images = halfTone(images);
        images = preprocessing(images);
        Matrix[] basisMatrixs = createBasisMatrixs(carrierCount);
        BufferedImage[] output;
        output = distribute(basisMatrixs, images);

        // BufferedImage[] permuteOutput = new BufferedImage[4];
        // for (int i = 0; i < 4; i++) {
        // permuteOutput[i] = output[3 - i];
        // }
        BufferedImage[] overlayImages = progressiveOverlay(output);
        saveOverlayImages(overlayImages);
        return savePaticipantsImages(output);

    }

    /**
     * 叠加图片.
     * 
     * @param output
     * @return
     */
    private BufferedImage[] progressiveOverlay(final BufferedImage[] input) {
        final int height = input[0].getHeight();
        final int width = input[0].getWidth();
        BufferedImage output[] = new BufferedImage[input.length];
        output[0] = deepCopy(input[0]);
        // BufferedImage temp = output[0];
        for (int i = 1; i < input.length; i++) {
            output[i] = overlayTwo(output[i - 1], input[i]);
        }
        return output;
    }

    /**
     * 叠加两张图片.
     * 
     * @param first
     * @param second
     * @return
     */
    private BufferedImage overlayTwo(final BufferedImage first, final BufferedImage second) {
        final int height = first.getHeight();
        final int width = first.getWidth();
        BufferedImage output = new BufferedImage(width, height, first.getType());
        int i;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (first.getRGB(x, y) == Color.BLACK.getRGB()
                        || second.getRGB(x, y) == Color.BLACK.getRGB()) {
                    output.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    output.setRGB(x, y, Color.WHITE.getRGB());
                }

            }
        }
        return output;
    }

    /**
     * 控制生成矩阵是否使用列混淆.
     */
    private static final boolean USE_PERMUTE = true;

    /**
     * @param basisMatrixs 基本矩阵数组.
     * @param inputImages 输入图像.
     * @return 处理后的图像.
     */
    private BufferedImage[] distribute(final Matrix[] basisMatrixs,
            final BufferedImage[] inputImages) {
        BufferedImage[] output = new BufferedImage[inputImages.length - 1];
        System.arraycopy(inputImages, 1, output, 0, output.length);
        final int width = output[0].getWidth();
        final int height = output[0].getHeight();
        final int count = inputImages.length;
        byte[] params = new byte[count];
        for (int j = 0; j < height; j += 2) {
            for (int i = 0; i < width; i += 2) {
                for (int k = 0; k < count; k++) {
                    if (inputImages[k].getRGB(i, j) == Color.BLACK.getRGB()) {
                        params[k] = Matrix.BLACK;
                    } else {
                        params[k] = Matrix.WHITE;
                    }
                }
                Matrix matrix = getSelectedMatrixs(basisMatrixs, params);
                if (USE_PERMUTE) {
                    matrix = Matrix.permuteColumns(matrix);
                }
                byte[][] byteMatrix = matrix.getMatrix();
                for (int k = 0; k < count - 1; k++) {
                    output[k].setRGB(i, j, getRGB(byteMatrix[k][0]));
                    output[k].setRGB(i + 1, j, getRGB(byteMatrix[k][1]));
                    output[k].setRGB(i, j + 1, getRGB(byteMatrix[k][2]));
                    output[k].setRGB(i + 1, j + 1, getRGB(byteMatrix[k][3]));
                }
            }
        }
        return output;
    }

    private int getRGB(final byte b) {
        if (b == Matrix.BLACK) {
            return Color.BLACK.getRGB();
        } else {
            return Color.WHITE.getRGB();
        }
    }

    /**
     * 根据参数得出对应的基本矩阵.几个参数 p,C1,C2...,Cn-1被当作各个位的值得出索引. 例如： 1，0，0，0，1
     * ，将会计算得到0x11,作为索引提取基本数组.
     * 
     * @param basisMatrixs
     * @param paras
     * @return
     */
    private Matrix getSelectedMatrixs(final Matrix[] basisMatrixs, final byte[] paras) {

        int pos = calPos(paras);
        return new Matrix(basisMatrixs[pos]);

    }

    /**
     * 根据参数快速计算索引.
     * 
     * @param paras
     * @param pos
     * @return
     */
    public static int calPos(final byte[] paras) {
        int pos = 0;
        for (int i = 0; i < paras.length; i++) {
            pos <<= 1;
            if (paras[i] == Matrix.BLACK) {
                pos |= 0x01;
            }

        }
        return pos;
    }

    /**
     * 产生基本矩阵并返回.
     * 
     * @param rowCount 行数.
     * @return
     * @throws FormatErrorException
     */
    public Matrix[] createBasisMatrixs(final int rowCount) throws FormatErrorException {
        final int size = (int) Math.pow(2, rowCount + 1);
        Matrix[] output = new Matrix[size];
        byte p;
        byte[] paras = new byte[rowCount];
        for (int i = 0; i < size; i++) {
            // 提取p和C0,C1...Cn-1等参数,这些参数对应着i的末位rowCount+1，位的值.
            byte[] ret = extractParameters(rowCount + 1, i);
            p = ret[0];
            System.arraycopy(ret, 1, paras, 0, rowCount);
            output[i] = Matrix.generateBasisMatrix(p, paras);
        }
        return output;
    }

    /**
     * 输入一个整数i，提取它的从末尾0-size 位作为输出.
     * 
     * @param size 参数的个数
     * @param i 输入
     * @return 以数组形式返回提取的参数.
     */
    public static byte[] extractParameters(final int size, final int i) {
        byte[] ret = new byte[size];
        // ret[0] = (byte) (i & 0x01);
        int value = i;
        for (int j = 0; j < size; j++) {
            ret[size - 1 - j] = (byte) (value & 0x01);
            value >>>= 1;
            // ret[j] = (byte) ((i >>> j) & 0x01);
        }

        return ret;
    }

    private BufferedImage[] preprocessing(final BufferedImage[] inputImages) {
        BufferedImage[] ret = new BufferedImage[inputImages.length];
        Preprocessor processor = new Preprocessor();
        for (int i = 0; i < inputImages.length; i++) {
            ret[i] = processor.preprocess(inputImages[i]);
        }
        return ret;
    }

    private BufferedImage[] halfTone(final BufferedImage[] images) {
        BufferedImage[] ret = new BufferedImage[images.length];
        FloydSteinbergDither fsd = new FloydSteinbergDither(FloydSteinbergDither.BINARY_PALETTE,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < images.length; i++) {
            ret[i] = fsd.transform(images[i]);
        }
        return ret;
    }

    private File[] savePaticipantsImages(final BufferedImage[] images) throws IOException {
        int i = 0;
        File parent = new File("paticipants");
        if (!parent.exists()) {
            parent.mkdir();
        }
        File files[] = new File[images.length];
        for (BufferedImage img : images) {
            files[i] = new File(parent, (i) + ".png");
            saveImage(img, files[i]);
            i++;
        }
        return files;
    }

    private void saveOverlayImages(final BufferedImage[] images) throws IOException {
        int i = 0;
        File parent = new File("overlayResult");
        if (!parent.exists()) {
            parent.mkdir();
        }
        // if (!file.exists()) {
        // file.mkdir();
        // }
        for (BufferedImage img : images) {
            saveImage(img, new File(parent, (i++) + ".png"));
        }
    }

    /**
     * @param output
     * @throws IOException
     */
    public static void saveImage(final BufferedImage output, final File outFile) throws IOException {
        // File outFile = new File(name);
        ImageIO.write(output, "png", outFile);
    }

    public static final int INPUT_IMAGE_COUNT = 5;

    public static void main(final String[] args) {
        File parent = new File("srcImage");
        File[] files = new File[INPUT_IMAGE_COUNT];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(parent, i + ".bmp");
        }
        try {
            VisualCryptography vc = new VisualCryptography(files);
            vc.process();
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
