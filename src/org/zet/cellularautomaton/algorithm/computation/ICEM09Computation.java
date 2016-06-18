package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ICEM09Computation implements Computation {

    protected PropertyAccess es;

    //////////////////////////////////ab hier: nicht benutzt////////////////////////////////////////////////////////
    ////* Updating of dynamic parameters *////
    @Override
    public double updateExhaustion(Individual individual, EvacCell targetCell) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double updatePreferredSpeed(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    ////* Threshold values for various decisions *////
    @Override
    public double changePotentialThreshold(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double movementThreshold(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }
    @Override
    public double idleThreshold(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double effectivePotential(EvacCell referenceCell, EvacCell targetCell, DynamicPotential dynamicPotential) {
        if (referenceCell.getState().isEmpty()) {
            throw new IllegalArgumentException(CellularAutomatonLocalization.LOC.getString("algo.ca.parameter.NoIndividualOnReferenceCellException"));
        }
        StaticPotential staticPotential = es.propertyFor(referenceCell.getState().getIndividual()).getStaticPotential();
        final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
        return statPotlDiff;
    }
}
