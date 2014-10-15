package in.ac.iitb.cfilt.context.helper;

import in.ac.iitb.cfilt.data.CandidateSynset;

import java.util.Arrays;
import java.util.Vector;

/**
 * <p>Class	: Node
 * <p>Purpose	: This class represents a node (vertex) with its m_potentials
 * @author salil 
 */
public class MRFNode {
	/**
	 * This field stores the index (i.e. name) of the node
	 */
	private int m_nodeIndex;
	/**
	 * This field stores the list of m_potentials for the node
	 */
	private Vector<Double> m_potentials = new Vector<Double>();
	/**
	 * This field stores the m_energies ( -log (m_potentials))
	 */
	private Vector<Double> m_energies = new Vector<Double>();

	/**
	 * This field stores the m_capacity of the node
	 */
	private double m_capacity = 0.0;

	/**
	 * 
	 */
	private Vector<CandidateSynset> m_candidateSynsets = new Vector<CandidateSynset>();

	/**
	 * Constructor
	 * <p>
	 * @param m_nodeIndex
	 * @param m_potentials
	 * @param m_candidateSynsets 
	 */
	public MRFNode(int nodeIndex, Vector<Double> potentials, Vector<CandidateSynset> candidateSynsets) {
		this.m_nodeIndex = nodeIndex;
		this.m_potentials = potentials;
		this.m_energies = calculateEnergies(potentials);
		if (candidateSynsets != null) {
			this.m_candidateSynsets.addAll(candidateSynsets);
		}
		normalizeEnergies();
	}

	/**
	 * <p>Method 	: getNodeIndex
	 * <p>Purpose	: Returns the index of the node 
	 * <p>@return int
	 */
	public int getNodeIndex() {
		return this.m_nodeIndex;
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
	 * <p>Method 	: getEnergies
	 * <p>Purpose	: Returns the m_energies of the node
	 * <p>@return Vector<Double>
	 */
	public Vector<Double> getEnergies() {
		return this.m_energies;
	}

	/**
	 * <p>Method 	: setEnergies
	 * <p>Purpose	: Sets the m_energies for a node
	 * <p>@param m_energies void
	 */
	public void setEnergies(Vector<Double> energies) {
		this.m_energies = energies;
		normalizeEnergies();
	}

	/**
	 * <p>Method 	: getPotentials
	 * <p>Purpose	: Returns the m_potentials of the node
	 * <p>@return Vector<Double>
	 */
	public Vector<Double> getPotentials() {
		return this.m_potentials;
	}

	/**
	 * <p>Method 	: getCapacity
	 * <p>Purpose	: Returns the m_capacity of the node
	 * <p>@return double
	 */
	public double getCapacity() {
		return this.m_capacity;
	}

	/**
	 * <p>Method 	: normalizeEnergies
	 * <p>Purpose	: Zero-normalized the m_energies of the edge
	 * <p> void
	 */
	public void normalizeEnergies() {
		Vector<Double> energies = new Vector<Double>();
		double minEnergy = Double.MAX_VALUE;
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
	 * <p>Method 	: setCapacity
	 * <p>Purpose	: Sets the m_capacity of the node
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
		buffer.append(this.m_nodeIndex + ":{");
		for (Double energy : this.m_energies) {
			buffer.append(energy + " ");
		}
		buffer.append("}");
		return buffer.toString();
	}

	public Vector<CandidateSynset> getCandidateSynsets() {
		return m_candidateSynsets;
	}

	public CandidateSynset getCandidateSynset(int index) {
		if (m_candidateSynsets == null || m_candidateSynsets.size() <= index) {
			return null;
		}
		return m_candidateSynsets.get(index);
	}

	public void setCandidateSynsets(Vector<CandidateSynset> candidateSynsets) {
		this.m_candidateSynsets = candidateSynsets;
	}

	public void clear() {
		this.m_potentials.clear();
		this.m_energies.clear();
		this.m_candidateSynsets.clear();
	}
}
