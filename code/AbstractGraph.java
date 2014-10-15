/*
 * file name :
 * /0/mascotte/jflaland/mascopt/mascoptLib/src/mascopt/abstractGraph/AbstractGraph.java
 * authors : Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 * created : mer 06 ao? 2003 11:16:14 MEST copyright : I3S/INRIA/UNSA/CNRS
 * modifications:
 */
package in.ac.iitb.cfilt.context.mascoptLib.abstractGraph;

import in.ac.iitb.cfilt.context.mascoptLib.graphs.Vertex;

import in.ac.iitb.cfilt.context.mascoptLib.util.*;

import java.util.Iterator;
import java.util.Vector;

// DOM stuffs
import org.w3c.dom.Element;

/**
 * The AbstractGraph class constructs a non directed graph using a vertex set
 * and an edge set. It guaranty that, at any time, the AbstractEdgeSet and
 * AbstractVertexSet objects stay coherent. It provides facilities to copy
 * graphs and construct subgraphs. AbstractGraph derives from MascoptObject.
 * 
 * @author Jean-Francois Lalande (Jean-Francois.Lalande@sophia.inria.fr)
 */
public abstract class AbstractGraph extends MascoptObject { //implements
  // Observer {

  /**
   * The Id generator. The idGenerator is incremented every times a vertex is
   * created.
   */
  private static int idGenerator = 0;

  private static int nbInstanceOfGraphs;

  private AbstractVertexSet abstractVertexSet;

  private AbstractEdgeSet abstractEdgeSet;

  private AbstractGraph superGraph;

  /**
   * Default Constructor of the graph.
   */
  public AbstractGraph() {
    super();
    id = "G" + idGenerator++;
    setValue("id", id);

    Trace.newObj(id);

    nbInstanceOfGraphs++;
  }

  /**
   * Constructor using a VertexSet and an EdgeSet.
   * 
   * @param abstractVertexSet
   *          the vertex set to use when constructing the graph.
   * @param abstractEdgeSet
   *          the edge set to use when constructing the graph.
   */
  public AbstractGraph(AbstractVertexSet abstractVertexSet,
      AbstractEdgeSet abstractEdgeSet) {
    this();

    // We want to maintain a coherence between abstractVertexSet and the vertex
    // set of
    // abstractEdgeSet. We keep that coherence by adding to abstractVertexSet
    // vertices
    // that are only present in the vertex set of abstractVertexSet and then
    // place an observer.
    // We need at least one vertexset not null to be able to do something. We
    // consider that
    // it's ok to provide only an edge set, since in that case you provide also
    // a vertex set
    // but providing only a vertex set is not enough. In that case we let
    // everything crash.
    if ((abstractVertexSet != null)
        || ((abstractEdgeSet != null) && (abstractEdgeSet
            .getAbstractVertexSet() != null))) {
      if (abstractVertexSet == null) {
        this.abstractVertexSet = abstractEdgeSet.getAbstractVertexSet();
        this.abstractEdgeSet = abstractEdgeSet;
      } else if ((abstractEdgeSet != null)
          && (abstractEdgeSet.getAbstractVertexSet() != null)) {
        this.abstractVertexSet = abstractVertexSet;
        this.abstractEdgeSet = abstractEdgeSet;

        if (abstractVertexSet != abstractEdgeSet.getAbstractVertexSet()) {
          syncAndAddObservers(abstractVertexSet, abstractEdgeSet
              .getAbstractVertexSet());
        }
      } else {
        this.abstractVertexSet = abstractVertexSet;
        //sometimes, you don't know why but an abstractEdgeSet points to a null
        // abstractVertexSet.
        // This behaviour is not normal, but can be corrected online
        if (abstractEdgeSet != null) {
          this.abstractEdgeSet = abstractEdgeSet;
          this.abstractEdgeSet.setAbstractVertexSet(this.abstractVertexSet);
          addMissingVertices(this.abstractVertexSet, this.abstractEdgeSet);
        }
      }
    }
  }

  /**
   * Constructor of a subgraph. Constructs a graph with a sub vertex set and a
   * sub edge set. The sub graph is empty when constructed.
   * 
   * @param superGraph
   *          the graph used for the construction of the subgraph.
   */
  public AbstractGraph(AbstractGraph superGraph) {
    this();
    setSuperGraph(superGraph);

    //    id = "G" + idGenerator++;
    AbstractGraphFactory agf = getFactory();
    AbstractVertexSet subVertexSet = agf.newAbstractVertexSet(superGraph
        .getAbstractVertexSet());
    subVertexSet.setName("Sub vertex set of "
        + superGraph.getAbstractVertexSet().getName() + " ("
        + superGraph.getAbstractVertexSet().getId() + ")");

    AbstractEdgeSet subEdgeSet = agf.newAbstractEdgeSet(superGraph
        .getAbstractEdgeSet(), subVertexSet);
    subEdgeSet.setName("Sub edge set of "
        + superGraph.getAbstractEdgeSet().getName() + " ("
        + superGraph.getAbstractEdgeSet().getId() + ")");

    this.abstractVertexSet = subVertexSet;
    this.abstractEdgeSet = subEdgeSet;

    if (this.abstractVertexSet != this.abstractEdgeSet.getAbstractVertexSet()) {
      syncAndAddObservers(this.abstractVertexSet, this.abstractEdgeSet
          .getAbstractVertexSet());
    }

    // Name
    this.setName("Subgraph of " + superGraph.getName() + " ("
        + superGraph.getId() + ")");
  }

  /**
   * Copy all vertices, edges, edge and vertex set of a graph, creating a new
   * graph. Note that only the structure of the graph is duplicated. All the
   * values stored with a context equal to <code>this</code> or with
   * <code>setValue</code> are keep while this others are lost.
   * 
   * @param copyElements
   *          indicates if new vertices and edges have to be duplicated. If
   *          false, the current vertices and edges of the graph are used and
   *          the supersets are preserved. On the contrary, if
   *          <code>copyElements</code> is <code>true</code>, new vertices
   *          and edges are created and you loose the values stored in it if the
   *          context is not <code>this</code>.
   */
  public AbstractGraph(AbstractGraph graph, boolean copyElements) {
    this();

    abstractVertexSet = this.getFactory().newAbstractVertexSet();
    abstractEdgeSet = this.getFactory().newAbstractEdgeSet(abstractVertexSet);

    // After creating the vertex set and edge set
    // We can attach the graph to the super graph (the condition between
    // vertex set and super vertex set, and the condition between edge set and
    // super edge set will be checked)
    if (!copyElements) {
      setSuperGraph(graph.getSuperGraph());
    }

    abstractVertexSet.setName("NS of graph " + id);
    abstractEdgeSet.setName("ES of graph " + id);

    java.util.HashMap vertexResolve = new java.util.HashMap();

    Iterator it = graph.getAbstractVertexSet().iterator();

    while (it.hasNext()) {
      AbstractVertex oldVertex = (AbstractVertex) it.next();

      if (copyElements) {
        AbstractVertex newVertex = getFactory().newAbstractVertex();
        newVertex.setName("copy of " + oldVertex.getId()
            + " instancied by Graph " + id);

        vertexResolve.put(oldVertex, newVertex);
        abstractVertexSet.add(newVertex);
        newVertex.copyValues(oldVertex);
      } else {
        abstractVertexSet.add(oldVertex);
      }
    }

    it = graph.getAbstractEdgeSet().iterator();

    while (it.hasNext()) {
      AbstractEdge oldEdge = (AbstractEdge) it.next();

      if (copyElements) {
        AbstractVertex[] vertices = oldEdge.getAbstractVertices();

        AbstractEdge newEdge = getFactory().newAbstractEdge(
            (AbstractVertex) vertexResolve.get(vertices[0]),
            (AbstractVertex) vertexResolve.get(vertices[1]));
        newEdge.setName("copy of " + oldEdge.getId() + " instancied by Graph "
            + id);

        abstractEdgeSet.add(newEdge);
        newEdge.copyValues(oldEdge);
      } else {
        abstractEdgeSet.add(oldEdge);
      }
    }

    this.setName("Copy of graph " + graph.getName() + " (" + graph.getId()
        + ")");
  }

  /**
   * Converts the Graph in string to be printed.
   */
  public String toString() {
    return "AG V=" + abstractVertexSet + " E=" + abstractEdgeSet;
  }

  /**
   * Give the factory creating objects not abstract. As all the library is
   * abstract, we need a factory that determines the type of graphs the user
   * manipulates. Then the factory is able to create each type of object when
   * necessary.
   */
  public abstract AbstractGraphFactory getFactory();

  //     /**
  //       * For the library coherence.
  //       * Behavior of graphs when other objects changes
  //       */
  //     public void update(Observable o,Object arg) {
  //         if (! (arg instanceof NotifyReason) ) return ;
  //         NotifyReason nr = (NotifyReason) arg;
  //         Object[] objs = nr.getObjects();
  //         String message = nr.getMessage();
  //         //Trace.print(toString()+"message="+message+" objs="+objs+" =>");
  //         Trace.println("je fait rien :) je devrai ptet forwarde ?");
  //     }
  // +++++++++++++++++++++++++++++++++++++++++++++++++++

  /**
   * Integrity of the graph. Default behavior: returns true.
   * 
   * @return true if the integrity of the graph is true
   */
  public boolean checkIntegrity() {
    return true;
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++++

  /**
   * Returns the vertex set of the graph.
   * 
   * @return the vertex set of the graph.
   */
  public AbstractVertexSet getAbstractVertexSet() {
    return abstractVertexSet;
  }

  /**
   * Returns the edge set of the graph.
   * 
   * @return the edge set of the graph.
   */
  public AbstractEdgeSet getAbstractEdgeSet() {
    return abstractEdgeSet;
  }

  /**
   * Change the vertex set of the graph. Since the vertex set is changed, it
   * must be noted that the edge set of the graph is emptied and the vertices
   * contained in the vertex set are changed (so to preserve the coherence
   * between vertex sets). This function should be used only if the default
   * constructor of <code>AbstractGraph</code> is used since it may be
   * considered strange to change the vertex set of a graph. Instead a new graph
   * should be created.
   * 
   * @param abstractVertexSet
   *          the vertex set to use in the graph.
   * @return true if successfull
   */
  protected boolean setAbstractVertexSet(AbstractVertexSet ans) {
    boolean retour = true;

    if (ans == null) {
      return false;
    }

    // we need to be able to change the vertexset while keeping
    // the coherence between the vertex set and the vertex set of the edge set
    // Do we have an edge set yet ?
    if ((this.abstractEdgeSet != null)
        && (this.abstractEdgeSet.getAbstractVertexSet() != null)
        && (this.abstractEdgeSet.getAbstractVertexSet() != ans)) {
      //first remove the observers of the old vertex set.
      this.abstractEdgeSet.getAbstractVertexSet().deleteAddObserver(
          this.abstractVertexSet);
      this.abstractEdgeSet.getAbstractVertexSet().deleteRemoveObserver(
          this.abstractVertexSet);
      this.abstractVertexSet.deleteAddObserver(this.abstractEdgeSet
          .getAbstractVertexSet());
      this.abstractVertexSet.deleteRemoveObserver(this.abstractEdgeSet
          .getAbstractVertexSet());

      // we then empty the vertex set of the edge set
      this.abstractEdgeSet.getAbstractVertexSet().clear();

      // and finally synchronize them
      retour = syncAndAddObservers(ans, abstractEdgeSet.getAbstractVertexSet());
    }

    if (retour) {
      //change the vertex set of the graph
      this.abstractVertexSet = ans;
    }

    return retour;
  }

  /**
   * Change the edge set of the graph.
   * 
   * @param abstractEdgeSet
   *          the edge set to use in the graph.
   * @return true if successfull
   */
  protected boolean setAbstractEdgeSet(AbstractEdgeSet aes) {
    boolean retour = true;

    // first we delete the observers if an edge set was previously defined
    if (this.abstractEdgeSet != null) {
      this.abstractEdgeSet.getAbstractVertexSet().deleteAddObserver(
          this.abstractVertexSet);
      this.abstractEdgeSet.getAbstractVertexSet().deleteRemoveObserver(
          this.abstractVertexSet);
      this.abstractVertexSet.deleteAddObserver(this.abstractEdgeSet
          .getAbstractVertexSet());
      this.abstractVertexSet.deleteRemoveObserver(this.abstractEdgeSet
          .getAbstractVertexSet());
    }

    // and finally we synchronize the vertex sets
    retour = syncAndAddObservers(this.abstractVertexSet, aes
        .getAbstractVertexSet());

    if (retour) {
      //then we add the new edge set
      this.abstractEdgeSet = aes;
    }

    return retour;
  }

  /**
   * Not Implemented .
   */
  public void replace(AbstractVertex vertex, AbstractGraph g) {
    Trace.println("Not implemented!");
  }

  /**
   * Not Implemented .
   */
  public void replace(AbstractVertex vertex, AbstractVertexSet vertexSet) {
    Trace.println("Not implemented!");
  }

  /**
   * Copy all vertices, edges, edge and vertex set of a graph, creating a new
   * graph. Note that only the structure of the graph is duplicated. All the
   * values stored with <code>setValue</code> are lost.
   * 
   * @return the duplicated graph.
   */
  public AbstractGraph copyAbstractGraph() {
    return this.getFactory().newAbstractCopyGraph(this, true);
  }

  /**
   * Free memory when this object is linked. This method is necessary when an
   * edge set is a sub set of an other edge set. As this sub set is listening to
   * his father (for changes), it is not removed of memory when no one is
   * pointing to it (because the father is still pointing to it). So, when the
   * user does not use this object anymore, he must free it from his father.
   * This method calls the free method on the edge set and vertex set of the
   * graph.
   */
  public void free() {
    super.free();
    abstractEdgeSet.free();
    abstractVertexSet.free();
  }

  protected void finalize() {
    nbInstanceOfGraphs--;
  }

  /**
   * Count all abstract graphs.
   * 
   * @return the number of abstract graphs.
   */
  public static int countAllAbstractGraphs() {
    return nbInstanceOfGraphs;
  }

  /*****************************************************************************
   * PRIVATE FUNCTIONS
   ****************************************************************************/

  // This function is useful to get sure that 2 AbstractVertexSets contain the
  // same elements
  // and observe each other.
  // The 2 parameters should be different, no test has been conducted to study
  // the
  // behaviour if the parameters are the sameobjects.
  //
  //It must be noted that the changes are made on the fly to the vertexsets and
  // if the
  // function returns false, then with high probability, the vertex sets have
  // changed. I
  // believe that this behaviour should be changed.
  private boolean syncAndAddObservers(AbstractVertexSet ans1,
      AbstractVertexSet ans2) {
    Vertex candidateVertex = null;
    boolean retour = true;

    // we make sure that ans2 is included in ans1
    Iterator itesn = ans2.iterator();

    while (itesn.hasNext()) {
      candidateVertex = (Vertex) itesn.next();

      if (!ans1.contains(candidateVertex)) {
        retour = retour & ans1.add(candidateVertex);
      }
    }

    // we make sure that ans1 is included in ans2
    Iterator itnsn = ans1.iterator();

    while (itnsn.hasNext()) {
      candidateVertex = (Vertex) itnsn.next();

      if (!ans2.contains(candidateVertex)) {
        retour = retour & ans2.add(candidateVertex);
      }
    }

    // and then the observers
    ans1.addAddObserver(ans2);
    ans2.addAddObserver(ans1);
    ans1.addRemoveObserver(ans2);
    ans2.addRemoveObserver(ans1);

    return retour;
  }

  // ---------- Subset Stuff --------------
  // is now in MascoptSet and AbstractGraph, where it belongs.

  /**
   * Returns the super set of the object if exists.
   * 
   * @return the super set of the object if exists.
   */
  public AbstractGraph getSuperGraph() {
    return superGraph;
  }

  /**
   * Says if the object is a subset of an other.
   * 
   * @return true if the object is a subset of an other.
   */
  public boolean isSubGraph() {
    return (superGraph != null);
  }

  /**
   * Sets the supersets. Some checking is done. More precisely, it is checked
   * that the AbstractVertexSet ( <i>Resp. </i>AbstractEdgeSet) of
   * <code>this</code> is a subset of the AbstractVertexSet ( <i>Resp.
   * </i>AbstractEdgeSet) of <code>superSet</code>.
   * <p>
   * <b>NO CHECK IS DONE ABOUT AN EVENTUALLY ALREADY EXISTING SUPERGRAPH. </b>
   * </p>
   * 
   * @param superGraph
   *          the super graph to use for the graph.
   */
  public boolean setSuperGraph(AbstractGraph superGraph) {
    AbstractVertexSet potentiallySuperVertexSet = null;
    if (abstractVertexSet != null) {
      potentiallySuperVertexSet = (AbstractVertexSet) abstractVertexSet
          .getSuperSet();
    }
    AbstractEdgeSet potentiallySuperEdgeSet = null;
    if (abstractEdgeSet != null) {
      potentiallySuperEdgeSet = (AbstractEdgeSet) abstractEdgeSet.getSuperSet();
    }

    if ((potentiallySuperVertexSet != null)
        && (potentiallySuperEdgeSet != null)
        && (potentiallySuperVertexSet == superGraph.getAbstractVertexSet())
        && (potentiallySuperEdgeSet == superGraph.getAbstractEdgeSet())) {
      this.superGraph = superGraph;

      return true;
    } else {
      return false;
    }
  }

  //------------------Search stuff ------------------

  /**
   * Enables the traverse of a graph in a Breadth First way. This method returns
   * an {@link java.util.Iterator}, which will describe the vertices in a
   * breadth first order.
   * 
   * @param root
   *          the vertex from which we begin the search.
   * @return an {@link java.util.Iterator}object.
   */
  public Iterator breadthFirstIterator(AbstractVertex root) {

    //I know that the code is not optimized, but the complexity of this algo
    // is not huge, so I can afford to write it rather cleanly.
    //
    //the list that will contain the elements in the right order
    Vector bfs = new Vector();
    //the working stack. It is a vector because we need a queue (fifo).
    //With a vector we add at the end and retrieve at the end.
    Vector queue = new Vector();
    Vector alreadyVisited = new Vector();

    MascoptFixedSet neighbors;
    AbstractVertex theNeighbor;
    AbstractVertex currentVertex;
    AbstractEdge currentEdge;
    //initialisation
    queue.add(root);
    alreadyVisited.add(root);

    //let's begin the exploration
    while (!(queue.isEmpty())) {
      currentVertex = (AbstractVertex) queue.remove(0);
      neighbors = currentVertex.getOutgoing(abstractEdgeSet);
      Iterator itN = neighbors.iterator();
      while (itN.hasNext()) {
        currentEdge = (AbstractEdge) itN.next();
        theNeighbor = (AbstractVertex) currentEdge
            .getOppositeAbstractVertex(currentVertex);
        if (!(alreadyVisited.contains(theNeighbor))) {
          alreadyVisited.add(theNeighbor);
          queue.add(theNeighbor);
          bfs.add(currentEdge);
        }
      }
    }
    return bfs.iterator();
  }

  /**
   * Enables the traverse of a graph in a Depth First way. This method returns
   * an {@link java.util.Iterator}, which will describe the vertices in a depth
   * first order.
   * 
   * @param root
   *          the vertex from which we begin the search.
   * @return an {@link java.util.Iterator}object.
   */
  public Iterator depthFirstIterator(AbstractVertex root) {
    //I know that the code is not optimized, but the complexity of this algo
    // is not huge, so I can afford to write it rather cleanly.
    //
    //the list that will contain the elements in the right order
    Vector dfs = new Vector();
    //the working stack. It is a vector because we need a queue (fifo).
    //With a vector we add at the end and retrieve at the end.
    Vector queue = new Vector();
    Vector alreadyVisited = new Vector();

    MascoptFixedSet neighbors;
    AbstractVertex theNeighbor;
    AbstractVertex currentVertex;
    AbstractEdge currentEdge;
    //initialisation
    queue.add(root);
    alreadyVisited.add(root);

    //let's begin the exploration
    while (!(queue.isEmpty())) {
      currentVertex = (AbstractVertex) queue.remove(queue.size() - 1);
      neighbors = currentVertex.getOutgoing(abstractEdgeSet);
      Iterator itN = neighbors.iterator();
      while (itN.hasNext()) {
        currentEdge = (AbstractEdge) itN.next();
        theNeighbor = (AbstractVertex) currentEdge
            .getOppositeAbstractVertex(currentVertex);
        if (!(alreadyVisited.contains(theNeighbor))) {
          alreadyVisited.add(theNeighbor);
          queue.add(theNeighbor);
          dfs.add(currentEdge);
        }
      }
    }
    return dfs.iterator();
  }

  /**
   * This function looks at the endpoints of every AbstractEdge from a given
   * AbstractEdgeSet, check that they belong to a given AbstractVertexSet and if
   * not add them.
   */
  public void addMissingVertices(AbstractVertexSet avs, AbstractEdgeSet aes) {
    Iterator itaes = aes.iterator();

    AbstractEdge ae = null;
    AbstractVertex n0 = null;
    AbstractVertex n1 = null;

    while (itaes.hasNext()) {
      ae = (AbstractEdge) itaes.next();
      n0 = ae.getAbstractVertices()[0];
      n1 = ae.getAbstractVertices()[1];
      if (!avs.contains(n0)) {
        avs.add(n0);
      }
      if (!avs.contains(n1)) {
        avs.add(n1);
      }
    }
  }

  // *************************************************************************************
  // For I/O
  // *************************************************************************************

  /**
   * The method writes the current object in the DOMTree.
   * 
   * @param element
   *          the current node of DOM document.
   */
  public Element toDOMTree(Element element) {
    // Getting the super node for this object
    Element node_to_go = super.toDOMTree(element);

    // Pointing to the two sets that are used
    this.getAbstractVertexSet().toDOMTreeAsRef(node_to_go);
    this.getAbstractEdgeSet().toDOMTreeAsRef(node_to_go);

    return node_to_go;
  }

}
