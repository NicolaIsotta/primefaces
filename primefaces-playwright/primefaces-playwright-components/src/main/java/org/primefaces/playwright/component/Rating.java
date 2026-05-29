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
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:rating}.
 */
public abstract class Rating extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @Override
    public Locator getInput() {
        return input;
    }

    /**
     * Is Cancel event AJAX enabled?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCancelAjaxified() {
        return ComponentUtils.hasBehavior(getWrappedLocator(), "cancel") || isOnchangeAjaxified();
    }

    /**
     * Is Rating event AJAX enabled?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isRatingAjaxified() {
        return ComponentUtils.hasBehavior(getWrappedLocator(), "rate") || isOnchangeAjaxified();
    }

    /**
     * Gets the cancel icon if available.
     *
     * @return the cancel icon
     */
    public Locator getCancelIcon() {
        return findElement(By.className("ui-rating-cancel"));
    }

    /**
     * Resets the rating so that no stars are selected using the cancel icon.
     */
    public void cancel() {
        Locator cancelIcon = getCancelIcon();
        if (isCancelAjaxified() || isRatingAjaxified()) {
            PrimePlaywright.guardAjax(cancelIcon).click();
        }
        else {
            cancelIcon.click();
        }

    }

    /**
     * Finds the current rating, i.e. the number of stars selected.
     *
     * @return The current rating value.
     */
    public Number getValue() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".getValue();");
    }

    /**
     * Sets the rating to the given value.
     *
     * @param value New rating value to set (number of starts selected).
     */
    public void setValue(Number value) {
        PrimePlaywright.executeScript(isRatingAjaxified(), getWidgetByIdScript() + ".setValue(" + value + ");");
    }

    /**
     * Resets the rating so that no stars are selected.
     */
    public void reset() {
        PrimePlaywright.executeScript(isCancelAjaxified(), getWidgetByIdScript() + ".reset();");
    }

    /**
     * Is this rating disabled?
     *
     * @return true if disabled
     */
    public boolean isDisabled() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".isDisabled();");
    }

    /**
     * Is this rating readonly?
     *
     * @return true if readonly
     */
    public boolean isReadOnly() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".isReadOnly();");
    }

    @Override
    public boolean isEnabled() {
        return !isDisabled();
    }
}



