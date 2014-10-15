/* file name  : src/mascopt/abstractGraph/AbstractVertex.java
 * authors    : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created    : mer 06 aoû 2003 11:06:01 MEST
 * copyright  : I3S/INRIA/UNSA/CNRS
 *
 * modifications:
 *
 */
package in.ac.iitb.cfilt.context.mascoptLib.abstractGraph;

import java.util.HashMap;
import java.util.Iterator;

/**
 * An AbstractVertex object is the most basic element one can build. An
 * AbstractVertex provides information about its neighbors, its degree, the edges or
 * arcs exiting or entering it. It derives from  MascoptObject.
 *
 * @author Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 */
public abstract class AbstractVertex extends MascoptObject {
    /**
     * The Id generator.
     * The idGenerator is incremented every times a vertex is created.
     */
    private static int idGenerator = 0;
    private static int nbInstanceVertex;
    private double x;
    private double y;
    HashMap inAbstractEdges;
    HashMap outAbstractEdges;
    HashMap inOutAbstractEdges;

    /**
     * Constructs a new <code>AbstractVertex</code> object.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public AbstractVertex(double x, double y) {
        super();
        id = "V" + idGenerator++;
        setX(x);
        setY(y);
        setValue("id", id);
        nbInstanceVertex++;
    }

    /**
      * Constructs a new <code>AbstractVertex</code> with default values
      * X=0.0
      * Y=0.0
      */
    public AbstractVertex() {
        this(0.0, 0.0);
    }

    /**
      * Object string output
      */
    public String toString() {
        return getName();
    }

    /**
      * Get X coordinate value of vertex
      */
    public double getX() {
        //return Double.parseDouble(getValue("x"));
        return x;
    }

    /**
      * Get Y coordinate value of vertex
      */
    public double getY() {
        return y;

        //return Double.parseDouble(getValue("y"));
    }

    /**
      * Set X coordinate value of vertex
      */
    public void setX(double x) {
        this.x = x;
        setValue("x", "" + x);
    }

    /**
      * Set Y coordinate value of vertex
      */
    public void setY(double y) {
        this.y = y;
        setValue("y", "" + y);
    }

    // ++++++++++++++ AbstractEdgeSet operations ++++++++++++++++
    // ----------- degree
    // 6k : Pourquoi considerer le in ou out degree en dehors du cas oriente?

    /**
     * Returns the degree of the vertex in an AbstractEdgeSet.
     * this method is equivalent to <code>inDeg(es)+outDeg(es)</code>
     * @param es the AbstractEdgeSet where to compute the degree
     * @return the degree of the vertex.
     */
    public int getDegree(AbstractEdgeSet es) {
        return getIncidentEdges(es).size();
    }

    /**
     * Returns the indegree of the vertex in a AbstractEdgeSet, ie the number of incoming
     * abstractEdges.
     * @param es the AbstractEdgeSet where to compute the degree
     * @return the indegree of the vertex.
     */
    public int getInDegree(AbstractEdgeSet es) {
        return getIncoming(es).size();
    }

    /**
     * Returns the out degree of the vertex in a AbstractEdgeSet, ie the number of outgoing abstractEdges.
     * @param es the AbstractEdgeSet where to compute the degree
     * @return the out degree of the vertex.
     */
    public int getOutDegree(AbstractEdgeSet es) {
        return getOutgoing(es).size();
    }

    // ---------- in/out abstractEdges

    /**
     * Returns a set of incoming abstractEdges for this vertex.
     * @param es the abstractEdgeset to consider for this operation.
     * @return a edgeSet of all incoming abstractEdges.
     */
    public MascoptFixedSet getIncoming(AbstractEdgeSet es) {
        return es.getSetIn(this);
    }

    /**
     * Returns a set of outgoing abstractEdges for this vertex.
     * @param es the abstractEdgeset to consider for this operation.
     * @return a edgeSet of all outgoing abstractEdges.
     */
    public MascoptFixedSet getOutgoing(AbstractEdgeSet es) {
        return es.getSetOut(this);
    }

    /**
     * Returns a set of incoming and outgoing abstractEdges for this vertex.
     * @param es the abstractEdgeset to consider for this operation.
     * @return a edgeSet of all incoming and outgoing abstractEdges.
     */
    public MascoptFixedSet getIncidentEdges(AbstractEdgeSet es) {
        return es.getSetInOut(this);
    }

    // ---------- Neighbours

    /**
    * Returns the set of vertices which are neighbours of this vertex.
    * @param es the AbstractEdgeSet to consider for this operation
    * @return a vertexSet of all the neighbours of this vertex.
    */
    public MascoptFixedSet getNeighbours(AbstractEdgeSet es) {

        MascoptFixedSet returnNs = new MascoptFixedSet();
        returnNs.setName("Fixed set created by vertex" + id + " for neighbours");

        MascoptFixedSet outAbstractEdges = getOutgoing(es);
        Iterator it = outAbstractEdges.iterator();

        while (it.hasNext()) { // for each outgoing edge

            AbstractEdge abstractEdge = (AbstractEdge) it.next(); // on ajoute le noeud connecte.
            returnNs.addProtected(abstractEdge.getConnected(this));
        }

        return returnNs;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++
    // ++++++++++++++ AbstractGraph operations ++++++++++++++++
    // just calls the AbstractEdgeSets operations

    /**
     * Returns the degree of the vertex in a AbstractGraph.
     * this method is equivalent to <code>degree(graph.getAbstractEdgeSet());</code>
     * @param graph the AbstractGraph where to compute the degree
     * @return the degree of the vertex.
     */
    public int getDegree(AbstractGraph graph) {
        return getDegree(graph.getAbstractEdgeSet());
    }

    /**
     * Returns the in degree of the vertex in a AbstractGraph, ie the number of incoming abstractEdges.
     * @param graph the AbstractGraph where to compute the degree
     * @return the in degree of the vertex.
     */
    public int getInDegree(AbstractGraph graph) {
        return getInDegree(graph.getAbstractEdgeSet());
    }

    /**
     * Returns the out degree of the vertex in a AbstractGraph, ie the number of outgoing abstractEdges.
     * @param graph the AbstractGraph where to compute the degree
     * @return the out degree of the vertex.
     */
    public int getOutDegree(AbstractGraph graph) {
        return getOutDegree(graph.getAbstractEdgeSet());
    }

    // ---------- in/out abstractEdges

    /**
     * Returns a set of incoming abstractEdges for this vertex.
     * @param graph the AbstractGraph to consider for this operation.
     * @return a edgeSet of all incoming abstractEdges.
     */
    public MascoptFixedSet getIncoming(AbstractGraph graph) {
        return getIncoming(graph.getAbstractEdgeSet());
    }

    /**
     * Returns a set of outgoing abstractEdges for this vertex.
     * @param graph the AbstractGraph to consider for this operation.
     * @return a edgeSet of all outgoing abstractEdges.
     */
    public MascoptFixedSet getOutgoing(AbstractGraph graph) {
        return getOutgoing(graph.getAbstractEdgeSet());
    }

    /**
     * Returns a set of incoming and outgoing abstractEdges for this vertex.
     * @param graph the AbstractGraph to consider for this operation.
     * @return a edgeSet of all incoming and outgoing abstractEdges.
     */
    public MascoptFixedSet getIncidentEdges(AbstractGraph graph) {
        return getIncidentEdges(graph.getAbstractEdgeSet());
    }

    // ---------- Neighbours

    /**
     * Returns a set of vertices which are neighbours of this vertex.
     * @param es the AbstractGraph to consider for this operation
     * @return a vertexSet of all the neighbors of this vertex.
     */
    public MascoptFixedSet getNeighbours(AbstractGraph graph) {
        return getNeighbours(graph.getAbstractEdgeSet());
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * Returns the vertex on the opposite side of this vertex on abstractEdge
     * @param abstractEdge the abstractEdge
     * @return the opposite vertex, <code>null</code> if there is no opposite vertex in abstractEdge.
     */
    public AbstractVertex getConnected(AbstractEdge abstractEdge) {
        return abstractEdge.getConnected(this);
    }

    /**
     * Construct an EdgeSet containing the edges leading to a vertex.
     * The set returned is not editable. This set is a subset of aes and
     * contains all edges connected to abstractVertex.
     * @param aed the edge set to consider
     * @param abstractVertex the vertex we want the edges connected to
     * @return an edgeset not editable
     */
    public MascoptFixedSet getEdgesTo(AbstractEdgeSet aes,
        AbstractVertex abstractVertex) {
        MascoptFixedSet outs = getOutgoing(aes);
        MascoptFixedSet ret = new MascoptFixedSet(outs);
        ret.setName("Edge Set stored by vertex " + this.getId() +
            " for edgesTo");

        Iterator it = outs.iterator();

        while (it.hasNext()) {
            AbstractEdge edge = (AbstractEdge) it.next();

            if (edge.leadsTo(abstractVertex)) {
                ret.addProtected(edge);
            }
        }

        // DESYNC
        ret.free();

        return ret;
    }

    /**
     * Construct an EdgeSet containing the edges leading to a vertex.
     * The set returned is not editable. This set is a subset of the edge set
     * of the graph and
     * contains all edges connected to abstractVertex.
     * @param ag the graph set to consider
     * @param abstractVertex the vertex we want the edges connected to
     * @return an edgeset not editable
     */
    public MascoptFixedSet getEdgesTo(AbstractGraph ag,
        AbstractVertex abstractVertex) {
        return getEdgesTo(ag.getAbstractEdgeSet(), abstractVertex);
    }

    protected void finalize() {
        nbInstanceVertex--;
    }

    /**
     * Counts all abstract vertices.
     * @return the number of abstract vertices, ie the order of the abstractGraph.
     */
    public static int order() {
        return nbInstanceVertex;
    }

}
