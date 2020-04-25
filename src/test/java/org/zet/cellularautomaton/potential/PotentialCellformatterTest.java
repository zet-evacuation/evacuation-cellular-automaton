package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.RoomCell;
import org.zetool.simulation.cellularautomaton.CellMatrix;
import org.zetool.simulation.cellularautomaton.tools.CellMatrixFormatter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialCellformatterTest {

    public static class MyMatrix implements CellMatrix<RoomCell> {

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 1;
        }

        @Override
        public Collection<RoomCell> getAllCells() {
            throw new AssertionError("Not to be called!");
        }

        @Override
        public RoomCell getCell(int x, int y) {
            return cell;
        }

        @Override
        public boolean existsCellAt(int x, int y) {
            throw new AssertionError("Not to be called!");
        }
    };

    static RoomCell cell = new RoomCell(0, 0);

    MyMatrix matrix;
    DynamicPotential d;
    PotentialCellFormatter dynamicFormatter;
    CellMatrixFormatter<RoomCell> formatter;

    @Before
    public void init() {
        matrix = new MyMatrix();
        d = new DynamicPotential();
        dynamicFormatter = new PotentialCellFormatter(d);
        formatter = new CellMatrixFormatter<>();
        formatter.registerFormatter(RoomCell.class, dynamicFormatter);
    }

    @Test
    public void testFormatterSingleUndefined() {
        formatter.registerFormatter(RoomCell.class, dynamicFormatter);
        String result = formatter.graphicalToString(matrix);
        assertBoxContenet(result, " 0 ");
    }

    @Test
    public void testFormatterSingleSmall() {
        d.setPotential(cell, 8);
        String result = formatter.graphicalToString(matrix);
        assertBoxContenet(result, " 8 ");
    }

    @Test
    public void testFormatterSingleMedium() {
        d.setPotential(cell, 83);
        String result = formatter.graphicalToString(matrix);
        assertBoxContenet(result, " 83");
    }

    @Test
    public void testFormatterSingleLarge() {
        d.setPotential(cell, 483);
        String result = formatter.graphicalToString(matrix);
        assertBoxContenet(result, "483");
    }

    @Test
    public void testFormatterSingleHuge() {
        d.setPotential(cell, 4831);
        String result = formatter.graphicalToString(matrix);
        assertBoxContenet(result, "###");
    }

    private void assertBoxContenet(String result, String content) {
        List<String> lines = Arrays.asList(result.split("\n"));
        assertThat(lines.size(), is(equalTo(3)));
        assertThat(lines, hasItem("┌───┐"));
        assertThat(lines, hasItem("│" + content + "│"));
        assertThat(lines, hasItem("└───┘"));
    }

}
