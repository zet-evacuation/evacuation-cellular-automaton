package org.zet.cellularautomaton.algorithm.state;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.jmock.AbstractExpectations.returnValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationStateController {

    private final static IndividualBuilder builder = new IndividualBuilder();
    private Mockery context = new Mockery();

    @Test
    public void deadIndividuals() {
        Individual alive = builder.build();
        Individual deadNotEnoughTime1 = builder.build();
        Individual deadNotEnoughTime2 = builder.build();
        Individual deadUnreachable = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(null,
                Arrays.asList(new Individual[]{alive, deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable}));

        Room room = createRoomWithExpectations(1, 4);
        es.propertyFor(alive).setCell(new RoomCell(1, 0, 1, room));
        
        EvacCell cellNotEnoughTime1 = new RoomCell(1, 0, 1, room);
        es.propertyFor(deadNotEnoughTime1).setCell(cellNotEnoughTime1);
        cellNotEnoughTime1.getState().setIndividual(deadNotEnoughTime1);
        
        EvacCell cellNotEnoughTime2 = new RoomCell(1, 0, 1, room);
        es.propertyFor(deadNotEnoughTime2).setCell(cellNotEnoughTime2);
        cellNotEnoughTime2.getState().setIndividual(deadNotEnoughTime2);
        
        EvacCell cellUnreachable = new RoomCell(1, 0, 1, room);
        es.propertyFor(deadUnreachable).setCell(cellUnreachable);
        cellUnreachable.getState().setIndividual(deadUnreachable);

        EvacuationStateController ec = new EvacuationStateController(es);
        
        context.checking(new Expectations() {{
            exactly(1).of(room).removeIndividual(with(deadNotEnoughTime1));
            exactly(1).of(room).removeIndividual(with(deadNotEnoughTime2));
            exactly(1).of(room).removeIndividual(with(deadUnreachable));
        }});
        
        ec.die(deadNotEnoughTime1, DeathCause.NOT_ENOUGH_TIME);
        ec.die(deadNotEnoughTime2, DeathCause.NOT_ENOUGH_TIME);
        ec.die(deadUnreachable, DeathCause.EXIT_UNREACHABLE);

        assertThat(cellNotEnoughTime1.getState().isEmpty(), is(true));
        assertThat(cellNotEnoughTime2.getState().isEmpty(), is(true));
        assertThat(cellUnreachable.getState().isEmpty(), is(true));
        
        assertThat(es.deadIndividualsCount(), is(equalTo(3)));
        assertThat(es.getRemainingIndividualCount(), is(equalTo(1)));
        assertThat(es.getDeadIndividuals(), contains(deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable));

        assertThat(es.getDeadIndividualCount(DeathCause.EXIT_UNREACHABLE), is(equalTo(1)));
        assertThat(es.getDeadIndividualCount(DeathCause.NOT_ENOUGH_TIME), is(equalTo(2)));
        assertThat(es.propertyFor(deadNotEnoughTime1).getDeathCause(), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(es.propertyFor(deadNotEnoughTime2).getDeathCause(), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(es.propertyFor(deadUnreachable).getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(es.propertyFor(alive).isDead(), is(equalTo(false)));
        assertThat(es.propertyFor(deadNotEnoughTime1).isDead(), is(equalTo(true)));
        assertThat(es.propertyFor(deadNotEnoughTime2).isDead(), is(equalTo(true)));
        assertThat(es.propertyFor(deadUnreachable).isDead(), is(equalTo(true)));
    }
    
    @Test
    public void testMoveIndividuals() {
        Individual moved = builder.build();
        Individual notToEvacuate = builder.build();
        
        MutableEvacuationState es = new MutableEvacuationState(null,
                Arrays.asList(new Individual[]{moved, notToEvacuate}));
        
        EvacuationStateController ec = new EvacuationStateController(es);

        Room room = createRoomWithExpectations(1, 2);
        EvacCell from = new RoomCell(1, 0, 1, room);
        es.propertyFor(moved).setCell(from);
        from.getState().setIndividual(moved);

        EvacCell to = new RoomCell(1, 1, 1, room);
        context.checking(new Expectations() {{
            exactly(1).of(room).removeIndividual(with(moved));
            exactly(1).of(room).addIndividual(to, moved);
        }});

        ec.move(from, to);
        assertThat(es.propertyFor(moved).getCell(), is(to));
        assertThat(to.getState().getIndividual(), is(moved));
        assertThat(from.getState().isEmpty(), is(true));
    }
    
    
    @Test
    public void remainingIndividuals() {
        Individual activeIndividual = builder.build();
        Individual evacuatedIndividual = builder.build();
        Individual saveIndividual = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(null,
                Arrays.asList(new Individual[]{activeIndividual, evacuatedIndividual, saveIndividual}));

        Room room = createRoomWithExpectations(1, 4);

        es.propertyFor(activeIndividual).setCell(new RoomCell(1, 0, 1, room));
        
        EvacCell cellEvacuated = new RoomCell(1, 0, 1, room);
        es.propertyFor(evacuatedIndividual).setCell(cellEvacuated);
        cellEvacuated.getState().setIndividual(evacuatedIndividual);
        
        EvacCell cellSave = new RoomCell(1, 0, 1, room);
        es.propertyFor(saveIndividual).setCell(cellSave);
        cellSave.getState().setIndividual(saveIndividual);

        EvacuationStateController ec = new EvacuationStateController(es);

        context.checking(new Expectations() {{
            exactly(1).of(room).removeIndividual(with(evacuatedIndividual));
        }});
        
        ec.setSafe(saveIndividual);
        assertThat(es.getRemainingIndividualCount(), is(equalTo(3)));
        assertThat(es.propertyFor(saveIndividual).isSafe(), is(true));
        assertThat(cellSave.getState().isEmpty(), is(false));
        assertThat(cellSave.getState().getIndividual(), is(saveIndividual));
        
        ec.evacuate(evacuatedIndividual);
        assertThat(es.getRemainingIndividualCount(), is(equalTo(2)));
        assertThat(es.propertyFor(evacuatedIndividual).isEvacuated(), is(true));
        assertThat(es.evacuatedIndividualsCount(), is(equalTo(1)));
        assertThat(es.getEvacuatedIndividuals(), contains(evacuatedIndividual));
        assertThat(cellEvacuated.getState().isEmpty(), is(true));
   }
    
    private Room createRoomWithExpectations(int id, int cells) {
        Room room = context.mock(Room.class, "room " + id);
        context.checking(new Expectations() {{
                //debugger throws error on the line below.
                atLeast(1).of(room).getCellCount(false);
                will(returnValue(cells));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getWidth();
                allowing(room).getHeight();
                allowing(room).getID();
                will(returnValue(id));
                allowing(room).getFloor();
                will(returnValue(0));
                allowing(room).getAllCells();
                will(returnValue(Collections.EMPTY_LIST));
        }});
        return room;
    }

}
