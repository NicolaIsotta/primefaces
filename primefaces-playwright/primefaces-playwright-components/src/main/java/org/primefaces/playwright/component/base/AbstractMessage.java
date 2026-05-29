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
package org.primefaces.playwright.component.base;

import org.primefaces.playwright.FindBy;

import com.microsoft.playwright.Locator;
public abstract class AbstractMessage extends AbstractComponent {

    @FindBy(css = ".ui-message-error-icon")
    private Locator iconError;
    @FindBy(css = ".ui-message-info-icon")
    private Locator iconInfo;
    @FindBy(css = ".ui-message-warn-icon")
    private Locator iconWarn;
    @FindBy(css = ".ui-message-fatal-icon")
    private Locator iconFatal;
    @FindBy(css = ".ui-message-error-summary")
    private Locator summaryError;
    @FindBy(css = ".ui-message-info-summary")
    private Locator summaryInfo;
    @FindBy(css = ".ui-message-warn-summary")
    private Locator summaryWarn;
    @FindBy(css = ".ui-message-fatal-summary")
    private Locator summaryFatal;
    @FindBy(css = ".ui-message-error-detail")
    private Locator detailError;
    @FindBy(css = ".ui-message-info-detail")
    private Locator detailInfo;
    @FindBy(css = ".ui-message-warn-detail")
    private Locator detailWarn;
    @FindBy(css = ".ui-message-fatal-detail")
    private Locator detailFatal;

    public Locator getIconError() {
        return iconError;
    }
    public Locator getIconInfo() {
        return iconInfo;
    }
    public Locator getIconWarn() {
        return iconWarn;
    }
    public Locator getIconFatal() {
        return iconFatal;
    }
    public Locator getDetailError() {
        return detailError;
    }
    public Locator getDetailInfo() {
        return detailInfo;
    }
    public Locator getDetailWarn() {
        return detailWarn;
    }
    public Locator getDetailFatal() {
        return detailFatal;
    }
    public Locator getSummaryError() {
        return summaryError;
    }
    public Locator getSummaryInfo() {
        return summaryInfo;
    }
    public Locator getSummaryWarn() {
        return summaryWarn;
    }
    public Locator getSummaryFatal() {
        return summaryFatal;
    }

}



