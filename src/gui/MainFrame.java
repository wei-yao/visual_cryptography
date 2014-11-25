package gui;

import java.awt.Color;
import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * 主画面.
 * 
 * @author yao
 * 
 */
public class MainFrame {

	private static final int OUTPUT_IMG_ID = 5;
	private PApplet pApplet;
	private boolean isOverlayed[] = new boolean[4];
	private boolean isLoaded[] = new boolean[5];

	public MainFrame(PApplet applet) {
		this.pApplet = applet;
		if (imagePos == null)
			initializeImagePos();
		if (buttonPos == null) {
			initializeButtionPos();
		}

		for (int i = 0; i < 5; i++)
			imgs[i] = new PImage(MySketch.DEFAULT_SIZE, MySketch.DEFAULT_SIZE);
	}

	private void initializeImagePos() {
		imagePos = new int[5][2];
		imagePos[0][0] = MySketch.PADDING;
		imagePos[0][1] = MySketch.PADDING;
		imagePos[1][0] = imagePos[0][0];
		imagePos[4][1] = imagePos[3][1] = imagePos[2][1] = imagePos[1][1] = imagePos[0][1]
				+ MySketch.DEFAULT_SIZE
				+ MySketch.PADDING
				* 2
				+ MySketch.BUTTON_HEIGHT;
		imagePos[2][0] = imagePos[1][0] + MySketch.DEFAULT_SIZE
				+ MySketch.PADDING;
		imagePos[3][0] = imagePos[2][0] + MySketch.DEFAULT_SIZE
				+ MySketch.PADDING;
		imagePos[4][0] = imagePos[3][0] + MySketch.DEFAULT_SIZE
				+ MySketch.PADDING;
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
			buttonPos[i][0] = imagePos[i][0] + MySketch.DEFAULT_SIZE / 2
					- MySketch.BUTTON_WIDTH / 2;
			buttonPos[i][1] = imagePos[i][1] + MySketch.DEFAULT_SIZE
					+ MySketch.PADDING;
		}
	}

	public void setup() {
		resetAllImages();
		pApplet.background(Color.BLACK.getRGB());
	}

	private void resetAllImages() {
		for (int i = 0; i < 5; i++) {
			resetInputImage(i);
		}
//		resetImage(overlayResult);
	}

	/**
	 * 重置输入的图片(要分享的图片或者载体图片)
	 * 
	 * @param id
	 */
	private void resetInputImage(int id) {
		resetImage(imgs[id]);
		isLoaded[id] = false;
		if (id != 0)
			isOverlayed[id - 1] = false;
	}
	
	public void draw() {
		for (int i = 0; i < 5; i++) {
			pApplet.image(imgs[i], imagePos[i][0], imagePos[i][1],
					MySketch.DEFAULT_SIZE, MySketch.DEFAULT_SIZE);
		}
//		pApplet.fill(255,255);
//		pApplet.rect(RESULT_X, RESULT_Y, MySketch.OUTPUT_SIZE,
//				MySketch.OUTPUT_SIZE);
//		pApplet.image(overlayResult, RESULT_X, RESULT_Y, MySketch.OUTPUT_SIZE,
//				MySketch.OUTPUT_SIZE);
		overlayImages();
		drawButtons();

	}

	private void drawButtons() {
		pApplet.stroke(255);
		pApplet.fill(255);
		for (int i = 0; i < 5; i++) {
			pApplet.rect(buttonPos[i][0], buttonPos[i][1],
					MySketch.BUTTON_WIDTH, MySketch.BUTTON_HEIGHT);
		}
	}

	public void onMouseClick(int mouseX, int mouseY) {
		int buttonid = getButtonId(mouseX, mouseY);
		if (buttonid != -1) {
			selectButton = buttonid;
			String title;
			if (buttonid == SRC_BUTTON_ID)
				title = "请选择要分享的图片";
			else
				title = "请选择要载体图片";
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
	private void handleCarrierImageClick(int id) {
		if (isLoaded[id] && !isOverlayed[id-1]) {
//			overlayImage(id);
			isOverlayed[id-1]=true;
		}
	}

	private void overlayImages() {
		boolean isFirst=true;
		for(int id=1;id<5;id++){
		if(isOverlayed[id-1])
			if(isFirst)
			{
			pApplet.image(imgs[id], RESULT_X, RESULT_Y,MySketch.OUTPUT_SIZE, MySketch.OUTPUT_SIZE);	
			isFirst=false;
			}else
			{
			pApplet.blend(imgs[id], imagePos[id][0], imagePos[id][1],
				imgs[id].width, imgs[id].height, RESULT_X, RESULT_Y,
				MySketch.OUTPUT_SIZE, MySketch.OUTPUT_SIZE, PApplet.BURN);
		}
		}
	}

	/**
	 * 清除叠加的结果.
	 */
	
	
	
	private void resetOverlay() {
		for (int i = 0; i < isOverlayed.length; i++) {
			isOverlayed[i] = false;
		}
//		resetImage(overlayResult);
	}

	private void onSrcImageClick() {
		if (isAllLoaded()) {
			for (int i = 1; i < 5; i++)
				resetInputImage(i);
//			resetImage(overlayResult);
			proceed();
		}
	}

	/**
	 * 运行视觉密码的算法.
	 */
	private void proceed() {

	}

	private boolean isAllLoaded() {
		for (boolean b : isLoaded)
			if (!b)
				return false;
		return true;
	}

	/**
	 * 初始化image对象.
	 * 
	 * @param image
	 */
	private void resetImage(PImage image) {
		// if(image!=null)
		image.filter(PImage.THRESHOLD, 0);
	}

	private static final int SOURCE_IMG_ID = 0;
	private int selectButton;
	PImage[] imgs = new PImage[5];
	/**
	 * 标志四张图片有没有没叠加过.
	 */
	boolean[] overlayImages = new boolean[4];
//	private PImage overlayResult = new PImage(MySketch.OUTPUT_SIZE,
//			MySketch.OUTPUT_SIZE);
	private static final int RESULT_X = MySketch.PADDING * 4
			+ MySketch.DEFAULT_SIZE * 3;
	private static final int RESULT_Y = MySketch.PADDING;

	public void fileSelected(File selectFile) {
		imgs[selectButton] = pApplet.loadImage(selectFile.getAbsolutePath());
		isLoaded[selectButton] = true;
	}

	public static boolean isOverRect(int x, int y, int rx, int ry, int width,
			int height) {
		return ((x >= rx && x <= rx + width) && (y >= ry & y <= ry + height));
	}

	private boolean isOverButton(int x, int y, int buttonId) {
		return isOverRect(x, y, buttonPos[buttonId][0], buttonPos[buttonId][1],
				MySketch.BUTTON_WIDTH, MySketch.BUTTON_HEIGHT);
	}

	private static final int SRC_BUTTON_ID = 0;
	private static final int CARRIER0_ID = 1;
	private static final int CARRIER1_ID = 2;
	private static final int CARRIER2_ID = 3;
	private static final int CARRIER3_ID = 4;

	private static int[][] buttonPos;
	private static int[][] imagePos;

	private int getButtonId(int mouseX, int mouseY) {
		for (int i = 0; i < 5; i++)
			if (isOverButton(mouseX, mouseY, i))
				return i;
		return -1;
	}

	private int getCheckedImageId(int x, int y) {
		for (int i = 0; i < 6; i++) {
			if (isOverImage(x, y, i))
				return i;
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
	private boolean isOverImage(int x, int y, int id) {
		int imagex, imagey, width, height;
		if (id == 5) {
			imagex = RESULT_X;
			imagey = RESULT_Y;
			width = MySketch.OUTPUT_SIZE;
			height = MySketch.OUTPUT_SIZE;
		} else {
			imagex = imagePos[id][0];
			imagey = imagePos[id][1];
			width = height = MySketch.DEFAULT_SIZE;
		}

		return isOverRect(x, y, imagex, imagey, width, height);
	}
}
