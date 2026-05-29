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
 * Component wrapper for the PrimeFaces {@code p:dialog}.
 */
public abstract class Dialog extends AbstractComponent {

    @FindBy(className = "ui-dialog-content")
    private Locator content;

    @FindBy(className = "ui-dialog-title")
    private Locator title;

    @FindBy(className = "ui-dialog-titlebar-close")
    private Locator closeButton;

    @FindBy(className = "ui-dialog-titlebar-minimize")
    private Locator minimizeButton;

    @FindBy(className = "ui-dialog-titlebar-maximize")
    private Locator maximizeButton;

    public Locator getContent() {
        return content;
    }

    public String getTitle() {
        return title.innerText();
    }

    public Locator getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(Locator closeButton) {
        this.closeButton = closeButton;
    }

    public Locator getMinimizeButton() {
        return minimizeButton;
    }

    public void setMinimizeButton(Locator minimizeButton) {
        this.minimizeButton = minimizeButton;
    }

    public Locator getMaximizeButton() {
        return maximizeButton;
    }

    public void setMaximizeButton(Locator maximizeButton) {
        this.maximizeButton = maximizeButton;
    }

    public void setContent(Locator content) {
        this.content = content;
    }

    /**
     * Is the dialog currently visible.
     *
     * @return true if visible false if not
     */
    public boolean isVisible() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".isVisible();");
    }

    /**
     * Minimize the dialog.
     */
    public void toggleMinimize() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".toggleMinimize();");
    }

    /**
     * Maximize the dialog.
     */
    public void toggleMaximize() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".toggleMaximize();");
    }

    /**
     * Shows the dialog.
     */
    public void show() {
        if (isEnabled() && !isDisplayed()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".show();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(this));
        }
    }

    /**
     * Hides the dialog.
     */
    public void hide() {
        if (isEnabled() && isDisplayed()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

}



