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

import com.microsoft.playwright.Locator;

public abstract class Password extends InputText {

    /**
     * Gets the Feedback panel showing password strength.
     *
     * @return the feedback panel
     */
    public Locator getFeedbackPanel() {
        return getWebDriver().locator("[id=\"" + getId() + "_panel\"]");
    }

    /**
     * Brings up the panel with the password strength indicator.
     */
    public void showFeedback() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".show();");
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getFeedbackPanel()));
    }

    /**
     * Hides the panel with the password strength indicator.
     */
    public void hideFeedback() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.animationNotActive());
    }
}
