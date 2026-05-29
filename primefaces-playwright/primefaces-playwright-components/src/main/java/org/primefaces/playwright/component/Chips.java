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
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.component.base.AbstractInputComponent;
import org.primefaces.playwright.component.base.ComponentUtils;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:chips}.
 */
public abstract class Chips extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private Locator input;

    @Override
    public Locator getInput() {
        return input;
    }

    public List<String> getValues() {
        List<Locator> chipTokens = getChipTokens();
        return chipTokens.stream()
                    .map(token -> token.locator(".ui-chips-token-label").innerText())
                    .collect(Collectors.toList());
    }

    public List<Locator> getChipTokens() {
        return findElements(By.cssSelector("ul li.ui-chips-token"));
    }

    public void addValue(String value) {
        Locator chipsInput = getInput();
        ComponentUtils.sendKeys(chipsInput, value);
        if (ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect")) {
            PrimePlaywright.guardAjax(chipsInput).press(Keys.ENTER.toString());
        }
        else {
            chipsInput.press(Keys.ENTER.toString());
        }
    }

    public void removeValue(String value) {
        for (Locator chipToken : getChipTokens()) {
            if (chipToken.locator(".ui-chips-token-label").innerText().equals(value)) {
                Locator closeIcon = chipToken.locator(".ui-icon-close");
                if (ComponentUtils.hasAjaxBehavior(getRoot(), "itemUnselect")) {
                    PrimePlaywright.guardAjax(closeIcon).click();
                }
                else {
                    closeIcon.click();
                }
            }
        }
    }

    /**
     * Converts the current list into a separator delimited list for mass editing while keeping original order of the items or closes the editor turning the
     * values back into chips.
     */
    public void toggleEditor() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".toggleEditor();");
    }
}



