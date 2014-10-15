/* file name  : /0/mascotte/jflaland/mascopt/mascoptLib/src/mascopt/graphs/Node.java
 * authors    : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created    : mer 06 aoû 2003 11:08:42 MEST
 * copyright  : I3S/INRIA/UNSA/CNRS
 *
 * modifications:
 *
 */
package mascoptLib.graphs;

import mascoptLib.abstractGraph.AbstractVertex;

/** 
 * A Node object is the most basic elements which can be built. A
 * Node provides information about its neighbors, its degree, the edges or 
 * arcs exiting or entering it. It derives from  MascoptObject.
 *
 * @author Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 */
public class Vertex extends AbstractVertex {

    /**
      * Default Node Constructor.
      */
    public Vertex() { 
        super();
    }
    
    /**
     * Constructs a new <code>Node</code> object.
     * @param x the x position
     * @param y the y position
     */
    public Vertex(double x, double y) { 
        super(x, y);
    }
    
    // *************************************************************************************
    // For I/O
    // *************************************************************************************

    /** 
     * Returns the XML hierarchy tag names.
     * 
     * @return a string TAG1/TAG2/TAG3/TAG4...
     */
    public String getDOMTagHierarchy()
    {
      return "VERTICES";
    }

    /** 
     * Returns the XML tag name for this object.
     * 
     * @return a string TAG
     */
    public String getDOMTagName()
    {
      return "VERTEX";
    }
}













