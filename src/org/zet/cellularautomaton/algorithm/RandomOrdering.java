/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.zet.cellularautomaton.Individual;
import org.zetool.rndutils.generators.GeneralRandomWrapper;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RandomOrdering implements Function<List<Individual>, Iterator<Individual>> {

    @Override
    public Iterator<Individual> apply(List<Individual> t) {
        List<Individual> individualsCopy = new LinkedList<>(t);
        Collections.shuffle(individualsCopy, new GeneralRandomWrapper(getRandomGenerator()));
        return individualsCopy.iterator();
    }
    
    protected GeneralRandom getRandomGenerator() {
        return (RandomUtils.getInstance()).getRandomGenerator();
    }

}
