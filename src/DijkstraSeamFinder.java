import edu.princeton.cs.algs4.Picture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import edu.princeton.cs.algs4.Picture;

enum DifferenceType {
    CentralDifference, ForwardDifference, BackwardDifference;
}

enum Dir {
    x, y;
}

class RedColorFunction implements Function<VerticalSeamGraphVertexNonEndpoint, Integer> {
    public Integer apply(VerticalSeamGraphVertexNonEndpoint x) {
        int rgb = x.getRGB();
        int r = (rgb >> 16) & 0xFF;
        return r;
    }
}

class GreenColorFunction implements Function<VerticalSeamGraphVertexNonEndpoint, Integer> {
    public Integer apply(VerticalSeamGraphVertexNonEndpoint x) {
        int rgb = x.getRGB();
        int g = (rgb >> 8) & 0xFF;
        return g;
    }
}

class BlueColorFunction implements Function<VerticalSeamGraphVertexNonEndpoint, Integer> {
    public Integer apply(VerticalSeamGraphVertexNonEndpoint x) {
        int rgb = x.getRGB();
        int b = (rgb >> 0) & 0xFF;
        return b;
    }
}


// (x,y) coordinates of a pixel
public class Pair<T> {
    public T x;
    public T y;

    public Pair(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public boolean equalsPoint(Pair<T> p) {
        return p.x == x && p.y == y;
    }
}

public enum DeletionCase {case1, case2, case3};



public class VerticalSeamGraphVertex {
    Pair<Integer> coord;
    boolean isSource;
    boolean isSink;
    public ArrayList<Edge<VerticalSeamGraphVertex>> edgeList;

    // if we're the source, child1 points to a linked list of vertices
    public VerticalSeamGraphVertex(Pair<Integer> coord) {
        this.coord = coord;
        this.edgeList = new ArrayList<>();
    }

    public boolean equals(VerticalSeamGraphVertex v) {
        return (this.coord.x == v.coord.x && this.coord.y == v.coord.y);
    }

    public void makeEdgeList() {
        return;
    }

}

// if we're at the bottom of the image, we only have bottomedge (the other edges will be null)
public class VerticalSeamGraphVertexNonEndpoint extends VerticalSeamGraphVertex {
    public VerticalSeamGraphVertex topLeft = null;
    public VerticalSeamGraphVertex top = null;
    public VerticalSeamGraphVertex topRight = null;
    public VerticalSeamGraphVertex left = null;
    public VerticalSeamGraphVertex right = null;
    public VerticalSeamGraphVertex bottomLeft = null;
    public VerticalSeamGraphVertex bottom = null;
    public VerticalSeamGraphVertex bottomRight = null;

    public Edge<VerticalSeamGraphVertex> leftEdge;
    public Edge<VerticalSeamGraphVertex> bottomEdge;
    public Edge<VerticalSeamGraphVertex> rightEdge;

    int rgb;

    public VerticalSeamGraphVertexNonEndpoint(Pair<Integer> coord, int rgb) {
        super(coord);
        leftEdge = new Edge<VerticalSeamGraphVertex>(null, null,0);
        bottomEdge = new Edge<VerticalSeamGraphVertex>(null, null,0);
        rightEdge = new Edge<VerticalSeamGraphVertex>(null, null,0);
        isSource = false;
        isSink = false;
    }

    // todo: pass in rgb from the picture when creating this vertex

    public int getRGB() {
        return rgb;
    }

}

class VerticalSeamGraphVertexSource extends VerticalSeamGraphVertex {
    public VerticalSeamGraphVertex leftChild; // linked list of children (starting with the leftmost)

    public VerticalSeamGraphVertexSource(Pair<Integer> coord) {
        super(coord);
        isSource = true;
        isSink = false;
    }

}

class VerticalSeamGraphVertexSink extends VerticalSeamGraphVertex {
    public VerticalSeamGraphVertex leftParent; // linked list of parents (starting with the leftmost)

    public VerticalSeamGraphVertexSink(Pair<Integer> coord) {
        super(coord);
        isSource = false;
        isSink = true;
    }
}


class DualGradientEnergyFunctionNodal implements NodalEnergyFunction {
    
    RedColorFunction r_func;
    BlueColorFunction b_func;
    GreenColorFunction g_func;
    
    public double apply(VerticalSeamGraphVertexNonEndpoint v) {
        System.out.println("Calling DualGradientEnergyFunction::apply....");

        DifferenceType xDifferenceType = getXDifferenceType(v);
        DifferenceType yDifferenceType = getYDifferenceType(v);
        int R_x = computeGrad(v, r_func, xDifferenceType, Dir.x);
        int G_x = computeGrad(v, g_func, xDifferenceType, Dir.x);
        int B_x = computeGrad(v, b_func, xDifferenceType, Dir.x);

        int R_y = computeGrad(v, r_func, yDifferenceType, Dir.y);
        int G_y = computeGrad(v, g_func, yDifferenceType, Dir.y);
        int B_y = computeGrad(v, b_func, yDifferenceType, Dir.y);

        int del_2_x = R_x * R_x + G_x * G_x + B_x * B_x;
        int del_2_y = R_y * R_y + G_y * G_y + B_y * B_y;

        return Math.sqrt(del_2_x + del_2_y);
    }

    private static int computeGrad(VerticalSeamGraphVertexNonEndpoint v, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func, DifferenceType differenceType, Dir d) {
        if (differenceType == DifferenceType.ForwardDifference && d == Dir.x) {
            return forwardDiff_x(v, func);
        } else if (differenceType == DifferenceType.ForwardDifference && d == Dir.y) {
            return forwardDiff_y(v, func);
        } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.x) {
            return backwardDiff_x(v, func);
        } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.y) {
            return backwardDiff_y(v, func);
        } else if (differenceType == DifferenceType.CentralDifference && d == Dir.x) {
            return centralDiff_x(v, func);
        } else if (differenceType == DifferenceType.CentralDifference && d == Dir.y) {
            return centralDiff_y(v, func);
        } else {
            System.out.println("System error in computeGrad");
            return -1;
        }
    }

    private static DifferenceType getXDifferenceType(VerticalSeamGraphVertexNonEndpoint v) {
        if (v.left == null) { // x == 0
            return DifferenceType.ForwardDifference;
        } else if (v.right == null) { // x == width - 1
            return DifferenceType.BackwardDifference;
        } else {
            return DifferenceType.CentralDifference;
        }
    }

    private static DifferenceType getYDifferenceType(VerticalSeamGraphVertexNonEndpoint v) {
        if (v.top == null) { // y == 0
            return DifferenceType.ForwardDifference;
        } else if (v.bottomLeft == null && v.bottomRight == null) { // y == height - 1
            return DifferenceType.BackwardDifference;
        } else {
            return DifferenceType.CentralDifference;
        }
    }

    private static int forwardDiff_x(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        //return -3 * func.apply(x, y) + 4 * func.apply(x + 1, y) - func.apply(x + 2, y);
        return -3 * func.apply(node) + 4 * func.apply((VerticalSeamGraphVertexNonEndpoint) node.right) - func.apply((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) (node.right)).right);
    }

    private static int forwardDiff_y(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((VerticalSeamGraphVertexNonEndpoint) node.bottom) - func.apply((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) node.bottom ).bottom));
    }

    private static int backwardDiff_x(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((VerticalSeamGraphVertexNonEndpoint)node.left) - func.apply((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) node.left).left));
    }

    private static int backwardDiff_y(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((VerticalSeamGraphVertexNonEndpoint) node.top) - func.apply((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint)node.top).top);
    }

    private static int centralDiff_x(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        //return func.apply(x + 1, y) - func.apply(x - 1, y);
        return func.apply((VerticalSeamGraphVertexNonEndpoint) node.right) - func.apply((VerticalSeamGraphVertexNonEndpoint) node.left);
    }

    private static int centralDiff_y(VerticalSeamGraphVertexNonEndpoint node, Function<VerticalSeamGraphVertexNonEndpoint, Integer> func) {
        //return func.apply(x, y + 1) - func.apply(x, y - 1);
        return func.apply((VerticalSeamGraphVertexNonEndpoint) node.bottom) - func.apply((VerticalSeamGraphVertexNonEndpoint) node.top);
    }

}


public class DijkstraSeamFinder implements SeamFinder {
    // Perhaps replace all 4 references to "Object" on the line below with whatever vertex type
    //  you choose for your graph
    private final ShortestPathFinder<Graph<Object, Edge<Object>>, Object, Edge<Object>> pathFinder;
    public VerticalSeamGraphOptimized oldVerticalSeamGraph = null;
    public List<Integer> lastSeam = null;
    VerticalSeamGraphVertex topRowVertex = null;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }

    /* The vertex for a pixel is numbered by the (x,y) location of the pixel.
     * (0,0) (1,0) (2,0) (3,0) (4,0) .....
     * (0,1) (1,1) (2,1) (3,1) (4,1) .....
     * (0,2) (1,2) (2,2) (3,2) (4,2) .....
     * ...................................
     *
     * The start vertex has coordinates (-1,-1) and the end vertex coordinates (-2,-2).
     * The weight of an edge is the cost of the 'to' vertex with the exception of the
     * edges from the bottom vertices to the end vertex, which all have weight 0.
     */
    public class VerticalSeamGraphOptimized implements Graph<VerticalSeamGraphVertex, Edge<VerticalSeamGraphVertex>> {
        VerticalSeamGraphVertexSource start;
        VerticalSeamGraphVertexSink end;
        int numHorizVertices;
        int numVertVertices;
        DualGradientEnergyFunctionNodal energyFunction;
        double[][] energies;
        Picture picture;

        // weight of edge = energy of 'from' vertex
        public VerticalSeamGraphOptimized(double[][] energies, Picture picture;) {
            this.energyFunction = new DualGradientEnergyFunctionNodal();
            this.energies = energies;
            this.picture = picture;

            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;

            // create source and sink
            end = new VerticalSeamGraphVertexSink(new Pair<>(-2, -2));
            start = new VerticalSeamGraphVertexSource(new Pair<Integer>(-1,-1);

            VerticalSeamGraphVertexNonEndpoint prevRow[] = new VerticalSeamGraphVertexNonEndpoint[numHorizVertices];
            VerticalSeamGraphVertexNonEndpoint currRow[] = new VerticalSeamGraphVertexNonEndpoint[numHorizVertices];

            for (int y = energies.length - 1; y > -1; y--) {

                for (int x = 0; x < energies[0].length; ++x) {

                    if (y == energies.length - 1) {

                        prevRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y), picture.getRGB(x,y));
                        prevRow[x].bottomEdge = new Edge<VerticalSeamGraphVertex>(prevRow[x], end, energyOfPixel(x, y));
                        prevRow[x].edgeList.add(prevRow[x].bottomEdge);

                    } else {

                        currRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y), picture.getRGB(x,y));
                        currRow[x].leftEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x - 1], energyOfPixel(x, y));
                        currRow[x].bottomEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x], energyOfPixel(x, y));
                        currRow[x].rightEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x + 1], energyOfPixel(x, y));

                        currRow[x].edgeList.add(currRow[x].leftEdge);
                        currRow[x].edgeList.add(currRow[x].bottomEdge);
                        currRow[x].edgeList.add(currRow[x].rightEdge);

                        if (x > 0) {
                            currRow[x].left = currRow[x - 1];
                            currRow[x - 1].right = currRow[x];
                        }

                        if (x > 0) {
                            currRow[x].bottomLeft = prevRow[x - 1];
                            prevRow[x - 1].topRight = currRow[x];
                        }

                        if (x < energies[0].length - 1) {
                            currRow[x].bottomRight = prevRow[x + 1];
                            prevRow[x + 1].topLeft = currRow[x];
                        }

                        currRow[x].bottom = prevRow[x];
                        prevRow[x].top = currRow[x];

                    }

                }

                if (y == energies.length - 1) {
                    end.leftParent = prevRow[0];
                }

                if (y == 0) {
                    for (int x = 0; x < prevRow.length; ++x) {
                        start.edgeList.add(new Edge(start, prevRow[x],0));
                    }
                }

            }
            start.leftChild = prevRow[0];
        }

        // update the weights of the edges of v
        public void computeEnergy(VerticalSeamGraphVertexNonEndpoint v) {
            if (v.leftEdge != null) v.leftEdge.weight = energyFunction.apply(v);
            if (v.rightEdge != null) v.rightEdge.weight = energyFunction.apply(v);
            if (v.bottomEdge != null) v.bottomEdge.weight = energyFunction.apply(v);
        }

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        public Collection<Edge<VerticalSeamGraphVertex>> outgoingEdgesFrom(VerticalSeamGraphVertex v) {
            return v.edgeList;
        }

        // todo: take care of the vertices above a given vertex being deleted?

        public VerticalSeamGraphOptimized UpdateVerticalSeamGraph(VerticalSeamGraphOptimized oldVSGraph, double[][] energies, Picture picture, List<Integer> lastSeam) {
            if (oldVSGraph == null) {
                return new VerticalSeamGraphOptimized(energies, picture);
            } else {
                try {

                    // only delete vertices which are adjacent to the recently found seam
                    VerticalSeamGraphVertex prevVertex =  oldVSGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
                    VerticalSeamGraphVertex currVertex = null;

                    for (int i=1; i<lastSeam.size(); ++i) {

                        int i1 = lastSeam.get(i-1);
                        int i2 = lastSeam.get(i);

                        DeletionCase deletionCase;

                        if (i1 < i2) {
                            currVertex = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight;
                            deletionCase = DeletionCase.case1;
                        } else if (i1 > i2) {
                            currVertex = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft;
                            deletionCase = DeletionCase.case3;
                        } else {
                            currVertex = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom;
                            deletionCase = DeletionCase.case2;
                        }

                        VerticalSeamGraphVertex leftVertex = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left;
                        VerticalSeamGraphVertex rightVertex = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                        if (deletionCase == DeletionCase.case1) {

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right != null) {

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).leftEdge.from = (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft));

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).bottomLeft = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft;

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).bottomEdge.from = (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom));

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).bottom = (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom));

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).left = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left;
                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left != null) {

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).right = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft != null){

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft)).topRight = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom != null){

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom)).top = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).top;

                            }

                        } else if (deletionCase == DeletionCase.case2) {

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right != null) {

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).leftEdge.from = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft;

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).bottomLeft = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomLeft;

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).left = ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left));

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left != null) {

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).rightEdge.from = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight;

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).bottomRight = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight;

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).right = ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right));

                            }


                        } else { // case3

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right != null) {

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right)).left = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left != null) {

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).rightEdge.from = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight;

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).bottomRight = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight;

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).bottomEdge.from = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom;

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).bottom = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom;

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).right = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left != null) {

                                (((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left)).right = ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight != null){

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottomRight)).topLeft = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left;

                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom != null){

                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) prevVertex).bottom)).top = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).top;

                            }

                        }

                        computeEnergy((VerticalSeamGraphVertexNonEndpoint) prevVertex);

                        // set prevVertex pointer
                        prevVertex = currVertex;

                    }

                    // todo: handle case where currVertex is the sink

                    if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).left != null) {

                        ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left).right != ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right;

                    }

                    if (((VerticalSeamGraphVertexNonEndpoint) prevVertex).right != null) {

                        ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).right).left != ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) prevVertex).left;

                    }

                    // todo: remove vertex from left parent list of the sink???

                    computeEnergy((VerticalSeamGraphVertexNonEndpoint) prevVertex);

                } catch(Exception e) { // todo remove exception handling
                    e.printStackTrace();
                }
            }
        }




    }

    /* The vertex for a pixel is numbered by the (x,y) location of the pixel.
     * (0,0) (1,0) (2,0) (3,0) (4,0) .....
     * (0,1) (1,1) (2,1) (3,1) (4,1) .....
     * (0,2) (1,2) (2,2) (3,2) (4,2) .....
     * ...................................
     *
     * The start vertex has coordinates (-1,-1) and the end vertex coordinates (-2,-2).
     * The weight of an edge is the cost of the 'to' vertex with the exception of the
     * edges from the bottom vertices to the end vertex, which all have weight 0.
     */
    public class VerticalSeamGraph implements Graph<Pair<Integer>, Edge<Pair<Integer>>> {
        double[][] energies;
        Pair<Integer> start;
        Pair<Integer> end;
        int numHorizVertices;
        int numVertVertices;

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        // weight of edge = energy of 'to' vertex
        public VerticalSeamGraph(double[][] energies) {
            this.energies = energies;
            start = new Pair<>(-1, -1);
            end = new Pair<>(-2, -2);
            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;
        }

        public Collection<Edge<Pair<Integer>>> outgoingEdgesFrom(Pair<Integer> vertex) {
            Set<Edge<Pair<Integer>>> neighbors = new HashSet<>();

            if (vertex.equalsPoint(start)) {
                for (int x = 0; x < numHorizVertices; ++x) {
                    neighbors.add(new Edge<>(start, new Pair<>(x, 0), energyOfPixel(x, 0)));
                }
                return neighbors;
            }

            if (vertex.equalsPoint(end)) {
                return neighbors; // no outgoing edges
            }

            if (vertex.y == numVertVertices - 1) {
                neighbors.add(new Edge<>(vertex, end, 0));
                return neighbors; // only an edge to end
            }

            List<Pair<Integer>> toVerticesOfOutgoingEdges = new ArrayList<Pair<Integer>>();

            if (vertex.x != 0) {
                toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x - 1, vertex.y + 1));
            }
            toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x, vertex.y + 1));
            if (vertex.x != numHorizVertices - 1) {
                toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x + 1, vertex.y + 1));
            }
            for (Pair<Integer> toVertex : toVerticesOfOutgoingEdges) {
                neighbors.add(new Edge<>(vertex, toVertex, energyOfPixel(toVertex.x, toVertex.y)));
            }
            return neighbors;
        }

    }

    public class HorizontalSeamGraph implements Graph<Pair<Integer>, Edge<Pair<Integer>>> {
        double[][] energies;
        Pair<Integer> start;
        Pair<Integer> end;
        int numHorizVertices;
        int numVertVertices;

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        // weight of edge = energy of 'to' vertex
        public HorizontalSeamGraph(double[][] energies) {
            this.energies = energies;
            start = new Pair<>(-1, -1);
            end = new Pair<>(-2, -2);
            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;
        }

        public Collection<Edge<Pair<Integer>>> outgoingEdgesFrom(Pair<Integer> vertex) {
            Set<Edge<Pair<Integer>>> neighbors = new HashSet<>();

            if (vertex.equalsPoint(start)) {
                for (int y = 0; y < numVertVertices; ++y) {
                    neighbors.add(new Edge<>(start, new Pair<>(0, y), energyOfPixel(0, y)));
                }
                return neighbors;
            }

            if (vertex.equalsPoint(end)) {
                return neighbors; // no outgoing edges
            }

            if (vertex.x == numHorizVertices - 1) {
                neighbors.add(new Edge<>(vertex, end, 0));
                return neighbors; // only an edge to end
            }

            List<Pair<Integer>> toVerticesOfOutgoingEdges = new ArrayList<>();

            if (vertex.y > 0) {
                toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x + 1, vertex.y - 1));
            }
            toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x + 1, vertex.y));
            if (vertex.y != numVertVertices - 1) {
                toVerticesOfOutgoingEdges.add(new Pair<>(vertex.x + 1, vertex.y + 1));
            }

            for (Pair<Integer> toVertex : toVerticesOfOutgoingEdges) {
                neighbors.add(new Edge<>(vertex, toVertex, energyOfPixel(toVertex.x, toVertex.y)));
            }

            return neighbors;
        }

    }

    public List<Integer> findVerticalSeam(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();

        VerticalSeamGraphOptimized verticalSeamGraph = UpdateVerticalSeamGraph(oldVerticalSeamGraph,energies,lastSeam);

        DijkstraShortestPathFinder<Graph<VerticalSeamGraphVertex, Edge<VerticalSeamGraphVertex>>, VerticalSeamGraphVertex, Edge<VerticalSeamGraphVertex>> pf;
        pf = new DijkstraShortestPathFinder<>();
        ShortestPath<VerticalSeamGraphVertex, Edge<VerticalSeamGraphVertex>> sp;
        sp = pf.findShortestPath(verticalSeamGraph, verticalSeamGraph.start, verticalSeamGraph.end);
        for (VerticalSeamGraphVertex vertex : sp.vertices()) {
            if (!vertex.equals(verticalSeamGraph.start) && !vertex.equals(verticalSeamGraph.end)) {
                ret.add(vertex.coord.x);
                if (vertex.coord.y == 0) {
                    topRowVertex = vertex;
                }
            }
        }

        oldVerticalSeamGraph = verticalSeamGraph;
        lastSeam = ret;

        return ret;
    }
    
    

    public List<Integer> findVerticalSeamOld(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();
        VerticalSeamGraph verticalSeamGraph = new VerticalSeamGraph(energies);
        DijkstraShortestPathFinder<Graph<Pair<Integer>, Edge<Pair<Integer>>>, Pair<Integer>, Edge<Pair<Integer>>> pf;
        pf = new DijkstraShortestPathFinder<>();
        ShortestPath<Pair<Integer>, Edge<Pair<Integer>>> sp;
        sp = pf.findShortestPath(verticalSeamGraph, verticalSeamGraph.start, verticalSeamGraph.end);
        for (Pair<Integer> vertex : sp.vertices()) {
            if (!vertex.equalsPoint(verticalSeamGraph.start) && !vertex.equalsPoint(verticalSeamGraph.end)) {
                ret.add(vertex.x);
            }
        }
        return ret;
    }
    
    

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();
        HorizontalSeamGraph horizontalSeamGraph = new HorizontalSeamGraph(energies);
        DijkstraShortestPathFinder<Graph<Pair<Integer>, Edge<Pair<Integer>>>, Pair<Integer>, Edge<Pair<Integer>>> pf;
        pf = new DijkstraShortestPathFinder<>();
        ShortestPath<Pair<Integer>, Edge<Pair<Integer>>> sp;
        sp = pf.findShortestPath(horizontalSeamGraph, horizontalSeamGraph.start, horizontalSeamGraph.end);
        for (Pair<Integer> vertex : sp.vertices()) {
            if (!vertex.equalsPoint(horizontalSeamGraph.start) && !vertex.equalsPoint(horizontalSeamGraph.end)) {
                ret.add(vertex.y);
            }
        }
        return ret;
    }
}
