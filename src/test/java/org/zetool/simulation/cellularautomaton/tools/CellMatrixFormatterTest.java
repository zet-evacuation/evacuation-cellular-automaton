/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zetool.simulation.cellularautomaton.tools;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zetool.common.util.Bounds;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellMatrix;
import org.zetool.simulation.cellularautomaton.FakeCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellMatrixFormatterTest {
    private final Mockery context = new Mockery();

    public static class MyMatrix implements CellMatrix<FakeCell> {

        private final int width;
        private final int height;
        private int undefinedX = -1;
        private int undefinedY = -1;

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
        public Collection<FakeCell> getAllCells() {
            throw new AssertionError("Not to be called!");
        }

        @Override
        public FakeCell getCell(int x, int y) {
            return x == undefinedX && y == undefinedY ? null : cell;
        }

        @Override
        public boolean existsCellAt(int x, int y) {
            throw new AssertionError("Not to be called!");
        }
    };

    static FakeCell cell = new FakeCell();

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
    public void testStatic() {
        MyMatrix matrix = new MyMatrix(1, 1);
        String result = CellMatrixFormatter.format(matrix);
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
        matrix.undefinedX = 1;
        matrix.undefinedY = 1;

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        String result = formatter.graphicalToString(matrix);
        List<String> lines = new LinkedList<>(Arrays.asList(result.split("\n")));
        assertThat(lines.size(), is(equalTo(5)));
        assertThat(lines, hasItem("┌───┬───┐"));
        assertThat(lines, hasItem("│   │   │"));
        assertThat(lines, hasItem("├───┼───┤"));
        assertThat(lines, hasItem("│   │ X │"));
        assertThat(lines, hasItem("└───┴───┘"));
    }
    
    @Test
    public void testStyleUsedComplete() {
        CellMatrixFormatterStyle s = context.mock(CellMatrixFormatterStyle.class);
        context.checking(new Expectations() {
            {
                atLeast(1).of(s).getCenter();
                will(returnValue('ö'));
                atLeast(1).of(s).getDelimiterBound(with(any(Direction8.class)));
                will(returnValue('ä'));
                atLeast(1).of(s).getBound(with(any(Bounds.class)));
                will(returnValue('ä'));
                atLeast(1).of(s).getGrid(with(any(Orientation.class)));
                will(returnValue('ä'));
                never(s).getUndefined();
                atLeast(1).of(s).getUndefined();
                will(returnValue('ä'));
            }
        });
        MyMatrix matrix = new MyMatrix(2, 2);
        matrix.undefinedX = 1;
        matrix.undefinedY = 1;

        CellMatrixFormatter formatter = new CellMatrixFormatter(s);
        formatter.graphicalToString(matrix);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testBasicStyle() {
        MyMatrix matrix = new MyMatrix(2, 2);
        matrix.undefinedX = 1;
        matrix.undefinedY = 1;

        CellMatrixFormatter formatter = new CellMatrixFormatter(new BasicCellMatrixFormatterStyle());
        String result = formatter.graphicalToString(matrix);
        List<String> lines = new LinkedList<>(Arrays.asList(result.split("\n")));
        assertThat(lines.size(), is(equalTo(5)));
        assertThat(lines, hasItem("+---+---+"));
        assertThat(lines, hasItem("|   |   |"));
        assertThat(lines, hasItem("+---+---+"));
        assertThat(lines, hasItem("|   | X |"));
        assertThat(lines, hasItem("+---+---+"));
    }
    
    @Test
    public void testCustomCellFormatter() {
        MyMatrix matrix = new MyMatrix(1, 1);
        CellFormatter customFormatter = context.mock(CellFormatter.class);
        context.checking(new Expectations() {
            {
                allowing(customFormatter).format(with(any(Cell.class)));
                will(returnValue("#0#"));
            }
        });
        CellMatrixFormatter formatter = new CellMatrixFormatter();
        formatter.registerFormatter(FakeCell.class, customFormatter);
        String result = formatter.graphicalToString(matrix);
        List<String> lines = Arrays.asList(result.split("\n"));
        assertThat(lines.size(), is(equalTo(3)));
        assertThat(lines, hasItem("┌───┐"));
        assertThat(lines, hasItem("│#0#│"));
        assertThat(lines, hasItem("└───┘"));
    }
    
}