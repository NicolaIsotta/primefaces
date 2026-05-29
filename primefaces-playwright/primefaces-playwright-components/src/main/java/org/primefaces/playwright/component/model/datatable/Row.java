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
package org.primefaces.playwright.component.model.datatable;

import org.primefaces.playwright.PrimePlaywright;

import java.util.List;

import com.microsoft.playwright.Locator;

public class Row {

    private Locator locator;
    private List<Cell> cells;

    public Row(Locator locator, List<Cell> cells) {
        this.locator = locator;
        this.cells = cells;
    }

    public boolean isToggleable() {
        return getToggler().count() > 0;
    }

    public Locator getToggler() {
        return locator.locator(".ui-row-toggler");
    }

    public void toggle() {
        if (isToggleable()) {
            PrimePlaywright.guardAjax(getToggler()).click();
        }
    }

    public boolean isExpanded() {
        return Boolean.parseBoolean(getToggler().getAttribute("aria-expanded"));
    }

    public void expand() {
        if (!isExpanded()) {
            toggle();
        }
    }

    public void collapse() {
        if (isExpanded()) {
            toggle();
        }
    }

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }
}
