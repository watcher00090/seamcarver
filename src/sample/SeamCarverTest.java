package sample;

import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeamCarverTest {

    @Test
    void testThatInTheVerticalSeamGraphEdgesAreSetUpCorrectly() {
        assertTrue(testThatInTheVerticalSeamGraphEdgesAreSetUpCorrectly("small_image_1.png"));
        assertTrue(testThatInTheVerticalSeamGraphEdgesAreSetUpCorrectly("small_image_2.png"));
        assertTrue(testThatInTheVerticalSeamGraphEdgesAreSetUpCorrectly("small_image_3.png"));
    }


    boolean testThatInTheVerticalSeamGraphEdgesAreSetUpCorrectly(String filename) {

        // Create a queue for BFS
        Queue<Pair<Integer>> queue = new LinkedBlockingQueue<>();

        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());

        DijkstraSeamFinder sf= new DijkstraSeamFinder(false);
        HashSet<Integer> XCoordRepo = new HashSet<>();
        sf.findVerticalSeam(energies);

        Collection<Edge<Pair<Integer>>> outgoingEdgesFromSource = sf.verticalSeamGraph.outgoingEdgesFrom(sf.verticalSeamGraph.start);
        for (Edge<Pair<Integer>> edge : outgoingEdgesFromSource) {
            if (XCoordRepo.contains(edge.to.x)) {
                return false;
            }
            XCoordRepo.add(edge.to.x);
            queue.add(edge.to);
        }

        for (int k=0; k<outgoingEdgesFromSource.size(); ++k) {
            if (!XCoordRepo.contains(k)) {
                return false;
            }
        }

        // while the queue is not empty
        while (!queue.isEmpty()) {

            // remove a vertex from the front of the queue
            Pair<Integer> v = queue.remove();

            Collection<Edge<Pair<Integer>>> outgoingEdgesFromList =  sf.verticalSeamGraph.outgoingEdgesFrom(v);
            boolean foundBottomLeft = false;
            boolean foundBottomRight = false;
            boolean foundBottom = false;

            for (Edge<Pair<Integer>> edge : outgoingEdgesFromList) {
                Pair<Integer> toVertex = edge.to();
                if (v.y == picture.height() - 1) {
                    if (!(toVertex.x < 0 && toVertex.y < 0 && outgoingEdgesFromList.size() == 1)) {
                        return false;
                    }
                } else { // keep going

                    if (toVertex.x + 1 == v.x) {
                        if (!foundBottomLeft && toVertex.y - 1 == v.y) {
                            foundBottomLeft = true;
                        } else {
                            return false;
                        }
                    }
                    if (toVertex.x == v.x) {
                        if (!foundBottom && toVertex.y - 1 == v.y) {
                            foundBottom = true;
                        } else {
                            return false;
                        }
                    }
                    if (toVertex.x - 1 == v.x) {
                        if (!foundBottomRight && toVertex.y - 1 == v.y) {
                            foundBottomRight = true;
                        } else {
                            return false;
                        }
                    }
                }
            }

            if (v.y == picture.height() - 1) {
                if (foundBottomLeft || !foundBottom || foundBottomRight) {
                    return false;
                }
            } else {

                if (v.x == 0) {
                    if (foundBottomLeft || !foundBottom || !foundBottomRight) {
                        return false;
                    }
                } else if (v.x == picture.width() - 1) {
                    if (!foundBottomLeft || !foundBottom || foundBottomRight) {
                        return false;
                    }
                } else {

                    if (!foundBottomLeft || !foundBottom || !foundBottomRight) {
                        return false;
                    }

                }

            }
            for (Edge<Pair<Integer>> edge : outgoingEdgesFromList) {
                queue.add(edge.to);
            }
        }
        return true;
    }


    public static void main(String[] args) {

    }
}
