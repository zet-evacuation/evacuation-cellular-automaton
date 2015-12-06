package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.zetool.common.util.Helper.in;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.zet.cellularautomaton.Individual;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestRandomOrdering {

    @Test
    public void testDefaultInAnyOrder() {
        List<Individual> individuals = new LinkedList<>();
        for (int i = 1; i <= 5; ++i) {
            Individual ind = new Individual();
            ind.setNumber(i);
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
