package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import java.util.function.Function;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ICEM09Computation implements Computation {

    protected PropertyAccess es;

    @Override
    public double effectivePotential(Individual individual, EvacCellInterface targetCell, Function<EvacCellInterface,Double> dynamicPotential) {
        EvacCellInterface referenceCell = es.propertyFor(individual).getCell();
        if (referenceCell.getState().isEmpty()) {
            throw new IllegalArgumentException(CellularAutomatonLocalization.LOC.getString("algo.ca.parameter.NoIndividualOnReferenceCellException"));
        }
        StaticPotential staticPotential = es.propertyFor(referenceCell.getState().getIndividual()).getStaticPotential();
        final double statPotlDiff = staticPotential.getPotential(referenceCell) - staticPotential.getPotential(targetCell);
        return statPotlDiff;
    }

    //////////////////////////////////ab hier: nicht benutzt////////////////////////////////////////////////////////
    ////* Updating of dynamic parameters *////
    @Override
    public double updateExhaustion(Individual individual, EvacCellInterface targetCell) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double updatePreferredSpeed(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }

    @Override
    public double updatePanic(Individual individual, EvacCellInterface targetCell, Collection<EvacCellInterface> preferedCells) {
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
    public double idleThreshold(Individual individual) {
        throw new IllegalStateException("Methode aus PaperParameterSet wurde aufgerufen!");
        //return 0;
    }
}
