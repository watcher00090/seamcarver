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

    public class VerticalSeamGraphVertexSource extends VerticalSeamGraphVertex {
        public VerticalSeamGraphVertex leftChild; // linked list of children (starting with the leftmost)

        public VerticalSeamGraphVertexSource(Pair<Integer> coord) {
            super(coord);
            isSource = true;
            isSink = false;
        }

    }

    public class VerticalSeamGraphVertexSink extends VerticalSeamGraphVertex {
        public VerticalSeamGraphVertex leftParent; // linked list of parents (starting with the leftmost)

        public VerticalSeamGraphVertexSink(Pair<Integer> coord) {
            super(coord);
            isSource = false;
            isSink = true;
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
    public class VerticalSeamGraphOptimized implements Graph<VerticalSeamGraphVertex, Edge<VerticalSeamGraphVertex>> {
        double[][] energies;
        VerticalSeamGraphVertexSource start;
        VerticalSeamGraphVertexSink end;
        int numHorizVertices;
        int numVertVertices;

        RedColorFunction red_color_func;
        GreenColorFunction green_color_func;
        BlueColorFunction blue_color_func;

        class RedColorFunction implements Function<VerticalSeamGraphVertexNonEndpoint, Integer> {

            public RedColorFunction() {}

            public Integer apply(VerticalSeamGraphVertexNonEndpoint v) {
                int rgb = v.getRGB();
                int r = (rgb >> 16) & 0xFF;
                return r;
            }
        }

        class G_func implements BiFunction<Integer, Integer, Integer> {
            Picture picture;

            public G_func(Picture picture) {
                this.picture = picture;
            }

            public Integer apply(Integer x, Integer y) {
                int rgb = picture.getRGB(x, y);
                int g = (rgb >> 8) & 0xFF;
                return g;
            }
        }

        public class B_func implements BiFunction<Integer, Integer, Integer> {
            Picture picture;

            public B_func(Picture picture) {
                this.picture = picture;
            }

            public Integer apply(Integer x, Integer y) {
                int rgb = picture.getRGB(x, y);
                int b = (rgb >> 0) & 0xFF;
                return b;
            }
        }

        class DualGradientEnergyFunction implements EnergyFunction {

            @Override
            public double apply(Picture picture, int x, int y) {
                System.out.println("Calling DualGradientEnergyFunction::apply....");

                DifferenceType xDifferenceType = getXDifferenceType(x, y, picture.width(), picture.height());
                DifferenceType yDifferenceType = getYDifferenceType(x, y, picture.width(), picture.height());
                int R_x = computeGrad(x, y, r_func, xDifferenceType, Dir.x);
                int G_x = computeGrad(x, y, g_func, xDifferenceType, Dir.x);
                int B_x = computeGrad(x, y, b_func, xDifferenceType, Dir.x);

                int R_y = computeGrad(x, y, r_func, yDifferenceType, Dir.y);
                int G_y = computeGrad(x, y, g_func, yDifferenceType, Dir.y);
                int B_y = computeGrad(x, y, b_func, yDifferenceType, Dir.y);

                int del_2_x = R_x * R_x + G_x * G_x + B_x * B_x;
                int del_2_y = R_y * R_y + G_y * G_y + B_y * B_y;

                return Math.sqrt(del_2_x + del_2_y);
            }

            private static int computeGrad(int x, int y, BiFunction<Integer, Integer, Integer> func, DifferenceType differenceType, Dir d) {
                if (differenceType == DifferenceType.ForwardDifference && d == Dir.x) {
                    return forwardDiff_x(x, y, func);
                } else if (differenceType == DifferenceType.ForwardDifference && d == Dir.y) {
                    return forwardDiff_y(x, y, func);
                } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.x) {
                    return backwardDiff_x(x, y, func);
                } else if (differenceType == DifferenceType.BackwardDifference && d == Dir.y) {
                    return backwardDiff_y(x, y, func);
                } else if (differenceType == DifferenceType.CentralDifference && d == Dir.x) {
                    return centralDiff_x(x, y, func);
                } else if (differenceType == DifferenceType.CentralDifference && d == Dir.y) {
                    return centralDiff_y(x, y, func);
                } else {
                    System.out.println("System error in computeGrad");
                    return -1;
                }
            }

            private static DifferenceType getXDifferenceType(int x, int y, int width, int height) {
                if (x == 0) {
                    return DifferenceType.ForwardDifference;
                } else if (x == width - 1) {
                    return DifferenceType.BackwardDifference;
                } else {
                    return DifferenceType.CentralDifference;
                }
            }

            private static DifferenceType getYDifferenceType(int x, int y, int width, int height) {
                if (y == 0) {
                    return DifferenceType.ForwardDifference;
                } else if (y == height - 1) {
                    return DifferenceType.BackwardDifference;
                } else {
                    return DifferenceType.CentralDifference;
                }
            }

            private static int forwardDiff_x(int x, int y, BiFunction<Integer, Integer, Integer> func) {
                return -3 * func.apply(x, y) + 4 * func.apply(x + 1, y) - func.apply(x + 2, y);
            }

            private static int forwardDiff_y(int x, int y, Function<VerticalSeamGraphVertex, Integer> func, VerticalSeamGraphVertexNonEndpoint node) {
                // (x,y) val = func.apply(node)
                // (x,y+1) val = func.apply(node.bottom)
                // (x,y+2) val = func.apply(node.bottom.bottom)

                return -3 * func.apply(node) + 4 * func.apply(node.bottom) - func.apply(((VerticalSeamGraphVertexNonEndpoint) node.bottom ).bottom);
            }

            private static int backwardDiff_x(Function<VerticalSeamGraphVertex, Integer> func, VerticalSeamGraphVertexNonEndpoint node) {
                return -3 * func.apply(node) + 4 * func.apply(node.left) - func.apply(((VerticalSeamGraphVertexNonEndpoint)  node.left).left);
            }

            private static int backwardDiff_y(Function<VerticalSeamGraphVertex, Integer> func, VerticalSeamGraphVertexNonEndpoint node) {
                return -3 * func.apply(node)) + 4 * func.apply(node.top) - func.apply(node.top.top);
            }

            private static int centralDiff_x(int x, int y, BiFunction<Integer, Integer, Integer> func) {
                return func.apply(x + 1, y) - func.apply(x - 1, y);
            }

            private static int centralDiff_y(int x, int y, BiFunction<Integer, Integer, Integer> func) {
                return func.apply(x, y + 1) - func.apply(x, y - 1);
            }

        }

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        // weight of edge = energy of 'from' vertex
        public VerticalSeamGraphOptimized(double[][] energies) {
            this.energies = energies;

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

                        prevRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y));
                        prevRow[x].bottomEdge = new Edge<VerticalSeamGraphVertex>(prevRow[x], end, energyOfPixel(x, y));
                        prevRow[x].edgeList.add(prevRow[x].bottomEdge);

                    } else {

                        currRow[x] = new VerticalSeamGraphVertexNonEndpoint(new Pair<Integer>(x, y));
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

        public Collection<Edge<VerticalSeamGraphVertex>> outgoingEdgesFrom(VerticalSeamGraphVertex v) {
            return v.edgeList;
        }

        public int VerticalSeamGraphNew(double[][] energies, int funky, int funky2) {
            return 0;
/*
            this.energies = energies;
            start = new Pair<>(-1, -1);
            end = new Pair<>(-2, -2);
            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;
            List<> vertex;

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
            */
        }

        public Collection<Edge<Pair<Integer>>> outgoingEdgesFrom_Old(Pair<Integer> vertex) {
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

        public void recomputeEnergy(VerticalSeamGraphVertexNonEndpoint v) {

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

    public VerticalSeamGraphOptimized UpdateVerticalSeamGraph(VerticalSeamGraphOptimized oldVSGraph, double[][] energies, List<Integer> lastSeam) {
        if (oldVSGraph == null) {
            return new VerticalSeamGraphOptimized(energies);
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

                    }

                    recomputeEnergy(prevVertex);

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

                recomputeEnergy(prevVertex);

            } catch(Exception e) { // todo remove exception handling
                e.printStackTrace();
            }
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
