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

enum VerticalSeamGraphEdgeLocator {
    leftEdge, bottomEdge, rightEdge;
}

class RedColorFunction implements Function<SeamGraphVertex, Integer> {
    public Integer apply(SeamGraphVertex x) {
        int rgb = x.getRGB();
        int r = (rgb >> 16) & 0xFF;
        return r;
    }
}

class GreenColorFunction implements Function<SeamGraphVertex, Integer> {
    public Integer apply(SeamGraphVertex x) {
        int rgb = x.getRGB();
        int g = (rgb >> 8) & 0xFF;
        return g;
    }
}

class BlueColorFunction implements Function<SeamGraphVertex, Integer> {
    public Integer apply(SeamGraphVertex x) {
        int rgb = x.getRGB();
        int b = (rgb >> 0) & 0xFF;
        return b;
    }
}


enum DeletionCase {case1, case2, case3};




class SeamGraphVertex {

    public ArrayList<Edge<SeamGraphVertex>> edgeList;

    public SeamGraphVertex topLeft = null;
    public SeamGraphVertex top = null;
    public SeamGraphVertex topRight = null;
    public SeamGraphVertex left = null;
    public SeamGraphVertex right = null;
    public SeamGraphVertex bottomLeft = null;
    public SeamGraphVertex bottom = null;
    public SeamGraphVertex bottomRight = null;

    // if an edge is non null, both it's 'from' and 'to' vertices will be non-null
    public Edge<SeamGraphVertex> leftEdge;
    public Edge<SeamGraphVertex> bottomEdge;
    public Edge<SeamGraphVertex> rightEdge;

    public boolean inSeam = false;

    public boolean isSource = false;
    public boolean isSink = false;

    public int rgb;

    Pair<Integer> coord;

    // if we're the source, child1 points to a linked list of vertices
    public SeamGraphVertex(Pair<Integer> coord, int rgb) {
        this.coord = coord;
        this.edgeList = new ArrayList<>();
        this.rgb = rgb;
        isSource = false;
        isSink = false;
    }

    public SeamGraphVertex(Pair<Integer> coord) {
        this.coord = coord;
    }

    public SeamGraphVertex(int rgb) {
        this.rgb = rgb;
    }


    public SeamGraphVertex() {

    }

    public int getRGB() {
        return this.rgb;
    }

    public boolean equals(SeamGraphVertex v) {
        return (this.coord.x == v.coord.x && this.coord.y == v.coord.y);
    }

    public static boolean checkEquality(SeamGraphVertex v1, SeamGraphVertex v2) {
        if (v1 == null) {
            return (v2 == null);
        } else if (v2 == null) {
            return v1 == null;
        } else { // neither is null
            return v1.equals(v2);
        }
    }

    public void updateVerticalSeamGraphEdgeTo(VerticalSeamGraphEdgeLocator locator, SeamGraphVertex newTo) {
        if (this.isSource || this.isSink) {
            System.out.println("Internal error, updateVerticalSeamGraphEdgeTo called on a source or sink!");
            System.exit(1);
        }

        switch (locator) {
            case bottomEdge:
                if (newTo == null) {
                    ((SeamGraphVertex) this).bottomEdge = null;
                } else {
                    ((SeamGraphVertex) this).bottomEdge.to = newTo;
                }
                break;
            case leftEdge:
                if (newTo == null) {
                    ((SeamGraphVertex) this).leftEdge = null;
                } else {
                    ((SeamGraphVertex) this).leftEdge.to = newTo;
                }
                break;
            case rightEdge:
                if (newTo == null) {
                    ((SeamGraphVertex) this).rightEdge = null;
                } else {
                    ((SeamGraphVertex) this).rightEdge.to = newTo;
                }
                break;
            default:
                break;
        }

    }
}

class VerticalSeamGraphVertexSource extends SeamGraphVertex {
    public SeamGraphVertex leftChild; // linked list of children (starting with the leftmost)

    public VerticalSeamGraphVertexSource() {
        super(null,-100);
        isSource = true;
        isSink = false;
    }

    public VerticalSeamGraphVertexSource(Pair<Integer> coord) {
        super(coord,-100);
        isSource = true;
        isSink = false;
    }

}

class VerticalSeamGraphVertexSink extends SeamGraphVertex {

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

    public double apply(SeamGraphVertex v) {
        //System.out.println("Calling DualGradientEnergyFunction::apply....");

        if (v == null) {
            System.out.println("v is null here");
            System.exit(1);
        }

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

    private static int computeGrad(SeamGraphVertex v, Function<SeamGraphVertex, Integer> func, DifferenceType differenceType, Dir d) {
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

    private static DifferenceType getXDifferenceType(SeamGraphVertex v) {
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

    private static DifferenceType getYDifferenceType(SeamGraphVertex v) {
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

    private static int forwardDiff_x(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        //return -3 * func.apply(x, y) + 4 * func.apply(x + 1, y) - func.apply(x + 2, y);
        return -3 * func.apply(node) + 4 * func.apply((SeamGraphVertex) node.right) - func.apply((SeamGraphVertex) ((SeamGraphVertex) (node.right)).right);
    }

    private static int forwardDiff_y(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((SeamGraphVertex) node.bottom) - func.apply((SeamGraphVertex) (((SeamGraphVertex) node.bottom ).bottom));
    }

    private static int backwardDiff_x(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((SeamGraphVertex)node.left) - func.apply((SeamGraphVertex) (((SeamGraphVertex) node.left).left));
    }

    private static int backwardDiff_y(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        return -3 * func.apply(node) + 4 * func.apply((SeamGraphVertex) node.top) - func.apply((SeamGraphVertex) ((SeamGraphVertex)node.top).top);
    }

    private static int centralDiff_x(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        //return func.apply(x + 1, y) - func.apply(x - 1, y);
        return func.apply((SeamGraphVertex) node.right) - func.apply((SeamGraphVertex) node.left);
    }

    private static int centralDiff_y(SeamGraphVertex node, Function<SeamGraphVertex, Integer> func) {
        //return func.apply(x, y + 1) - func.apply(x, y - 1);
        return func.apply((SeamGraphVertex) node.bottom) - func.apply((SeamGraphVertex) node.top);
    }

}

public class DijkstraSeamFinderOptimized {
    public boolean DEBUG_MODE = false;

    private final ShortestPathFinder<Graph<SeamGraphVertex, Edge<SeamGraphVertex>>, SeamGraphVertex, Edge<SeamGraphVertex>> pathFinder;
    public List<Integer> lastSeam = null;
    public SeamGraphVertex topRowVertex = null;
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
    public class VerticalSeamGraphOptimized implements Graph<SeamGraphVertex, Edge<SeamGraphVertex>> {
        VerticalSeamGraphVertexSource start;
        VerticalSeamGraphVertexSink end;
        int numHorizVertices;
        int numVertVertices;
        DualGradientEnergyFunctionNodal energyFunction;
        double[][] energies;
        public List<Integer> debugSeam;

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

            SeamGraphVertex prevRow[] = new SeamGraphVertex[numHorizVertices];

            for (int y = numVertVertices - 1; y > -1; y--) {

                SeamGraphVertex currRow[] = new SeamGraphVertex[numHorizVertices];

                for (int x = 0; x < numHorizVertices; ++x) {

                    if (y == numVertVertices - 1) {

                        currRow[x] = new SeamGraphVertex(new Pair<Integer>(x, y), picture.getRGB(x, y));
                        currRow[x].bottomEdge = new Edge<SeamGraphVertex>(currRow[x], end, energyOfPixel(x, y));
                        currRow[x].bottom = end;
                        //prevRow[x].edgeList.add(prevRow[x].bottomEdge);

                    } else {

                        currRow[x] = new SeamGraphVertex(new Pair<Integer>(x, y), picture.getRGB(x, y));

                        if (x > 0) {
                            currRow[x].leftEdge = new Edge<SeamGraphVertex>(currRow[x], prevRow[x - 1], energyOfPixel(x, y));
                            currRow[x].bottomLeft = prevRow[x - 1];
                            prevRow[x - 1].topRight = currRow[x];
                        }

                        currRow[x].bottomEdge = new Edge<SeamGraphVertex>(currRow[x], prevRow[x], energyOfPixel(x, y));

                        if (x < numHorizVertices - 1) {
                            currRow[x].rightEdge = new Edge<SeamGraphVertex>(currRow[x], prevRow[x + 1], energyOfPixel(x, y));
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

            SeamGraphVertex curs = (SeamGraphVertex) (((VerticalSeamGraphVertexSource) verticalSeamGraph.start).edgeList.get(0).to);

            for (int y=0; y<verticalSeamGraph.numVertVertices; ++y) {
                SeamGraphVertex start = curs;

                for (int x=0; x<verticalSeamGraph.numHorizVertices; ++x) {

                    // check invariants on curs
                    SeamGraphVertex v = (SeamGraphVertex) curs;
                    p.setRGB(x,y,v.getRGB());

                    curs = ((SeamGraphVertex) curs).right;

                }
                curs = ((SeamGraphVertex) start).bottom;
            }

            return p;
        }

        // update the weights of the edges of v
        public double computeEnergy(SeamGraphVertex v) {

            /*
            double val = energyFunction.apply(v);
            if (v.leftEdge != null) v.leftEdge.weight = energyFunction.apply(v);
            if (v.rightEdge != null) v.rightEdge.weight = energyFunction.apply(v);
            if (v.bottomEdge != null) v.bottomEdge.weight = energyFunction.apply(v);
            return val;
             */
            return 1.0;
        }

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        public Collection<Edge<SeamGraphVertex>> outgoingEdgesFrom(SeamGraphVertex v) {
            return v.edgeList;
        }

        public void removeSeamNew(List<Integer> lastSeam) {
            SeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;
            ((SeamGraphVertex) v).inSeam = true;

            for (int i = 1; i < lastSeam.size(); ++i) {

                //SeamGraphVertex v1 =

                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((SeamGraphVertex) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((SeamGraphVertex) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((SeamGraphVertex) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                ((SeamGraphVertex) v).inSeam = true;


            }

            // remove edge from seam
        }

        // will always be passed in an arrayList
        public void removeSeam(List<Integer> lastSeam) {

            if (verticalSeamGraph == null) {
                System.out.println("Internal error, the VerticalSeamGraph has not been initialized!\"");
                System.exit(1);
            }

            SeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((SeamGraphVertex) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            ((SeamGraphVertex) v).inSeam = true;

            for (int i = 1; i < lastSeam.size(); ++i) {

                correctXCoordinatesOfRightVertices(v);

                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((SeamGraphVertex) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((SeamGraphVertex) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((SeamGraphVertex) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                ((SeamGraphVertex) v).inSeam = true;

                if (((SeamGraphVertex) v).topLeft != null) {
                    SeamGraphVertex topLeft = ((SeamGraphVertex) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        ((SeamGraphVertex) topLeft).rightEdge.to = ((SeamGraphVertex) v).right;
                    }
                }

                if (((SeamGraphVertex) v).topRight != null) {
                    SeamGraphVertex topRight = ((SeamGraphVertex) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        ((SeamGraphVertex) topRight).leftEdge.to = ((SeamGraphVertex) v).left;
                    }
                }

                if (((SeamGraphVertex) v).top != null) {
                    SeamGraphVertex top = ((SeamGraphVertex) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {
                            ((SeamGraphVertex) top).bottomEdge.to = ((SeamGraphVertex) v).right;
                            ((SeamGraphVertex) top).rightEdge.to = ((SeamGraphVertex) ((SeamGraphVertex) v).right).right;
                        } else { // deletionCase  = case1
                            ((SeamGraphVertex) top).bottomEdge.to = ((SeamGraphVertex) v).left;
                            ((SeamGraphVertex) top).leftEdge.to = ((SeamGraphVertex) ((SeamGraphVertex) v).left).left;
                        }
                    }
                }

            }

            correctXCoordinatesOfRightVertices(v);
            removeSeamHelper(lastSeam);
            removeSeamHelper2(lastSeam);
            recomputeEnergiesOfVerticalSeamGraph(lastSeam);
            start.edgeList.remove((lastSeam.get(0).intValue())); // remove the edge from the source
            this.numHorizVertices--;
        }

        // v is in the seam
        private void correctXCoordinatesOfRightVertices(SeamGraphVertex v) {
            if (DEBUG_MODE) {
                SeamGraphVertex curs = (SeamGraphVertex) (((SeamGraphVertex) v).right);
                while (curs != null) {
                    curs.coord.x--;
                    curs = (SeamGraphVertex) (curs.right);
                }
            }
        }

        public void removeSeamHelper(List<Integer> lastSeam) {

            SeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((SeamGraphVertex) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                SeamGraphVertex leftVertex = ((SeamGraphVertex) v).left;
                SeamGraphVertex rightVertex = ((SeamGraphVertex) v).right;

                if (leftVertex != null) ((SeamGraphVertex) leftVertex).right = ((SeamGraphVertex) v).right;
                if (rightVertex != null) ((SeamGraphVertex) rightVertex).left = ((SeamGraphVertex) v).left;

                if (i1 < i2) {
                    v = ((SeamGraphVertex) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((SeamGraphVertex) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((SeamGraphVertex) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                if (((SeamGraphVertex) v).topLeft != null) {
                    SeamGraphVertex topLeft = ((SeamGraphVertex) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        ((SeamGraphVertex) topLeft).bottomRight = ((SeamGraphVertex) topLeft).rightEdge.to;
                    }
                }

                if (((SeamGraphVertex) v).topRight != null) {
                    SeamGraphVertex topRight = ((SeamGraphVertex) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        ((SeamGraphVertex) topRight).bottomLeft = ((SeamGraphVertex) topRight).leftEdge.to;
                    }
                }

                if (((SeamGraphVertex) v).top != null) {
                    SeamGraphVertex top = ((SeamGraphVertex) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {
                            ((SeamGraphVertex) top).bottom = ((SeamGraphVertex) top).bottomEdge.to;
                            ((SeamGraphVertex) top).bottomRight = ((SeamGraphVertex) top).rightEdge.to;
                        } else { // deletionCase  = case1
                            ((SeamGraphVertex) top).bottom = ((SeamGraphVertex) top).bottomEdge.to;
                            ((SeamGraphVertex) top).bottomLeft = ((SeamGraphVertex) top).leftEdge.to;
                        }
                    }
                }

            }

            SeamGraphVertex leftVertex = ((SeamGraphVertex) v).left;
            SeamGraphVertex rightVertex = ((SeamGraphVertex) v).right;

            if (leftVertex != null) ((SeamGraphVertex) leftVertex).right = ((SeamGraphVertex) v).right;
            if (rightVertex != null) ((SeamGraphVertex) rightVertex).left = ((SeamGraphVertex) v).left;

        }

        public void removeSeamHelper2(List<Integer> lastSeam) {

            SeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the iamge)
            DeletionCase deletionCase;

            if (!(((SeamGraphVertex) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i-1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((SeamGraphVertex) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((SeamGraphVertex) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((SeamGraphVertex) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                if (((SeamGraphVertex) v).topLeft != null) {
                    SeamGraphVertex topLeft = ((SeamGraphVertex) v).topLeft;
                    if (topLeft != null && deletionCase != DeletionCase.case1) {
                        if (((SeamGraphVertex) topLeft).bottomRight != null) {
                            ((SeamGraphVertex) ((SeamGraphVertex) topLeft).bottomRight).topLeft =  topLeft;
                        }
                    }
                }

                if (((SeamGraphVertex) v).topRight != null) {
                    SeamGraphVertex topRight = ((SeamGraphVertex) v).topRight;
                    if (topRight != null && deletionCase != DeletionCase.case3) {
                        if (((SeamGraphVertex) topRight).bottomLeft != null) {
                            ((SeamGraphVertex) ((SeamGraphVertex) topRight).bottomLeft).topRight = topRight;
                        }
                    }
                }

                if (((SeamGraphVertex) v).top != null) {
                    SeamGraphVertex top = ((SeamGraphVertex) v).top;
                    if (top != null && deletionCase != DeletionCase.case2) {
                        if (deletionCase == DeletionCase.case3) {

                            if (((SeamGraphVertex) top).bottom != null) {
                                ((SeamGraphVertex) ((SeamGraphVertex) top).bottom).top = top;

                                // edge case handling
                                if (((SeamGraphVertex) top).left == null) {
                                    ((SeamGraphVertex) ((SeamGraphVertex) top).bottom).topLeft = null;
                                }

                            }

                            if (((SeamGraphVertex) top).bottomRight != null) {
                                ((SeamGraphVertex) (((SeamGraphVertex) top).bottomRight)).topLeft = top;
                            }

                        } else { // deletionCase  = case1

                            if (((SeamGraphVertex) top).bottom != null) {
                                ((SeamGraphVertex) (((SeamGraphVertex) top).bottom)).top = top;

                                // edge case handling
                                if (((SeamGraphVertex) top).right == null) {
                                    ((SeamGraphVertex) ((SeamGraphVertex) top).bottom).topRight = null;
                                }

                            }

                            if (((SeamGraphVertex) top).bottomLeft != null) {
                                ((SeamGraphVertex) (((SeamGraphVertex) top).bottomLeft)).topRight = top;
                            }

                        }
                    } else { // deletionCase = case2

                        // hacks
                        if (((SeamGraphVertex) v).right != null) {
                            if ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) v).right).topLeft != null) {
                                if ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) v).right).topLeft).left == null) {
                                    ((SeamGraphVertex) (((SeamGraphVertex) v).right)).topLeft = null;
                                }
                            }
                        }
                        if (((SeamGraphVertex) v).left != null) {
                            if ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) v).left).topRight != null) {
                                if ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) ((SeamGraphVertex) v).left).topRight).right == null) {
                                    ((SeamGraphVertex) (((SeamGraphVertex) v).left)).topRight = null;
                                }
                            }
                        }

                    }

                }

            }
        }

        public void recomputeEnergiesOfVerticalSeamGraph(List<Integer> lastSeam) {
            SeamGraphVertex v = verticalSeamGraph.start.edgeList.get(lastSeam.get(0)).to; // (currVertex and prevVertex as used to traverse the image)
            DeletionCase deletionCase;

            if (!(((SeamGraphVertex) v).top).isSource) {
                System.out.println("Internal system error, expected v.top to be the source for the seam's first vertex");
                System.exit(1);
            }

            (((SeamGraphVertex) v).top).edgeList.remove(lastSeam.get(0)); // remove the edge from the source
            computeEnergy((SeamGraphVertex) (((SeamGraphVertex) v).left));
            computeEnergy((SeamGraphVertex) (((SeamGraphVertex) v).right));

            for (int i = 1; i < lastSeam.size(); ++i) {
                int i1 = lastSeam.get(i - 1);
                int i2 = lastSeam.get(i);

                if (i1 < i2) {
                    v = ((SeamGraphVertex) v).bottomRight;
                    deletionCase = DeletionCase.case1;
                } else if (i1 > i2) {
                    v = ((SeamGraphVertex) v).bottomLeft;
                    deletionCase = DeletionCase.case3;
                } else {
                    v = ((SeamGraphVertex) v).bottom;
                    deletionCase = DeletionCase.case2;
                }

                computeEnergy((SeamGraphVertex) (((SeamGraphVertex) v).left));
                computeEnergy((SeamGraphVertex) (((SeamGraphVertex) v).right));

            }
        }




    }




    public List<Integer> findVerticalSeam(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();

        DijkstraShortestPathFinder<Graph<SeamGraphVertex, Edge<SeamGraphVertex>>, SeamGraphVertex, Edge<SeamGraphVertex>> pf;
        pf = new DijkstraShortestPathFinder<>();
        ShortestPath<SeamGraphVertex, Edge<SeamGraphVertex>> sp;
        sp = pf.findShortestPath(verticalSeamGraph, verticalSeamGraph.start, verticalSeamGraph.end);
        for (SeamGraphVertex vertex : sp.vertices()) {
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

        double lowThreshold = ((double) 1.0)/3;
        double highThreshold = ((double) 2.0)/3;
        double middleThreshold = ((double) 1.0)/2;

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
            } else if (onBottom) {
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
            prevVal = val;
        }

        return seam;
    }

    // for testing purposes
    public List<Integer> generateRandomVerticalSeam() {

        assert(verticalSeamGraph.numHorizVertices > 1 && verticalSeamGraph.numVertVertices > 1);

        List<Integer> seam = new ArrayList<>();

        double lowThreshold = ((double) 1.0)/3;
        double highThreshold = ((double) 2.0)/3;
        double middleThreshold = ((double) 1.0)/2;

        boolean onLeftSide = false;
        boolean onRightSide = false;

        int min = 0;
        int max = verticalSeamGraph.numHorizVertices-1;

        int val = ThreadLocalRandom.current().nextInt(min, max + 1);
        int prevVal = val;

        seam.add(val);

        for (int i=0; i<verticalSeamGraph.numVertVertices-1; ++i) {

            if (val == verticalSeamGraph.numHorizVertices - 1) {
                onRightSide = true;
            }

            if (val == 0) {
                onLeftSide = true;
            }

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

            seam.add(val);
            prevVal = val;
        }

        return seam;
    }



}