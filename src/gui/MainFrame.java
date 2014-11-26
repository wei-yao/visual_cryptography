
package gui;

import com.example.visualcryptography.FormatErrorException;
import com.example.visualcryptography.VisualCryptography;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;

/**
 * 主画面.页面布局适用于128×128的原图像.
 * 
 * @author yao
 */
public class MainFrame {

    private static final int OUTPUT_IMG_ID = 5;
    private PApplet pApplet;
    private boolean isOverlayed[] = new boolean[5];
    private boolean isLoaded[] = new boolean[5];

    public MainFrame(final PApplet applet) {
        this.pApplet = applet;
        if (imagePos == null) {
            initializeImagePos();
        }
        if (buttonPos == null) {
            initializeButtionPos();
        }

        for (int i = 0; i < 5; i++) {
            imgs[i] = new PImage(Sketch.DEFAULT_SIZE, Sketch.DEFAULT_SIZE);
        }
    }

    /**
     * 将输入图片的左上角的位置写入数组.
     */
    private void initializeImagePos() {
        imagePos = new int[5][2];
        imagePos[3][0] = imagePos[1][0] = Sketch.PADDING;
        imagePos[4][0] = imagePos[2][0] = imagePos[1][0] + Sketch.PADDING + Sketch.DEFAULT_SIZE;
        imagePos[2][1] = imagePos[1][1] = Sketch.PADDING;
        imagePos[0][1] = imagePos[4][1] = imagePos[3][1] = imagePos[1][1]
                + Sketch.DEFAULT_SIZE
                + Sketch.PADDING
                * 2
                + Sketch.BUTTON_HEIGHT;
        imagePos[0][0] = imagePos[4][0] + Sketch.PADDING + Sketch.DEFAULT_SIZE;
    }

    /**
     * 初始化button位置矩形的坐标数组.
     */
    private void initializeButtionPos() {
        buttonPos = new int[5][2];
        // buttonPos[0][0] = MySketch.PADDING + MySketch.DEFAULT_SIZE / 2
        // - MySketch.BUTTON_WIDTH/2;
        // buttonPos[0][1] = 2 * MySketch.PADDING + MySketch.DEFAULT_SIZE;
        // buttonPos[1][0] = buttonPos[0][0];
        // buttonPos[1][1] = 3 * MySketch.PADDING + MySketch.DEFAULT_SIZE * 3;
        // buttonPos[4][1] = buttonPos[3][1] = buttonPos[2][1] =
        // buttonPos[1][1];
        // buttonPos[2][0] = buttonPos[1][0] + MySketch.PADDING
        // + MySketch.DEFAULT_SIZE;
        // buttonPos[3][0] = buttonPos[2][0] + MySketch.PADDING
        // + MySketch.DEFAULT_SIZE;
        // buttonPos[4][0] = buttonPos[3][0] + MySketch.PADDING
        // + MySketch.DEFAULT_SIZE;
        for (int i = 0; i < buttonPos.length; i++) {
            buttonPos[i][0] = imagePos[i][0] + Sketch.DEFAULT_SIZE / 2
                    - Sketch.BUTTON_WIDTH / 2;
            buttonPos[i][1] = imagePos[i][1] + Sketch.DEFAULT_SIZE
                    + Sketch.PADDING;
        }
    }

    public void setup() {
        resetAllImages();
        pApplet.background(0);
        // f = pApplet.createFont("Arial", 25, true);
    }

    private void resetAllImages() {
        for (int i = 0; i < 5; i++) {
            resetInputImage(i);
        }
        // resetImage(overlayResult);
    }

    /**
     * 重置输入的图片(要分享的图片或者载体图片).
     * 
     * @param id
     */
    private void resetInputImage(final int id) {
        resetImage(imgs[id]);
        isLoaded[id] = false;
        if (id != 0) {
            isOverlayed[id] = false;
        }
    }

    public void draw() {
        for (int i = 0; i < 5; i++) {
            if (isLoaded[i]) {
                pApplet.image(imgs[i], imagePos[i][0], imagePos[i][1],
                        Sketch.DEFAULT_SIZE, Sketch.DEFAULT_SIZE);
            } else
            {
                pApplet.fill(255, 255);
                pApplet.rect(imagePos[i][0], imagePos[i][1], Sketch.DEFAULT_SIZE,
                        Sketch.DEFAULT_SIZE);
            }

        }
        // showTips();
        overlayImages();
        drawButtons();

    }

    // /**
    // * 显示提示文字.
    // */
    // private void showTips() {
    //
    // pApplet.textFont(f);
    // pApplet.fill(0);
    // pApplet.textAlign(PApplet.LEFT);
    // pApplet.text("请选择原图", buttonPos[0][0], buttonPos[0][1]);
    // }

    // private PFont f;

    private void drawButtons() {
        pApplet.stroke(255);
        pApplet.fill(Sketch.FILL_COLOR);
        for (int i = 0; i < 5; i++) {
            pApplet.rect(buttonPos[i][0], buttonPos[i][1],
                    Sketch.BUTTON_WIDTH, Sketch.BUTTON_HEIGHT);
        }
    }

    public void onMouseClick(final int mouseX, final int mouseY) throws IOException {
        int buttonid = getButtonId(mouseX, mouseY);
        if (buttonid != -1) {
            selectButton = buttonid;
            String title;
            if (buttonid == SRC_BUTTON_ID) {
                title = "请选择要分享的图片";
            } else {
                title = "请选择要载体图片";
            }
            pApplet.selectInput(title, "fileSelected");
        } else {
            int imageId = getCheckedImageId(mouseX, mouseY);
            if (imageId != -1) {
                if (imageId == SOURCE_IMG_ID) {
                    onSrcImageClick();
                } else if (imageId == OUTPUT_IMG_ID) {
                    resetOverlay();
                } else {
                    handleCarrierImageClick(imageId);
                }

            }

        }
    }

    /**
     * 处理载体图片被点中.
     */
    private void handleCarrierImageClick(final int id) {
        if (isLoaded[id]) {
            isOverlayed[id] = !isOverlayed[id];
        }
    }

    private void overlayImages() {
        boolean isFirst = true;

        for (int id = 1; id < 5; id++) {
            if (isOverlayed[id]) {
                if (isFirst)
                {
                    pApplet.image(imgs[id], RESULT_X, RESULT_Y, Sketch.OUTPUT_SIZE,
                            Sketch.OUTPUT_SIZE);
                    isFirst = false;
                } else
                {
                    pApplet.blend(imgs[id], 0, 0,
                            imgs[id].width, imgs[id].height, RESULT_X, RESULT_Y,
                            Sketch.OUTPUT_SIZE, Sketch.OUTPUT_SIZE, PApplet.BURN);
                }
            }
        }
        // 如果没有图片叠加，填充白色，直接用白色作为初值blend有问题.
        if (isFirst) {
            pApplet.fill(255);
            pApplet.rect(RESULT_X, RESULT_Y, Sketch.OUTPUT_SIZE, Sketch.OUTPUT_SIZE);
        }
    }

    /**
     * 清除叠加的结果.
     */

    private void resetOverlay() {
        for (int i = 0; i < isOverlayed.length; i++) {
            isOverlayed[i] = false;
        }
    }

    /**
     * 应该重新生成并重新加载.
     * 
     * @throws IOException
     * @throws FormatErrorException
     */
    private void onSrcImageClick() throws IOException {
        if (isAllLoaded()) {
            for (int i = 1; i < 5; i++) {
                // resetInputImage(i);
                // resetImage(overlayResult);
                try {
                    proceed();
                } catch (FormatErrorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 运行视觉密码的算法.
     * 
     * @throws IOException
     * @throws FormatErrorException
     */
    private void proceed() throws FormatErrorException, IOException {
        VisualCryptography vc = new VisualCryptography(loadFiles);
        File[] files = vc.process();
        for (int i = 0; i < 4; i++)
        {
            readCarrierImage(files[i], i + 1);
        }
    }

    private boolean isAllLoaded() {
        for (boolean b : isLoaded) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    private void readCarrierImage(final File file, final int id) {
        imgs[id] = pApplet.loadImage(file.getAbsolutePath());
        isLoaded[id] = true;
        loadFiles[id] = file;
        isOverlayed[id] = false;
    }

    /**
     * 初始化image对象.
     * 
     * @param image
     */
    private void resetImage(final PImage image) {
        image.filter(PImage.THRESHOLD, 0);
    }

    private static final int SOURCE_IMG_ID = 0;
    private int selectButton;
    PImage[] imgs = new PImage[5];
    private static final int RESULT_X = Sketch.PADDING * 4
            + Sketch.DEFAULT_SIZE * 3;
    private static final int RESULT_Y = Sketch.PADDING;

    public void fileSelected(final File selectFile) {
        imgs[selectButton] = pApplet.loadImage(selectFile.getAbsolutePath());
        isLoaded[selectButton] = true;
        loadFiles[selectButton] = selectFile;
    }

    private File[] loadFiles = new File[5];

    public static boolean isOverRect(final int x, final int y, final int rx, final int ry,
            final int width,
            final int height) {
        return ((x >= rx && x <= rx + width) && (y >= ry & y <= ry + height));
    }

    private boolean isOverButton(final int x, final int y, final int buttonId) {
        return isOverRect(x, y, buttonPos[buttonId][0], buttonPos[buttonId][1],
                Sketch.BUTTON_WIDTH, Sketch.BUTTON_HEIGHT);
    }

    private static final int SRC_BUTTON_ID = 0;
    /**
     * 存放5个button的左上角，button0对应原图的按钮.
     */
    private static int[][] buttonPos;
    /**
     * 存放5张图片左上角的坐标，imagePos[0] 对应原图.
     */
    private static int[][] imagePos;

    private int getButtonId(final int mouseX, final int mouseY) {
        for (int i = 0; i < 5; i++) {
            if (isOverButton(mouseX, mouseY, i)) {
                return i;
            }
        }
        return -1;
    }

    private int getCheckedImageId(final int x, final int y) {
        for (int i = 0; i < 6; i++) {
            if (isOverImage(x, y, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 检查x,y 是否在id指定的 图像 的区域内
     * 
     * @param x
     * @param y
     * @param id
     * @return
     */
    private boolean isOverImage(final int x, final int y, final int id) {
        int imagex, imagey, width, height;
        if (id == 5) {
            imagex = RESULT_X;
            imagey = RESULT_Y;
            width = Sketch.OUTPUT_SIZE;
            height = Sketch.OUTPUT_SIZE;
        } else {
            imagex = imagePos[id][0];
            imagey = imagePos[id][1];
            width = height = Sketch.DEFAULT_SIZE;
        }

        return isOverRect(x, y, imagex, imagey, width, height);
    }
}
