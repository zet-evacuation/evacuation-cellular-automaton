package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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

}
