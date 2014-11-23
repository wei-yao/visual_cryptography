package com.example.visualcryptography;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.Color;

public class MyProcessingSketch extends PApplet {

    /**
     * 
     */
    private static final long serialVersionUID = -1530381177827393671L;

    PImage img, img2, img3, img4;
    float offset = 0;
    float easing = (float) 0.05;
    int mode;
    private static final int size = 512;

    public void setup() {

        size(1024, 1024);
        img = loadImage("0.png"); // Load an image into the program
        // img2 = loadImage("1.png");
        // img.mask(img2);
        // img = createOverlayImage("0.png");
        img2 = loadImage("1.png");
        img3 = loadImage("2.png");
        img4 = loadImage("3.png");
        // int orange = color(0, 0, 0, 0);
        // int blue = color(255, 255, 255, 0);
        // int orangeblueadd = blendColor(blue, orange, BLEND);
        // System.out.println(Integer.toHexString(orangeblueadd));
        // img2 = createOverlayImage("1.png");
        // img3 = createOverlayImage("2.png");
        // img4 = createOverlayImage("3.png");
        // img.mask(img2);
        // img.mask(img3);
        // img.mask(img4);
        // img3.mask(img4);
        mode = processing.core.PConstants.BURN;
    }

    public void draw() {
        // tint(255, 0);
        image(img, 0, 0); // Display at full opacity
        // float dx = (mouseX - img.width / 2) - offset;
        // offset += dx * easing;
        offset = mouseX - img.width / 2;
        // offset = 0;
        blend(img2, 0, 0, img2.width, img2.height, (int) offset, 0, img.width, img.height, mode);

        blend(img3, 0, 0, img2.width, img2.height, 0, 0, img.width,
                img.height, mode);
        blend(img4, 0, 0, img2.width, img2.height, 0, 0, img.width,
                img.height, mode);
        // // tint(255, 255); // Display at half opacity
        //
        // image(img2, 0, 0);
        // PFont f = createFont("Arial", 16, true);
        // textFont(f, 16);
        // fill(0);
        // text("Hello Strings!", 10, 100);
    }

    private PImage createOverlayImage(final String name) {
        PImage image = loadImage(name);
        for (int i = 0; i < image.pixels.length; i++) {
            image.pixels[i] = color(image.pixels[i], getAlpha(image.pixels[i]));
        }
        return image;
    }

    private int getAlpha(final int pixel) {
        if (pixel == Color.BLACK.getRGB()) {
            return 0;
        } else {
            return 255;
        }
    }

    public static void main(final String args[]) {
        PApplet.main(new String[] {
                "--present", "MyProcessingSketch"
        });
    }
}
