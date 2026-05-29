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

import org.primefaces.playwright.spi.PlaywrightProvider;

public abstract class AbstractPrimePage {

    private com.microsoft.playwright.Page page;

    public String getBaseLocation() {
        return null;
    }

    public abstract String getLocation();

    public void goTo() {
        PrimePlaywright.goTo(this);
    }

    public boolean isAt() {
        return PlaywrightProvider.get().url().contains(getLocation());
    }

    public com.microsoft.playwright.Page getPage() {
        return page;
    }

    public com.microsoft.playwright.Page getWebDriver() {
        return page;
    }

    public void setPage(com.microsoft.playwright.Page page) {
        this.page = page;
    }
}
