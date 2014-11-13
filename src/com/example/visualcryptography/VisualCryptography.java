
package com.example.visualcryptography;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VisualCryptography {
    /**
     * 原图片与四张隐写图片，一共5张.
     */
    private static final int IMAGE_COUNT = 5;
    // private BufferedImage originImage;
    /**
     * 原图片与载体图片数组.
     */
    private BufferedImage[] images;

    // private File[] files;

    public VisualCryptography(final File[] files) throws FormatErrorException, IOException {
        if (files.length != IMAGE_COUNT) {
            throw new FormatErrorException("file lenght is not 5");
        }
        images = new BufferedImage[IMAGE_COUNT];
        readImages(files);
        // System.arraycopy(files, 0, this.files, 0, files.length);
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
        for (int i = 0; i < 5; i++) {
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
        return (type == BufferedImage.TYPE_BYTE_GRAY);
    }

    public void process() throws IOException {
        images = halfTone(images);
        images = preprocessing(images);
        saveImages(images);

    }

    private BufferedImage[] preprocessing(final BufferedImage[] inputImages) {
        BufferedImage[] ret = new BufferedImage[5];
        Preprocessor processor = new Preprocessor();
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ret[i] = processor.preprocess(inputImages[i]);
        }
        return ret;
    }

    private BufferedImage[] halfTone(final BufferedImage[] images) {
        BufferedImage[] ret = new BufferedImage[5];
        FloydSteinbergDither fsd = new FloydSteinbergDither(FloydSteinbergDither.BINARY_PALETTE,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ret[i] = fsd.transform(images[i]);
        }
        return ret;
    }

    private void saveImages(final BufferedImage[] images) throws IOException {
        int i = 0;
        for (BufferedImage img : images) {
            saveImage(img, (i++) + ".png");
        }
    }

    /**
     * @param output
     * @throws IOException
     */
    public static void saveImage(final BufferedImage output, final String name) throws IOException {
        File outFile = new File(name);
        ImageIO.write(output, "png", outFile);
    }

    public static void main(final String[] args) {
        File parent = new File("srcImage");
        File[] files = new File[IMAGE_COUNT];
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
