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

import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:panel}.
 */
public abstract class Panel extends AbstractComponent {

    @FindByParentPartialId("_toggler")
    private Locator toggler;

    @FindByParentPartialId("_header")
    private Locator header;

    @FindByParentPartialId("_content")
    private Locator content;

    public Locator getToggler() {
        return toggler;
    }

    public Locator getContent() {
        return content;
    }

    public Locator getHeader() {
        return header;
    }

    /**
     * Is this component AJAX enabled with "toggle"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isToggleAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "toggle");
    }

    /**
     * Is this component AJAX enabled with "close"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCloseAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "close");
    }

    public void close() {
        PrimePlaywright.executeScript(isCloseAjaxified(), getWidgetByIdScript() + ".close();");
    }

    public void expand() {
        PrimePlaywright.executeScript(isToggleAjaxified(), getWidgetByIdScript() + ".expand();");
    }

    public void collapse() {
        PrimePlaywright.executeScript(isToggleAjaxified(), getWidgetByIdScript() + ".collapse();");
    }

    public void toggle() {
        PrimePlaywright.executeScript(isToggleAjaxified(), getWidgetByIdScript() + ".toggle();");
    }
}



