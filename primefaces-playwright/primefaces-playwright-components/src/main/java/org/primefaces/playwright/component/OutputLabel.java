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
import org.primefaces.playwright.component.base.AbstractComponent;

import com.microsoft.playwright.Locator;

public abstract class OutputLabel extends AbstractComponent {

    /**
     * Does this outputLabel have the * required indicator?
     *
     * @return true if has required indicator false if not
     */
    public boolean hasRequiredIndicator() {
        return findElement(By.className("ui-outputlabel-rfi")).count() > 0;
    }

    /**
     * Gets the component this label is for.
     *
     * @return the {@link Locator} this label is for or NULL if not for any
     */
    public Locator getFor() {
        String forAttr = getDomAttribute("for");
        if (forAttr == null) {
            return null;
        }
        Locator target = getWebDriver().locator("[id=\"" + forAttr + "\"]");
        return target.count() > 0 ? target : null;
    }
}
