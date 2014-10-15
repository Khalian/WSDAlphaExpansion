/* file name  : /0/mascotte/jflaland/mascopt/mascoptLib/src/mascopt/abstractGraph/AbstractVertexSet.java
 * authors    : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created    : mer 06 aoû 2003 11:18:35 MEST
 * copyright  : I3S/INRIA/UNSA/CNRS
 *
 * modifications:
 *
 */
package mascoptLib.abstractGraph;

import mascoptLib.util.*;

import java.util.Iterator;
import java.util.Observable;


/**
 * The AbstractVertexSet class is derivated from MascoptSet. It allow to group
 * AbstractVertex objects in a set.
 * @author Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 */
public abstract class AbstractVertexSet extends MascoptSet {
    /**
     * The Id generator.
     * The idGenerator is incremented every times a vertex is created.
     */
    private static int idGenerator = 0;

    /**
      * The number of instances
      */
    private static long nbInstanceNS = 0;
    NotifyReason lastReason = null;

    /**
      * Default Constructor.
      *
      */
    public AbstractVertexSet() {
        super();
        nbInstanceNS++;
        id = "NS" + idGenerator++;
        Trace.newObj(id);
    }

    /**
      * Constructor creating a sub set of the VertexSet.
      * The sub set created contains no elements of the <code>superSet</code>. The set
      * can easily be filled with the method <code>add</code> or
      * <code>addAll</code>.
      *
      * @param superSet the set of vertex on which is based the vertex set
      * created.
      */
    public AbstractVertexSet(AbstractVertexSet superSet) {
        super(superSet);
        nbInstanceNS++;
        id = "NS" + idGenerator++;
        Trace.newObj(id);
    }

    /**
     * Constructor for copy sets.
     * Creates a new <code>AbstractVertexSet</code> similar to <code>originalSet</code>.
     * If <code>copyElements</code> is set to <code>true</code> then all the elements of <code>originalSet</code> are duplicated (and of course their id is changed)
     * and the structure of the set is preserved (there exists an isomorphism between <code>originalSet</code> and the new set).
     * In that case, the simple values are also copied, but those depending on a context are dropped.
     * If <code>copyElements</code> is set to <code>false</code>, then the new set's elements point to those of <code>originalSet</code> but the set is not a subset of <code>originalSet</code>.
     * @param originalSet - the set to be copied.
     * @param copyElements - indicates if the elements of the set have to be duplicated
    **/
    public AbstractVertexSet(AbstractVertexSet originalSet, boolean copyElements) {
        this();
        setName("Copy of set " + originalSet.getName() + " (" +
            originalSet.getId() + ")");

        if ((originalSet != null) && (copyElements)) {
            AbstractVertexSetFactory ansf = originalSet.getFactory();

            // describe all the vertices
            Iterator itOS = originalSet.iterator();

            while (itOS.hasNext()) {
                AbstractVertex vertexToCopy = ((AbstractVertex) itOS.next());

                // make a copy
                AbstractVertex newVertex = ansf.newAbstractVertex(vertexToCopy.getX(),
                        vertexToCopy.getY());
                newVertex.copyValues(vertexToCopy);

                //We add the name ONLY NOW because the name is considered as an entry !! STUPID (from my point of view)-yv
                newVertex.setName("Copy of vertex with id=" +
                    vertexToCopy.getId());

                // and finally, we add the vertices
                this.add(newVertex);
            }
        } else if ((originalSet != null) && !(copyElements)) {
            Iterator itOS = originalSet.iterator();

            while (itOS.hasNext()) {
                this.add(itOS.next());
            }
        }
    }


    /**
      * Give the factory creating objects not abstract.
      * As all the library is abstract, we need a factory that determines the
      * type of graphs the user manipulates. Then the factory is able to
      * create each type of object when necessary.
      */
    public abstract AbstractVertexSetFactory getFactory();

    /**
      * For the library coherence.
      * Behavior of vertex set when other objects changes
      */
    public void update(Observable o, Object arg) {
        if (!(arg instanceof NotifyReason)) {
            return;
        }

        NotifyReason nr = (NotifyReason) arg;
        Object[] objs = nr.getObjects();
        String message = nr.getMessage();

        Trace.print(getId() + "message=" + message + " objs=" + objs + " =>");

        if (message.equalsIgnoreCase("Remove")) {
            // we check that the vertex exists in this set in order to avoid infinite recursive removal
            // between inter-observing sets.
            if ((objs[0] instanceof AbstractVertex) &&
                    (this.contains(objs[0]))) {
                // ??? faire plus de verification ? si on est bien le sous ensemble etc ?
                Trace.println("Je l'enleve je suis un subset");
                remove(objs[0]);
            } else if (message.equalsIgnoreCase("Add")) {
            }
        } else {
            Trace.println("pas implemente");
        }
    }

    /**
      * Add a vertex in the set.
      *
      * @param o the vertex that must be added in the set.
      * @return true if the vertex has been added in the set.
      */
    public boolean add(AbstractVertex o) {
	    //The observers are warned at a higher level
        return super.add(o);
    }

    /**
      * Remove a vertex in the set.
      *
      * @param o the vertex that must be removed in the set.
      * @return true if the vertex has been removed in the set.
      */

    //    public boolean remove(Object o) {
    public boolean remove(AbstractVertex o) {
	    //The observers are warned at a higher level
        return super.remove((AbstractVertex) o);
    }

    protected void finalize() {
        super.finalize();
        nbInstanceNS--;
    }

    /**
     * Counts the number of vertex sets.
     */
    public static long countAllAbstractVertexSets() {
        return nbInstanceNS;
    }

    /**
     * Clone this object.
     */

    //    public Object clone() throws CloneNotSupportedException

    /*    public Object clone()
    {
      nbInstanceNS++;
      return super.clone();
      }*/
}
