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

import org.primefaces.playwright.Keys;
import org.primefaces.playwright.PrimePlaywright;

import com.microsoft.playwright.Locator;
public abstract class AbstractInputComponent extends AbstractComponent {

    /**
     * The input element reference.
     *
     * @return the {@link Locator} representing the input.
     */
    public Locator getInput() {
        return getRoot();
    }

    /**
     * Is this input component enabled?
     *
     * @return true if enabled, false if not
     */
    @Override
    public boolean isEnabled() {
        return getInput().isEnabled() && !PrimePlaywright.hasCssClass(this, "ui-state-disabled");
    }

    /**
     * Is the input using AJAX "change" or "valueChange" event?
     *
     * @return true if using AJAX for onchange, change or valueChange
     */
    public boolean isOnchangeAjaxified() {
        return isAjaxified(getInput(), "onchange") || ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * The HTML label assigned to this input.
     *
     * @return the {@link Locator} representing the label.
     */
    public Locator getAssignedLabel() {
        return getWebDriver().locator("label[for='" + getInput().getAttribute("id") + "']");
    }

    /**
     * The HTML label text assigned to this input.
     *
     * @return the value of the label text
     */
    public String getAssignedLabelText() {
        return getAssignedLabel().innerText();
    }

    /**
     * Copy the current value in the Input to the clipboard.
     *
     * @return the value copied to the clipboard
     */
    public String copyToClipboard() {
        Locator input = getInput();
        Keys command = PrimePlaywright.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        input.press(Keys.chord(command, "a")); // select everything
        input.press(Keys.chord(command, "c")); // copy
        return input.inputValue();
    }

    /**
     * Paste the current value of the clipboard to the Input.
     *
     * @return the value pasted into the input
     */
    public String pasteFromClipboard() {
        Locator input = getInput();
        Keys command = PrimePlaywright.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        input.press(Keys.chord(command, "a")); // select everything
        input.press(Keys.chord(command, "v")); // paste
        return input.inputValue();
    }

    /**
     * Selects all text in the input component.
     */
    public void selectAllText() {
        Keys command = PrimePlaywright.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        getInput().press(Keys.chord(command, "a"));
    }

    /**
     * Clears the input and guards AJAX for "clear" event.
     */
    public void clear() {
        PrimePlaywright.clearInput(getInput(), false);
    }

    /**
     * Enables the input.
     */
    public void enable() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables the input.
     */
    public void disable() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".disable();");
    }

    /**
     * Sets the focus to the element.
     */
    public void focus() {
        getInput().focus();
    }

}



