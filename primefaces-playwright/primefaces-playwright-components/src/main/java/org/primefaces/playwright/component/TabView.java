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
import org.primefaces.playwright.FindBy;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.component.model.Tab;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;
/**
 * Component wrapper for the PrimeFaces {@code p:tabView}.
 */
public abstract class TabView extends AbstractComponent {

    @FindBy(css = ".ui-tabs-header")
    private List<Locator> headers;

    @FindBy(css = ".ui-tabs-panel")
    private List<Locator> contents;

    private List<Tab> tabs = null;

    public List<Tab> getTabs() {
        if (tabs == null) {
            List<Tab> tabs = new ArrayList<>();

            headers.forEach(headerElt -> {
                String title = headerElt.locator("a").innerText();
                int index = getIndexOfHeader(headerElt);
                Locator content = contents.get(index);

                tabs.add(new Tab(title, index, headerElt, content));
            });

            this.tabs = tabs;
        }

        return tabs;
    }

    /**
     * Toggle the tab denoted by the specified index.
     *
     * @param index the index of the tab to expand
     */
    public void toggleTab(int index) {
        final JSONObject cfg = getWidgetConfiguration();
        final boolean isDynamic = cfg.has("dynamic") && cfg.getBoolean("dynamic");

        if (isDynamic || ComponentUtils.hasAjaxBehavior(getRoot(), "tabChange")) {
            PrimePlaywright.guardAjax(headers.get(index)).click();
        }
        else {
            headers.get(index).click();
        }
    }

    /**
     * Provides the selected {@link TabView} tab.
     *
     * @return the selected tab
     */
    public Tab getSelectedTab() {
        Locator selectedTabHeader = findElement(By.className("ui-tabs-selected"));
        int index = getIndexOfHeader(selectedTabHeader);

        return getTabs().get(index);
    }

    private Integer getIndexOfHeader(Locator headerElt) {
        return Integer.valueOf(headerElt.getAttribute("data-index"));
    }
}



