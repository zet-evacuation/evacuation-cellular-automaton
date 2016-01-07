package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.zetool.common.util.Helper.in;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestRandomOrdering {
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testDefaultInAnyOrder() {
        List<Individual> individuals = new LinkedList<>();
        for (int i = 1; i <= 5; ++i) {
            Individual ind = builder.build();
            individuals.add(ind);
        }

        RandomOrdering d = new RandomOrdering();
        Iterator<Individual> individualIterator = d.apply(individuals);

        List<Individual> result = new LinkedList<>();
        for (Individual i : in(individualIterator)) {
            result.add(i);
        }
        assertThat(result, containsInAnyOrder(individuals.toArray()));
    }
}
