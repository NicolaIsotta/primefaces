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

import java.util.function.BooleanSupplier;

import com.microsoft.playwright.Locator;

public class PrimeExpectedConditions {

    private PrimeExpectedConditions() {
    }

    public static BooleanSupplier animationNotActive() {
        return () -> {
            try {
                Object val = PlaywrightProvider.get().evaluate(
                        "() => typeof jQuery !== 'undefined' ? jQuery(':animated').length === 0 : true");
                return Boolean.TRUE.equals(val);
            }
            catch (Exception e) {
                return false;
            }
        };
    }

    public static BooleanSupplier visibleAndAnimationComplete(Locator locator) {
        return () -> {
            try {
                boolean visible = locator.isVisible();
                return visible && Boolean.TRUE.equals(locator.evaluate(
                        "el => typeof jQuery !== 'undefined' ? !jQuery(el).is(':animated') : true"));
            }
            catch (Exception e) {
                return false;
            }
        };
    }

    public static BooleanSupplier visibleAndAnimationComplete(WrapsLocator element) {
        return visibleAndAnimationComplete(element.getWrappedLocator());
    }

    public static BooleanSupplier invisibleAndAnimationComplete(Locator locator) {
        return () -> {
            try {
                boolean invisible = !locator.isVisible();
                return invisible && Boolean.TRUE.equals(locator.evaluate(
                        "el => typeof jQuery !== 'undefined' ? !jQuery(el).is(':animated') : true"));
            }
            catch (Exception e) {
                return false;
            }
        };
    }

    public static BooleanSupplier invisibleAndAnimationComplete(WrapsLocator element) {
        return invisibleAndAnimationComplete(element.getWrappedLocator());
    }

    public static BooleanSupplier script(String js) {
        return () -> {
            try {
                String script = js;
                if (script.startsWith("return ")) {
                    script = "() => { " + script + " }";
                }
                else {
                    script = "() => " + script;
                }
                Object val = PlaywrightProvider.get().evaluate(script);
                return Boolean.TRUE.equals(val);
            }
            catch (Exception e) {
                return false;
            }
        };
    }
}
