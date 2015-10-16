package org.zet.cellularautomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellState {
	Individual individual;

	public EvacuationCellState( Individual individual ) {
		this.individual = individual;
	}

	public Individual getIndividual() {
		return individual;
	}

	public void setIndividual( Individual individual ) {
		this.individual = individual;
	}
}
