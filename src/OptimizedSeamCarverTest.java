import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void energyCalculationsMatchUp() {
        testThatEnergyCalculationsMatchUpForImage("small_image_1.png");
        testThatEnergyCalculationsMatchUpForImage("small_image_2.png");
        testThatEnergyCalculationsMatchUpForImage("small_image_3.png");
    }

    private void testThatEnergyCalculationsMatchUpForImage(String filename) {

        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);

        DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G = sf.verticalSeamGraph;

        VerticalSeamGraphVertex curs = (VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexSource) G.start).edgeList.get(0).to);

        for (int y=0; y<G.numVertVertices; ++y) {
            VerticalSeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                VerticalSeamGraphVertexNonEndpoint v = (VerticalSeamGraphVertexNonEndpoint) curs;

                assertTrue((G.computeEnergy(v) - G.energyOfPixel(x,y)) < 1E-6);

                curs = ((VerticalSeamGraphVertexNonEndpoint) curs).right;

            }

            curs = ((VerticalSeamGraphVertexNonEndpoint) start).bottom;
        }

    }

    @Test
    public void bothSeamFindersFindCorrectSeam() {
        bothSeamFindersFindCorrectSeam("small_image_1.png");
        bothSeamFindersFindCorrectSeam("small_image_2.png");
        bothSeamFindersFindCorrectSeam("small_image_3.png");
    }

    public void bothSeamFindersFindCorrectSeam(String filename) {

    }

    @Test
    public void testThatTheRandomlyGeneratedVerticalSeamsAreCorrect() {
        testThatTheRandomlyGeneratedVerticalSeamsAreCorrect("small_image_1.png",1000);
        testThatTheRandomlyGeneratedVerticalSeamsAreCorrect("small_image_2.png",1000);
        testThatTheRandomlyGeneratedVerticalSeamsAreCorrect("small_image_3.png",1000);
    }

    public void testThatTheRandomlyGeneratedVerticalSeamsAreCorrect(String filename, int numIterations) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);

        DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G = sf.verticalSeamGraph;

        int i = 0;
        do {
            List<Integer> seam = sf.generateRandomVerticalSeam();

            assertTrue(seam.size() == G.numVertVertices);
            int prev = seam.get(0);
            for (int j=0; j<seam.size(); ++j) {
                int val = seam.get(j);
                assertTrue(0 <= val && val < G.numHorizVertices);
                assertTrue(Math.abs(prev - val) <= 1);
                prev = val;
            }

            System.out.println("Seam generation testing: passed iteration " + i + " with file " + filename);

            ++i;

        } while (i < numIterations);

    }

    @Test
    public void removingSeamsYieldsIdenticalImages() {
        removingSeamsYieldsIdenticalImages("small_image_1.png");
        removingSeamsYieldsIdenticalImages("small_image_2.png");
        removingSeamsYieldsIdenticalImages("small_image_3.png");
    }

    public void removingSeamsYieldsIdenticalImages(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);

        DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G = sf.verticalSeamGraph;
        List<Integer> seam1 = sf.generateRandomVerticalSeam();
    }

    @Test
    public void removingSeamsPreservesGraphInvariants() {
        removingSeamsPreservesGraphInvariants("small_image_1.png");
        removingSeamsPreservesGraphInvariants("small_image_2.png");
        removingSeamsPreservesGraphInvariants("small_image_3.png");
    }

    public void removingSeamsPreservesGraphInvariants(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);

        DijkstraSeamFinderOptimized.VerticalSeamGraphOptimized G = sf.verticalSeamGraph;

        List<Integer> seam1 = sf.generateRandomVerticalSeam();
        G.removeSeam(seam1);

        assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        List<Integer> seam2 = sf.generateRandomVerticalSeam();
        G.removeSeam(seam2);

        assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        List<Integer> seam3 = sf.generateRandomVerticalSeam();
        G.removeSeam(seam3);

        assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

    }

    public static void main(String[] args) {

    }
}
