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
import org.primefaces.playwright.ExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.util.Objects;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:triStateCheckbox}.
 */
public abstract class TriStateCheckbox extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getBox() {
        return findElement(By.className("ui-chkbox-box"));
    }

    public void click() {
        PrimePlaywright.waitGui().until(ExpectedConditions.elementToBeClickable(getRoot()));

        if (isOnchangeAjaxified()) {
            PrimePlaywright.guardAjax(getRoot()).click();
        }
        else {
            getRoot().click();
        }
    }

    public void setValue(Boolean value) {
        while (!Objects.equals(getValue(), value)) {
            click();
        }
    }

    public Boolean getValue() {
        String value = input.inputValue();
        switch (value) {
            case "0":
                return null;
            case "1":
                return Boolean.TRUE;
            case "2":
                return Boolean.FALSE;
            default:
                throw new IllegalStateException("Invalid value: " + value);
        }
    }

    /**
     * Toggles between its three states. (null, true, false)
     */
    public void toggle() {
        PrimePlaywright.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".toggle();");
    }
}



