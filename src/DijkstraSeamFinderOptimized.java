import edu.princeton.cs.algs4.Picture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.concurrent.ThreadLocalRandom;

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


enum DeletionCase {case1, case2, case3};


class SeamGraphVertex {
    Pair<Integer> coord;
    boolean isSource;
    boolean isSink;

    public SeamGraphVertex() {
        this.coord = null;
        this.isSource = false;
        this.isSink = false;
    }

    // if we're the source, child1 points to a linked list of vertices
    public SeamGraphVertex(Pair<Integer> coord) {
        this.coord = coord;
        this.isSource = false;
        this.isSink = false;
    }

    public boolean equals(VerticalSeamGraphVertex v) {
        return (this.coord.x == v.coord.x && this.coord.y == v.coord.y);
    }

    public void makeEdgeList() {
        return;
    }

}

class VerticalSeamGraphVertex extends SeamGraphVertex {
    public ArrayList<Edge<VerticalSeamGraphVertex>> edgeList;

    // if we're the source, child1 points to a linked list of vertices
    public VerticalSeamGraphVertex(Pair<Integer> coord) {
        this.coord = coord;
        this.edgeList = new ArrayList<>();
    }

    public VerticalSeamGraphVertex() {
        super();
        this.edgeList = new ArrayList<>();
    }

    public boolean equals(VerticalSeamGraphVertex v) {
        return (this.coord.x == v.coord.x && this.coord.y == v.coord.y);
    }

    public static boolean checkEquality(VerticalSeamGraphVertex v1, VerticalSeamGraphVertex v2) {
        if (v1 == null) {
            return (v2 == null);
        } else if (v2 == null) {
            return v1 == null;
        } else { // neither is null
            return v1.equals(v2);
        }
    }

}

// if the vertex resides at the bottom of the image, only it's bottomEdge is non-null
class VerticalSeamGraphVertexNonEndpoint extends VerticalSeamGraphVertex {
    public VerticalSeamGraphVertex topLeft = null;
    public VerticalSeamGraphVertex top = null;
    public VerticalSeamGraphVertex topRight = null;
    public VerticalSeamGraphVertex left = null;
    public VerticalSeamGraphVertex right = null;
    public VerticalSeamGraphVertex bottomLeft = null;
    public VerticalSeamGraphVertex bottom = null;
    public VerticalSeamGraphVertex bottomRight = null;

    // if an edge is non null, both it's 'from' and 'to' vertices will be non-null
    public Edge<VerticalSeamGraphVertex> leftEdge;
    public Edge<VerticalSeamGraphVertex> bottomEdge;
    public Edge<VerticalSeamGraphVertex> rightEdge;

    int rgb;

    public VerticalSeamGraphVertexNonEndpoint(Pair<Integer> coord, int rgb) {
        super(coord);
        isSource = false;
        isSink = false;
        this.rgb = rgb;
    }

    // todo: pass in rgb from the picture when creating this vertex

    public int getRGB() {
        return rgb;
    }

}

class VerticalSeamGraphVertexSource extends VerticalSeamGraphVertex {
    public VerticalSeamGraphVertex leftChild; // linked list of children (starting with the leftmost)

    public VerticalSeamGraphVertexSource() {
        super();
        isSource = true;
        isSink = false;
    }

    public VerticalSeamGraphVertexSource(Pair<Integer> coord) {
        super(coord);
        isSource = true;
        isSink = false;
    }

}

class VerticalSeamGraphVertexSink extends VerticalSeamGraphVertex {

    public VerticalSeamGraphVertexSink() {
        super();
        isSource = false;
        isSink = true;
    }

    public VerticalSeamGraphVertexSink(Pair<Integer> coord) {
        super(coord);
        isSource = false;
        isSink = true;
    }

}

class DualGradientEnergyFunctionNodal implements NodalEnergyFunction {

    RedColorFunction r_func = new RedColorFunction();
    BlueColorFunction b_func = new BlueColorFunction();
    GreenColorFunction g_func = new GreenColorFunction();

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
        if (v.isSource || v.isSink) {
            System.out.println("Internal error, getXDifferenceType was called on a source or sink");
            System.exit(1);
            return DifferenceType.CentralDifference;
        } else if (v.left == null) { // x == 0
            return DifferenceType.ForwardDifference;
        } else if (v.right == null) { // x == width - 1
            return DifferenceType.BackwardDifference;
        } else {
            return DifferenceType.CentralDifference;
        }
    }

    private static DifferenceType getYDifferenceType(VerticalSeamGraphVertexNonEndpoint v) {
        if (v.isSource || v.isSink) {
            System.out.println("Internal error, getXDifferenceType was called on a source or sink");
            System.exit(1);
            return DifferenceType.CentralDifference;
        } else if (v.coord.y == 0) { // y == 0
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

public class DijkstraSeamFinderOptimized {
    private final ShortestPathFinder<Graph<SeamGraphVertex, Edge<SeamGraphVertex>>, SeamGraphVertex, Edge<SeamGraphVertex>> pathFinder;
    public List<Integer> lastSeam = null;
    public VerticalSeamGraphVertex topRowVertex = null;
    public VerticalSeamGraphOptimized verticalSeamGraph;

    public DijkstraSeamFinderOptimized(Picture picture, double[][] energies) {
        this.pathFinder = createPathFinder();
        this.verticalSeamGraph = new VerticalSeamGraphOptimized(picture, energies);
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
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

        // weight of edge = energy of 'from' vertex
        public VerticalSeamGraphOptimized(Picture picture, double[][] energies) {
            this.energyFunction = new DualGradientEnergyFunctionNodal();
            this.energies = energies;

            assert (energies.length >= 3 && energies[0].length >= 3); // for the sake of the nodal energy function


            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;

            // create source and sink
            start = new VerticalSeamGraphVertexSource(new Pair<Integer>(-1, -1));
            end = new VerticalSeamGraphVertexSink(new Pair<>(-2, -2));

            VerticalSeamGraphVertexNonEndpoint prevRow[] = new VerticalSeamGraphVertexNonEndpoint[numHorizVertices];

            for (int y = numVertVertices - 1; y > -1; y--) {

                VerticalSeamGraphVertexNonEndpoint currRow[] = new VerticalSeamGraphVertexNonEndpoint[numHorizVertices];

                for (int x = 0; x < numHorizVertices; ++x) {

                    if (y == numVertVertices - 1) {

                        currRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y), picture.getRGB(x, y));
                        currRow[x].bottomEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], end, energyOfPixel(x, y));
                        currRow[x].bottom = end;
                        //prevRow[x].edgeList.add(prevRow[x].bottomEdge);

                    } else {

                        currRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y), picture.getRGB(x, y));

                        if (x > 0) {
                            currRow[x].leftEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x - 1], energyOfPixel(x, y));
                            currRow[x].bottomLeft = prevRow[x - 1];
                            prevRow[x - 1].topRight = currRow[x];
                        }

                        currRow[x].bottomEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x], energyOfPixel(x, y));

                        if (x < numHorizVertices - 1) {
                            currRow[x].rightEdge = new Edge<VerticalSeamGraphVertex>(currRow[x], prevRow[x + 1], energyOfPixel(x, y));
                            currRow[x].bottomRight = prevRow[x + 1];
                            prevRow[x + 1].topLeft = currRow[x];
                        }

                        currRow[x].edgeList.add(currRow[x].leftEdge);
                        currRow[x].edgeList.add(currRow[x].bottomEdge);
                        currRow[x].edgeList.add(currRow[x].rightEdge);

                        currRow[x].bottom = prevRow[x];
                        prevRow[x].top = currRow[x];

                    }

                    if (x > 0) {
                        currRow[x].left = currRow[x - 1];
                        currRow[x - 1].right = currRow[x];
                    }

                }

                prevRow = currRow;

            }

            for (int x = 0; x < prevRow.length; ++x) {
                start.edgeList.add(new Edge(start, prevRow[x], 0));
                prevRow[x].top = start;
            }

            start.leftChild = prevRow[0];
        }

        public Picture toPicture() {
            Picture p = new Picture(numHorizVertices,numVertVertices);

            VerticalSeamGraphVertex curs = (VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexSource) verticalSeamGraph.start).edgeList.get(0).to);

            for (int y=0; y<verticalSeamGraph.numVertVertices; ++y) {
                VerticalSeamGraphVertex start = curs;

                for (int x=0; x<verticalSeamGraph.numHorizVertices; ++x) {

                    // check invariants on curs
                    VerticalSeamGraphVertexNonEndpoint v = (VerticalSeamGraphVertexNonEndpoint) curs;
                    p.setRGB(x,y,v.getRGB());

                    curs = ((VerticalSeamGraphVertexNonEndpoint) curs).right;

                }
                curs = ((VerticalSeamGraphVertexNonEndpoint) start).bottom;
            }

            return p;
        }

        // update the weights of the edges of v
        public double computeEnergy(VerticalSeamGraphVertexNonEndpoint v) {
            double val = energyFunction.apply(v);
            if (v.leftEdge != null) v.leftEdge.weight = energyFunction.apply(v);
            if (v.rightEdge != null) v.rightEdge.weight = energyFunction.apply(v);
            if (v.bottomEdge != null) v.bottomEdge.weight = energyFunction.apply(v);
            return val;
        }

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        public Collection<Edge<VerticalSeamGraphVertex>> outgoingEdgesFrom(VerticalSeamGraphVertex v) {
            return v.edgeList;
        }

        // will always be passed in an arrayList
        public void removeSeam(List<Integer> lastSeam) {

            if (verticalSeamGraph == null) {
                System.out.println("Internal error, the VerticalSeamGraph has not been initialized!\"");
                System.exit(1);
            }

            VerticalSeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((VerticalSeamGraphVertexNonEndpoint) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            (((VerticalSeamGraphVertexNonEndpoint) v).top).edgeList.remove(lastSeam.get(0)); // remove the edge from the source

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                VerticalSeamGraphVertex leftVertex = ((VerticalSeamGraphVertexNonEndpoint) v).left;
                VerticalSeamGraphVertex rightVertex = ((VerticalSeamGraphVertexNonEndpoint) v).right;

                if (((VerticalSeamGraphVertexNonEndpoint) v).topLeft != null) {
                    VerticalSeamGraphVertex topLeft = ((VerticalSeamGraphVertexNonEndpoint) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        ((VerticalSeamGraphVertexNonEndpoint) topLeft).rightEdge.from = ((VerticalSeamGraphVertexNonEndpoint) v).right;
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).topRight != null) {
                    VerticalSeamGraphVertex topRight = ((VerticalSeamGraphVertexNonEndpoint) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        ((VerticalSeamGraphVertexNonEndpoint) topRight).leftEdge.from = ((VerticalSeamGraphVertexNonEndpoint) v).left;
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).top != null) {
                    VerticalSeamGraphVertex top = ((VerticalSeamGraphVertexNonEndpoint) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottomEdge.from = ((VerticalSeamGraphVertexNonEndpoint) v).right;
                            ((VerticalSeamGraphVertexNonEndpoint) top).rightEdge.from = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) v).right).right;

                        } else { // deletionCase  = case1
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottomEdge.from = ((VerticalSeamGraphVertexNonEndpoint) v).left;
                            ((VerticalSeamGraphVertexNonEndpoint) top).leftEdge.from = ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) v).left).left;

                        }
                    }
                }
            }
            removeSeamHelper(lastSeam);
            removeSeamHelper2(lastSeam);
            recomputeEnergiesOfVerticalSeamGraph(lastSeam);
        }

        public void removeSeamHelper(List<Integer> lastSeam) {

            VerticalSeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((VerticalSeamGraphVertexNonEndpoint) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                VerticalSeamGraphVertex leftVertex = ((VerticalSeamGraphVertexNonEndpoint) v).left;
                VerticalSeamGraphVertex rightVertex = ((VerticalSeamGraphVertexNonEndpoint) v).right;

                ((VerticalSeamGraphVertexNonEndpoint) leftVertex).right = ((VerticalSeamGraphVertexNonEndpoint) v).right;
                ((VerticalSeamGraphVertexNonEndpoint) rightVertex).left = ((VerticalSeamGraphVertexNonEndpoint) v).left;

                if (((VerticalSeamGraphVertexNonEndpoint) v).topLeft != null) {
                    VerticalSeamGraphVertex topLeft = ((VerticalSeamGraphVertexNonEndpoint) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        ((VerticalSeamGraphVertexNonEndpoint) topLeft).bottomRight = ((VerticalSeamGraphVertexNonEndpoint) topLeft).rightEdge.from;
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).topRight != null) {
                    VerticalSeamGraphVertex topRight = ((VerticalSeamGraphVertexNonEndpoint) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        ((VerticalSeamGraphVertexNonEndpoint) topRight).bottomLeft = ((VerticalSeamGraphVertexNonEndpoint) topRight).leftEdge.from;
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).top != null) {
                    VerticalSeamGraphVertex top = ((VerticalSeamGraphVertexNonEndpoint) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottom = ((VerticalSeamGraphVertexNonEndpoint) top).bottomEdge.from;
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottomRight = ((VerticalSeamGraphVertexNonEndpoint) top).rightEdge.from;
                        } else { // deletionCase  = case1
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottom = ((VerticalSeamGraphVertexNonEndpoint) top).bottomEdge.from;
                            ((VerticalSeamGraphVertexNonEndpoint) top).bottomLeft = ((VerticalSeamGraphVertexNonEndpoint) top).leftEdge.from;
                        }
                    }
                }

            }
        }

        public void removeSeamHelper2(List<Integer> lastSeam) {

            VerticalSeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((VerticalSeamGraphVertexNonEndpoint) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                VerticalSeamGraphVertex leftVertex = ((VerticalSeamGraphVertexNonEndpoint) v).left;
                VerticalSeamGraphVertex rightVertex = ((VerticalSeamGraphVertexNonEndpoint) v).right;

                if (((VerticalSeamGraphVertexNonEndpoint) v).topLeft != null) {
                    VerticalSeamGraphVertex topLeft = ((VerticalSeamGraphVertexNonEndpoint) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        if (((VerticalSeamGraphVertexNonEndpoint) topLeft).bottomRight != null) {
                            ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) topLeft).bottomRight).topLeft =  topLeft;
                        }
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).topRight != null) {
                    VerticalSeamGraphVertex topRight = ((VerticalSeamGraphVertexNonEndpoint) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        if (((VerticalSeamGraphVertexNonEndpoint) topRight).bottomLeft != null) {
                            ((VerticalSeamGraphVertexNonEndpoint) ((VerticalSeamGraphVertexNonEndpoint) topRight).bottomLeft).topRight = topRight;
                        }
                    }
                }

                if (((VerticalSeamGraphVertexNonEndpoint) v).top != null) {
                    VerticalSeamGraphVertex top = ((VerticalSeamGraphVertexNonEndpoint) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {

                            if (((VerticalSeamGraphVertexNonEndpoint) top).bottom != null) {
                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) top).bottom)).top = top;
                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) top).bottomRight != null) {
                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) top).bottomRight)).topLeft = top;
                            }

                        } else { // deletionCase  = case1

                            if (((VerticalSeamGraphVertexNonEndpoint) top).bottom != null) {
                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) top).bottom)).top = top;
                            }

                            if (((VerticalSeamGraphVertexNonEndpoint) top).bottomLeft != null) {
                                ((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) top).bottomLeft)).topRight = top;
                            }

                        }
                    }
                }

            }
        }

        public void recomputeEnergiesOfVerticalSeamGraph(List<Integer> lastSeam) {
            VerticalSeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the image)
            DeletionCase deletionCase;

            if (!(((VerticalSeamGraphVertexNonEndpoint) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            (((VerticalSeamGraphVertexNonEndpoint) v).top).edgeList.remove(lastSeam.get(0)); // remove the edge from the source
            computeEnergy((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) v).left));
            computeEnergy((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) v).right));

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i - 1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((VerticalSeamGraphVertexNonEndpoint) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                computeEnergy((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) v).left));
                computeEnergy((VerticalSeamGraphVertexNonEndpoint) (((VerticalSeamGraphVertexNonEndpoint) v).right));

            }
        }




    }




    public List<Integer> findVerticalSeam(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();

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

        verticalSeamGraph.removeSeam(ret);

        return ret;
    }

    // for testing purposes
    public List<Integer> generateRandomHorizontalSeam() {

        assert(verticalSeamGraph.numHorizVertices > 1 && verticalSeamGraph.numVertVertices > 1);

        List<Integer> seam = new ArrayList<>();

        double lowThreshold = 1/3;
        double highThreshold = 2/3;
        double middleThreshold = 1/2;

        boolean onTop = false;
        boolean onBottom = false;

        int min = 0;
        int max = verticalSeamGraph.numVertVertices-1;

        int val = ThreadLocalRandom.current().nextInt(min, max + 1);
        int prevVal = val;

        for (int i=0; i<verticalSeamGraph.numHorizVertices; ++i) {

            double x = Math.random();

            if (onTop) {
                if (x <= middleThreshold) {
                    val = prevVal;
                } else {
                    val = prevVal + 1;
                    onTop = false;
                }
            } else if (onBottom){
                if (x <= middleThreshold) {
                    val = prevVal - 1;
                    onBottom = false;
                } else {
                    val = prevVal;
                }
            } else {
                if (x < lowThreshold) {
                    val = prevVal - 1;
                } else if (lowThreshold <= x && x < highThreshold) {
                    val = prevVal;
                } else { // highThreshold <= x
                    val = prevVal + 1;
                }
            }

            if (val == verticalSeamGraph.numVertVertices - 1) {
                onBottom = true;
            }

            if (val == 0) {
                onTop = true;
            }

            seam.add(val);
        }

        return seam;
    }

    // for testing purposes
    public List<Integer> generateRandomVerticalSeam() {

        assert(verticalSeamGraph.numHorizVertices > 1 && verticalSeamGraph.numVertVertices > 1);

        List<Integer> seam = new ArrayList<>();

        double lowThreshold = 1/3;
        double highThreshold = 2/3;
        double middleThreshold = 1/2;

        boolean onLeftSide = false;
        boolean onRightSide = false;

        int min = 0;
        int max = verticalSeamGraph.numHorizVertices-1;

        int val = ThreadLocalRandom.current().nextInt(min, max + 1);
        int prevVal = val;

        for (int i=0; i<verticalSeamGraph.numVertVertices; ++i) {

            double x = Math.random();

            if (onLeftSide) {
                if (x <= middleThreshold) {
                    val = prevVal;
                } else {
                    val = prevVal + 1;
                    onLeftSide = false;
                }
            } else if (onRightSide){
                if (x <= middleThreshold) {
                    val = prevVal - 1;
                    onRightSide = false;
                } else {
                    val = prevVal;
                }
            } else {
                if (x < lowThreshold) {
                    val = prevVal - 1;
                } else if (lowThreshold <= x && x < highThreshold) {
                    val = prevVal;
                } else { // highThreshold <= x
                    val = prevVal + 1;
                }
            }

            if (val == verticalSeamGraph.numHorizVertices - 1) {
                onRightSide = true;
            }

            if (val == 0) {
                onLeftSide = true;
            }

            seam.add(val);
        }

        return seam;
    }



}