package org.zet.cellularautomaton;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.EvacuationCellularAutomatonBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonBuilderTest {

    private final Mockery context = new Mockery();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void doubleFloorInsertionFails() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();
        builder.addFloor(0, "some floor");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("some floor"));
        thrown.expectMessage(containsString("0"));
        builder.addFloor(0, "another");
    }

    @Test
    public void addWithoutFloorFails() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();
        builder.addFloor(0, "floor");

        Room room = context.mock(Room.class);
        roomExpectations(room, 1, 1);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("69"));
        builder.addRoom(69, room);
    }

    @Test
    public void addRooms() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();
        builder.addFloor(0, "floor");

        Room r1 = getRoom(0, 0, 3, 1, "above");
        Room r2 = getRoom(1, 1, 4, 1, "below");
        Room r3 = getRoom(5, -2, 1, 6, "vertical right");
        Room r4 = getRoom(3, -1, 2, 2, "fill");

        builder.addRoom(0, r1);
        builder.addRoom(0, r2);
        builder.addRoom(0, r3);
        builder.addRoom(0, r4);
    }

    @Test
    public void overlapsRooms() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();
        builder.addFloor(0, "floor");

        Room r1 = getRoom(3, 4, 2, 6, "room");
        Room r2 = getRoom(1, 2, 3, 3, "intersectTopCorner");
        Room r3 = getRoom(4, 3, 2, 2, "intersectBottomCorner");
        Room r4 = getRoom(2, 7, 2, 4, "intersectRightCorner");
        Room r5 = getRoom(4, 9, 1, 1, "intersectLeftCorner");
        Room r6 = getRoom(3, 6, 2, 2, "in");
        Room r7 = getRoom(3, 4, 2, 6, "same");

        List<Room> rooms = Arrays.asList(r2, r3, r4, r5, r6, r7);

        builder.addRoom(0, r1);
        for (Room r : rooms) {
            try {
                builder.addRoom(0, r2);
            } catch (IllegalArgumentException ex) {
                // expected
                continue;
            }
            throw new AssertionError("Room: " + r + " should fail");
        }

    }

    private Room getRoom(int x, int y, int width, int height, String name) {
        Room r = context.mock(Room.class, name);
        context.checking(new Expectations() {
            {
                allowing(r).getXOffset();
                will(returnValue(x));
                allowing(r).getYOffset();
                will(returnValue(y));
                allowing(r).getWidth();
                will(returnValue(width));
                allowing(r).getHeight();
                will(returnValue(height));
                allowing(r).getCellCount(false);
                will(returnValue(width * height));
                allowing(r).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
            }
        });
        return r;
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyRoom() {
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();
        builder.addFloor(0, "floor");

        Room r = getRoom(0, 0, 0, 0, "empty room");

        builder.addRoom(0, r);
    }

    @Test
    public void roomWithExitCellsNotExisting() {
        RoomImpl r = new RoomImpl(2, 2, 0, 0, 0);
        r.setCell(new RoomCell(0, 0));
        r.setCell(new RoomCell(0, 1));
        r.setCell(new RoomCell(1, 1));
        // Missing: Cell at 1, 0
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor");

        Collection<ExitCell> exitCluster = Collections.singleton(new ExitCell(1, 0));
        Exit e = new Exit(exitCluster);

        thrown.expect(IllegalArgumentException.class);
        builder.addRoom(0, r, Collections.singletonList(e));
    }

    @Test
    public void roomWithExitCellsNotEqual() {
        RoomImpl r = new RoomImpl(2, 2, 0, 0, 0);
        r.setCell(new RoomCell(0, 0));
        r.setCell(new RoomCell(0, 1));
        r.setCell(new RoomCell(1, 1));
        // Missing: Cell at 1, 0
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor");

        Collection<ExitCell> exitCluster = Collections.singleton(new ExitCell(0, 0));
        Exit e = new Exit(exitCluster);

        thrown.expect(IllegalArgumentException.class);
        builder.addRoom(0, r, Collections.singletonList(e));
    }

    @Test
    public void roomWithExit() {
        RoomImpl r = new RoomImpl(2, 2, 0, 0, 0);
        r.setCell(new RoomCell(0, 0));
        r.setCell(new RoomCell(0, 1));
        r.setCell(new ExitCell(1, 1));
        // Missing: Cell at 1, 0
        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor");

        Collection<ExitCell> exitCluster = Collections.singleton((ExitCell) r.getCell(1, 1));
        Exit e = new Exit(exitCluster);

        builder.addRoom(0, r, Collections.singletonList(e));
    }

    @Test
    public void roomWithTwoExits() {
        RoomImpl r = new RoomImpl(4, 4, 0, 0, 0);
        r.setCell(new ExitCell(0, 0)); // A single cell exit
        r.setCell(new RoomCell(0, 1));
        r.setCell(new RoomCell(0, 2));
        r.setCell(new RoomCell(0, 3));
        r.setCell(new RoomCell(1, 0));
        r.setCell(new RoomCell(1, 1));
        r.setCell(new RoomCell(1, 2));
        r.setCell(new RoomCell(1, 3));
        r.setCell(new RoomCell(2, 0));
        r.setCell(new RoomCell(2, 1));
        r.setCell(new ExitCell(2, 2)); // A 4 cell exit
        r.setCell(new ExitCell(2, 3)); // A 4 cell exit
        r.setCell(new RoomCell(3, 0));
        r.setCell(new RoomCell(3, 1));
        r.setCell(new ExitCell(3, 2)); // A 4 cell exit
        r.setCell(new ExitCell(3, 3)); // A 4 cell exit

        EvacuationCellularAutomatonBuilder builder = new EvacuationCellularAutomatonBuilder();

        builder.addFloor(0, "floor");

        Collection<Exit> newExits = builder.addRoom(0, r);
        assertThat(newExits, hasSize(2));
        Iterator<Exit> ei = newExits.iterator();
        Exit e1 = ei.next();
        Exit smallExit;
        Exit bigExit;
        if (e1.getExitCluster().size() == 1) {
            smallExit = e1;
            bigExit = ei.next();
        } else {
            smallExit = ei.next();
            bigExit = e1;
        }
        assertThat(smallExit.getExitCluster(), containsInAnyOrder(
                r.getCell(0, 0)
        ));
        assertThat(bigExit.getExitCluster(), containsInAnyOrder(
                r.getCell(2, 2),
                r.getCell(2, 3),
                r.getCell(3, 2),
                r.getCell(3, 3)
        ));
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
