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

import org.primefaces.playwright.PrimePlaywright;

import com.microsoft.playwright.Locator;
public final class ComponentUtils {

    private ComponentUtils() {
        // prevent instantiation
    }

    public static boolean hasAjaxBehavior(Locator element, String behavior) {
        if (!hasBehavior(element, behavior)) {
            return false;
        }

        String id = element.getAttribute("id");
        String result = PrimePlaywright.executeScript("return " + getWidgetByIdScript(id) + ".getBehavior('" + behavior + "').toString();");
        return isAjaxScript(result);
    }

    public static boolean hasBehavior(Locator element, String behavior) {
        if (!isWidget(element)) {
            return false;
        }

        String id = element.getAttribute("id");
        return PrimePlaywright.executeScript("return " + getWidgetByIdScript(id) + ".hasBehavior('" + behavior + "');");
    }

    public static boolean isWidget(Locator element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            return false;
        }

        return PrimePlaywright.executeScript("return " + getWidgetByIdScript(id) + " != null;");
    }

    public static boolean isAjaxScript(String script) {
        if (script == null || script.isEmpty()) {
            return false;
        }

        return script.contains("PrimeFaces.ab(") || script.contains("pf.ab(")
                || script.contains("mojarra.ab(")
                || script.contains("myfaces.ab(")
                || script.contains("jsf.ajax.request") || script.contains("faces.ajax.request");
    }

    public static String getWidgetConfiguration(Locator element) {
        String id = element.getAttribute("id");
        return PrimePlaywright.executeScript("return JSON.stringify(" + getWidgetByIdScript(id) + ".cfg, function(key, value) {\n" +
                    "  if (typeof value === 'function') {\n" +
                    "    return value.toString();\n" +
                    "  } else if (value && value.constructor && value.constructor.name === 'RegExp') {\n" +
                    "    return value.toString();\n" +
                    "  } else {\n" +
                    "    return value;\n" +
                    "  }\n" +
                    "});");
    }

    public static String getWidgetByIdScript(String id) {
        return "PrimeFaces.getWidgetById('" + id + "')";
    }

    /**
     * Types text into an input using Playwright's {@code pressSequentially}, which handles
     * inter-key timing natively across all browsers.
     *
     * @param input the input component to send keys to
     * @param value the value to send to the input
     */
    public static void sendKeys(Locator input, CharSequence value) {
        if (input == null || value == null) {
            return;
        }
        input.pressSequentially(value.toString());
    }

}



