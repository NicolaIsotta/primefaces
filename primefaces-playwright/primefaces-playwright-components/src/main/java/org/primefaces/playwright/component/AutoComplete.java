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
import org.primefaces.playwright.Keys;
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:autoComplete}.
 */
public abstract class AutoComplete extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private Locator panel;

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getItems() {
        return getWebDriver().locator(".ui-autocomplete-items");
    }

    public List<String> getItemValues() {
        List<Locator> itemElements = getItems().locator(".ui-autocomplete-item").all();
        return itemElements.stream().map(Locator::innerText).collect(Collectors.toList());
    }

    public Locator getPanel() {
        return panel;
    }

    public Locator getDropDownButton() {
        return findElement(By.className("ui-autocomplete-dropdown"));
    }

    public String getValue() {
        return getInput().inputValue();
    }

    /**
     * Is the input using AJAX "clear" event?
     *
     * @return true if using AJAX for clear
     */
    public boolean isClearAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "clear");
    }

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

    /**
     * Is the input using AJAX "query" event?
     *
     * @return true if using AJAX for query
     */
    public boolean isQueryAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "query");
    }

    /**
     * If using multiple mode gets the values of the tokens.
     *
     * @return the values in a list
     */
    public List<String> getValues() {
        List<Locator> tokens = getTokens();
        return tokens.stream()
                    .map(token -> token.locator(".ui-autocomplete-token-label").innerText())
                    .collect(Collectors.toList());
    }

    /**
     * Gets the actual token elements in mutliple mode.
     *
     * @return the List of tokens
     */
    public List<Locator> getTokens() {
        return findElements(By.cssSelector("ul li.ui-autocomplete-token"));
    }

    /**
     * Sets the value and presses tab afterwards. Attention: Pressing tab selects the first suggested value.
     *
     * @param value the value to set
     */
    public void setValue(String value) {
        int delay = setValueWithoutTab(value);
        if (delay > 0) {
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
        sendTabKey();
    }

    /**
     * Sets the value without pressing tab afterwards.
     *
     * @param value the value to set
     * @return the delay in milliseconds
     */
    public int setValueWithoutTab(Serializable value) {
        Locator input = getInput();
        input.clear();
        ComponentUtils.sendKeys(input, value.toString());
        int delay = getDelay();
        PrimePlaywright.wait(delay * 2);
        return delay;
    }

    /**
     * Sends the Tab-Key to jump to the next input. Attention: Pressing tab selects the first suggested value.
     */
    public void sendTabKey() {
        if (isOnchangeAjaxified()) {
            PrimePlaywright.guardAjax(getInput()).press(Keys.TAB.toString());
        }
        else {
            getInput().press(Keys.TAB.toString());
        }
    }

    /**
     * Clears the Autocomplete input and guards AJAX for "clear" event.
     */
    @Override
    public void clear() {
        PrimePlaywright.clearInput(getInput(), isClearAjaxified());
    }

    /**
     * Waits until the AutoComplete-Panel containing the suggestions shows up. (eg after typing)
     */
    public void waitForPanel() {
        PrimePlaywright.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
    }

    /**
     * Shows the AutoComplete-Panel.
     */
    public void show() {
        Locator panel = getPanel();
        if (isEnabled() && !panel.isVisible()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".show();");
            waitForPanel();
        }
    }

    /**
     * Hides the AutoComplete-Panel.
     */
    public void hide() {
        Locator panel = getPanel();
        if (isEnabled() && panel.isVisible()) {
            PrimePlaywright.executeScript(getWidgetByIdScript() + ".hide();");
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    /**
     * Activates search behavior
     */
    public void activate() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".activate();");
    }

    /**
     * Deactivates search behavior
     */
    public void deactivate() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".deactivate();");
    }

    /**
     * Adds an item to the input field. Especially useful in 'multiple' mode.
     *
     * @param item the item to add to the tokens
     */
    public void addItem(String item) {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".addItem('" + item + "');");
    }

    /**
     * Removes an item from the input field. Especially useful in 'multiple' mode.
     *
     * @param item the item to remove from the tokens
     */
    public void removeItem(String item) {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".removeItem('" + item + "');");
    }

    /**
     * Execute the AutoComplete search.
     *
     * @param value the search to execute
     */
    public void search(String value) {
        // search always uses AJAX no matter what
        PrimePlaywright.executeScript(true, getWidgetByIdScript() + ".search(arguments[0]);", value);
        waitForPanel();
    }

    /**
     * Delay to wait in milliseconds before sending each query to the server.
     *
     * @return Delay in milliseconds.
     */
    public int getDelay() {
        return getWidgetConfiguration().getInt("delay");
    }
}



