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

import org.primefaces.playwright.FindBy;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.io.Serializable;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:spinner}.
 */
public abstract class Spinner extends InputText {

    @FindByParentPartialId("_input")
    private Locator input;

    @FindBy(css = ".ui-spinner-up")
    private Locator buttonUp;

    @FindBy(css = ".ui-spinner-down")
    private Locator buttonDown;

    @Override
    public Locator getInput() {
        return input;
    }

    /**
     * Gets the Spinner's Up button.
     *
     * @return the {@link Locator} representing the up button
     */
    public Locator getButtonUp() {
        return buttonUp;
    }

    /**
     * Gets the Spinner's Down button.
     *
     * @return the {@link Locator} representing the down button
     */
    public Locator getButtonDown() {
        return buttonDown;
    }

    @Override
    public void setValue(Serializable value) {
        if (value == null) {
            value = "\"\"";
        }

        PrimePlaywright.executeScript(getWidgetByIdScript() + ".setValue(" + value.toString() + ")");
    }

    public String getWidgetValue() {
        return PrimePlaywright.executeScript(getWidgetByIdScript() + ".getValue()");
    }

    /**
     * Increments this spinner by one SpinnerCfg.step
     */
    public void increment() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".spin(1);");
    }

    /**
     * Decrements this spinner by one SpinnerCfg.step
     */
    public void decrement() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".spin(-1);");
    }

    /**
     * Fire the change event for the spinner
     */
    public void change() {
        PrimePlaywright.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".input.trigger('change');");
    }
}



