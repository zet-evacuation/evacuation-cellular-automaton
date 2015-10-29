package org.zet.cellularautomaton;

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
        ca.addFloor("floor1");

        Room room = context.mock(Room.class, "room1");
        roomExpectations(room, 1, 3);
        Room room2 = context.mock(Room.class, "room2");
        roomExpectations(room2, 2, 2);

        ca.addRoom(room);
        assertThat(ca.getCellCount(), is(equalTo(3)));
        
        ca.addRoom(room2);
        assertThat(ca.getCellCount(), is(equalTo(5)));
    }
    
    private void roomExpectations(Room room, int id, int cells) {
        context.checking(new Expectations() {{
                //debugger throws error on the line below.
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(cells));
                allowing(room).getID();
                will(returnValue(id));
                allowing(room).getFloorID();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
        }});
    }
}
