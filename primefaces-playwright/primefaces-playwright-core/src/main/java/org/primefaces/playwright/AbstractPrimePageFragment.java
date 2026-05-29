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
package org.primefaces.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class AbstractPrimePageFragment implements WrapsLocator {

    private Locator locator;
    private Page page;

    public Locator getLocator() {
        return locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Page getWebDriver() {
        return page;
    }

    @Override
    public Locator getWrappedLocator() {
        return getLocator();
    }

    public Locator getRoot() {
        return getLocator();
    }

    public String getId() {
        return getDomAttribute("id");
    }

    // Playwright native backward compatibility methods
    public String getText() {
        return getRoot().innerText();
    }

    public String getDomAttribute(String name) {
        return getRoot().getAttribute(name);
    }

    public String getDomProperty(String name) {
        Object val = getRoot().evaluate("el => el." + name);
        return val != null ? val.toString() : null;
    }

    public boolean isDisplayed() {
        return getRoot().isVisible();
    }

    public boolean isEnabled() {
        return getRoot().isEnabled();
    }

    public boolean isSelected() {
        return getRoot().isChecked();
    }

    public String getTagName() {
        return getLocator().evaluate("el => el.tagName").toString().toLowerCase();
    }

    public void sendKeys(CharSequence... keysToSend) {
        for (CharSequence keys : keysToSend) {
            String s = keys.toString();
            if (s.contains("+") || s.length() == 1 || isKeyName(s)) {
                getLocator().press(s);
            }
            else {
                getLocator().pressSequentially(s);
            }
        }
    }

    private boolean isKeyName(String s) {
        for (Keys key : Keys.values()) {
            if (key.toString().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public Locator findElement(By by) {
        return getRoot().locator(by.getSelector());
    }

    public java.util.List<Locator> findElements(By by) {
        return getRoot().locator(by.getSelector()).all();
    }
}