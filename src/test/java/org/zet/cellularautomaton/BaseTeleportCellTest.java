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
package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.zet.cellularautomaton.algorithm.TestEvacuationCellularAutomatonAlgorithm.MockEvacCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BaseTeleportCellTest {
    
    private static class FakeBaseTeleportCell extends BaseTeleportCell<MockEvacCell> {
        List<MockEvacCell> removed = new LinkedList<>();
        public FakeBaseTeleportCell() {
            super(new EvacuationCellState(null), 1, 0, 0);
        }

        @Override
        public void addTarget(MockEvacCell target) {
            
        }

        @Override
        public void removeTarget(MockEvacCell target) {
            super.teleportTargets.remove(target);
            removed.add(target);
        }

        @Override
        public EvacCell clone() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        
    }
    
    @Test
    public void initialization() {
        BaseTeleportCell cell = new FakeBaseTeleportCell();
        assertThat(cell.targetCount(), is(equalTo(0)));
    }
    
    @Test
    public void removeAllRemovesAll() {
        FakeBaseTeleportCell cell = new FakeBaseTeleportCell();
        MockEvacCell cell1 = new MockEvacCell(0, 0);
        MockEvacCell cell2 = new MockEvacCell(0, 1);
        MockEvacCell cell3 = new MockEvacCell(1, 1);
        cell.addTargetSimple(cell1);
        cell.addTargetSimple(cell2);
        cell.addTargetSimple(cell3);
        
        cell.removeAllTargets();
        
        assertThat(cell.removed, contains(new MockEvacCell[] {cell1, cell2, cell3}));
    }
    
    @Test
    public void sizeCalculation() {
        FakeBaseTeleportCell cell = new FakeBaseTeleportCell();
        MockEvacCell cell1 = new MockEvacCell(0, 0);
        MockEvacCell cell2 = new MockEvacCell(0, 1);
        MockEvacCell cell3 = new MockEvacCell(1, 1);
        cell.addTargetSimple(cell1);
        cell.addTargetSimple(cell2);
        
        assertThat(cell.targetCount(), is(equalTo(2)));
        assertThat(cell.containsTarget(cell1), is(true));
        assertThat(cell.containsTarget(cell2), is(true));
        assertThat(cell.containsTarget(cell3), is(false));
        
        cell.removeTarget(cell2);
        assertThat(cell.containsTarget(cell2), is(false));
        assertThat(cell.targetCount(), is(equalTo(1)));        
    }
    
    @Test
    public void noDoubleInsert() {
        FakeBaseTeleportCell cell = new FakeBaseTeleportCell();
        MockEvacCell cell1 = new MockEvacCell(0, 0);

        assertThat(cell.targetCount(), is(equalTo(0)));
        cell.addTargetSimple(cell1);
        assertThat(cell.targetCount(), is(equalTo(1)));        
        cell.addTargetSimple(cell1);
        assertThat(cell.targetCount(), is(equalTo(1)));        
        
    }
    
    @Test
    public void getByIndex() {
        FakeBaseTeleportCell cell = new FakeBaseTeleportCell();
        MockEvacCell cell1 = new MockEvacCell(0, 0);
        MockEvacCell cell2 = new MockEvacCell(0, 1);
        MockEvacCell cell3 = new MockEvacCell(1, 1);
        cell.addTargetSimple(cell1);
        cell.addTargetSimple(cell2);
        cell.addTargetSimple(cell3);
        
        assertThat(cell.getTarget(0), is(equalTo(cell1)));
        assertThat(cell.getTarget(1), is(equalTo(cell2)));
        assertThat(cell.getTarget(2), is(equalTo(cell3)));
    }
    
}
