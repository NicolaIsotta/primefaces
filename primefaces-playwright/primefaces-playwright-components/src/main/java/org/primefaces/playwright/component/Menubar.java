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
import org.primefaces.playwright.component.base.AbstractInputComponent;

import java.util.List;
import java.util.NoSuchElementException;

import com.microsoft.playwright.Locator;

public abstract class Menubar extends AbstractInputComponent {

    public Locator findMenuitemByValue(String value) {
        return findMenuitemByValue(getWrappedLocator(), value);
    }

    public Locator findMenuitemByValue(Locator parentElt, String value) {
        List<Locator> subElements = parentElt.locator("ul > li.ui-menuitem").all();
        return subElements.stream()
                .filter(e -> e.innerText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));
    }

    /**
     * Select L1 menuitem
     * @param value
     * @return
     */
    public Locator selectMenuitemByValue(String value) {
        getRoot().hover();
        return selectMenuitemByValue(getWrappedLocator(), value);
    }

    /**
     * Select L2+ menuitem
     * @param parentElt parent menuitem
     * @param value
     * @return
     */
    public Locator selectMenuitemByValue(Locator parentElt, String value) {
        Locator elt = findMenuitemByValue(parentElt, value);

        if ((PrimePlaywright.hasCssClass(elt, "ui-menu-parent")) && isToggleEventHover()) {
            elt.hover();
        }
        else {
            Locator eltA = elt.locator("xpath=./a");
            if (isAjaxified(eltA, "onclick")) {
                PrimePlaywright.guardAjax(elt).click();
            }
            else {
                elt.click();
            }
            // some more cases?
        }

        return  elt;
    }

    public boolean isToggleEventHover() {
        return "hover".equals(getWidgetConfiguration().getString("toggleEvent"));
    }

    public boolean isToggleEventClick() {
        return "click".equals(getWidgetConfiguration().getString("toggleEvent"));
    }
}
