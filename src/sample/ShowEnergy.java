package sample;

import edu.princeton.cs.algs4.Picture;

/**
 * Shows 2 images: original image and the energies of each of its pixel (as a grayscale image).
 */
public class ShowEnergy {
    public static void main(String[] args) {
        Picture picture = PictureUtils.loadPicture("small image 1");
        SeamCarver sc = new SeamCarver(picture,
            new DualGradientEnergyFunction(),
            new DijkstraSeamFinder());
        main(sc, picture);
    }

    public static void main(SeamCarver sc, Picture picture) {
        System.out.printf("%d-by-%d image%n", picture.width(), picture.height());
        picture.show();

        System.out.println("Displaying energy calculated for each pixel.");
        PictureUtils.showEnergy(sc);
    }
}


