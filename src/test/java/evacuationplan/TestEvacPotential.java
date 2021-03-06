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
package evacuationplan;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacPotential {
    private final static int NEW_POTENTIAL = 2;
    private final static int NEW_POTENTIAL_EXIT = 0;
    
    private final Mockery context = new Mockery();
    Individual i;
    CellularAutomatonDirectionChecker checker;
    ExitCell exitCell;
    RoomCell individualCell;
    EvacPotential ep;
    

    @Before
    public void init() {
        StaticPotential potential = new StaticPotential();

        individualCell = new RoomCell(0, 1);
        potential.setPotential(individualCell, NEW_POTENTIAL);

        exitCell = new ExitCell(0, 0);
        potential.setPotential(exitCell, NEW_POTENTIAL_EXIT);
        
        List<ExitCell> exits = Collections.singletonList(exitCell);
        potential.setAssociatedExitCells(exits);

        i = new IndividualBuilder().build();
        IndividualProperty is = new IndividualProperty(i);
        is.setCell(individualCell);
        
        checker = context.mock(CellularAutomatonDirectionChecker.class);
        PropertyAccess es = context.mock(PropertyAccess.class);
        context.checking(new Expectations() {{
            allowing(es).propertyFor(i);
            will(returnValue(is));
        }});

        ep = new EvacPotential(potential, i, checker);
        ep.setPropertyAccess(es);
    }
    
    @Test
    public void evacuationPotentialCorrectlySet() {
        context.checking(new Expectations() {{
            allowing(checker).canPass(i, individualCell, individualCell);
            will(returnValue(true));
            allowing(checker).canPass(i, individualCell, exitCell);
            will(returnValue(true));
        }});
        
        assertThat(ep.getPotential(individualCell), is(equalTo(NEW_POTENTIAL)));
        assertThat(ep.getPotential(exitCell), is(equalTo(NEW_POTENTIAL_EXIT)));
    }
    
    @Test
    public void evacuationPotentialIfUnpassable() {
        context.checking(new Expectations() {{
            allowing(checker).canPass(i, individualCell, individualCell);
            will(returnValue(true));
            allowing(checker).canPass(i, individualCell, exitCell);
            will(returnValue(false));
        }});
        
        assertThat(ep.getPotential(individualCell), is(equalTo(NEW_POTENTIAL)));
        assertThat(ep.getPotential(exitCell), is(equalTo(Integer.MAX_VALUE)));
    }
    
    @Test
    public void evacuationPotentialForUnknownCell() {
        RoomCell newCell = new RoomCell(1, 1);
        
        assertThat(ep.getPotential(newCell), is(equalTo(Integer.MAX_VALUE)));
    }
}
