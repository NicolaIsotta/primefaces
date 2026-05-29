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

import org.primefaces.playwright.ExpectedConditions;
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.findby.FindByParentPartialId;

import com.microsoft.playwright.Locator;

public abstract class AbstractToggleComponent extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @Override
    public Locator getInput() {
        return input;
    }

    @Override
    public boolean isSelected() {
        return getValue();
    }

    public void click() {
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(getRoot()));

        if (isOnChangeAjaxified()) {
            PrimePlaywright.guardAjax(getRoot()).click();
        }
        else {
            getRoot().click();
        }
    }

    /**
     * Is this toggle component AJAX enabled?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isOnChangeAjaxified() {
        return isAjaxified(getInput(), "onchange") || ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * Set the value of the the toggle component.
     *
     * @param value true for checked, false for unchecked
     */
    public void setValue(boolean value) {
        if (getValue() != value) {
            click();
        }
    }

    /**
     * Gets the value of the toggle component.
     *
     * @return true for checked, false for unchecked
     */
    public boolean getValue() {
        return getInput().isChecked();
    }

    /**
     * Turns this switch in case it is off, or turns of off in case it is on.
     */
    public void toggle() {
        PrimePlaywright.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".toggle();");
    }

    /**
     * Turns this switch on if it is not already turned on.
     */
    public void check() {
        PrimePlaywright.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".check();");
    }

    /**
     * Turns this switch off if it is not already turned of.
     */
    public void uncheck() {
        PrimePlaywright.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".uncheck();");
    }
}
