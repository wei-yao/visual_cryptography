##visual cryptography 
the code implements a progressive visual cryptgraphy algorithm based on Shivendra Shivani1 and Suneeta Agarwal's paper :

" Novel Basis Matrix Creation and Preprocessing Algorithms for Unexpanded Meaningful Shares in Progressive Visual  Cryptography".



There are two ways to run :

>1.run VisualCryptography.java

> before run ,you should put one souce image and four(or more than four) images in the srcImage folder,and make sure all images are of the same height and width ,if the extension is not bmp,you can mod the constant EXTENSION.

	 /**
     * this is the input image count, one original image plus carrier images. if
     * the carrier image count increase ,you can mod this field.
     */
    public static final int INPUT_IMAGE_COUNT = 5;
    /**
     * the extension of the input images.
     */
    private static final String EXTENSION = ".bmp";

>and if the carrier image count is larger than 4,you have to mod the NPUT_IMAGE_COUNT field.

>after run ,you will find the carrier images in "paticipants" folder,and the overlay result in "overlayResult" folder.

>2 run gui.Sketch.java.

> Sketch.java is writen in processing.

![](http://i.imgur.com/IBdLzmv.png)

The little button below images is used to select input image.
image 5 is source image,and image 1 to 4 is carrier image,image 6 is overlay result,which is twice the size of
image 1.The limitation of images is as above ，and the 256*256 image input  will get best visual performance.

After load 5 images ,click image 5 to run the visual cryptography algorithm ,and the image 1-4 will change .
Click image 1-4 to overlay image. click one image to add it to the overlay result,and click two times to remove from the result.
Click image 6 will clear overlay result.


