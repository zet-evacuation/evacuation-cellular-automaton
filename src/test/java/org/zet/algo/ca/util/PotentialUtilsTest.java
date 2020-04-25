package org.zet.algo.ca.util;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.jmock.AbstractExpectations.returnValue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialUtilsTest {
        private final Mockery context = new Mockery();

    @Test
    public void minimumPotentialEmpty() {
        Potential sp = PotentialUtils.mergePotentials(Collections.emptyList());
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
        
        Potential minimum = PotentialUtils.mergePotentials(potentials);
        
        assertThat(minimum.getPotential(cell1), is(equalTo(1)));
        assertThat(minimum.getPotential(cell2), is(equalTo(2)));
        assertThat(minimum.getPotential(cell3), is(equalTo(1)));
    }

    @Test
    public void negativeIsIgnored() {
        StaticPotential sp1 = new StaticPotential();
        StaticPotential sp2 = new StaticPotential();

        EvacCellInterface cell1 = context.mock(EvacCellInterface.class, "cell1");
        EvacCellInterface cell2 = context.mock(EvacCellInterface.class, "cell2");

        List<StaticPotential> potentials = new LinkedList<>();

        sp1.setPotential(cell1, 1);
        sp1.setPotential(cell2, -1);
        potentials.add(sp1);

        sp2.setPotential(cell1, -1);
        sp2.setPotential(cell2, 2);
        potentials.add(sp2);

        // Expectations
        context.checking(new Expectations() {
            {
                allowing(cell1).getX();
                allowing(cell2).getX();
                allowing(cell1).getY();
                will(returnValue(1));
                allowing(cell2).getY();
                will(returnValue(2));
            }
        });

        Potential minimum = PotentialUtils.mergePotentials(potentials);

        assertThat(minimum.getPotential(cell1), is(equalTo(1)));
        assertThat(minimum.getPotential(cell2), is(equalTo(2)));
    }

    
    @Test
    public void associatedExitCellsMerged() {
        StaticPotential sp1 = new StaticPotential();
        StaticPotential sp2 = new StaticPotential();

        ExitCell cell1 = new ExitCell(0, 1);
        ExitCell cell2 = new ExitCell(0, 2);
        EvacCellInterface cell3 = context.mock(EvacCellInterface.class, "standard cell");

        List<StaticPotential> potentials = new LinkedList<>();

        sp1.setPotential(cell1, 1);
        sp1.setPotential(cell2, 2);
        sp1.setPotential(cell3, 3);
        sp1.getAssociatedExitCells().add(cell1);
        potentials.add(sp1);

        sp2.setPotential(cell1, -1);
        sp2.setPotential(cell2, 2);
        sp2.setPotential(cell3, 2);
        sp2.getAssociatedExitCells().add(cell2);
        potentials.add(sp2);

        // Expectations
        context.checking(new Expectations() {
            {
                allowing(cell3).getX();
                allowing(cell3).getY();
            }
        });

        StaticPotential minimum = (StaticPotential)PotentialUtils.mergePotentials(potentials);

        assertThat(minimum.getAssociatedExitCells(), containsInAnyOrder(new ExitCell[] {cell1, cell2}));
    }
}
