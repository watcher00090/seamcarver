package sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// (x,y) coordinates of a pixel
class Pair<T> {
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

public class DijkstraSeamFinder implements SeamFinder {
    // Perhaps replace all 4 references to "Object" on the line below with whatever vertex type
    //  you choose for your graph
    private final ShortestPathFinder<Graph<Object, Edge<Object>>, Object, Edge<Object>> pathFinder;
    public List<Integer> lastSeam = null;
    boolean weightBasedOnFromVertex = false;
    public VerticalSeamGraph verticalSeamGraph = null;
    //VerticalSeamGraphVertex topRowVertex = null;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    public DijkstraSeamFinder(boolean weightBasedOnFromVertex) {
        this.pathFinder = createPathFinder();
        this.weightBasedOnFromVertex = weightBasedOnFromVertex;
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
    public class VerticalSeamGraph implements Graph<Pair<Integer>, Edge<Pair<Integer>>> {
        double[][] energies;
        Pair<Integer> start;
        Pair<Integer> end;
        int numHorizVertices;
        int numVertVertices;
        boolean weightBasedOnFromVertex;

        public double energyOfPixel(int x, int y) {
            return energies[x][y];
        }

        public Edge<Pair<Integer>> createEdge(Pair<Integer> from, Pair<Integer> to) {
            int x;
            int y;
            if (weightBasedOnFromVertex) {
                x = from.x;
                y = from.y;
            } else {
                x = to.x;
                y = to.y;
            }
            if (x < 0 || y < 0) {
                return new Edge<>(from,to,0);
            } else {
                return new Edge<>(from,to,energyOfPixel(x,y));
            }

        }

        // weight of edge = energy of 'to' vertex
        public VerticalSeamGraph(double[][] energies) {
            this.energies = energies;
            start = new Pair<>(-1, -1);
            end = new Pair<>(-2, -2);
            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;
            weightBasedOnFromVertex = false;
        }

        // weight of edge = energy of 'to' vertex
        public VerticalSeamGraph(double[][] energies, boolean weightBasedOnFromVertex) {
            this.energies = energies;
            start = new Pair<>(-1, -1);
            end = new Pair<>(-2, -2);
            assert (energies.length > 0 && energies[0].length > 0);
            numHorizVertices = energies.length;
            numVertVertices = energies[0].length;
            this.weightBasedOnFromVertex = weightBasedOnFromVertex;
        }

        public Collection<Edge<Pair<Integer>>> outgoingEdgesFrom(Pair<Integer> vertex) {
            Set<Edge<Pair<Integer>>> neighbors = new HashSet<>();

            if (vertex.equalsPoint(start)) {
                for (int x = 0; x < numHorizVertices; ++x) {
                    neighbors.add(createEdge(start, new Pair<>(x, 0)));
                }
                return neighbors;
            }

            if (vertex.equalsPoint(end)) {
                return neighbors; // no outgoing edges
            }

            if (vertex.y == numVertVertices - 1) {
                neighbors.add(createEdge(vertex, end));
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
                neighbors.add(createEdge(vertex, toVertex));
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


    public List<Integer> findVerticalSeam(double[][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<>();
        VerticalSeamGraph verticalSeamGraph = new VerticalSeamGraph(energies,this.weightBasedOnFromVertex);
        this.verticalSeamGraph = verticalSeamGraph;
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
