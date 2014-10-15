package in.ac.iitb.cfilt.context.helper;

import in.ac.iitb.cfilt.context.mascoptLib.graphs.Edge;
import in.ac.iitb.cfilt.context.mascoptLib.graphs.EdgeSet;
import in.ac.iitb.cfilt.context.mascoptLib.graphs.Graph;
import in.ac.iitb.cfilt.context.mascoptLib.graphs.Vertex;
import in.ac.iitb.cfilt.context.mascoptLib.graphs.VertexSet;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Class	: AlphaExpander
 * <p>Purpose	: This class runs the Alpha expansion algorithm.
 * <p>Steps:<br/>
 * <ol>
 * <li>Build the MRF graph from input file</li>
 * <li>Assert that the graph follows required constraints of Symmetry and Sub-modularity</li>
 * <li>Start with an arbitrary assignment of labels for all the nodes</li>
 * <li>Vary \alpha over the range of labels, and minimize the energies using Graph-cut</li>
 * <li>Repeat till lesser energy assignments are possible</li>
 * <li>Return the configuration with least energy</li>
 * </ol>
 * 
 */
public class AlphaExpander {

	/**
	 * This field stores the Special vertex S (start node)
	 */
	Vertex m_SVertex = new Vertex();
	/**
	 * This field stores the Special vertex T (sink node)
	 */
	Vertex m_TVertex = new Vertex();

	protected void finalize() {
		m_SVertex.free();
		m_TVertex.free();
	}
	
	/**
	 * <p>Method 	: isExpandable
	 * <p>Purpose	: Check if the constrains on edges of the graph
	 * are satisfied in order to run alpha expansion. If the constrains
	 * are not satisfied, simply return with an error. 
	 * <p>@param mrfGraph
	 * <p>@return boolean
	 */
	private static boolean isExpandable(MRFGraph mrfGraph) {
		Vector<Double> energies = null;
		int nodeEnergySize = 0;
		for (MRFEdge edge : mrfGraph.getEdges()) {
			//Check for symmetry of Energies
			energies = edge.getEnergies();
			nodeEnergySize = edge.getFirstNode().getEnergies().size();
			/*if (!energies.elementAt(0).equals(energies.elementAt(3)) || !energies.elementAt(1).equals(energies.elementAt(2))) {
				System.out.println("Bad Graph on edge " + edge + "! Symmetry broken! Terminating!!!");
				return false;
			}*/
			//Check for Sub-modularity
			for (int i = 0; i < nodeEnergySize; i++) {
				for (int j = i + 1; j < nodeEnergySize; j++) {
					for (int k = j + 1; k < nodeEnergySize; k++) {
						if (energies.elementAt(i * nodeEnergySize + k) < (energies.elementAt(i * nodeEnergySize + j) + energies.elementAt(j * nodeEnergySize + k))) {
							//System.out.println(energies.elementAt(i * nodeEnergySize + k) + " " + energies.elementAt(i * nodeEnergySize + j) + " " + energies.elementAt(j * nodeEnergySize + k));
							//System.out.println("Bad Graph on edge " + edge + "! Sub-modularity broken! Terminating!!!");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private MRFGraph getAlphaGraph(MRFGraph mrfGraph, Vector<Integer> currentAssignment, int alpha, int maxAlpha) {
		MRFGraph binaryMRFGraph = new MRFGraph();
		MRFNode binaryNode = null;
		MRFEdge binaryEdge = null;
		Vector<MRFNode> binaryNodeVector = new Vector<MRFNode>();
		Vector<Double> binaryEnergies = null;
		int firstNodeIndex = 0;
		int secondNodeIndex = 0;
		int potentialsLength = 0;
		for (MRFNode node : mrfGraph.getNodes()) {
			binaryEnergies = new Vector<Double>();
			binaryEnergies.add(node.getEnergies().elementAt(currentAssignment.elementAt(node.getNodeIndex())));
			binaryEnergies.add(node.getEnergies().elementAt(alpha));
			binaryNode = new MRFNode(node.getNodeIndex(), binaryEnergies, null);
			binaryNode.setEnergies(binaryEnergies);
			binaryMRFGraph.addVertex(binaryNode);
			binaryNodeVector.add(binaryNode);
		}
		for (MRFEdge edge : mrfGraph.getEdges()) {
			binaryEnergies = new Vector<Double>();
			firstNodeIndex = mrfGraph.getNodes().indexOf(edge.getFirstNode());
			secondNodeIndex = mrfGraph.getNodes().indexOf(edge.getSecondNode());
			potentialsLength = maxAlpha;

			binaryEnergies.add(edge.getEnergies().elementAt(currentAssignment.elementAt(firstNodeIndex) * potentialsLength + currentAssignment.elementAt(secondNodeIndex)));
			binaryEnergies.add(edge.getEnergies().elementAt(currentAssignment.elementAt(firstNodeIndex) * potentialsLength + alpha));
			binaryEnergies.add(edge.getEnergies().elementAt(alpha * potentialsLength + currentAssignment.elementAt(secondNodeIndex)));
			binaryEnergies.add(edge.getEnergies().elementAt(alpha * potentialsLength + alpha));
			binaryEdge = new MRFEdge(binaryNodeVector.elementAt(firstNodeIndex), binaryNodeVector.elementAt(secondNodeIndex), binaryEnergies);
			binaryEdge.setEnergies(binaryEnergies);
			binaryMRFGraph.addEdge(binaryEdge);
		}
		return binaryMRFGraph;
	}

	/**
	 * <p>Method 	: createSTGraph
	 * <p>Purpose	: Creates a Graph with S-T Nodes from
	 * given MRF graph. The edge weights (capacities) are assigned
	 * using the rules of alpha expansion (i.e. using the current 
	 * assignment and value of \alpha)
	 * <p>@param mrfGraph void
	 */
	private Graph createSTGraph(MRFGraph binaryMRFGraph) {
		Graph graph = new Graph();
		VertexSet vertexSet = new VertexSet();
		Vector<Vertex> vertexVector = new Vector<Vertex>();
		Vertex currentVertex = null;
		EdgeSet edgeSet = new EdgeSet(vertexSet);
		Edge currentEdge = null;
		Vertex firstVertex = null;
		Vertex secondVertex = null;
		Vector<Double> nodeEnergies = null;
		m_SVertex.setName("S");
		vertexSet.add(m_SVertex);
		m_TVertex.setName("T");
		vertexSet.add(m_TVertex);
		for (MRFNode node : binaryMRFGraph.getNodes()) {
			currentVertex = new Vertex(node.getNodeIndex(), 0);
			currentVertex.setName(node.getNodeIndex() + "");
			vertexSet.add(currentVertex);
			vertexVector.add(currentVertex);
		}
		for (MRFEdge edge : binaryMRFGraph.getEdges()) {
			firstVertex = vertexVector.elementAt(edge.getFirstNode().getNodeIndex());
			secondVertex = vertexVector.elementAt(edge.getSecondNode().getNodeIndex());
			currentEdge = new Edge(firstVertex, secondVertex);
			nodeEnergies = edge.getFirstNode().getEnergies();
			nodeEnergies.setElementAt(edge.getFirstNode().getEnergies().elementAt(1) + edge.getEnergies().elementAt(2) - edge.getEnergies().elementAt(0), 1);
			edge.getFirstNode().setEnergies(nodeEnergies);
			nodeEnergies = edge.getSecondNode().getEnergies();
			nodeEnergies.setElementAt(edge.getSecondNode().getEnergies().elementAt(1) + edge.getEnergies().elementAt(3) - edge.getEnergies().elementAt(2), 1);
			edge.getSecondNode().setEnergies(nodeEnergies);
			currentEdge.setName(edge.getFirstNode().getNodeIndex() + "_" + edge.getSecondNode().getNodeIndex());
			currentEdge.setValue("capacity", edge.getEnergies().elementAt(1) + edge.getEnergies().elementAt(2) - edge.getEnergies().elementAt(0) - edge.getEnergies().elementAt(3));
			edgeSet.add(currentEdge);
		}
		for (MRFNode node : binaryMRFGraph.getNodes()) {
			firstVertex = vertexVector.elementAt(node.getNodeIndex());
			//Add Edges from S
			currentEdge = new Edge(m_SVertex, firstVertex);
			currentEdge.setName(m_SVertex.getName() + "_" + firstVertex.getName());
			if (node.getEnergies().elementAt(1) > node.getEnergies().elementAt(0)) {
				currentEdge.setValue("capacity", node.getEnergies().elementAt(1) - node.getEnergies().elementAt(0));
			} else {
				currentEdge.setValue("capacity", 0.0);
			}
			edgeSet.add(currentEdge);
			//Add Edges to T
			currentEdge = new Edge(firstVertex, m_TVertex);
			currentEdge.setName(firstVertex.getName() + "_" + m_TVertex.getName());
			if (node.getEnergies().elementAt(1) > node.getEnergies().elementAt(0)) {
				currentEdge.setValue("capacity", 0.0);
			} else {
				currentEdge.setValue("capacity", node.getEnergies().elementAt(0) - node.getEnergies().elementAt(1));
			}
			edgeSet.add(currentEdge);
		}
		graph.setVertexSet(vertexSet);
		graph.setEdgeSet(edgeSet);
		return graph;
	}

	/**
	 * <p>Method 	: getSTMinCut
	 * <p>Purpose	: Finds the min cut for a given graph constructed 
	 * using {@link AlphaExpander.createSTGraph()} and returns the  set
	 * of edges involved in the min cut.
	 * ## Code taken from MascoptLib ##
	 * <p>@param graph
	 * <p>@return EdgeSet
	 */
	private EdgeSet getSTMinCut(Graph graph) {
		STMinCut minCutFinder = new STMinCut(graph, m_SVertex, m_TVertex);
		//System.out.println("Min Cut Value: " + minCutFinder.minCutValue());
		//System.out.println("Min Cut:" + minCutFinder.edgeSetCutMin());
		return minCutFinder.edgeSetCutMin();
	}

	/**
	 * <p>Method 	: calculateScore
	 * <p>Purpose	: Calculates the score of the latest assignment and prints it 
	 * <p>@param newAssignment void
	 * @return 
	 */
	private Double getScore(MRFGraph mrfGraph, Vector<Integer> assignment) {
		double energy = 0.0;
		double edgeEnergy = 0.0;
		int firstNodeLabel = 0;
		int secondNodeLabel = 0;
		for (MRFEdge edge : mrfGraph.getEdges()) {
			firstNodeLabel = assignment.elementAt(edge.getFirstNode().getNodeIndex());
			secondNodeLabel = assignment.elementAt(edge.getSecondNode().getNodeIndex());
			edgeEnergy = edge.getFirstNode().getEnergies().elementAt(firstNodeLabel);
			if (edgeEnergy != Double.POSITIVE_INFINITY) {
				energy += edgeEnergy;
			} else {
				energy += 1;
			}
			edgeEnergy = edge.getSecondNode().getEnergies().elementAt(secondNodeLabel);
			if (edgeEnergy != Double.POSITIVE_INFINITY) {
				energy += edgeEnergy;
			} else {
				energy += 1;
			}
			//potential += edge.getPotentials().elementAt(firstNodeLabel*numberOfLabels+secondNodeLabel);
		}
		return energy;
		//System.out.println("Score of Assignment " + potential);
	}

	/**
	 * <p>Method 	: expand
	 * <p>Purpose	: Applies steps of alpha expansion algorithm on
	 * given Undirected graph.
	 * <p>@param mrfGraph void
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> expand(MRFGraph mrfGraph, Vector<Integer> startAssignment, int maxAlpha) {
		//Check if the MRF is metric
		/*if (!isExpandable(mrfGraph)) {
			return startAssignment;
		}
		*/
		Vector<Integer> iterationLastAssignment = new Vector<Integer>();
		Vector<Integer> currentAssignment = new Vector<Integer>();
		Vector<Integer> newAssignment = new Vector<Integer>();
		double currentScore = getScore(mrfGraph, startAssignment);
		double newScore = 0.0;
		MRFGraph binaryMRFGraph = null;
		boolean change = true;
		Iterator<Edge> edgeIterator = null;
		EdgeSet minCut = null;
		Edge currentEdge = null;
		Graph graph = null;
		int cutNode = 0;

		/*for (int i = 0; i < mrfGraph.getNodes().size(); i++) {
			currentAssignment.add(1);
		}*/
		currentAssignment.addAll(startAssignment);
		//System.out.println(currentAssignment);

		//Alpha expand until better assignments are found
		while (change) {
			change = false;
			//System.out.println("\n\nIteration Begins");
			for (int alpha = 0; alpha < maxAlpha; alpha++) {
				//System.out.println("\nalpha = " + alpha);
				binaryMRFGraph = getAlphaGraph(mrfGraph, currentAssignment, alpha, maxAlpha);
				graph = createSTGraph(binaryMRFGraph);
				binaryMRFGraph.clear();
				//System.out.println("Current Assignment: " + currentAssignment);
				//System.out.println("Current " + graph);
				/*while (edgeIterator.hasNext()) {
					Edge edge = edgeIterator.next();
					System.out.println(edge + " " + edge.getValue("capacity"));
				}*/
				minCut = getSTMinCut(graph);
				//System.out.println(graph);
				freeMemory(graph);
				//System.out.println(graph);
				edgeIterator = minCut.iterator();
				newAssignment.clear();
				newAssignment.addAll(currentAssignment);
				while (edgeIterator.hasNext()) {
					currentEdge = edgeIterator.next();
					if (currentEdge.getName().startsWith("S_")) {
						cutNode = Integer.parseInt(currentEdge.getName().replace("S_", ""));
						newAssignment.setElementAt(alpha, cutNode);
					}
				}
				minCut.free();
				//System.out.println("Revised Assignment: " + newAssignment);
				newScore = getScore(mrfGraph, newAssignment);
				if (!currentAssignment.equals(newAssignment) && currentScore >= newScore) {
					//System.out.println(currentScore + " " + newScore);
					currentAssignment.clear();
					currentScore = newScore;
					currentAssignment.addAll(newAssignment);
					change = true;
				}
			}
			if (iterationLastAssignment.equals(newAssignment)) {
				break;
			}
			iterationLastAssignment.clear();
			iterationLastAssignment.addAll(newAssignment);
			//System.out.println("Iteration Ends\n###############");
		}
		iterationLastAssignment.clear();
		newAssignment.clear();
		return currentAssignment;
	}

	private void freeMemory(Graph graph) {
		graph.getAbstractEdgeSet().clear();
		graph.getAbstractVertexSet().clear();
		graph.free();
	}

	/**
	 * <p>Method 	: main
	 * <p>Purpose	: Testing purpose
	 * <p>@param args
	 * <p>@throws FileNotFoundException void
	 */
	public static void main(String args[]) throws FileNotFoundException {
		GraphReader reader = new GraphReader();
		//System.setOut(new PrintStream( new File("/home/salil/Desktop/AlhapExpansionLog.txt")));
		reader.buildGraph(args[0]);
		MRFGraph mrfGraph = reader.getGraph();
		System.out.println("Given " + mrfGraph);
		AlphaExpander expander = new AlphaExpander();
		expander.expand(mrfGraph, new Vector<Integer>(), 1);
	}
}
