
package com.example.visualcryptography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ʵ��progressive visualcryptography��������.
 * 
 * @author weiyao
 */
public class VisualCryptography {
    /**
     * ���ɾ��������.
     */
    public static final int COLUMN_COUNT = 4;
    /**
     * ԭͼƬ��������дͼƬ ����С����.
     */
    private static final int MIN_IMAGE_COUNT = COLUMN_COUNT + 1;
    /**
     * ���ͼƬ����.
     */
    private static final int MAX_IMAGE_COUNT = 8 * Integer.BYTES;
    // private BufferedImage originImage;
    /**
     * ԭͼƬ������ͼƬ����.
     */
    private BufferedImage[] images;
    /**
     * ����ͼƬ������.
     */
    private int carrierCount;

    // private File[] files;
    /**
     * ���캯���Ǵ���һ��ͼ���file ���飬��һ��Ԫ����Ҫ�����ͼƬ��������ͼƬ������ͼƬ.
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
     * ��ȡͼƬ.
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
     * �Ӿ���������������. �����������ܵ�����ͼƬ�ļ���������.
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
     * ����ͼƬ.
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
     * ��������ͼƬ.
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
     * �������ɾ����Ƿ�ʹ���л���.
     */
    private static final boolean USE_PERMUTE = true;

    /**
     * @param basisMatrixs ������������.
     * @param inputImages ����ͼ��.
     * @return ������ͼ��.
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
     * ���ݲ����ó���Ӧ�Ļ�������.�������� p,C1,C2...,Cn-1����������λ��ֵ�ó�����. ���磺 1��0��0��0��1
     * ���������õ�0x11,��Ϊ������ȡ��������.
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
     * ���ݲ������ټ�������.
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
     * �����������󲢷���.
     * 
     * @param rowCount ����.
     * @return
     * @throws FormatErrorException
     */
    public Matrix[] createBasisMatrixs(final int rowCount) throws FormatErrorException {
        final int size = (int) Math.pow(2, rowCount + 1);
        Matrix[] output = new Matrix[size];
        byte p;
        byte[] paras = new byte[rowCount];
        for (int i = 0; i < size; i++) {
            // ��ȡp��C0,C1...Cn-1�Ȳ���,��Щ������Ӧ��i��ĩλrowCount+1��λ��ֵ.
            byte[] ret = extractParameters(rowCount + 1, i);
            p = ret[0];
            System.arraycopy(ret, 1, paras, 0, rowCount);
            output[i] = Matrix.generateBasisMatrix(p, paras);
        }
        return output;
    }

    /**
     * ����һ������i����ȡ���Ĵ�ĩβ0-size λ��Ϊ���.
     * 
     * @param size �����ĸ���
     * @param i ����
     * @return ��������ʽ������ȡ�Ĳ���.
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
