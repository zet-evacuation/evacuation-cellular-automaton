package org.zet.cellularautomaton.algorithm;

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
public class TestDefaultEvacuationState {
    private final Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testRemoveIndividuals() {
        ParameterSet ps = context.mock(ParameterSet.class);
        EvacuationCellularAutomatonInterface eca = context.mock(EvacuationCellularAutomatonInterface.class);
        EvacuationState es = new DefaultEvacuationState(ps, eca);
        Individual toEvacuate = builder.build();
        Individual notToEvacuate = builder.build();
        
        context.checking(new Expectations() {{
            allowing(eca).setIndividualEvacuated(toEvacuate);
        }});
        
        es.getIndividualState().addIndividual(toEvacuate);
        es.getIndividualState().addIndividual(notToEvacuate);

        es.markIndividualForRemoval(toEvacuate);
        es.removeMarkedIndividuals();
        assertThat(es.getIndividualState().getIndividuals(), not(hasItem(toEvacuate)));
        assertThat(es.getIndividualState().getIndividuals(), hasItem(notToEvacuate));
    }
}
