package in.ac.iitb.cfilt.context.helper;

import java.util.Vector;

/**
 * <p>Class	: Edge
 * <p>Purpose	: This class represents edge with its two nodes and m_potentials
 * @author salil 
 */
/**
 * <p>Class	: MRFEdge
 * <p>Purpose	: This class represents an edge with its m_potentials
 * The two end points of the edge are of the type {@link MRFNode}
 * @author salil
 */
public class MRFEdge {
	/**
	 * This field stores node on one end of the edge
	 */
	private MRFNode m_firstNode;
	/**
	 * This field stores node on the other end of the graph
	 */
	private MRFNode m_secondNode;
	/**
	 * This field stores the m_potentials of the edge
	 */
	private Vector<Double> m_potentials;
	/**
	 * This field stores the m_energies ( -log (m_potentials))
	 */
	private Vector<Double> m_energies;
	/**
	 * This field stores the m_capacity of the edge
	 */
	private double m_capacity = 0.0;

	/**
	 * Constructor
	 * <p>
	 * @param m_firstNode
	 * @param m_secondNode
	 * @param m_potentials
	 */
	public MRFEdge(MRFNode firstNode, MRFNode secondNode, Vector<Double> potentials) {
		this.m_firstNode = firstNode;
		this.m_secondNode = secondNode;
		this.m_potentials = potentials;
		this.m_energies = calculateEnergies(potentials);
		normalizeEnergies();
	}

	/**
	 * <p>Method 	: calculateEnergies
	 * <p>Purpose	: Calculates the energied based on m_potentials
	 * energy = -log (potential)
	 * <p>@param m_potentials
	 * <p>@return Vector<Double>
	 */
	private Vector<Double> calculateEnergies(Vector<Double> potentials) {
		Vector<Double> energies = new Vector<Double>();
		for (Double potential : potentials) {
			if (potential == null) {
				energies.add(-Math.log(0));
			} else {
				energies.add(-Math.log(potential));
			}
		}
		return energies;
	}

	/**
	 * <p>Method 	: getFirstNode
	 * <p>Purpose	: Returns the node on one end of the edge
	 * <p>@return MRFNode
	 */
	public MRFNode getFirstNode() {
		return this.m_firstNode;
	}

	/**
	 * <p>Method 	: getSecondNode
	 * <p>Purpose	: Returns the node on the other end of the edge
	 * <p>@return MRFNode
	 */
	public MRFNode getSecondNode() {
		return this.m_secondNode;
	}

	/**
	 * <p>Method 	: getEnergies
	 * <p>Purpose	: Returns the m_energies of the edge
	 * <p>@return Vector<Double>
	 */
	public Vector<Double> getEnergies() {
		return this.m_energies;
	}

	/**
	 * <p>Method 	: setEnergies
	 * <p>Purpose	: Sets the m_energies of the edge 
	 * <p>@param m_energies void
	 */
	public void setEnergies(Vector<Double> energies) {
		this.m_energies = energies;
		normalizeEnergies();
	}

	/**
	 * <p>Method 	: normalizeEnergies
	 * <p>Purpose	: Zero-normalize the m_energies of the edge
	 * <p> void
	 */
	public void normalizeEnergies() {
		Vector<Double> energies = new Vector<Double>();
		Double minEnergy = Double.MAX_VALUE;
		for (Double energy : this.m_energies) {
			if (energy < minEnergy)
				minEnergy = energy;
		}
		for (Double energy : this.m_energies) {
			energies.add(energy - minEnergy);
		}
		this.m_energies.clear();
		this.m_energies.addAll(energies);
	}

	/**
	 * <p>Method 	: getPotentials
	 * <p>Purpose	: Returns the m_potentials of the edge
	 * <p>@return Vector<Double>
	 */
	public Vector<Double> getPotentials() {
		return this.m_potentials;
	}

	/**
	 * <p>Method 	: getCapacity
	 * <p>Purpose	: Returns the m_capacity of the edge
	 * <p>@return double
	 */
	public double getCapacity() {
		return this.m_capacity;
	}

	/**
	 * <p>Method 	: setCapacity
	 * <p>Purpose	: Sets the m_capacity of the edge
	 * <p>@param m_capacity void
	 */
	public void setCapacity(Double capacity) {
		this.m_capacity = capacity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(this.m_firstNode.getNodeIndex() + "-");
		buffer.append(this.m_secondNode.getNodeIndex());
		buffer.append("]: {");
		for (Double energy : this.m_energies) {
			buffer.append(energy + " ");
		}
		buffer.append("}");
		return buffer.toString();
	}

	public void clear() {
		this.m_firstNode.clear();
		this.m_secondNode.clear();
		this.m_potentials.clear();
		this.m_energies.clear();
	}
}
