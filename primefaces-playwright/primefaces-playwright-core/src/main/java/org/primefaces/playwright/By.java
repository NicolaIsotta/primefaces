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

public class By {

    private final String selector;

    private By(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public static By id(String id) {
        return new By("[id=\"" + id + "\"]");
    }

    public static By name(String name) {
        return new By("[name=\"" + name + "\"]");
    }

    public static By className(String className) {
        return new By("." + className);
    }

    public static By cssSelector(String css) {
        return new By(css);
    }

    public static By xpath(String xpath) {
        return new By("xpath=" + xpath);
    }

    public static By tagName(String tagName) {
        return new By(tagName);
    }

    public static By linkText(String linkText) {
        return new By("a:has-text(\"" + linkText + "\")");
    }
}
