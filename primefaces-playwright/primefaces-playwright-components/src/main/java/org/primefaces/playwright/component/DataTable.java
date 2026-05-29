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
package org.primefaces.playwright.component;

import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractTable;
import org.primefaces.playwright.component.model.datatable.Cell;
import org.primefaces.playwright.component.model.datatable.Row;

import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;

public abstract class DataTable extends AbstractTable<Row> {

    public List<Row> getRows() {
        return getRowsWebElement().stream()
                .filter(rowElt -> !PrimePlaywright.hasCssClass(rowElt, "ui-datatable-empty-message")
                        && !PrimePlaywright.hasCssClass(rowElt, "ui-expanded-row-content"))
                .map(rowElt -> {
                    List<Cell> cells = rowElt.locator("td").all().stream().map(cellElt -> new Cell(cellElt)).collect(Collectors.toList());
                    return new Row(rowElt, cells);
                }).collect(Collectors.toList());
    }

    public Locator getExpandedRow(int index) {
        return getExpandedRows().get(index);
    }

    public List<Locator> getExpandedRows() {
        return getRowsWebElement().stream()
                .filter(rowElt -> PrimePlaywright.hasCssClass(rowElt, "ui-expanded-row-content"))
                .collect(Collectors.toList());
    }

    @Override
    public Row getRow(int index) {
        return getRows().get(index);
    }
}
