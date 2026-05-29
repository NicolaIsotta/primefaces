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

import org.primefaces.playwright.By;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:schedule}.
 */
public abstract class Schedule extends AbstractComponent {

    /**
     * Selects either a date or event or any other element in the schedule by its CSS class.
     *
     * @param cssClass the CSS class to select
     */
    public void select(String cssClass) {
        Locator element = findElement(By.className(cssClass));
        PrimePlaywright.guardAjax(element).click();
    }

    /**
     * Updates and refreshes the schedule view refetching all events
     */
    public void update() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".update();");
    }

    public Locator getTodayButton() {
        return getButton("fc-today-button");
    }

    public Locator getMonthButton() {
        return getButton("fc-dayGridMonth-button");
    }

    public Locator getWeekButton() {
        return getButton("fc-timeGridWeek-button");
    }

    public Locator getDayButton() {
        return getButton("fc-timeGridDay-button");
    }

    public Locator getButton(String buttonClass) {
        return findElement(By.className(buttonClass));
    }

}



