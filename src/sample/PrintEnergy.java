package sample;

import edu.princeton.cs.algs4.Picture;

/**
 *  Print energy of each pixel as calculated by an EnergyFunction.
 */
public class PrintEnergy {
    public static void main(String[] args) {
        EnergyFunction energyFunction = new DualGradientEnergyFunction();
        Picture picture = PictureUtils.loadPicture("java bread.jpg");
        main(energyFunction, picture);
    }

    public static void main(EnergyFunction energyFunction, Picture picture) {
        System.out.printf("%d-by-%d image%n", picture.width(), picture.height());
        System.out.println("Printing energy calculated for each pixel.");

        for (int row = 0; row < picture.height(); row++) {
            for (int col = 0; col < picture.width(); col++) {
                System.out.printf("%9.0f ", energyFunction.apply(picture, col, row));
            }
            System.out.println();
        }
    }
}
