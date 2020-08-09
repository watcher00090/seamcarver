package sample;

import edu.princeton.cs.algs4.Picture;
import javafx.scene.image.Image;


import javafx.scene.image.PixelFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OptimizedSeamCarverTest {

    public boolean checkThatEdgeEndpointsHaveCorrectCoordinates(DijkstraSeamFinderOptimized.SeamGraphOptimized G) {
        for (Edge<SeamGraphVertex> e : ((VerticalSeamGraphVertexSource) G.start).edgeList) {
            if (!((SeamGraphVertex) e.to).top.equals(e.from)) {
                return false;
            }
        }

        SeamGraphVertex curs = (SeamGraphVertex) (((VerticalSeamGraphVertexSource) G.start).edgeList.get(0).to);

        for (int y=0; y<G.numVertVertices; ++y) {
            SeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                // check invariants on curs
                SeamGraphVertex v = (SeamGraphVertex) curs;

                if (v.inSeam) {
                    System.out.println("oops, traversing a vertex in the seam!");
                }

                SeamGraphVertex bottomLeftVertex = v.bottomLeft;
                SeamGraphVertex bottomVertex = v.bottom;
                SeamGraphVertex bottomRightVertex = v.bottomRight;
                SeamGraphVertex rightVertex = v.right;
                SeamGraphVertex topRightVertex = v.topRight;

                if (v.bottomLeftEdge != null && !SeamGraphVertex.checkEquality(bottomLeftVertex, v.bottomLeftEdge.to)) {
                    return false;
                }

                if (v.bottomEdge != null && !v.bottom.isSink && !SeamGraphVertex.checkEquality(bottomVertex, v.bottomEdge.to)) {
                    return false;
                }

                if (v.bottomRightEdge != null && !SeamGraphVertex.checkEquality(bottomRightVertex, v.bottomRightEdge.to)) {
                    return false;
                }

                if (v.rightEdge != null && !SeamGraphVertex.checkEquality(rightVertex, v.rightEdge.to)) {
                    return false;
                }

                if (v.topRightEdge != null && !SeamGraphVertex.checkEquality(topRightVertex, v.topRightEdge.to)) {
                    return false;
                }

            }
            curs = ((SeamGraphVertex) start).bottom;
        }
        return true;
    }

    public boolean checkThatNeighborsHaveCorrectCoordinates(DijkstraSeamFinderOptimized.SeamGraphOptimized G) {
        for (Edge<SeamGraphVertex> e : ((VerticalSeamGraphVertexSource) G.start).edgeList) {
            if (!((SeamGraphVertex) e.to).top.equals(e.from)) {
                return false;
            }
        }

        SeamGraphVertex curs = (SeamGraphVertex) (((VerticalSeamGraphVertexSource) G.start).edgeList.get(0).to);

        for (int y=0; y<G.numVertVertices; ++y) {
            SeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                // check invariants on curs
                SeamGraphVertex v = (SeamGraphVertex) curs;
                SeamGraphVertex topVertex = v.top;
                SeamGraphVertex topLeftVertex = v.topLeft;
                SeamGraphVertex topRightVertex = v.topRight;
                SeamGraphVertex bottomLeftVertex = v.bottomLeft;
                SeamGraphVertex bottomVertex = v.bottom;
                SeamGraphVertex bottomRightVertex = v.bottomRight;
                SeamGraphVertex leftVertex = v.left;
                SeamGraphVertex rightVertex = v.right;

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

                if (bottomRightVertex != null && !(bottomRightVertex.coord.y - 1 == v.coord.y && bottomRightVertex.coord.x - 1 == v.coord.x)) {
                    return false;
                }

                if (leftVertex != null && !(leftVertex.coord.x + 1 == v.coord.x)) {
                    return false;
                }

                if (rightVertex != null && !(rightVertex.coord.x - 1 == v.coord.x)) {
                    return false;
                }

                curs = ((SeamGraphVertex) curs).right;

            }
            curs = ((SeamGraphVertex) start).bottom;
        }

        return true;
    }

    public boolean checkNonNullityOfEdges(DijkstraSeamFinderOptimized.SeamGraphOptimized G) {

        SeamGraphVertex curs = G.start.edgeList.get(0).to;

        for (int y=0; y<G.numVertVertices; ++y) {
            SeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                // check invariants on curs
                SeamGraphVertex v = curs;
                SeamGraphVertex topVertex = v.top;
                SeamGraphVertex topLeftVertex = v.topLeft;
                SeamGraphVertex topRightVertex = v.topRight;
                SeamGraphVertex bottomLeftVertex = v.bottomLeft;
                SeamGraphVertex bottomVertex = v.bottom;
                SeamGraphVertex bottomRightVertex = v.bottomRight;
                SeamGraphVertex leftVertex = v.left;
                SeamGraphVertex rightVertex = v.right;

                if (v.rightEdge == null) {
                    return false;
                }

                if (v.bottomEdge == null) {
                    return false;
                }

                if (y != 0 && x != G.numHorizVertices - 1 && v.topRightEdge == null) {
                    return false;
                }

                if (x != 0 && y != G.numVertVertices - 1 && v.bottomLeftEdge == null) {
                    return false;
                }

                if (x != G.numHorizVertices - 1 && y != G.numVertVertices - 1 && v.bottomRightEdge == null) {
                    return false;
                }

                curs = ((SeamGraphVertex) curs).right;

            }
            curs = ((SeamGraphVertex) start).bottom;
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
    public void checkThatVerticalSeamGraphIsSetUpCorrectly() {
        checkThatVerticalSeamGraphIsSetUpCorrectly("small_image_1.png");
        checkThatVerticalSeamGraphIsSetUpCorrectly("small_image_2.png");
        checkThatVerticalSeamGraphIsSetUpCorrectly("small_image_3.png");
    }

    @Test
    public void checkThatSeamGraphPixelReaderIsOperational() {
        checkThatSeamGraphPixelReaderIsOperational("small_image_1.png");
        checkThatSeamGraphPixelReaderIsOperational("small_image_2.png");
        checkThatSeamGraphPixelReaderIsOperational("small_image_3.png");
    }

    public void checkThatSeamGraphPixelReaderIsOperational(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        SeamGraphPixelReader pixelReader = new SeamGraphPixelReader(sf);

        int[] buf = new int[picture.width() * picture.height()];
        pixelReader.getPixels(0,0,picture.width(),picture.height(), PixelFormat.getIntArgbInstance(), buf, 0, picture.width());

        for (int x=0; x<picture.width(); ++x) {
            for (int y=0; y<picture.height(); ++y) {
                assertEquals(picture.getRGB(x,y),buf[y*picture.width() + x] );
            }
        }
    }

    @Test
    public void checkThatPictureWrapperIsWorkingCorrectly() {
        checkThatPictureWrapperIsWorkingCorrectly("small_image_1.png");
        checkThatPictureWrapperIsWorkingCorrectly("small_image_2.png");
        checkThatPictureWrapperIsWorkingCorrectly("small_image_3.png");
        checkThatPictureWrapperIsWorkingCorrectly("small_image_4.png");
        checkThatPictureWrapperIsWorkingCorrectly("small_image_5.png");
    }

    public void checkThatPictureWrapperIsWorkingCorrectly(String filename) {
        try {
            Picture picture = PictureUtils.loadPicture(filename);
            Image image = new Image(new FileInputStream(filename));

            RGBFetcher pictureWrapper = new PictureWrapper(picture);
            RGBFetcher imageWrapper = new ImageWrapper(image);

            assertEquals(picture.width(), image.getWidth());
            assertEquals(picture.height(), image.getHeight());

            for (int x=0; x<picture.width(); ++x) {
                for (int y=0; y<picture.height(); ++y) {
                    assertEquals(pictureWrapper.getRGB(x,y), imageWrapper.getRGB(x,y));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkThatVerticalSeamGraphIsSetUpCorrectly(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        assertEquals(true,checkThatEdgeEndpointsHaveCorrectCoordinates(sf.verticalSeamGraph));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(sf.verticalSeamGraph));
    }

    @Test
    public void checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly() {
        checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly("small_image_1.png");
        checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly("small_image_2.png");
        checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly("small_image_3.png");
    }

    public void checkThatInTheVerticalSeamGraphsLowerEdgesAreSetUpCorrectly(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
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
    public void testThatEdgesSupposedToBeNonNullAreNonNullAfterSetup() {
        testThatEdgesSupposedToBeNonNullAreNonNullAfterSetup("small_image_1.png");
        testThatEdgesSupposedToBeNonNullAreNonNullAfterSetup("small_image_2.png");
        testThatEdgesSupposedToBeNonNullAreNonNullAfterSetup("small_image_3.png");
    }

    public void testThatEdgesSupposedToBeNonNullAreNonNullAfterSetup(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double energies[][] = SeamCarver.computeEnergies(picture,new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;
        assertTrue(checkNonNullityOfEdges(G));
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

        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        SeamGraphVertex curs = G.start.edgeList.get(0).to;

        for (int y=0; y<G.numVertVertices; ++y) {
            SeamGraphVertex start = curs;

            for (int x=0; x<G.numHorizVertices; ++x) {

                SeamGraphVertex v = curs;

                if ((G.computeEnergy(v) - G.energyOfPixel(x,y)) >= 1E-20) {
                    System.out.println("Oops, this shouldn't have happened");
                }
                assertTrue((G.computeEnergy(v) - G.energyOfPixel(x,y)) < 1E-20);

                curs = curs.right;

            }

            curs = start.bottom;
        }

    }

    @Test
    // TODO: implement this method
    public void bothSeamFindersFindSameSeam() {
        //bothSeamFindersFindSameSeam("small_image_1.png");
        for (int i=0; i<6; ++i) {
            bothSeamFindersFindSameSeam("small_image_1.png");
            bothSeamFindersFindSameSeam("small_image_2.png");
            bothSeamFindersFindSameSeam("small_image_3.png");
        }
    }

    // TODO: implement this method
    public void bothSeamFindersFindSameSeam(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        perturb(energies);

        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        List<Integer> seamA = sf.findVerticalSeam();

        DijkstraSeamFinder sf2 = new DijkstraSeamFinder(true);
        List<Integer> seamB = sf2.findVerticalSeam(energies);

        assertTrue(seamA.size() == seamB.size());
        for (int i=0; i<seamA.size(); ++i) {
            int A = seamA.get(i).intValue();
            int B =  seamB.get(i).intValue();
            System.out.print( (A - B) + ", ");
            assertTrue(seamA.get(i).intValue() == seamB.get(i).intValue());
        }
        System.out.println();
    }

    // perturb so to prevent multiple shortest paths
    private void perturb(double[][] energies) {
        for (int i=0;i<energies.length; ++i) {
            for (int j=0;j<energies[i].length; ++j) {
                energies[i][j] += (Math.random() / 1.0E5);
            }
        }
    }

    @Test public void testThatSameSeamIsFoundAfterRemovingSeamsRepeatedly() {
        assertTrue(false);
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

        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

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
    // TODO: implement this method
    public void removingSeamsYieldsIdenticalImages() {
        removingSeamsYieldsIdenticalImages("small_image_1.png");
        removingSeamsYieldsIdenticalImages("small_image_2.png");
        removingSeamsYieldsIdenticalImages("small_image_3.png");
    }

    // TODO: implement this method
    public void removingSeamsYieldsIdenticalImages(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        DualGradientEnergyFunction energyFunc = new DualGradientEnergyFunction();
        double[][] energies = SeamCarver.computeEnergies(picture, energyFunc);
        perturb(energies);

        DijkstraSeamFinderOptimized sfA = new DijkstraSeamFinderOptimized(picture, energies);
        List<Integer> seamA = sfA.findVerticalSeam();
        sfA.verticalSeamGraph.removeSeam(seamA);
        Picture pictureA = sfA.verticalSeamGraph.toPicture();

        DijkstraSeamFinder sfB = new DijkstraSeamFinder();
        List<Integer> seamB = sfB.findVerticalSeam(energies);
        SeamCarver scB = new SeamCarver(picture, energyFunc, sfB);
        scB.removeVerticalSeam(seamB);
        Picture pictureB = scB.picture();

        assertEquals(pictureA.width(), pictureB.width());
        assertEquals(pictureA.height(), pictureB.height());
        for (int x=0; x<pictureA.width(); ++x) {
            for (int y=0; y<pictureA.height(); ++y) {
                if (pictureA.getRGB(x,y) != pictureB.getRGB(x,y)) {
                    System.out.println("Oops, they should have been equal!");
                }
                assertEquals(pictureA.getRGB(x,y), pictureB.getRGB(x,y));
            }
        }
    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariants() {
        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");
    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsHarderTest() {
        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

        removingVerticalSeamsPreservesGraphInvariants("small_image_1.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_2.png");
        removingVerticalSeamsPreservesGraphInvariants("small_image_3.png");

    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsLargeImage() {
        removingVerticalSeamsPreservesGraphInvariants("large_image_1.png");
    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsHardcodedSeam1() {
        Integer[] arr = {0, 0, 1, 2, 3, 2, 3, 3, 2, 2, 2};
        List<Integer> seam = new ArrayList<>(Arrays.asList(arr));

        Picture picture = PictureUtils.loadPicture("small_image_3.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        sf.DEBUG_MODE = true;
        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        G.debugSeam = seam;
        G.removeSeam(seam);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        boolean ret = checkThatNeighborsHaveCorrectCoordinates(G);
        assertEquals(true, ret);
    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsHardcodedSeam2() {
        Integer[] arr = {10, 11, 10, 10, 11, 11, 11, 12, 12, 12, 11};
        List<Integer> seam = new ArrayList<>(Arrays.asList(arr));

        Picture picture = PictureUtils.loadPicture("small_image_3.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        sf.DEBUG_MODE = true;
        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        G.debugSeam = seam;
        G.removeSeam(seam);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        boolean ret = checkThatNeighborsHaveCorrectCoordinates(G);
        assertEquals(true, ret);
    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsHardCodedSeam3() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        sf.DEBUG_MODE = true;

        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        Integer[] arr = {7,8,8,8,8,8,7,6,7};
        List<Integer> seam1 = new ArrayList<>(Arrays.asList(arr));

        G.debugSeam = seam1;
        G.removeSeam(seam1);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));


    }

    @Test
    public void removingVerticalSeamsPreservesGraphInvariantsHardCodedSeam4() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        sf.DEBUG_MODE = true;

        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        Integer arr[] = {3,4,5,6,5,6,6,5,4};
        List<Integer> seam1 = new ArrayList<>(Arrays.asList(arr));

        G.debugSeam = seam1;
        G.removeSeam(seam1);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));
    }

    public void removingVerticalSeamsPreservesGraphInvariants(String filename) {
        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        sf.DEBUG_MODE = true;

        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        List<Integer> seam1 = sf.generateRandomVerticalSeam();
        G.debugSeam = seam1;
        G.removeSeam(seam1);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        List<Integer> seam2 = sf.generateRandomVerticalSeam();
        G.debugSeam = seam2;
        G.removeSeam(seam2);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));

        List<Integer> seam3 = sf.generateRandomVerticalSeam();
        G.debugSeam = seam3;
        G.removeSeam(seam3);

        //assertEquals(true, checkThatEdgeEndpointsHaveCorrectCoordinates(G));
        assertEquals(true, checkThatNeighborsHaveCorrectCoordinates(G));
    }

    public void printAFewVerticalSeams() {
        Picture picture = PictureUtils.loadPicture("small_image_1.png");
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());
        DijkstraSeamFinderOptimized sf = new DijkstraSeamFinderOptimized(picture, energies);
        DijkstraSeamFinderOptimized.SeamGraphOptimized G = sf.verticalSeamGraph;

        for (int i=0; i<50; ++i) {
            List<Integer> seam = sf.generateRandomVerticalSeam();
            assertTrue(seam.size() == G.numVertVertices);
            for (int j = 0; j < seam.size(); ++j) {
                System.out.print(seam.get(j) + ", ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

    }
}
