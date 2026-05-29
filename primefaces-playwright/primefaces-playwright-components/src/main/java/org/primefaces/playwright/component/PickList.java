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
import org.primefaces.playwright.Keys;
import org.primefaces.playwright.PrimeExpectedConditions;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import org.json.JSONObject;
/**
 * Component wrapper for the PrimeFaces {@code p:pickList}.
 */
public abstract class PickList extends AbstractComponent {

    private static final String ITEM_LABEL_ATTR = "data-item-label";
    private static final String ITEM_VALUE_ATTR = "data-item-value";
    private static final By CHECK_SELECTOR = By.cssSelector("div[class*='ui-chkbox']");

    @FindBy(css = "button[class*='ui-picklist-button-add']")
    private Locator add;

    @FindBy(css = "button[class*='ui-picklist-button-add-all']")
    private Locator addAll;

    @FindBy(css = "button[class*='ui-picklist-button-remove']")
    private Locator remove;

    @FindBy(css = "button[class*='ui-picklist-button-remove-all']")
    private Locator removeAll;

    @FindBy(css = "ul[class*='ui-picklist-source']")
    private Locator sourceList;

    @FindBy(css = "ul[class*='ui-picklist-target']")
    private Locator targetList;

    @FindByParentPartialId("_source_filter")
    private Locator sourceFilter;

    @FindByParentPartialId("_target_filter")
    private Locator targetFilter;

    /**
     * Picks the given items from the source list.
     *
     * @param items the items to pick. These can either be the labels displayed in the list or the underlying values.
     */
    public void pick(final String... items) {
        selectAndAction(sourceList, add, items);
    }

    /**
     * Picks all the items in the source list.
     */
    public void pickAll() {
        addAll.click();
    }

    /**
     * Picks the given items from the target list.
     *
     * @param items the items to remove. These can either be the labels displayed in the list or the underlying values.
     */
    public void remove(final String... items) {
        selectAndAction(targetList, remove, items);
    }

    /**
     * Removes all the items in the target list.
     */
    public void removeAll() {
        removeAll.click();
    }

    /**
     * Selects the given items in the source list and moves them up.
     *
     * @param items the items to move up. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveSourceItemsUp(final String... items) {
        final Locator moveUp = getMoveUpButton(getSourceControls());
        selectAndAction(sourceList, moveUp, items);
    }

    /**
     * Selects the given items in the source list and moves them down.
     *
     * @param items the items to move down. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveSourceItemsDown(final String... items) {
        final Locator moveDown = getMoveDownButton(getSourceControls());
        selectAndAction(sourceList, moveDown, items);
    }

    /**
     * Selects the given items in the source list and moves them to the top of the list.
     *
     * @param items the items to move to the top. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveSourceItemsToTop(final String... items) {
        final Locator moveTop = getMoveTopButton(getSourceControls());
        selectAndAction(sourceList, moveTop, items);
    }

    /**
     * Selects the given items in the source list and moves them to the bottom of the list.
     *
     * @param items the items to move to the bottom. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveSourceItemsToBottom(final String... items) {
        final Locator moveBottom = getMoveBottomButton(getSourceControls());
        selectAndAction(sourceList, moveBottom, items);
    }

    /**
     * Selects the given items in the target list and moves them up.
     *
     * @param items the items to move up. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveTargetItemsUp(final String... items) {
        final Locator moveUp = getMoveUpButton(getTargetControls());
        selectAndAction(targetList, moveUp, items);
    }

    /**
     * Selects the given items in the target list and moves them down.
     *
     * @param items the items to move down. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveTargetItemsDown(final String... items) {
        final Locator moveDown = getMoveDownButton(getTargetControls());
        selectAndAction(targetList, moveDown, items);
    }

    /**
     * Selects the given items in the target list and moves them to the top
     *
     * @param items the items to move to the top. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveTargetItemsToTop(final String... items) {
        final Locator moveTop = getMoveTopButton(getTargetControls());
        selectAndAction(targetList, moveTop, items);
    }

    /**
     * Selects the given items in the target list and moves them to the bottom
     *
     * @param items the items to move to the bottom. These can either be the labels displayed in the list or the underlying values.
     */
    public void moveTargetItemsToBottom(final String... items) {
        final Locator moveBottom = getMoveBottomButton(getTargetControls());
        selectAndAction(targetList, moveBottom, items);
    }

    /**
     * Filters the source list with the provided filter value
     *
     * @param filterValue the value to use for filtering the list
     */
    public void filterSourceList(final String filterValue) {
        setFilterValue(sourceFilter, filterValue);
    }

    /**
     * Filters the target list with the provided filter value
     *
     * @param filterValue the value to use for filtering the list
     */
    public void filterTargetList(final String filterValue) {
        setFilterValue(targetFilter, filterValue);
    }

    /**
     * Clears the source list filter
     */
    public void clearSourceListFilter() {
        setFilterValue(sourceFilter, "");
    }

    /**
     * Clears the target list filter
     */
    public void clearTargetListFilter() {
        setFilterValue(targetFilter, "");
    }

    /**
     * Filter using the Widget configuration "filterDelay" and "filterEvent" values.
     *
     * @param filterElement the source or target filter
     * @param filterValue the value to set the filter
     */
    public void setFilterValue(Locator filterElement, String filterValue) {
        JSONObject cfg = getWidgetConfiguration();
        String filterEvent = cfg.getString("filterEvent");
        int filterDelay = cfg.getInt("filterDelay");
        setFilterValue(filterElement, filterValue, filterEvent, filterDelay);
    }

    /**
     * Filter the column using these values.
     *
     * @param filterElement the source or target filter
     * @param filterValue the value to filter for
     * @param filterEvent the event causing the filter to trigger such as "keyup" or "enter"
     * @param filterDelay the delay in milliseconds if a "keyup" filter
     */
    public void setFilterValue(Locator filterElement, String filterValue, String filterEvent, int filterDelay) {
        PrimePlaywright.clearInput(filterElement, false);

        Keys triggerKey = null;
        filterEvent = filterEvent.toLowerCase(Locale.ROOT);
        switch (filterEvent) {
            case "keyup":
            case "keydown":
            case "keypress":
            case "input":
                if (filterDelay == 0) {
                    filterElement = PrimePlaywright.guardAjax(filterElement);
                }
                break;
            case "enter":
                triggerKey = Keys.ENTER;
                break;
            case "change":
            case "blur":
                triggerKey = Keys.TAB;
                break;
            default:
                break;
        }

        if (filterValue != null) {
            ComponentUtils.sendKeys(filterElement, filterValue);
        }
        else {
            // null filter press backspace to trigger the re-filtering
            filterElement.press("Backspace");
        }

        if (triggerKey != null) {
            filterElement.press(triggerKey.toString());
        }
        else if (filterDelay > 0) {
            PrimePlaywright.wait(filterDelay + 10);
            PrimePlaywright.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
    }

    /**
     * Gets a list of the values in the source list
     *
     * @return the list of values
     */
    public List<String> getSourceListValues() {
        return getSourceListElements().stream()
                    .map(e -> e.getAttribute(ITEM_VALUE_ATTR)).collect(Collectors.toList());
    }

    /**
     * Gets a list of the labels in the source list
     *
     * @return the list of labels
     */
    public List<String> getSourceListLabels() {
        return getSourceListElements().stream()
                    .map(e -> e.getAttribute(ITEM_LABEL_ATTR)).collect(Collectors.toList());
    }

    /**
     * Gets a list of the items in the source list
     *
     * @return the items in the source list
     */
    public List<Locator> getSourceListElements() {
        return sourceList.locator("li").all().stream().filter(e -> PrimePlaywright.isElementDisplayed(e))
                    .collect(Collectors.toList());
    }

    /**
     * Gets a list of the values in the target list
     *
     * @return the list of values
     */
    public List<String> getTargetListValues() {
        return getTargetListElements().stream()
                    .map(e -> e.getAttribute(ITEM_VALUE_ATTR)).collect(Collectors.toList());
    }

    /**
     * Gets a list of the labels in the target list
     *
     * @return the list of labels
     */
    public List<String> getTargetListLabels() {
        return getTargetListElements().stream()
                    .map(e -> e.getAttribute(ITEM_LABEL_ATTR)).collect(Collectors.toList());
    }

    /**
     * Gets the list of items in the target list
     *
     * @return the items in the target list
     */
    public List<Locator> getTargetListElements() {
        return targetList.locator("li").all().stream().filter(e -> PrimePlaywright.isElementDisplayed(e))
                    .collect(Collectors.toList());
    }

    /**
     * Determines if the pick list has checkbox selection enabled
     *
     * @return true if checkbox selection is enabled, false otherwise
     */
    public boolean isCheckboxSelectionEnabled() {
        // Check the first source item for a checkbox
        final List<Locator> sourceElements = getSourceListElements();
        if (!sourceElements.isEmpty()) {
            return getCheckbox(sourceElements.get(0)) != null;
        }

        // Check the first target item for a checkbox if the source list is empty
        final List<Locator> targetElements = getTargetListElements();
        if (!targetElements.isEmpty()) {
            return getCheckbox(targetElements.get(0)) != null;
        }

        return false;
    }

    /**
     * Gets the source control element housing the source control buttons
     *
     * @return the source control element
     */
    private Locator getSourceControls() {
        return findElement(By.cssSelector("div[class*='ui-picklist-source-controls']"));
    }

    /**
     * Gets the target control element housing the target control buttons
     *
     * @return the target control element
     */
    private Locator getTargetControls() {
        return findElement(By.cssSelector("div[class*='ui-picklist-target-controls']"));
    }

    /**
     * Gets the "Move up" button from the given {@link Locator}
     *
     * @param controls either the source or target controls for the picklist
     * @return the "Move up" button
     */
    private Locator getMoveUpButton(final Locator controls) {
        return controls.locator("button[title='Move Up']");
    }

    /**
     * Gets the "Move down" button from the given {@link Locator}
     *
     * @param controls either the source or target controls for the picklist
     * @return the "Move down" button
     */
    private Locator getMoveDownButton(final Locator controls) {
        return controls.locator("button[title='Move Down']");
    }

    /**
     * Gets the "Move top" button from the given {@link Locator}
     *
     * @param controls either the source or target controls for the picklist
     * @return the "Move top" button
     */
    private Locator getMoveTopButton(final Locator controls) {
        return controls.locator("button[title='Move Top']");
    }

    /**
     * Gets the "Move bottom" button from the given {@link Locator}
     *
     * @param controls either the source or target controls for the picklist
     * @return the "Move bottom" button
     */
    private Locator getMoveBottomButton(final Locator controls) {
        return controls.locator("button[title='Move Bottom']");
    }

    /**
     * Selects the array of items in the given list and then performs an action.
     *
     * @param list the list from which to select the items
     * @param actionButton the action button which should be clicked to perform the action
     * @param items the array of items to select
     */
    private void selectAndAction(final Locator list, final Locator actionButton, final String... items) {
        final boolean hasCheckboxes = isCheckboxSelectionEnabled();
        for (final String item : items) {
            final Locator element = findListItem(list, item);

            if (hasCheckboxes) {
                final Locator checkbox = getCheckbox(element);
                checkbox.click();
            }
            else {
                element.click();
                actionButton.click();
            }
        }

        if (hasCheckboxes && PrimePlaywright.isElementEnabled(actionButton)) {
            actionButton.click();
        }
    }

    /**
     * Finds a list item with either a value or label that matches the input string
     *
     * @param listElement
     * @param item the item to find
     * @return the {@link Locator} of the item if it is found
     * @throws NoSuchElementException thrown if the element could not be found
     */
    private Locator findListItem(final Locator listElement, final String item) {
        final Optional<Locator> listItem = listElement.locator("li").all().stream().filter(e -> {
            final String value = e.getAttribute(ITEM_VALUE_ATTR);
            final String label = e.getAttribute(ITEM_LABEL_ATTR);
            return value.equals(item) || label.equals(item);
        }).findFirst();

        if (listItem.isPresent()) {
            return listItem.get();
        }

        throw new NoSuchElementException("Could not find element " + item + " in picklist " + getId());
    }

    /**
     * Gets the first checkbox in the context of the {@link Locator}
     *
     * @param element the {@link Locator} from which to get the checkbox
     * @return the checkbox
     * @throws NoSuchElementException thrown if the checkbox could not be found
     */
    private Locator getCheckbox(final Locator element) {
        return element.locator(CHECK_SELECTOR.getSelector()).all().stream().findFirst().orElse(null);
    }
}



