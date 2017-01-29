package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacCellInterfaceTest {
    Mockery context = new Mockery();

    @Test
    public void absoluteCoordinates() {
        Room room = context.mock(Room.class);
        
        EvacCellInterface ec1 = new DefaultEvacCellImplementation(room, 1, 2);
        
        context.checking(new Expectations() {
            {
                allowing(room).getXOffset();
                will(returnValue(3));
                allowing(room).getYOffset();
                will(returnValue(4));
            }
        });
        
        assertThat(ec1.getAbsoluteX(), is(equalTo(4)));
        assertThat(ec1.getAbsoluteY(), is(equalTo(6)));
        
    }
    
    /**
     * Default implementation that only supports methods used by default implementations.
     */
    private static class DefaultEvacCellImplementation implements EvacCellInterface {
        private final Room room;
        private final int x;
        private final int y;

        public DefaultEvacCellImplementation(Room room, int x, int y) {
            this.room = room;
            this.x = x;
            this.y = y;
        }
        
        @Override
        public Collection<EvacCellInterface> getDirectNeighbors() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public List<EvacCellInterface> getFreeNeighbours() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public Level getLevel(Direction8 direction) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public EvacCell getNeighbor(Direction8 dir) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public List<EvacCellInterface> getNeighbours() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public double getOccupiedUntil() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public Direction8 getRelative(EvacCellInterface c) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public Room getRoom() {
            return room;
        }

        @Override
        public double getSpeedFactor() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public boolean isOccupied() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public void setOccupiedUntil(double occupiedUntil) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public boolean isOccupied(double time) {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public EvacuationCellState getState() {
            throw new UnsupportedOperationException("Not supported for test.");
        }

        @Override
        public int getSides() {
            throw new UnsupportedOperationException("Not supported for test.");
        }
        
    }
}
