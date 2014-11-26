
package gui;

import processing.core.PApplet;

import java.io.File;
import java.io.IOException;

/**
 * 视觉密码.主界面.输入为128*128的图片显示最佳.
 * 
 * @author weiyao
 */
public class Sketch extends PApplet {
    private static final long serialVersionUID = -1530381177827393671L;
    public static final int OUTPUT_SIZE = 512;
    public static final int DEFAULT_SIZE = 256;
    public static final int BUTTON_WIDTH = 50;
    public static final int BUTTON_HEIGHT = 20;
    public static final int PADDING = 10;
    public static final int FILL_COLOR = 255;
    private int frameMode = 0;
    MainFrame mainFrame;

    public void setup() {
        mainFrame = new MainFrame(this);
        final int width = DEFAULT_SIZE * 5 + PADDING * 5;
        final int height = DEFAULT_SIZE * 2 + PADDING * 8 + BUTTON_HEIGHT * 2;
        size(width, height);
        mainFrame.setup();
    }

    public void draw() {
        mainFrame.draw();
    }

    public void mousePressed() {
        try {
            mainFrame.onMouseClick(mouseX, mouseY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileSelected(final File selectFile) {
        mainFrame.fileSelected(selectFile);
    }

    public static void main(final String args[]) {
        PApplet.main(new String[] {
                "--present", "gui.Sketch"
        });
    }
}
