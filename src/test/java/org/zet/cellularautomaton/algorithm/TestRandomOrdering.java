/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.MersenneTwister;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestRandomOrdering {

    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void defaultInAnyOrder() {
        List<Individual> individuals = new LinkedList<>();
        for (int i = 1; i <= 5; ++i) {
            Individual ind = builder.build();
            individuals.add(ind);
        }

        RandomUtils.getInstance().setRandomGenerator(new MersenneTwister());
        RandomOrdering d = new RandomOrdering();
        Iterator<Individual> individualIterator = d.apply(individuals);

        List<Individual> result = new LinkedList<>();
        for (Individual i : in(individualIterator)) {
            result.add(i);
        }
        assertThat(result, containsInAnyOrder(individuals.toArray()));
    }
}
