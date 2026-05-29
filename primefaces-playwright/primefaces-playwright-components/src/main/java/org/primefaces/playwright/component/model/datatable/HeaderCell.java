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

import org.primefaces.playwright.Keys;
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.ComponentUtils;

import java.util.Locale;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;

public class HeaderCell extends Cell {

    public HeaderCell(Locator locator) {
        super(locator);
    }

    /**
     * Gets the title element if it exists for this column.
     *
     * @return the Locator representing the title
     */
    public Locator getColumnTitle() {
        if (getLocator() != null) {
            return getLocator().locator(".ui-column-title");
        }

        return null;
    }

    /**
     * Gets the filter element if it exists for this column.
     *
     * @return the Locator representing the filter
     */
    public Locator getColumnFilter() {
        if (getLocator() != null) {
            return getLocator().locator(".ui-column-filter");
        }

        return null;
    }

    /**
     * Filter using the Widget configuration "filterDelay" and "filterEvent" values.
     *
     * @param cfg the widget configuration JSON object
     * @param filterValue the value to set the filter
     */
    public void setFilterValue(JSONObject cfg, String filterValue) {
        String filterEvent = cfg.has("filterEvent") ? cfg.getString("filterEvent") : "keyup";
        int filterDelay = cfg.has("filterDelay") ? cfg.getInt("filterDelay") : 0;
        setFilterValue(filterValue, filterEvent, filterDelay);
    }

    /**
     * Filter the column using these values.
     *
     * @param filterValue the value to filter for
     * @param filterEvent the event causing the filter to trigger such as "keyup" or "enter"
     * @param filterDelay the delay in milliseconds if a "keyup" filter
     */
    public void setFilterValue(String filterValue, String filterEvent, int filterDelay) {
        Locator columnFilter = getColumnFilter();

        if (columnFilter == null || columnFilter.count() == 0) {
            columnFilter = getLocator().locator("input");
        }

        columnFilter.clear();

        Keys triggerKey = null;
        filterEvent = filterEvent.toLowerCase(Locale.ROOT);
        switch (filterEvent) {
            case "keyup":
            case "keydown":
            case "keypress":
            case "input":
                if (filterDelay == 0) {
                    columnFilter = PrimePlaywright.guardAjax(columnFilter);
                }
                break;
            case "enter":
                triggerKey = Keys.ENTER;
                break;
            case "change":
            case "blur":
                triggerKey = Keys.TAB;
                break;
            default:
                break;
        }

        if (filterValue != null) {
            ComponentUtils.sendKeys(columnFilter, filterValue);
        }
        else {
            // null filter press backspace to trigger the re-filtering
            columnFilter.press("Backspace");
        }

        if (triggerKey != null) {
            PrimePlaywright.guardAjax(columnFilter).press(triggerKey.toString());
        }
        else if (filterDelay > 0) {
            try {
                // default-filter runs delayed - so wait...
                Thread.sleep(filterDelay * 2);
            }
            catch (InterruptedException ex) {
                System.err.println("AJAX Guard delay was interrupted!");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
    }

    @Override
    public String toString() {
        return "HeaderCell{text=" + getColumnTitle().innerText() + "}";
    }
}
