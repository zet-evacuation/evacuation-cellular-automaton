package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.jmock.AbstractExpectations.returnValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.EvacuationCellularAutomatonBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MultiFloorEvacuationCellularAutomatonTest {

    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();
    private final Mockery context = new Mockery();

    @Test
    public void testInitialization() {
        MultiFloorEvacuationCellularAutomaton ca = new MultiFloorEvacuationCellularAutomaton();
        assertThat(ca.getCellCount(), is(equalTo(0)));
        assertThat(ca.getDimension(), is(equalTo(2)));
        assertThat(ca.getFloors(), is(empty()));
        assertThat(ca.getRooms(), is(empty()));
        assertThat(ca.graphicalToString(), is(equalTo("")));
    }

    @Test
    public void testSingleRoom() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor1");
        

        Room room = context.mock(Room.class, "room1");
        roomExpectations(room, 1, 3);
        Room room2 = context.mock(Room.class, "room2");
        roomExpectations(room2, 2, 2);

        builder.addRoom(0, room);
        MultiFloorEvacuationCellularAutomaton ca = builder.build();
        assertThat(ca.getCellCount(), is(equalTo(3)));

        builder.addRoom(0, room2);
        ca = builder.build();
        assertThat(ca.getCellCount(), is(equalTo(5)));
    }

    private void roomExpectations(Room room, int id, int cells) {
        context.checking(new Expectations() {
            {
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(cells));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getWidth();
                allowing(room).getHeight();
                allowing(room).getID();
                will(returnValue(id));
                allowing(room).getFloorID();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
            }
        });
    }
}
