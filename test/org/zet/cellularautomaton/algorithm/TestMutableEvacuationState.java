package org.zet.cellularautomaton.algorithm;

import java.util.LinkedList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestMutableEvacuationState {
    private final Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testRemoveIndividuals() {
        ParameterSet ps = context.mock(ParameterSet.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        Individual toEvacuate = builder.build();
        Individual notToEvacuate = builder.build();
        
        LinkedList<Individual> individuals = new LinkedList<>();
        individuals.add(toEvacuate);
        individuals.add(notToEvacuate);
        
        EvacuationState es = new MutableEvacuationState(ps, eca, individuals);

        context.checking(new Expectations() {{
            allowing(eca).setIndividualEvacuated(toEvacuate);
        }});
        
        es.markIndividualForRemoval(toEvacuate);
        es.removeMarkedIndividuals();
        assertThat(es.getIndividualState().getRemainingIndividuals(), not(hasItem(toEvacuate)));
        assertThat(es.getIndividualState().getRemainingIndividuals(), hasItem(notToEvacuate));
    }
}
