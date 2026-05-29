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
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractPageableData;

import java.util.List;

import com.microsoft.playwright.Locator;

public abstract class DataView extends AbstractPageableData {

    public enum Layout { LIST, GRID };

    @FindBy(className = "ui-dataview")
    private Locator locator;

    @FindBy(className = "ui-dataview-header")
    private Locator header;

    @FindBy(className = "ui-dataview-content")
    private Locator content;

    @Override
    public List<Locator> getRowsWebElement() {
        if (getActiveLayout() == Layout.LIST) {
            return content.locator(".ui-dataview-row").all();
        }
        else {
            return content.locator(".ui-dataview-column").all();
        }
    }

    public Locator getRowWebElement(int index) {
        return getRowsWebElement().get(index);
    }

    public Locator getLayoutOptionsWebElement() {
        return header.locator(".ui-dataview-layout-options");
    }

    public Layout getActiveLayout() {
        List<Locator> layoutButtons = getLayoutOptionsWebElement().locator(".ui-button").all();
        for (Locator layoutButton: layoutButtons) {
            Locator layoutButtonInputHidden = layoutButton.locator("input");
            if (layoutButtonInputHidden.isChecked()) {
                if ("list".equals(layoutButtonInputHidden.getAttribute("value"))) {
                    return Layout.LIST;
                }
                else {
                    return Layout.GRID;
                }
            }
        }

        return null;
    }

    public void setActiveLayout(Layout layout) {
        List<Locator> layoutButtons = getLayoutOptionsWebElement().locator(".ui-button").all();
        for (Locator layoutButton: layoutButtons) {
            Locator layoutButtonInputHidden = layoutButton.locator("input");
            if (layout == Layout.LIST && "list".equals(layoutButtonInputHidden.getAttribute("value"))) {
                PrimePlaywright.guardAjax(layoutButton).click();
            }
            else if (layout == Layout.GRID && "grid".equals(layoutButtonInputHidden.getAttribute("value"))) {
                PrimePlaywright.guardAjax(layoutButton).click();
            }
        }
    }
}
