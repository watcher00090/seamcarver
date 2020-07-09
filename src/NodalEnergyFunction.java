
@FunctionalInterface
public interface NodalEnergyFunction {

    /* Returns the energy of pixel (x, y) in the given image.
     * @throws IndexOutOfBoundsException if (x, y) is not inside of the given image,
     *                                   or if image has less than 3 pixels in either dimension.
     */
    double apply(VerticalSeamGraphVertexNonEndpoint v);

}
