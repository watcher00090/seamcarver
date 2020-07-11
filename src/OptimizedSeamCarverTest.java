import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimizedSeamCarverTest {

    public boolean checkThatEdgeEndpointsHaveCorrectCoordinates(DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G) {
        for (Edge<VerticalSeamGraphVertex> e : ((VerticalSeamGraphVertexSource) G.start).edgeList) {
            if (!((VerticalSeamGraphVertexNonEndpoint) e.to).top.equals(e.from)) {
                return false;
            }
        }

        VerticalSeamGraphVertex curs = (VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexSource) G.start).edgeList.get(0).to);

        for (int y=0; y<G.numVertVertices; ++y) {
            VerticalSeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                // check invariants on curs
                VerticalSeamGraphVertexNonEndpoint v = (VerticalSeamGraphVertexNonEndpoint) curs;
                VerticalSeamGraphVertex bottomLeftVertex = v.bottomLeft;
                VerticalSeamGraphVertex bottomVertex = v.bottom;
                VerticalSeamGraphVertex bottomRightVertex = v.bottomRight;

                if (v.leftEdge != null && !VerticalSeamGraphVertex.checkEquality(bottomLeftVertex, v.leftEdge.to)) {
                    return false;
                }

                if (v.bottomEdge != null && !VerticalSeamGraphVertex.checkEquality(bottomVertex, v.bottomEdge.to)) {
                    return false;
                }

                if (v.rightEdge != null && !VerticalSeamGraphVertex.checkEquality(bottomRightVertex, v.rightEdge.to)) {
                    return false;
                }

                if (v.leftEdge != null && !(v.leftEdge.to.coord.x + 1 == v.coord.x && v.leftEdge.to.coord.y - 1 == v.coord.y)) {
                    return false;
                }

                if (v.bottomEdge != null && !(((VerticalSeamGraphVertexNonEndpoint) v).bottom).isSink && !(v.bottomEdge.to.coord.y - 1 == v.coord.y)) {
                    return false;
                }

                if (v.rightEdge != null && !(v.rightEdge.to.coord.x -1 == v.coord.x && v.rightEdge.to.coord.y - 1 == v.coord.y)) {
                    return false;
                }

            }
            curs = ((VerticalSeamGraphVertexNonEndpoint) start).bottom;
        }
        return true;
    }

    public boolean checkThatNeighborsHaveCorrectCoordinates(DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G) {
        for (Edge<VerticalSeamGraphVertex> e : ((VerticalSeamGraphVertexSource) G.start).edgeList) {
            if (!((VerticalSeamGraphVertexNonEndpoint) e.to).top.equals(e.from)) {
                return false;
            }
        }

        VerticalSeamGraphVertex curs = (VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexSource) G.start).edgeList.get(0).to);

        for (int y=0; y<G.numVertVertices; ++y) {
            VerticalSeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                // check invariants on curs
                VerticalSeamGraphVertexNonEndpoint v = (VerticalSeamGraphVertexNonEndpoint) curs;
                VerticalSeamGraphVertex topVertex = v.top;
                VerticalSeamGraphVertex topLeftVertex = v.topLeft;
                VerticalSeamGraphVertex topRightVertex = v.topRight;
                VerticalSeamGraphVertex bottomLeftVertex = v.bottomLeft;
                VerticalSeamGraphVertex bottomVertex = v.bottom;
                VerticalSeamGraphVertex bottomRightVertex = v.bottomRight;
                VerticalSeamGraphVertex leftVertex = v.left;
                VerticalSeamGraphVertex rightVertex = v.right;

                if (topVertex != null && !topVertex.isSink && !(topVertex.coord.y + 1 == v.coord.y)) {
                    return false;
                }

                // todo: what if v is in the top row?
                if (topLeftVertex != null && !(topLeftVertex.coord.x + 1 == v.coord.x && topLeftVertex.coord.y + 1 == v.coord.y)) {
                    return false;
                }

                if (topRightVertex != null && !(topRightVertex.coord.x - 1 == v.coord.x && topRightVertex.coord.y + 1 == v.coord.y)) {
                    return false;
                }

                if (bottomLeftVertex != null && !(bottomLeftVertex.coord.x + 1 == v.coord.x && bottomLeftVertex.coord.y - 1 == v.coord.y)) {
                    return false;
                }

                if (bottomVertex != null && !(bottomVertex.isSink) && !(bottomVertex.coord.y - 1 == v.coord.y)) {
                    return false;
                }

                if (bottomRightVertex != null && !(bottomRightVertex.coord.y -1 == v.coord.y && bottomRightVertex.coord.x - 1 == v.coord.x)) {
                    return false;
                }

                if (leftVertex != null && !(leftVertex.coord.x + 1 == v.coord.x)) {
                    return false;
                }

                if (rightVertex != null && !(rightVertex.coord.x - 1 == v.coord.x)) {
                    return false;
                }

                curs = ((VerticalSeamGraphVertexNonEndpoint) curs).right;

            }
            curs = ((VerticalSeamGraphVertexNonEndpoint) start).bottom;
        }

        return true;
    }

    public class InfrastructureTester implements ThrowingSupplier<Object> {
        public InfrastructureTester() {
            Picture picture = PictureUtils.loadPicture("small_image_1.png");
            double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
            DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        }

        public Object get() {
            return new Object();
        }
    }

    @Test
    public void infrastructureTest() {
        try {
            System.out.println(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(new InfrastructureTester());
    }

    @Test
    public void checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        assertEquals(true,checkThatEdgeEndpointsHaveCorrectCoordinates(sf.verticalSeamGraph));
    }

    @Test
    public void checkThatInTheVerticalSeamGraphNeighborsHaveCorrectCoordinates() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        assertEquals(true,checkThatNeighborsHaveCorrectCoordinates(sf.verticalSeamGraph));
    }


    @Test
    public void verticalSeamGraphProducesCorrectPicturesUponRequest() {
        verticalSeamGraphConstructorTestImage1();
        verticalSeamGraphConstructorTestImage2();
        verticalSeamGraphConstructorTestImage3();
    }

    public void verticalSeamGraphConstructorTestImage1() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        Picture picture2 =  sf.verticalSeamGraph.toPicture();
        assertEquals(picture, picture2);
    }

    public void verticalSeamGraphConstructorTestImage2() {
        Picture picture = PictureUtils.loadPicture("small_image_2.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        Picture picture2 =  sf.verticalSeamGraph.toPicture();
        assertEquals(picture, picture2);
    }

    public void verticalSeamGraphConstructorTestImage3() {
        Picture picture = PictureUtils.loadPicture("small_image_3.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        Picture picture2 =  sf.verticalSeamGraph.toPicture();
        assertEquals(picture, picture2);
    }


    @Test
    public void testForVerticalSeamGraphThatTheNeighborsHaveCorrectCoordinates() {
        //return checkThatNeighborsHaveCorrectCoordinates();
    }

    @Test
    public void testForVerticalSeamGraphThatTheEdgesAreConnectedToTheCorrectVertices() {
       // return checkThatEdgeEndpointsHaveCorrectCoordinates();
    }

    @Test
    public void energyCalculationsMatchUp() {
    }

    @Test
    public void bothSeamFindersFindCorrectSeam() {

    }

    @Test
    public void removingSeamsYieldsIdenticalImages() {

    }

    @Test
    public void removingSeamsPreservesGraphInvariants() {

    }

    public static void main(String[] args) {

    }
}
