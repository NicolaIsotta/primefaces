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
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;
/**
 * Component wrapper for the PrimeFaces {@code p:selectOneMenu}.
 */
public abstract class SelectOneMenu extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private Locator panel;

    @FindByParentPartialId(value = "_filter", searchFromRoot = true)
    private Locator filterInput;

    /**
     * Is the input using AJAX "itemSelect" event?
     *
     * @return true if using AJAX for itemSelect
     */
    public boolean isItemSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Either display the dropdown or hide it if is already displayed.
     */
    public void toggleDropdown() {
        if (getPanel().isVisible()) {
            hide();
        }
        else {
            show();
        }
    }

    /**
     * Shows the SelectOneMenu panel.
     */
    public void show() {
        Locator panel = getPanel();
        if (isEnabled() && !panel.isVisible()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".show();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        }
    }

    /**
     * Hides the SelectOneMenu panel.
     */
    public void hide() {
        Locator panel = getPanel();
        if (isEnabled() && panel.isVisible()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    public void deselect(String label) {
        if (!isSelected(label) || !isEnabled()) {
            return;
        }

        if (!getPanel().isVisible()) {
            toggleDropdown();
        }

        for (Locator element : getItems().locator("li.ui-selectonemenu-item").all()) {
            if (element.innerText().equalsIgnoreCase(label)) {
                click(element);
                break;
            }
        }

        if (getPanel().isVisible()) {
            toggleDropdown();
        }
    }

    public void select(String label) {
        if (isSelected(label) || !isEnabled()) {
            return;
        }

        if (!getPanel().isVisible()) {
            toggleDropdown();
        }

        for (Locator element : getItems().locator("li.ui-selectonemenu-item").all()) {
            if (element.innerText().equalsIgnoreCase(label)) {
                click(element);
                break;
            }
        }

        if (getPanel().isVisible()) {
            toggleDropdown();
        }
    }

    public String getSelectedLabel() {
        Locator label = getLabel();
        if (PrimePlaywright.isElementDisplayed(label)) {
            return label.innerText();
        }
        return label.textContent();
    }

    public boolean isSelected(String label) {
        boolean result = false;
        try {
            result = getSelectedLabel().equalsIgnoreCase(label);
        }
        catch (Exception e) {
            // do nothing
        }
        return result;
    }

    /**
     * All labels independent of filter.
     * @return
     */
    public List<String> getLabels() {
        JSONObject widgetConfiguration = getWidgetConfiguration();

        if (widgetConfiguration.has("filter") && widgetConfiguration.getBoolean("filter")) {
            show(); //listElt.isDisplayed only works when panel is visible

            return getItems().locator("li.ui-selectonemenu-item").all().stream()
                    .filter(listElt -> listElt.isVisible())
                    .map(e -> e.innerHTML())
                    .collect(Collectors.toList());
        }
        else {
            return getInput().locator("option").all().stream()
                    .map(e -> e.innerHTML())
                    .collect(Collectors.toList());
        }
    }

    public void select(int index) {
        if (isSelected(index)) {
            return;
        }

        select(getLabel(index));
    }

    public void deselect(int index) {
        if (!isSelected(index)) {
            return;
        }

        deselect(getLabel(index));
    }

    public void selectByValue(String value) {
        PrimePlaywright.executeScript(String.format("PrimeFaces.getWidgetById('%s').selectValue('%s');", getId(), value));
    }

    public boolean isSelected(int index) {
        return getLabel(index).equals(getSelectedLabel());
    }

    public String getLabel(int index) {
        return getLabels().get(index);
    }

    @Override
    public Locator getAssignedLabel() {
        return getWebDriver().locator("label[for='" + getId() + (isEditable() ? "_focus" : "_label") + "']");
    }

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getEditableInput() {
        return findElement(By.name(getId() + "_editableInput"));
    }

    public Locator getLabel() {
        return findElement(By.id(getId() + "_label"));
    }

    /**
     * Gets items when using normal rendering.
     * @return the Locator of the items
     */
    public Locator getItems() {
        return getWebDriver().locator("#" + getId() + "_items");
    }

    /**
     * Gets the table element when using advanced rendering.
     * @return the Locator of the table
     */
    public Locator getTable() {
        return getWebDriver().locator("#" + getId() + "_table");
    }

    public Locator getPanel() {
        return panel;
    }

    protected void click(Locator element) {
        if (isOnchangeAjaxified() || isItemSelectAjaxified()) {
            PrimePlaywright.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }

    public Locator getFilterInput() {
        return filterInput;
    }

    public boolean isEditable() {
        return getWidgetConfiguration().optBoolean("editable");
    }
}



