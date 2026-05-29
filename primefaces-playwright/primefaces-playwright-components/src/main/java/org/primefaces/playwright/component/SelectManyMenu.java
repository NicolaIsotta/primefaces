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
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;
/**
 * Component wrapper for the PrimeFaces {@code p:selectOneMenu}.
 */
public abstract class SelectManyMenu extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @FindBy(css = ".ui-selectlistbox-listcontainer .ui-selectlistbox-list")
    private Locator selectlistbox;

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
     * Is the input using AJAX "itemUnselect" event?
     *
     * @return true if using AJAX for itemUnselect
     */
    public boolean isItemUnselectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemUnselect");
    }

    public void deselect(String label) {
        deselect(label, isItemUnselectAjaxified());
    }

    public void deselect(String label, boolean withGuardAjax) {
        if (!isSelected(label)) {
            return;
        }

        toggleSelection(label, true, withGuardAjax);
    }

    public void select(String label, boolean withMetaKey) {
        select(label, withMetaKey, isItemSelectAjaxified());
    }

    public void select(String label, boolean withMetaKey, boolean withGuardAjax) {
        if (isSelected(label)) {
            return;
        }

        toggleSelection(label, withMetaKey, withGuardAjax);
    }

    public void toggleSelection(String label, boolean withMetaKey) {
        toggleSelection(label, withMetaKey, false);
    }

    public void toggleSelection(String label, boolean withMetaKey, boolean withGuardAjax) {
        if (!isEnabled()) {
            return;
        }

        clickOnListItemWithMetaKey(label, withMetaKey, withGuardAjax);
    }

    private void clickOnListItemWithMetaKey(String label, boolean withMetaKey, boolean withGuardAjax) {
        for (Locator element : getSelectlistbox().locator("li").all()) {
            if (element.innerText().equalsIgnoreCase(label)) {
                if (withMetaKey) {
                    if (withGuardAjax) {
                        element = PrimePlaywright.guardAjax(element);
                    }
                    com.microsoft.playwright.options.KeyboardModifier modifier = PrimePlaywright.isMacOs() ?
                            com.microsoft.playwright.options.KeyboardModifier.META :
                            com.microsoft.playwright.options.KeyboardModifier.CONTROL;
                    element.click(new com.microsoft.playwright.Locator.ClickOptions()
                            .setModifiers(java.util.Arrays.asList(modifier)));
                }
                else {
                    if (withGuardAjax) {
                        element = PrimePlaywright.guardAjax(element);
                    }
                    element.click();
                }
                break;
            }
        }
    }

    public boolean isSelected(String label) {
        try {
            for (Locator element : getSelectlistbox().locator("li").all()) {
                if (element.innerText().equalsIgnoreCase(label)) {
                    if (PrimePlaywright.hasCssClass(element, "ui-state-highlight")) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            // do nothing
        }
        return false;
    }

    public List<String> getLabels() {
        JSONObject widgetConfiguration = getWidgetConfiguration();

        if (widgetConfiguration.has("filter") && widgetConfiguration.getBoolean("filter")) {
            return getSelectlistbox().locator("li.ui-selectlistbox-item").all().stream()
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

    public boolean isSelected(int index) {
        return isSelected(getLabel(index));
    }

    public String getLabel(int index) {
        return getLabels().get(index);
    }

    public List<String> getSelectedLabels() {
        List<String> selectedLabels = new ArrayList<>();

        for (Locator element : getSelectlistbox().locator("li").all()) {
            if (PrimePlaywright.hasCssClass(element, "ui-state-highlight")) {
                selectedLabels.add(element.innerText());
            }
        }

        return selectedLabels;
    }

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getSelectlistbox() {
        return selectlistbox;
    }

    public Locator getFilterInput() {
        return filterInput;
    }
}



