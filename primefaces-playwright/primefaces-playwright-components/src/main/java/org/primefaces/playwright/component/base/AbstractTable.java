/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.playwright.component.base;

import org.primefaces.playwright.By;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.model.datatable.Body;
import org.primefaces.playwright.component.model.datatable.Cell;
import org.primefaces.playwright.component.model.datatable.Header;
import org.primefaces.playwright.component.model.datatable.HeaderCell;
import org.primefaces.playwright.component.model.datatable.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;

public abstract class AbstractTable<T extends Row> extends AbstractPageableData {

    @Override
    public List<Locator> getRowsWebElement() {
        return findElement(By.tagName("tbody")).locator("tr").all();
    }

    public abstract T getRow(int index);

    /**
     * Gets the Cell at the row/column coordinates.
     *
     * @param rowIndex the index of the row
     * @param colIndex the index of the column in the row
     * @return the {@link Cell} representing these coordinates
     * @throws IndexOutOfBoundsException if either row or column not found
     */
    public Cell getCell(int rowIndex, int colIndex) throws IndexOutOfBoundsException {
        Row row = getRow(rowIndex);
        if (row == null) {
            throw new IndexOutOfBoundsException("Row " + rowIndex + " was not found in table");
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            throw new IndexOutOfBoundsException("Column " + colIndex + " was not found in Row " + rowIndex + ".");
        }
        return cell;
    }

    public Locator getHeaderWebElement() {
        return findElement(By.tagName("table")).locator("thead");
    }

    public Header getHeader() {
        List<HeaderCell> cells = getHeaderWebElement().locator("th").all().stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());
        return new Header(getHeaderWebElement(), cells);
    }

    /**
     * Sorts the column found by its header text. It toggles to the next sort direction.
     *
     * @param headerText the header text to look for
     */
    public void sort(String headerText) {
        Optional<HeaderCell> cell = getHeader().getCell(headerText);
        if (cell.isPresent()) {
            PrimePlaywright.guardAjax(cell.get().getLocator().locator(".ui-sortable-column-icon")).click();
        }
        else {
            System.err.println("Header Cell '" + headerText + "' not found.");
        }
    }

    /**
     * Sorts the column found by its index. It toggles to the next sort direction.
     *
     * @param index the index of the column
     */
    public void sort(int index) {
        HeaderCell cell = getHeader().getCell(index);
        if (cell != null) {
            PrimePlaywright.guardAjax(cell.getLocator().locator(".ui-sortable-column-icon")).click();
        }
        else {
            System.err.println("Header Cell '" + index + "' not found.");
        }
    }

    /**
     * Filter the column by its index.
     *
     * @param cellIndex the index of the column
     * @param filterValue the value to pass to the filter
     */
    public void filter(int cellIndex, String filterValue) {
        filter(getHeader().getCell(cellIndex), filterValue);
    }

    /**
     * Filter the column by its header text.
     *
     * @param headerText the header text to look for
     * @param filterValue the value to pass to the filter
     */
    public void filter(String headerText, String filterValue) {
        Optional<HeaderCell> cell = getHeader().getCell(headerText);
        if (cell.isPresent()) {
            filter(cell.get(), filterValue);
        }
        else {
            System.err.println("Header Cell '" + headerText + "' not found.");
        }
    }

    /**
     * Removes the current filter at this column index.
     *
     * @param cellIndex the index of the column
     */
    public void removeFilter(int cellIndex) {
        filter(cellIndex, null);
    }

    /**
     * Removes the current filter for a column with the header text
     *
     * @param headerText the header text to look for
     */
    public void removeFilter(String headerText) {
        filter(headerText, null);
    }

    /**
     * Filter using the widget configuration for "filterDelay" and "filterEvent".
     *
     * @param cell the cell to filter
     * @param filterValue the value to pass to the filter.
     */
    private void filter(HeaderCell cell, String filterValue) {
        JSONObject cfg = getWidgetConfiguration();
        if (filterValue != null && filterValue.isEmpty()) {
            filterValue = null;
        }
        cell.setFilterValue(cfg, filterValue);
    }

    /**
     * If using multiple checkbox mode this toggles the Select All checkbox in the header.
     */
    public void toggleSelectAllCheckBox() {
        Locator checkboxAll = getSelectAllCheckBox();
        if (ComponentUtils.hasBehavior(getWrappedLocator(), "rowSelect") || ComponentUtils.hasBehavior(getWrappedLocator(), "rowUnselect")) {
            PrimePlaywright.guardAjax(checkboxAll).click();
        }
        else {
            checkboxAll.click();
        }
    }

    /**
     * Gets the Select All checkbox in the header of the table.
     *
     * @return the Locator representing the checkbox
     */
    public Locator getSelectAllCheckBox() {
        return getHeader().getCell(0).getLocator();
    }

    public Locator getFrozenHeaderWebElement() {
        return findElement(By.className("ui-datatable-frozenlayout-left")).locator("thead");
    }

    public Header getFrozenHeader() {
        List<HeaderCell> frozenHeaderCells = getFrozenHeaderWebElement().locator("th").all().stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());

        return new Header(getFrozenHeaderWebElement(), frozenHeaderCells);
    }

    public Locator getScrollableHeaderWebElement() {
        return findElement(By.className("ui-datatable-frozenlayout-right")).locator("thead");
    }

    public Header getScrollableHeader() {
        List<HeaderCell> scrollableHeaderCells = getScrollableHeaderWebElement().locator("th").all().stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());

        return new Header(getScrollableHeaderWebElement(), scrollableHeaderCells);
    }

    public Locator getFrozenBodyWebElement() {
        return findElement(By.className("ui-datatable-frozenlayout-left")).locator("tbody");
    }

    public Locator getScrollableBodyWebElement() {
        return findElement(By.className("ui-datatable-frozenlayout-right")).locator("tbody");
    }

    public Body getFrozenBody() {
        List<Row> rows = new ArrayList<>();
        List<Locator> frozenBodyRows = getFrozenBodyWebElement().locator("tr").all();

        frozenBodyRows.forEach(row -> {
            List<Locator> rowCellsTmp = row.locator("td").all();
            List<Cell> rowCells = rowCellsTmp.stream()
                    .map(Cell::new)
                    .collect(Collectors.toList());
            rows.add(new Row(row, rowCells));
        });

        return new Body(getFrozenBodyWebElement(), rows);
    }

    public Body getScrollableBody() {
        List<Row> rows = new ArrayList<>();
        List<Locator> scrollableBodyRows = getScrollableBodyWebElement().locator("tr").all();

        scrollableBodyRows.forEach(row -> {
            List<Locator> rowCellsTmp = row.locator("td").all();
            List<Cell> rowCells = rowCellsTmp.stream()
                    .map(Cell::new)
                    .collect(Collectors.toList());
            rows.add(new Row(row, rowCells));
        });

        return new Body(getScrollableBodyWebElement(), rows);
    }
}
