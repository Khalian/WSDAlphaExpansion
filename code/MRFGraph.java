/**
 * Project 	 : UnsupervisedWSD
 * 
 * Team 	 : CFILT, IIT Bombay.
 *
 * File Name : MRFGraph.java
 *
 * Created On: 01-May-2012
 *
 * Created By: Salil
 *
 * Revision History:
 * Modification Date 	Modified By		Comments
 * 
 */
package in.ac.iitb.cfilt.context.helper.alpha;

import java.util.Vector;

/**
 * <p>Class	: Graph
 * <p>Purpose	: This class represents undirected graph with nodes (vertices)
 * and edges
 * @author salil 
*/
public class MRFGraph {
	/**
	 * This field stores the list of nodes
	 */
	Vector<MRFNode> m_Nodes = new Vector<MRFNode>();
	/**
	 * This field stores the list of edges
	 */
	Vector<MRFEdge> m_Edges = new Vector<MRFEdge>();

	/**
	 * <p>Method 	: addVertex
	 * <p>Purpose	: Adds a node to the graph 
	 * <p>@param node void
	 */
	public void addVertex(MRFNode node) {
		m_Nodes.add(node);
	}

	/**
	 * <p>Method 	: addEdge
	 * <p>Purpose	: Adds an undirected edge to the graph 
	 * <p>@param edge void
	 */
	public void addEdge(MRFEdge edge) {
		m_Edges.add(edge);
	}

	/**
	 * <p>Method 	: getNodes
	 * <p>Purpose	: Returns the list of nodes
	 * <p>@return Vector<MRFNode>
	 */
	public Vector<MRFNode> getNodes() {
		return m_Nodes;
	}

	/**
	 * <p>Method 	: setNodes
	 * <p>Purpose	: Sets the set of vertices in an undirected graph
	 * <p>@param nodes void
	 */
	public void setNodes(Vector<MRFNode> nodes) {
		m_Nodes = nodes;
	}

	/**
	 * <p>Method 	: getEdges
	 * <p>Purpose	: Returns the list of edges
	 * <p>@return Vector<MRFEdge>
	 */
	public Vector<MRFEdge> getEdges() {
		return m_Edges;
	}

	/**
	 * <p>Method 	: setEdges
	 * <p>Purpose	: Sets the set of Edges in an undirected graph
	 * <p>@param edges void
	 */
	public void setEdges(Vector<MRFEdge> edges) {
		m_Edges = edges;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Graph (V = ");
		for (MRFNode node : m_Nodes) {
			stringBuffer.append(node + " ");
		}
		stringBuffer.append("E = ");
		for (MRFEdge edge : m_Edges) {
			stringBuffer.append(edge + " ");
		}
		stringBuffer.append(")");
		return stringBuffer.toString();
	}

	public void clear() {
		for (MRFEdge edge : m_Edges) {
			edge.clear();
		}
		for (MRFNode node : m_Nodes) {
			node.clear();
		}
		m_Edges.clear();
		m_Nodes.clear();
	}
}
