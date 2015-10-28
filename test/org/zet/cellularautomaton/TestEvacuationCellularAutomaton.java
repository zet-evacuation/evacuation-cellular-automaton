package org.zet.cellularautomaton;

import java.math.BigDecimal;
import java.util.Collections;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationCellularAutomaton {
    private final Mockery context = new Mockery();

    @Test
    public void testInitialization() {
        EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton();
        assertThat(ca.getCellCount(), is(equalTo(0)));
        assertThat(ca.getDimension(), is(equalTo(2)));
        assertThat(ca.getFloors(), is(empty()));
        assertThat(ca.getIndividualCount(), is(equalTo(0)));
        assertThat(ca.getIndividuals(), is(empty()));
        assertThat(ca.getInitialIndividualCount(), is(equalTo(0)));
        assertThat(ca.getRooms(), is(empty()));
        assertThat(ca.getState(), is(equalTo(EvacuationCellularAutomaton.State.ready)));
        assertThat(ca.getTimeStep(), is(equalTo(0)));
        assertThat(ca.graphicalToString(), is(equalTo("")));
    }
    
    @Test
    public void testSingleRoom() {
        EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton();
        Room room = context.mock(Room.class);
        context.checking(new Expectations() {{
                //debugger throws error on the line below.
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(2));
                allowing(room).getID();
                will(returnValue(3));
                allowing(room).getFloorID();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
        }});        
        
        ca.addFloor("floor1");
        ca.addRoom(room);
        assertThat(ca.getCellCount(), is(equalTo(2)));
    }
}
