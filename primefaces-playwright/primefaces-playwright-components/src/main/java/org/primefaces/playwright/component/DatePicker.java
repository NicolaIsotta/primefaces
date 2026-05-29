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

import org.primefaces.playwright.ExpectedConditions;
import org.primefaces.playwright.Keys;
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.microsoft.playwright.Locator;

public abstract class DatePicker extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    public void click() {
        input.click();
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getPanel()));
    }

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getPanel() {
        return getWebDriver().locator("[id=\"" + getId() + "_panel\"]");
    }

    /**
     * Is this component AJAX enabled with "dateSelect"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isDateSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "dateSelect");
    }

    /**
     * Is this component AJAX enabled with "viewChange"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isViewChangeAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "viewChange");
    }

    /**
     * Is this component AJAX enabled with "close"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCloseAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "close");
    }

    /**
     * Gets the Next Month link in the navigator.
     *
     * @return the Next Month link
     */
    public Locator getNextMonthLink() {
        Locator link = showPanel().locator(".ui-datepicker-next");
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isViewChangeAjaxified()) {
            link = PrimePlaywright.guardAjax(link);
        }
        return link;
    }

    /**
     * Gets the Previous Month link in the navigator.
     *
     * @return the Previous Month link
     */
    public Locator getPreviousMonthLink() {
        Locator link = showPanel().locator(".ui-datepicker-prev");
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isViewChangeAjaxified()) {
            link = PrimePlaywright.guardAjax(link);
        }
        return link;
    }

    /**
     * Selects a day in the overlay panel.
     *
     * @param day the day to select
     * @return the day selected
     */
    public Locator selectDay(String day) {
        Locator link = showPanel().locator("a:has-text(\"" + day + "\")");
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isDateSelectAjaxified()) {
            link = PrimePlaywright.guardAjax(link);
        }
        link.click();
        return link;
    }

    /**
     * Gets the Clear button on the overlay panel.
     *
     * @return the Clear button
     */
    public Locator getClearButton() {
        Locator button = showPanel().locator(".ui-datepicker-buttonbar").locator(".ui-clear-button");
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return button;
    }

    /**
     * Gets the Today button on the overlay panel.
     *
     * @return the Today button
     */
    public Locator getTodayButton() {
        Locator button = showPanel().locator(".ui-datepicker-buttonbar").locator(".ui-today-button");
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return button;
    }

    public LocalDateTime getValue() {
        if (!hasDate()) {
            return null;
        }

        Number epoch = PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".getDate().getTime();");
        // Move epoch into server-timezone
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch.longValue()), ZoneId.systemDefault());
        return dateTime;
    }

    public LocalDate getValueAsLocalDate() {
        LocalDateTime dt = getValue();
        return dt != null ? dt.toLocalDate() : null;
    }

    public void setValue(LocalDate localDate) {
        setValue(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public void setValue(LocalDateTime dateTime) {
        long millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        setValue(millis);
    }

    public void setValue(long millis) {
        setDate(millis);
    }

    public String millisAsFormattedDate(long millis) {
        return PrimePlaywright.executeScript(
                    "return " + getWidgetByIdScript() + ".jq.data().primeDatePicker.formatDateTime(new Date(" + millis + "));");
    }

    /**
     * Widget API call to set date to this LocalDateTime.
     *
     * @param dateTime the LocalDateTime to set to
     */
    public void setDate(LocalDateTime dateTime) {
        long millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        setDate(millis);
    }

    /**
     * Widget API call to set date to this epoch in millis.
     *
     * @param epoch epoch in milliseconds
     */
    public void setDate(long epoch) {
        PrimePlaywright.executeScript(isDateSelectAjaxified(), getWidgetByIdScript() + ".setDate(new Date(" + epoch + "));");
    }

    /**
     * Gets the JS date value from the widget.
     *
     * @return the JS date value or null
     */
    public String getWidgetDate() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".getDate();");
    }

    /**
     * Checks whether a date is selected
     *
     * @return true if a date is selected.
     */
    public boolean hasDate() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".hasDate();");
    }

    /**
     * Is this a lazy datepicker.
     *
     * @return true if lazy
     */
    public boolean isLazy() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript()
                + ".cfg.lazyDataModel === undefined ? false : " + getWidgetByIdScript() + ".cfg.lazyDataModel");
    }

    /**
     * Widget API call to update the overlay popup to this epoch in millis.
     *
     * @param epoch epoch in milliseconds
     */
    public void updateViewDate(long epoch) {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".jq.data().primeDatePicker.updateViewDate(null, new Date(" + epoch + "));");
    }

    /**
     * Shows the overlay panel.
     *
     * @return the panel shown
     */
    public Locator showPanel() {
        if (isEnabled()) {
            PrimePlaywright.wait(110); // due to an async setTimeout in hideOverlay
            if (!getPanel().isVisible()) {
                PrimePlaywright.executeScript(getWidgetByIdScript() + ".show()");
            }
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getPanel()));
        }
        return getPanel();
    }

    /**
     * Hides the overlay panel.
     */
    public void hidePanel() {
        if (isEnabled()) {
            if (getPanel().isVisible()) {
                PrimePlaywright.executeScript(isCloseAjaxified(), getWidgetByIdScript() + ".hide();");
                PrimePlaywright.wait(110); // due to an async setTimeout in hideOverlay
            }
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getPanel()));
        }
    }

    /**
     * Gets the browser time zone offset.
     *
     * @return the browser time zone offset in milliseconds
     */
    public long getTimezoneOffset() {
        return (Long) PrimePlaywright.executeScript("return new Date().getTimezoneOffset();");
    }

    /**
     * Open the month select dropdown.
     */
    public void toggleMonthDropdown() {
        Locator monthDropDown = showPanel().locator("select.ui-datepicker-month");
        monthDropDown.click();
    }

    /**
     * Select a month from the drodown.
     *
     * @param month the month to select
     */
    public void selectMonthDropdown(int month) {
        Locator monthDropDown = showPanel().locator("select.ui-datepicker-month");
        monthDropDown.selectOption(Integer.toString(month));
    }

    /**
     * Select a year.
     *
     * @param year the year to select
     */
    public void selectYear(int year) {
        Locator panel = showPanel();
        Locator yearInput = panel.locator("input.ui-datepicker-year");
        if (yearInput.count() > 0) {
            yearInput.pressSequentially(Integer.toString(year));
        }
        else {
            Locator yearDropDown = showPanel().locator("select.ui-datepicker-year");
            yearDropDown.selectOption(Integer.toString(year));
        }
    }

    /**
     * Increment the years by count.
     *
     * @param count the number of years to increment
     */
    public void incrementYear(int count) {
        Locator yearInput = showPanel().locator("input.ui-datepicker-year");
        for (int i = 0; i <  count; i++) {
            yearInput.press(Keys.ARROW_UP.toString());
        }
    }

    /**
     * Decrement the years by count.
     *
     * @param count the number of years to decrement
     */
    public void decrementYear(int count) {
        Locator yearInput = showPanel().locator("input.ui-datepicker-year");
        for (int i = 0; i <  count; i++) {
            yearInput.press(Keys.ARROW_DOWN.toString());
        }
    }
}
