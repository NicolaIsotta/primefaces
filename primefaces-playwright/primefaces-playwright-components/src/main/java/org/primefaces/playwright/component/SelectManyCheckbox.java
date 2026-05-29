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
import org.primefaces.playwright.component.model.SelectItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:selectManyCheckbox}.
 */
public abstract class SelectManyCheckbox extends AbstractComponent {

    @FindBy(css = ".ui-chkbox")
    private List<Locator> checkboxes;

    public List<Locator> getCheckboxes() {
        return checkboxes;
    }

    public void toggle(int... indexes) {
        for (int i : indexes) {
            Locator checkbox = getCheckboxes().get(i);
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(checkbox));

            Locator input = checkbox.locator("input");
            if (isAjaxified(input, "onchange")) {
                PrimePlaywright.guardAjax(checkbox).click();
            }
            else {
                checkbox.click();
            }
        }
    }

    public void toggleAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            toggle(i);
        }
    }

    public void select(int... indexes) {
        deselectAll();

        for (int i : indexes) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void selectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselect(int... indexes) {
        for (int i : indexes) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public int getItemsSize() {
        return getCheckboxes().size();
    }

    public List<String> getLabels() {
        return getCheckboxes().stream()
                    .map(Locator::innerText)
                    .collect(Collectors.toList());
    }

    public String getLabel(int index) {
        return getItems().get(index).getLabel();
    }

    public List<SelectItem> getItems() {
        ArrayList<SelectItem> items = new ArrayList<>();

        int idx = 0;
        for (Locator checkbox : getCheckboxes()) {
            Locator input = checkbox.locator("input");
            Locator label = getRoot().locator("label[for='" + input.getAttribute("id") + "']");
            Locator box = checkbox.locator(".ui-chkbox-box");

            SelectItem item = new SelectItem();
            item.setIndex(idx);
            item.setLabel(label.innerText());
            item.setValue(input.inputValue());
            item.setSelected(PrimePlaywright.hasCssClass(box, "ui-state-active"));
            items.add(item);

            idx++;
        }

        return items;
    }

    public boolean isSelected(int index) {
        return getItems().get(index).isSelected();
    }
}



