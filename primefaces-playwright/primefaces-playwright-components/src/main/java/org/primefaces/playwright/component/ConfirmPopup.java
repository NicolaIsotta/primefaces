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
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:confirmPopup}.
 */
public abstract class ConfirmPopup extends AbstractComponent {

    @FindBy(className = "ui-confirm-popup-icon")
    private Locator icon;

    @FindBy(className = "ui-confirm-popup-message")
    private Locator message;

    @FindBy(className = "ui-confirm-popup-yes")
    private CommandButton yesButton;

    @FindBy(className = "ui-confirm-popup-no")
    private CommandButton noButton;

    public Locator getMessage() {
        return message;
    }

    public Locator getIcon() {
        return icon;
    }

    public CommandButton getYesButton() {
        return yesButton;
    }

    public CommandButton getNoButton() {
        return noButton;
    }

    /**
     * Is the popup currently visible.
     *
     * @return true if visible false if not
     */
    public boolean isVisible() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".isVisible();");
    }

    /**
     * Hides the overlay panel.
     */
    public void hidePopup() {
        if (isEnabled() && isDisplayed()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

}



