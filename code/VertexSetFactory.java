/* file name  : src/mascoptLib/graphs/VertexSetFactory.java
 * authors    : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created    : mer 05 mai 2004 12:08:15 MEST
 * copyright  : I3S/INRIA/UNSA/CNRS
 *
 * modifications:
 *
 */
package in.ac.iitb.cfilt.context.mascoptLib.graphs;

import in.ac.iitb.cfilt.context.mascoptLib.abstractGraph.*;
/**
 * The factory to produce Vertex Sets.
 * 
 *
 * @author  bbongiov@bing.inria.fr
 * @version Wed Feb 20 18:37:37 2002
 */
public class VertexSetFactory implements AbstractVertexSetFactory {

    /**
      * Default node set constructor.
      */
    public AbstractVertexSet newAbstractVertexSet() {
        return new VertexSet();
    }

    /**
      * Constructs a new node set based on a node set.
      * The newly constructed <code>NodeSet</code> is a subset of <code>nodeSet</code>.
      * @param nodeSet the node set used as base to the node set.
      * @return an abstract node set empty.
      */
    public AbstractVertexSet newAbstractVertexSet(AbstractVertexSet nodeSet) {
        return new VertexSet((VertexSet)nodeSet);
    }

    /**
      * Constructs a new node set based on a node set.
      * If <code>copyElements</code> is true, the newly constructed <code>NodeSet</code> is independant of <code>nodeSet</code> but an isomorphism exists between the two sets. Otherwise it is a subset.
      * @param nodeSet the node set used as base to the node set.
      * @param copyElements a boolean indicating the behaviour of this copy constructor.
      * @return an abstract node set empty.
      */
    public AbstractVertexSet newAbstractVertexSet(AbstractVertexSet nodeSet,boolean copyElements) {
        return new VertexSet((VertexSet)nodeSet,copyElements);
    }

    /**
      * Default node constructor.
      */
    public AbstractVertex newAbstractVertex() {
        return new Vertex();
    }

    /**
      * Constructs a node with coordinates.
      * @param x the x position
      * @param y the y position
      */
    public AbstractVertex newAbstractVertex(double x, double y) {
        return new Vertex(x,y);
    }

    
}  // end of class SubSet
