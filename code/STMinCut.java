// ***************************************** //
//  Algorithme d'Edmonds Karp pour les graphes 
//      non orientes.
//  SP 2003
// ***************************************** //
package mascoptLib.algos.graph;

// MascoptLib
import mascoptLib.graphs.*;
import mascoptLib.algos.digraph.MinCut;
import mascoptLib.abstractGraph.MascoptFixedSet;

// Java
import java.util.*;


/**
 * Provides an algorithm to compute a s-t cut min in a graph.
 * 
 * We consider the digraph associated to the graph given and we
 * apply the algorithm of Edmonds Karps (which is implemented in
 * the class MinCut).
 * The name of the capacities is "capacity" but can be change (CAPACITY="name_given")
 * It is necessary to define the value of the variable INFINITY (which equals 999999) 
 * if some capacity is greater than the default value.
 *
 * Entries : A graph g and the two vertices to consider (s,t).
 * Results : vertexSetCutMin() returns the set of vertices which give the minimal cut
 *           edgeSetCutMin() returns the set of edges which belong to the minimal cut
 *           minCutValue() returns the value of the minimal cut
 */
public class STMinCut {
    private Graph g_;
    private Vertex s_;
    private Vertex t_;
    private EdgeSet es;
    private VertexSet ns;
    private VertexSet nodecutmin;
    private EdgeSet edgecutmin = new EdgeSet(ns);

    /** 
     * The string used to search the capacity on edges.
     */
    public String CAPACITY = "capacity";

    /** 
     * A constant for the "infinity" number.
     */
    public double INFINITY = Double.MAX_VALUE;

    /** 
     * Build the algorithm on a graph.
     * s and t are the two vertices to consider for the cut.
     * 
     * @param g the graph
     * @param s the first vertex
     * @param t the second vertex
     */
    public STMinCut(Graph g, Vertex s, Vertex t) {
        g_ = g;
        s_ = s;
        t_ = t;
        es = g_.getEdgeSet();
        ns = g_.getVertexSet();
    }

    // transformation du graphe g non oriente en un grahe gd oriente
    // (chaque arete est transformee en 2 arcs de sens inverse et de meme
    // capacite 
    private DiGraph orientation() {
        VertexSet Vd = g_.getVertexSet();
        ArcSet Ad = new ArcSet(Vd);
        DiGraph Gd = new DiGraph(Vd, Ad);
        double cap;

        Iterator ites = es.iterator();

        while (ites.hasNext()) {
            Edge currentEdge = (Edge) ites.next();
            Vertex[] ext = currentEdge.getVertices();
            Arc forward = new Arc(ext[0], ext[1]);
            cap = currentEdge.getDouValue(CAPACITY);
            forward.setDouValue(CAPACITY, cap);

            Arc backward = new Arc(ext[1], ext[0]);
            backward.setDouValue(CAPACITY, cap);
            Ad.add(forward);
            Ad.add(backward);
        }

        return Gd;
    }

    /** 
     * Returns the set for the min cut.
     * 
     * @return a vertex set
     */
    public VertexSet vertexSetCutMin() {
        DiGraph diG = this.orientation();
        MinCut coupeGd = new MinCut(diG, s_, t_);
        coupeGd.CAPACITY = CAPACITY;
        coupeGd.INFINITY = INFINITY;
        coupeGd.run();
        nodecutmin = coupeGd.vertexSetCutMin();

        return nodecutmin;
    }

    /** 
     * Returns the edges of the min cut
     * 
     * @return an edge set.
     */
    public EdgeSet edgeSetCutMin() {
        Vertex ncurrent;
        Vertex neighbour;
        Edge ecurrent;
        VertexSet nodec;

        if (nodecutmin.isEmpty()) {
            nodec = this.vertexSetCutMin();
        }

        Iterator nodesIt = nodecutmin.iterator();

        while (nodesIt.hasNext()) {
            ncurrent = (Vertex) nodesIt.next();

            // on recupere les aretes incidentes au sommet courant
            MascoptFixedSet esetcurrent = ncurrent.getIncidentEdges(g_);

            // on regarde si l'autre extremite de l'arete est dans S,sinon, 
            // l'arete appartient a la coupe min
            Iterator edgesIt = esetcurrent.iterator();

            while (edgesIt.hasNext()) {
                ecurrent = (Edge) edgesIt.next();
                neighbour = (Vertex) ecurrent.getConnected(ncurrent);

                if (!nodecutmin.contains(neighbour)) {
                    edgecutmin.add(ecurrent);
                }
            }
        }

        return edgecutmin;
    }

    /** 
     * Computes the value of the min cut.
     * 
     * @return a double
     */
    public double minCutValue() {
        EdgeSet esmin;
        double value;

        value = 0.0;

        if (edgecutmin.isEmpty()) {
            esmin = this.edgeSetCutMin();
        } 
	else {
            esmin = edgecutmin;
        }

        Iterator itesmin = esmin.iterator();

        while (itesmin.hasNext()) {
            Edge ecur = (Edge) itesmin.next();
            value = value + ecur.getDouValue(CAPACITY);
        }

        return value;
    }
}
