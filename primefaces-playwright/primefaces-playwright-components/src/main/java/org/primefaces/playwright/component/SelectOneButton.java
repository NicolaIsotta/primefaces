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
import org.primefaces.playwright.component.base.AbstractInputComponent;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:selectOneButton}.
 */
public abstract class SelectOneButton extends AbstractInputComponent {

    @FindBy(css = ".ui-button")
    private List<Locator> options;

    @FindBy(css = ".ui-button.ui-state-active")
    private Locator activeOption;

    public List<Locator> getOptions() {
        return options;
    }

    public Locator getActiveOption() {
        return activeOption;
    }

    public List<String> getOptionLabels() {
        List<String> result = new ArrayList<>();
        getOptions().forEach((element) -> result.add(element.innerText()));

        return result;
    }

    public String getSelectedLabel() {
        Locator label = getActiveOption().locator(".ui-button-text");
        if (PrimePlaywright.isElementDisplayed(label)) {
            return label.innerText();
        }
        return label.textContent();
    }

    public boolean isSelected(String label) {
        return getSelectedLabel().equalsIgnoreCase(label);
    }

    public boolean isSelected(int index) {
        return index == getOptions().indexOf(getActiveOption());
    }

    public void selectNext() {
        int activeIndex = getOptions().indexOf(getActiveOption());
        int nextIndex = activeIndex + 1;

        if (nextIndex >= getOptions().size()) {
            nextIndex = 0;
        }

        select(nextIndex);
    }

    public void select(String label) {
        if (isSelected(label)) {
            return;
        }

        for (Locator element : getOptions()) {
            if (element.innerText().equalsIgnoreCase(label)) {
                click(element);
                return;
            }
        }
    }

    public void select(int index) {
        if (index > getOptions().size()) {
            throw new IndexOutOfBoundsException("Index " + index + ", Size " + getOptions().size());
        }

        if (isSelected(index)) {
            return;
        }

        click(getOptions().get(index));
    }

    public void selectFirst() {
        select(0);
    }

    public void selectLast() {
        select(getOptions().size() - 1);
    }

    public void deselect(String label) {
        deselect(label, false);
    }

    public void deselect(String label, boolean ignoreDeselectable) {
        if (!ignoreDeselectable && !isUnselectable()) {
            return;
        }

        if (!isSelected(label)) {
            return;
        }

        for (Locator element : getOptions()) {
            if (element.innerText().equalsIgnoreCase(label)) {
                click(element);
                return;
            }
        }
    }

    public void deselect(int index) {
        deselect(index, false);
    }

    public void deselect(int index, boolean ignoreDeselectable) {
        if (index > getOptions().size()) {
            throw new IndexOutOfBoundsException("Index " + index + ", Size " + getOptions().size());
        }

        if (!ignoreDeselectable && !isUnselectable()) {
            return;
        }

        if (!isSelected(index)) {
            return;
        }

        click(getOptions().get(index));
    }

    public boolean isUnselectable() {
        return "true".equals(PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".cfg.unselectable"));
    }

    protected void click(Locator element) {
        if (isOnchangeAjaxified()) {
            PrimePlaywright.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }
}



