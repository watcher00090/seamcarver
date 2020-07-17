package sample;

import edu.princeton.cs.algs4.Picture;

import java.util.List;

/**
 *  Shows 3 images: original image and the horizontal and vertical seams of that image.
 */
public class ShowSeams {

    protected static void showHorizontalSeam(SeamCarver sc) {
        Picture ep = PictureUtils.toEnergyPicture(sc);
        List<Integer> horizontalSeam = sc.findHorizontalSeam();
        Picture epOverlay = PictureUtils.seamOverlay(ep, true, horizontalSeam);
        epOverlay.show();
    }

    protected static void showVerticalSeam(SeamCarver sc) {
        Picture ep = PictureUtils.toEnergyPicture(sc);
        List<Integer> verticalSeam = sc.findVerticalSeam();
        Picture epOverlay = PictureUtils.seamOverlay(ep, false, verticalSeam);
        epOverlay.show();
    }

    public static void main(String[] args) {
        Picture picture = PictureUtils.loadPicture("small image 1");
        SeamCarver sc = new SeamCarver(picture, new DualGradientEnergyFunction(), new DijkstraSeamFinder());
        main(sc, picture);
    }

    public static void main(SeamCarver sc, Picture picture) {
        System.out.printf("%d-by-%d image%n", picture.width(), picture.height());
        picture.show();

        System.out.println("Displaying horizontal seam calculated.");
        showHorizontalSeam(sc);

        System.out.println("Displaying vertical seam calculated.");
        showVerticalSeam(sc);
    }
}
