package org.zet.algo.ca.util;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialUtilsTest {
        private final Mockery context = new Mockery();

    @Test
    public void minimumPotentialEmpty() {
        StaticPotential sp = PotentialUtils.mergePotentials(Collections.emptyList());
        assertThat(sp, is(notNullValue()));
    }
    
    @Test
    public void testMinimumPotential() {
        StaticPotential sp1 = new StaticPotential();
        StaticPotential sp2 = new StaticPotential();
        
        EvacCellInterface cell1 = context.mock(EvacCellInterface.class, "cell1");
        EvacCellInterface cell2 = context.mock(EvacCellInterface.class, "cell2");
        EvacCellInterface cell3 = context.mock(EvacCellInterface.class, "cell3");
        
        List<StaticPotential> potentials = new LinkedList<>();
        
        sp1.setPotential(cell1, 1);
        sp1.setPotential(cell2, 2);
        sp1.setPotential(cell3, 3);
        potentials.add(sp1);
        
        sp2.setPotential(cell1, 3);
        sp2.setPotential(cell2, 2);
        sp2.setPotential(cell3, 1);
        potentials.add(sp2);
        
        // Expectations
        context.checking(new Expectations() {
            {
                allowing(cell1).getX();
                allowing(cell2).getX();
                allowing(cell3).getX();
                allowing(cell1).getY(); will(returnValue(1));
                allowing(cell2).getY(); will(returnValue(2));
                allowing(cell3).getY(); will(returnValue(3));
            }
        });
        
        StaticPotential minimum = PotentialUtils.mergePotentials(potentials);
        
        assertThat(minimum.getPotential(cell1), is(equalTo(1)));
        assertThat(minimum.getPotential(cell2), is(equalTo(2)));
        assertThat(minimum.getPotential(cell3), is(equalTo(1)));
    }

    static EvacCell getCell() {
        return new EvacCell(new EvacuationCellState(null), 1, 0, 0) {

            @Override
            public EvacCell clone() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };        
    }

}
