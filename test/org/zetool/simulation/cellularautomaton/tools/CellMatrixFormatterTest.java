package org.zetool.simulation.cellularautomaton.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellMatrixFormatterTest {

    private static class MyCell implements Cell<MyCell, Void> {

        @Override
        public Collection<MyCell> getDirectNeighbors() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public int getSides() {
            return 4;
        }

        @Override
        public Iterator<MyCell> iterator() {
            return getDirectNeighbors().iterator();
        }
    };

    private static class MyMatrix implements CellMatrix<CellMatrixFormatterTest.MyCell, Void> {

        private final int width;
        private final int height;

        public MyMatrix(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public Collection<MyCell> getAllCells() {
            throw new AssertionError("Not to be called!");
        }

        @Override
        public MyCell getCell(int x, int y) {
            return cell;
        }

        @Override
        public boolean existsCellAt(int x, int y) {
            throw new AssertionError("Not to be called!");
        }
    };

    static MyCell cell = new MyCell();

    @Test
    public void testFormatterSingle() {
        MyMatrix matrix = new MyMatrix(1, 1);

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        String result = formatter.graphicalToString(matrix);
        List<String> lines = Arrays.asList(result.split("\n"));
        assertThat(lines.size(), is(equalTo(3)));
        assertThat(lines, hasItem("┌───┐"));
        assertThat(lines, hasItem("│   │"));
        assertThat(lines, hasItem("└───┘"));
    }

    @Test
    public void testFormatterTwoHorizontal() {
        MyMatrix matrix = new MyMatrix(2, 1);

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        String result = formatter.graphicalToString(matrix);
        List<String> lines = Arrays.asList(result.split("\n"));
        assertThat(lines.size(), is(equalTo(3)));
        assertThat(lines, hasItem("┌───┬───┐"));
        assertThat(lines, hasItem("│   │   │"));
        assertThat(lines, hasItem("└───┴───┘"));
    }

    @Test
    public void testFormatterTwoVertical() {
        MyMatrix matrix = new MyMatrix(1, 2);

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        String result = formatter.graphicalToString(matrix);
        List<String> lines = new LinkedList<>(Arrays.asList(result.split("\n")));
        assertThat(lines.size(), is(equalTo(5)));
        assertThat(lines, hasItem("┌───┐"));
        assertThat(lines, hasItem("│   │"));
        assertThat(lines, hasItem("├───┤"));
        assertThat(lines, hasItem("│   │"));
        assertThat(lines, hasItem("└───┘"));
    }

    @Test
    public void testFormatterAllCases() {
        MyMatrix matrix = new MyMatrix(2, 2);

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        String result = formatter.graphicalToString(matrix);
        List<String> lines = new LinkedList<>(Arrays.asList(result.split("\n")));
        assertThat(lines.size(), is(equalTo(5)));
        assertThat(lines, hasItem("┌───┬───┐"));
        assertThat(lines, hasItem("│   │   │"));
        assertThat(lines, hasItem("├───┼───┤"));
        assertThat(lines, hasItem("│   │   │"));
        assertThat(lines, hasItem("└───┴───┘"));
    }

}
