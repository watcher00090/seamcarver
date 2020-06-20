import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.MinPQ;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 *
 * @see ShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    implements ShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
        You'll also need to change the part of the class declaration that says
        `ArrayHeapMinPQ<T extends Comparable<T>>` to `ArrayHeapMinPQ<T>`.
         */
        //return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    public ShortestPath<V, E> shortestPathFromEdgesList(V end, Map<V, E> predecessorEdge) {
        List<E> edges = new LinkedList<>();
        V curr = end;
        while (predecessorEdge.containsKey(curr)) { // there is a previous vertex in the path
            E edge = predecessorEdge.get(curr);
            edges.add(0, edge);
            curr = edge.from();
        }
        return new ShortestPath.Success<>(edges);
    }

    @Override
    public ShortestPath<V, E> findShortestPath(G weightedGraph, V start, V end) {
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        Map<V, E> predecessorEdge = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>(); // maps a vertex to its distance from 'start'
        ExtrinsicMinPQ<V> orderedPerimeter = createMinPQ();

        orderedPerimeter.add(start, 0);
        distTo.put(start, 0.0);

        while (!orderedPerimeter.isEmpty()) {
            V from = orderedPerimeter.removeMin();

            if (from.equals(end)) {
                return shortestPathFromEdgesList(from, predecessorEdge);
            }

            for (E e : weightedGraph.outgoingEdgesFrom(from)) {
                V to = e.to();

                double oldDist = distTo.containsKey(to) ? distTo.get(to) : Double.POSITIVE_INFINITY;
                double newDist = distTo.get(from) + e.weight();

                if (newDist < oldDist) {
                    predecessorEdge.put(to, e);
                    distTo.put(to, newDist);

                    // change value of 'to' in distances table
                    if (orderedPerimeter.contains(to)) {
                        orderedPerimeter.changePriority(to, newDist);
                    } else {
                        orderedPerimeter.add(to, newDist);
                    }

                }
            }
        }
        // 'end' is not reachable from 'start', return failure
        return new ShortestPath.Failure<>();
    }


}
