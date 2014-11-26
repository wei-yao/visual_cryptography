
package com.example.visualcryptography;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * 预处理类，负责halftone图片的预处理. extend the image width and height by two times
 * 
 * @author weiyao
 */
public class Preprocessor {
    public Preprocessor() {

    }

    public BufferedImage preprocess(final BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        BufferedImage imageRes = new BufferedImage(width * 2, height * 2,
                image.getType());
        Graphics2D g = imageRes.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, 0, 0, width * 2, height * 2, 0, 0, width,
                height, null);
        g.dispose();
        return imageRes;

    }
}
