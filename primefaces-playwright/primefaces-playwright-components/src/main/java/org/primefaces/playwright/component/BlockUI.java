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

import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;

import java.util.List;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:blockUI}.
 */
public abstract class BlockUI extends AbstractComponent {

    /**
     * Finds all Block UI overlay elements by class name.
     *
     * @return a List<Locator> containing at least one element
     */
    public List<Locator> getOverlays() {
        return getWebDriver().locator(".ui-blockui").all();
    }

    /**
     * Finds all Block UI content elements by class name.
     *
     * @return a List<Locator> containing at least one element
     */
    public List<Locator> getContents() {
        return getWebDriver().locator(".ui-blockui-content").all();
    }

    /**
     * Is the blocker currently blocking.
     *
     * @return true if blocking false if not
     */
    public boolean isBlocking() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".isBlocking();");
    }

    /**
     * Does the block have content.
     *
     * @return true if has content, false if not
     */
    public boolean hasContent() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".hasContent();");
    }

    /**
     * Shows the blocker.
     */
    public void show() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".show();");
        List<Locator> overlays = getOverlays();
        if (!overlays.isEmpty()) {
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(overlays.get(0)));
        }
    }

    /**
     * Hides the blocker.
     */
    public void hide() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
        List<Locator> overlays = getOverlays();
        if (!overlays.isEmpty()) {
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(overlays.get(0)));
        }
    }

}



