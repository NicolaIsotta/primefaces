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

import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.io.Serializable;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:textEditor}.
 */
public abstract class TextEditor extends InputText {

    @FindByParentPartialId(value = "_input", name = "_input")
    private Locator input;

    @FindByParentPartialId("_editor")
    private Locator editor;

    @FindByParentPartialId("_toolbar")
    private Locator toolbar;

    @Override
    public Locator getInput() {
        return input;
    }

    public Locator getEditor() {
        return editor;
    }

    public Locator getToolbar() {
        return toolbar;
    }

    /**
     * Finds an returns the current contents of the editor.
     *
     * @return The current contents of the editor, as an HTML string.
     */
    public String getEditorValue() {
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript() + ".getEditorValue();");
    }

    @Override
    public void setValue(Serializable value) {
        if (value == null) {
            value = "\"\"";
        }

        PrimePlaywright.executeScript(getWidgetByIdScript() + ".setValue('" + value.toString() + "');");
    }

    /**
     * Clears the entire text of the editor.
     */
    @Override
    public void clear() {
        PrimePlaywright.executeScript(getWidgetByIdScript() + ".clear();");
    }

}



