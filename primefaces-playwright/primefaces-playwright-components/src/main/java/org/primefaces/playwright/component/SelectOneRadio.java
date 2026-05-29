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
 * Component wrapper for the PrimeFaces {@code p:selectOneRadio}.
 */
public abstract class SelectOneRadio extends AbstractComponent {

    @FindBy(css = ".ui-radiobutton")
    private List<Locator> radioButtons;

    public List<Locator> getRadioButtons() {
        return radioButtons;
    }

    public Locator getRadioButton(int index) {
        Locator radiobutton = getRadioButtons().get(index);
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(radiobutton));
        return radiobutton;
    }

    public Locator getRadioButtonBox(int index) {
        Locator radiobutton = getRadioButton(index);
        return radiobutton.locator(".ui-radiobutton-box");
    }

    public void select(int index) {
        if (!getWidgetConfiguration().getBoolean("unselectable")) {
            if (getSelectedIndex() == index) {
                return;
            }
        }

        Locator radiobutton = getRadioButton(index);
        Locator box = radiobutton.locator(".ui-radiobutton-box");
        Locator input = radiobutton.locator("input");
        if (isAjaxified(input, "onchange")) {
            PrimePlaywright.guardAjax(box).click();
        }
        else {
            box.click();
        }
    }

    public void select(String text) {
        int indexToSelect = getLabels().indexOf(text);
        if (indexToSelect >= 0) {
            select(indexToSelect);
        }
    }

    public int getSelectedIndex() {
        for (SelectItem item : getItems()) {
            if (item.isSelected()) {
                return item.getIndex();
            }
        }

        return -1;
    }

    public String getSelectedLabel() {
        for (SelectItem item : getItems()) {
            if (item.isSelected()) {
                return item.getLabel();
            }
        }

        return "";
    }

    public List<String> getLabels() {
        return getItems().stream()
                    .map(SelectItem::getLabel)
                    .collect(Collectors.toList());
    }

    public String getLabel(int index) {
        return getItems().get(index).getLabel();
    }

    public int getItemsSize() {
        return getRadioButtons().size();
    }

    public List<SelectItem> getItems() {
        ArrayList<SelectItem> items = new ArrayList<>();

        int idx = 0;
        for (Locator radiobutton : getRadioButtons()) {
            Locator input = radiobutton.locator("input");
            Locator label = getRoot().locator("label[for='" + input.getAttribute("id") + "']");
            Locator box = radiobutton.locator(".ui-radiobutton-box");

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

    /**
     * Disables the entire component.
     */
    public void disable() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".disable();");
    }

    /**
     * Enables the entire component
     */
    public void enable() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables a given radio button option of this widget.
     *
     * @param index Index of the radio button option to disable.
     */
    public void disableOption(int index) {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".disable(" + index + ");");
    }

    /**
     * Enables a given radio button option of this widget.
     *
     * @param index Index of the radio button option to enable.
     */
    public void enableOption(int index) {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".enable(" + index + ");");
    }

    /**
     * Is this component readonly?
     *
     * @return true if readonly
     */
    public boolean isReadOnly() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".cfg.readonly");
    }
}



