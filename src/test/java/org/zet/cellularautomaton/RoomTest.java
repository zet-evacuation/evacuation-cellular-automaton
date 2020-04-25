package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RoomTest {

    private Mockery context;

    @Before
    public void init() {
        context = new Mockery();
    }

    @Test
    public void initRoom() {
        Room room = new RoomImpl(4, 5, 3, 12, -3, 1);
        assertThat(room.getID(), is(equalTo(12)));
        assertThat(room.getFloor(), is(equalTo(3)));
        assertThat(room.getXOffset(), is(equalTo(-3)));
        assertThat(room.getYOffset(), is(equalTo(1)));
        assertThat(room.getWidth(), is(equalTo(4)));
        assertThat(room.getHeight(), is(equalTo(5)));
    }

    @Test
    public void emptyRoomCellCount() {
        Room room = new RoomImpl(3, 1, 0, 0, 0);
        assertThat(room.getCellCount(true), is(equalTo(3)));
        assertThat(room.getCellCount(false), is(equalTo(0)));
    }

    @Test
    public void roomCellCount() {
        RoomImpl room = new RoomImpl(3, 1, 0, 0, 0);
        EvacuationCellState state = new EvacuationCellState(null);
        EvacCell ec = new EvacCell(state, 1, 1, 0) {

            @Override
            public EvacCell clone() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        room.setCell(ec);
        assertThat(room.getCellCount(true), is(equalTo(3)));
        assertThat(room.getCellCount(false), is(equalTo(1)));
    }

    @Test
    public void properties() {
        RoomImpl room = new RoomImpl(3, 1, 0, 0, 0);
        assertThat(room.isAlarmed(), is(false));
        room.setAlarmstatus(true);
        assertThat(room.isAlarmed(), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void addIndividualFromDifferentRoom() {
        RoomImpl room = new RoomImpl(2, 1, 0, 0, 0);

        EvacCellInterface cell = context.mock(EvacCellInterface.class);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        context.checking(new Expectations() {
            {
                allowing(cell).getRoom();
                will(returnValue(context.mock(Room.class)));
            }
        });

        room.addIndividual(cell, i);
    }

    @Test
    public void addIndividual() {
        RoomImpl room = new RoomImpl(2, 1, 0, 0, 0);

        EvacCellInterface cell = context.mock(EvacCellInterface.class);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        context.checking(new Expectations() {
            {
                allowing(cell).getRoom();
                will(returnValue(room));
            }
        });

        room.addIndividual(cell, i);

        assertThat(room.getIndividuals(), contains(i));
    }

    @Test(expected = IllegalStateException.class)
    public void addIndividualTwiceFails() {
        RoomImpl room = new RoomImpl(2, 1, 0, 0, 0);

        EvacCellInterface cell = context.mock(EvacCellInterface.class);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        context.checking(new Expectations() {
            {
                allowing(cell).getRoom();
                will(returnValue(room));
            }
        });

        room.addIndividual(cell, i);
        room.addIndividual(cell, i);
    }
    
    @Test
    public void equalsSelf() {
        RoomImpl r = new RoomImpl(0, 0, 0, 0, 0);
        assertThat(r.equals(r), is(true));
        assertThat(r.equals(null), is(false));
    }

}
