/* file name  : /0/mascotte/jflaland/mascopt/mascoptLib/src/mascopt/abstractGraph/AbstractEdge.java
 * authors    : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created    : mer 06 aoû 2003 11:11:28 MEST
 * copyright  : I3S/INRIA/UNSA/CNRS
 *
 * modifications:
 *
 */
package mascoptLib.abstractGraph;

import mascoptLib.util.Trace;

import org.w3c.dom.Element;

/**
 * An AbstractEdge object is built using two AbstractVertex objects. Given one vertex, the
 * AbstractEdge object provides facilities to walk trough this edge when covering an
 * AbstractGraph. AbstractEdge is derivated from MascoptObject.
 *
 * @author Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 */
public abstract class AbstractEdge extends MascoptObject {
    /**
      * The Id generator.
      * The idGenerator is incremented every times a vertex is created.
      */
    private static int idGenerator = 0;
    private static int nbInstanceEdge;

    /**
      * The vertices of the edge.
      * the first vertex is in AbstractVertex[0] and the second is in AbstractVertex[1]
      */
    protected AbstractVertex[] vertices;

    /**
      * Constructs a new AbstractEdge object between n1 and n2.
      * This object is observer of the two vertices.
      * Arcs and Edges must call super(AbstractVertex, AbstractVertex)
      * @param n1 one of the vertices.
      * @param n2 the other vertex.
      */
    public AbstractEdge(AbstractVertex n1, AbstractVertex n2) {
        super();
        id = "AE" + idGenerator++;
        vertices = new AbstractVertex[2];
        vertices[0] = n1;
        vertices[1] = n2;

        setValue("id", id);
        Trace.newObj(id);

        nbInstanceEdge++;
    }

    /**
      * Returns the vertices linked by this edge. No order is guaranteed by this
      * function. It may return the two vertices in random order in the table.
      * @return a vector containing all concerned vertices.
      */
    public AbstractVertex[] getAbstractVertices() {
        return vertices;
    }

    /**
     * Returns the opposite vertex of an edge
     * There is no check about the edge orientation.
     * @param vertex the vertex to use the opposite one.
     * @return the opposite vertex considering this edge.
     */
    public AbstractVertex getOppositeAbstractVertex(AbstractVertex vertex) {
        if (vertices[0] == vertex) {
            return vertices[1];
        } else {
            return vertices[0];
        }
    }

    /**
     * Specifies if the current edge leads to vertex n.
     * @param n the AbstractVertex to test
     * @return true if this AbstractEdge leads to this vertex, false otherwise
     */
    public abstract boolean leadsTo(AbstractVertex n);

    /**
      * Specifies if the current edge comes from vertex n.
      * @param n the AbstractVertex to test
      * @return true if this AbstractEdge comes from this vertex, false otherwise
      */
    public abstract boolean leaves(AbstractVertex n);

    /**
      * Returns the vertex connected by current edge from vertex n.
      * @param n the AbstractVertex to test
      * @return the vertex connected to n via the current edge
      */
    public abstract AbstractVertex getConnected(AbstractVertex n);

    /**
      * Gives the edge status concerning the loop state.
      * @return true if the edge have the same start and end vertex
      */
    public boolean isLoop() {
        return (vertices[0] == vertices[1]);
    }

    protected void finalize() {
        nbInstanceEdge--;
    }

    /**
     * Counts all abstract vertices.
     * @return the number of abstract vertices, ie the size of the abstractGraph.
     */
    public static int size() {
        return nbInstanceEdge;
    }
    
    // *************************************************************************************
    // For I/O
    // *************************************************************************************

    /** 
     * The method writes the current object in the DOMTree.
     * 
     * @param element the current node of DOM document.
     */
    public Element toDOMTree(Element element)
    {
      // Getting the super node for this object
      Element node_to_go = super.toDOMTree(element);

      // Pointing to the two vertices that are used
      vertices[0].toDOMTreeAsRef(node_to_go);
      vertices[1].toDOMTreeAsRef(node_to_go);

      return node_to_go;
    }

}
