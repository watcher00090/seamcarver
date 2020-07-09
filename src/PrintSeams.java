import edu.princeton.cs.algs4.Picture;

import java.util.List;

/**
 *  Prints energies of pixels, a vertical seam, and a horizontal seam.
 *
 *  The table gives the dual-gradient energies of each pixel.
 *  The asterisks denote a minimum-energy vertical or horizontal seam.
 */
public class PrintSeams {

    protected static void printSeam(SeamCarver carver, List<Integer> seam, Utils.Orientation direction) {
        double[][] energies = carver.computeEnergies();

        System.out.println(Utils.superimposeSeamOnEnergies(energies, seam, direction, ""));
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        Picture picture = PictureUtils.loadPicture("small image 1");
        SeamCarver carver = new SeamCarver(picture, new DualGradientEnergyFunction(), new DijkstraSeamFinder());
        main(carver, picture);
    }

    public static void main(SeamCarver carver, Picture picture) {
        System.out.printf("%d-by-%d image%n", picture.width(), picture.height());
        System.out.println();
        System.out.println("The table gives the dual-gradient energies of each pixel.");
        System.out.println("The asterisks denote a minimum energy vertical or horizontal seam.");
        System.out.println();

        List<Integer> verticalSeam = carver.findVerticalSeam();
        System.out.println("Vertical seam: " + verticalSeam);
        printSeam(carver, verticalSeam, Utils.Orientation.VERTICAL);

        List<Integer> horizontalSeam = carver.findHorizontalSeam();
        System.out.println("Horizontal seam: " + horizontalSeam);
        printSeam(carver, horizontalSeam, Utils.Orientation.HORIZONTAL);
    }

}
