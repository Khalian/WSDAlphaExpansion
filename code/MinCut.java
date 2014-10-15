// ******************************************************* //
// Edmonds-Karp Algorithm
//
// SP - 2003
//
// ******************************************************* //

package in.ac.iitb.cfilt.context.mascoptLib.algos.digraph;


// les imports
import in.ac.iitb.cfilt.context.mascoptLib.graphs.*;
import in.ac.iitb.cfilt.context.mascoptLib.abstractGraph.*;
import java.lang.Math;
import java.util.*;


/**
 * Provides an algorithm to compute the maximum st-flow and so minimum st-cut of
 * a simple directed graph
 *
 * The algorithm used to compute the s-t flow max is Edmonds-Karp algorithm.
 * The name of the capacities is "capacity" but can be change (CAPACITY="name_given")
 * It is necessary to define the value of the variable INFINITY (which equals 999999) 
 * if some capacity is greater than the default value.
 *
 * The void "run" compute the max flow and must always be done to obtain the results.
 *
 * Entries : a digraph, two vertices s and t.
 * Results : vertexSetCutMin() returns the set of vertices which give the minimal cut
 *           arcSetCutMin() returns the set of arcs which belong to the minimal cut
 *           minCutValue() returns the value of the minimal cut
 */
public class MinCut {
    private DiGraph g_;
    private Vertex s_;
    private Vertex t_;
    private HashMap label;
    private HashMap flow;
    
    /** 
     * The constant for infinity.
     */
    public double INFINITY = Double.MAX_VALUE;

    /** 
     * The constant to read the capacity on edges.
     */
    public String CAPACITY = "capacity";

    private LinkedList scan;
    private double cutValue = 0.0;
    private ArcSet cutArc = new ArcSet();
    private VertexSet cutNode = new VertexSet();

    /** 
     * Constructor for the min cut.
     * 
     * @param g the graph to consider
     * @param s the first vertex
     * @param t the second vertex
     */
    public MinCut(DiGraph g, Vertex s, Vertex t) {
        g_ = g;
        s_ = s;
        t_ = t;
	// the initial value of s is infinity
        s_.setDouValue("value", INFINITY);
        label = new HashMap();
        label.put(s_, null);
        flow = new HashMap();
        scan = new LinkedList();
        scan.add(s_);

        // initialization, the value of the flow equals 0
        ArcSet arcs = g_.getArcSet();
        Iterator itarcs = arcs.iterator();

        while (itarcs.hasNext()) {
            Arc curarc = (Arc) itarcs.next();
            flow.put(curarc, new Double(0));
        }
    }

    /** 
     * Run the algorithm.
     */
    public void run() {
        double valcurnode;
        double delta;
        double rescap;
        double Xij;
        double valflow;
        double signeNode;
        Vertex origNode;
        Vertex endNode;
        Arc arcin;
        Arc arcout;
        Arc aij;
        MascoptFixedSet arcrecup;
        Double valflowd;

        while (scan.size() > 0) {
            // scan contains the labelled node to scan
            Vertex currentNode = (Vertex) scan.getFirst();

            // we get the arcs incoming and outgoing of currentNode
            MascoptFixedSet ain = currentNode.getIncoming(g_);
            MascoptFixedSet aout = currentNode.getOutgoing(g_);
            Iterator itarcin = ain.iterator();
            Iterator itarcout = aout.iterator();

            while (itarcin.hasNext()) {
                arcin = (Arc) itarcin.next();
                Xij = ((Double) flow.get(arcin)).doubleValue();
                origNode = (Vertex) arcin.getTail();

                if ((Xij > 0) && (!label.containsKey(origNode))) {
                    valcurnode = currentNode.getDouValue("value");
                    delta = Math.min(Math.abs(valcurnode),
                            ((Double) flow.get(arcin)).doubleValue());
                    origNode.setDouValue("value", -1 * delta);
                    label.put(origNode, currentNode);
                    scan.add(origNode);
                }
            }

            while (itarcout.hasNext()) {
                arcout = (Arc) itarcout.next();
                Xij = ((Double) flow.get(arcout)).doubleValue();
                rescap = arcout.getDouValue(CAPACITY) - Xij;
                origNode = (Vertex) arcout.getHead();

                if ((rescap > 0) && (!label.containsKey(origNode))) {
                    valcurnode = currentNode.getDouValue("value");
                    delta = Math.min(Math.abs(valcurnode), rescap);
                    origNode.setDouValue("value", delta);
                    label.put(origNode, currentNode);
                    scan.add(origNode);
                }
            }

            // we update scan 
            scan.remove(currentNode);

            // we regard if t is labelled.
            if (scan.contains(t_)) {
                // we have found an augmenting path from s to t
                // we update the flow 
		// we begin with the node t
                endNode = t_;
                delta = endNode.getDouValue("value");

                while (!endNode.equals(s_)) {
                    origNode = (Vertex) label.get(endNode);
                    signeNode = endNode.getDouValue("value");
                    if (signeNode > 0) {
                        // we get the arc origNode-endNode
                        arcrecup =  origNode.getEdgesTo(g_, endNode);
                        Iterator itarc = arcrecup.iterator();

                        // we update the flow on the arc origNode-endNode
                        while (itarc.hasNext()) {
                            aij = (Arc) itarc.next();
                            valflow = ((Double) flow.get(aij)).doubleValue() +
                                delta;
                            valflowd = new Double(valflow);
                            flow.put(aij, valflowd);
                        }
                    } else {
                        // we get the arc endNode-origNode
                        arcrecup =  endNode.getEdgesTo(g_, origNode);
                        Iterator itarc = arcrecup.iterator();

                        // we update the flow on the arc endNode-origNode
                        while (itarc.hasNext()) {
                            aij = (Arc) itarc.next();
                            valflow = ((Double) flow.get(aij)).doubleValue() -
                                delta;
                            valflowd = new Double(valflow);
                            flow.put(aij, valflowd);
                        }
                    }
		    // we treat the next node.
                    endNode = origNode;
                }
                // end while

                // we update label and scan
                s_.setDouValue("value", INFINITY);
                label.clear();
                label.put(s_, null);
                scan.clear();
                scan.add(s_);
            }
        }

        // scan est vide, ce qui signifie que la coupe min est donnee par label
    }

    /** 
     * Returns the set of vertices of the min cut.
     * 
     * @return a vertex set.
     */
    public VertexSet vertexSetCutMin() {
        VertexSet noderesult = new VertexSet();
        Vertex nlabel;

        if (cutNode.isEmpty()) {
            Iterator nodesIt = label.keySet().iterator();

            while (nodesIt.hasNext()) {
                nlabel = (Vertex) nodesIt.next();
                cutNode.add(nlabel);
            }
        }

        return cutNode;
    }

    /** 
     * Returns the set of arcs of the min cut.
     * 
     * @return an arc set.
     */
    public ArcSet arcSetCutMin() {
        Vertex nlabel;
        Vertex nnext;
        Arc arcout;

        if (cutArc.isEmpty()) {
            Iterator nodesIt = label.keySet().iterator();

            while (nodesIt.hasNext()) {
                nlabel = (Vertex) nodesIt.next();

                // on recupere les arcs sortant de nlabel
                MascoptFixedSet aout = nlabel.getOutgoing(g_);
                Iterator itarcout = aout.iterator();

                // si la queue de l'arc n'est pas labellee, l'arc appartient a la coupe min
                while (itarcout.hasNext()) {
                    arcout = (Arc) itarcout.next();
                    nnext = (Vertex) arcout.getHead();

                    if (!label.containsKey(nnext)) {
                        cutArc.add(arcout);
                        cutValue = cutValue + arcout.getDouValue(CAPACITY);
                    }
                }
            }
        }

        return cutArc;
    }

    /** 
     * Returns the value of the min cut.
     * 
     * @return a double
     */
    public double minCutValue() {
        if (cutValue == 0.0) {
            cutArc = this.arcSetCutMin();
        }

        return cutValue;
    }
 
    /** 
     * Returns the hashmap of the max flow.
     * 
     * @return an hashmap.
     */
    public HashMap maxFlow() {
        return flow;
    }
}
