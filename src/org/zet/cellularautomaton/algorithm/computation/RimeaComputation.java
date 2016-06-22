package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collection;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RimeaComputation extends DefaultComputation {

    public RimeaComputation(PropertyAccess es, ParameterSet parameterSet) {
        super(es, parameterSet);
    }
    
    /**
     * Updates the exhaustion. Disabled for rimea parameter set.
     *
     * @param individual
     * @param targetCell
     * @return
     */
    @Override
    public double updateExhaustion(Individual individual, EvacCell targetCell) {
        es.propertyFor(individual).setExhaustion(0);
        return 0;
    }

    /**
     * Updates the panic. Disabled for rimea parameter set.
     *
     * @param individual
     * @param targetCell
     * @param preferedCells
     * @return
     */
    @Override
    public double updatePanic(Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells) {
        es.propertyFor(individual).setPanic(0);
        return 0;
    }

    /**
     * {@inheritDoc }
     *
     * @param i
     * @return
     */
    @Override
    public double updatePreferredSpeed(Individual i) {
        es.propertyFor(i).setRelativeSpeed(i.getMaxSpeed());
        return i.getMaxSpeed();
    }
}
