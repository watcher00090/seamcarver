package sample;

import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
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
        Queue<Pair<Integer>> queue = new SynchronousQueue<>();

        Picture picture = PictureUtils.loadPicture(filename);
        double[][] energies = SeamCarver.computeEnergies(picture, new DualGradientEnergyFunction());

        DijkstraSeamFinder sf= new DijkstraSeamFinder(false);
        sf.findVerticalSeam(energies);

        ArrayList<Edge<Pair<Integer>>> outgoingEdgesFromSource = (ArrayList) sf.verticalSeamGraph.outgoingEdgesFrom(sf.verticalSeamGraph.start);
        for (int i=0; i<outgoingEdgesFromSource.size(); ++i) {
            if (!outgoingEdgesFromSource.get(i).to.equalsPoint(new Pair(i,0))) {
                return false;
            } else {
                queue.add(outgoingEdgesFromSource.get(i).to);
            }
        }

        // while the queue is not empty
        while (!queue.isEmpty()) {

            // remove a vertex from the front of the queue
            Pair<Integer> v = queue.remove();

            ArrayList<Edge<Pair<Integer>>> outgoingEdgesFromList = (ArrayList) sf.verticalSeamGraph.outgoingEdgesFrom(v);
            boolean foundBottomLeft = false;
            boolean foundBottomRight = false;
            boolean foundBottom = false;

            for (int i = 0; i < outgoingEdgesFromList.size(); ++i) {
                Pair<Integer> toVertex = outgoingEdgesFromList.get(i).to();
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
                if (foundBottomRight || foundBottomLeft || !foundBottom) {
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
            for (int i = 0; i < outgoingEdgesFromList.size(); ++i) {
                queue.add(outgoingEdgesFromList.get(i).to);
            }
        }
        return true;
    }


    public static void main(String[] args) {

    }
}
