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

import org.primefaces.playwright.internal.junit.BootstrapExtension;
import org.primefaces.playwright.internal.junit.PageInjectionExtension;
import org.primefaces.playwright.internal.junit.PlaywrightExtension;
import org.primefaces.playwright.internal.junit.ScreenshotOnFailureExtension;
import org.primefaces.playwright.spi.PlaywrightProvider;

import java.lang.reflect.InvocationTargetException;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BootstrapExtension.class)
@ExtendWith(PlaywrightExtension.class)
@ExtendWith(PageInjectionExtension.class)
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class AbstractPrimePageTest {

    @BeforeEach
    public void beforeEach() {
        clearConsole();
    }

    protected void assertPresent(Locator element) {
        if (!PrimePlaywright.isElementPresent(element)) {
            Assertions.fail("Element should be present!");
        }
    }

    protected void assertPresent(WrapsLocator element) {
        assertPresent(element.getWrappedLocator());
    }

    protected void assertPresent(String selector) {
        if (!PrimePlaywright.isElementPresent(selector)) {
            Assertions.fail("Element should be present!");
        }
    }

    protected void assertNotPresent(Locator element) {
        if (PrimePlaywright.isElementPresent(element)) {
            Assertions.fail("Element should not be present!");
        }
    }

    protected void assertNotPresent(WrapsLocator element) {
        assertNotPresent(element.getWrappedLocator());
    }

    protected void assertNotPresent(String selector) {
        if (PrimePlaywright.isElementPresent(selector)) {
            Assertions.fail("Element should not be present!");
        }
    }

    protected void assertDisplayed(Locator element) {
        if (!PrimePlaywright.isElementDisplayed(element)) {
            Assertions.fail("Element should be displayed!");
        }
    }

    protected void assertDisplayed(WrapsLocator element) {
        assertDisplayed(element.getWrappedLocator());
    }

    protected void assertDisplayed(String selector) {
        if (!PrimePlaywright.isElementDisplayed(selector)) {
            Assertions.fail("Element should be displayed!");
        }
    }

    protected void assertNotDisplayed(Locator element) {
        if (PrimePlaywright.isElementDisplayed(element)) {
            Assertions.fail("Element should not be displayed!");
        }
    }

    protected void assertNotDisplayed(WrapsLocator element) {
        assertNotDisplayed(element.getWrappedLocator());
    }

    protected void assertNotDisplayed(String selector) {
        if (PrimePlaywright.isElementDisplayed(selector)) {
            Assertions.fail("Element should not be displayed!");
        }
    }

    protected void assertEnabled(Locator element) {
        if (!PrimePlaywright.isElementEnabled(element)) {
            Assertions.fail("Element should be enabled!");
        }
    }

    protected void assertEnabled(WrapsLocator element) {
        assertEnabled(element.getWrappedLocator());
    }

    protected void assertEnabled(String selector) {
        if (!PrimePlaywright.isElementEnabled(selector)) {
            Assertions.fail("Element should be enabled!");
        }
    }

    protected void assertDisabled(Locator element) {
        if (PrimePlaywright.isElementEnabled(element)) {
            Assertions.fail("Element should be disabled!");
        }
    }

    protected void assertDisabled(WrapsLocator element) {
        assertDisabled(element.getWrappedLocator());
    }

    protected void assertDisabled(String selector) {
        if (PrimePlaywright.isElementEnabled(selector)) {
            Assertions.fail("Element should be disabled!");
        }
    }

    protected void assertIsAt(AbstractPrimePage page) {
        assertIsAt(page.getLocation());
    }

    protected void assertClickable(Locator element) {
        if (!PrimePlaywright.isElementClickable(element)) {
            Assertions.fail("Element should be clickable!");
        }
    }

    protected void assertClickable(WrapsLocator element) {
        assertClickable(element.getWrappedLocator());
    }

    protected void assertClickableOrLoading(Locator element) {
        if (!PrimePlaywright.hasCssClass(element, "ui-state-loading") && !PrimePlaywright.isElementClickable(element)) {
            Assertions.fail("Element should be clickable or loading!");
        }
    }

    protected void assertClickableOrLoading(WrapsLocator element) {
        assertClickableOrLoading(element.getWrappedLocator());
    }

    protected void assertNotClickable(Locator element) {
        if (PrimePlaywright.isElementClickable(element)) {
            Assertions.fail("Element should not be clickable!");
        }
    }

    protected void assertNotClickable(WrapsLocator element) {
        assertNotClickable(element.getWrappedLocator());
    }

    protected void assertIsAt(Class<? extends AbstractPrimePage> pageClass) {
        String location;
        try {
            location = PrimePlaywright.getUrl(pageClass.getDeclaredConstructor().newInstance());
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        assertIsAt(location);
    }

    /**
     * Checks the browser console and asserts there are no SEVERE level messages.
     */
    protected void assertNoJavascriptErrors() {
        // Playwright console errors can be asserted via custom listeners if required.
    }

    /**
     * Clears the browser console.
     */
    protected void clearConsole() {
        PrimePlaywright.executeScript("console.clear();");
    }

    /**
     * Dumps to System.out or System.err any messages found in the browser console.
     */
    protected void printConsole() {
    }

    protected void assertIsAt(String relativePath) {
        Assertions.assertTrue(PlaywrightProvider.get().url().contains(relativePath));
    }

    protected <T extends AbstractPrimePage> T goTo(Class<T> pageClass) {
        return PrimePlaywright.goTo(pageClass);
    }

    protected void goTo(String partialUrl) {
        PrimePlaywright.goTo(partialUrl);
    }

    protected Page getPage() {
        return PlaywrightProvider.get();
    }

    /**
     * Asserts text of a web element and cleanses it of whitespace issues due to different browser results.
     *
     * @param element the element to check its text
     * @param text the text expected in the element
     */
    protected void assertText(Locator element, String text) {
        String actual = StringUtils.normalizeSpace(element.innerText()).trim();
        String expected = StringUtils.normalizeSpace(text).trim();
        Assertions.assertEquals(expected, actual);
    }

    protected void assertText(WrapsLocator element, String text) {
        assertText(element.getWrappedLocator(), text);
    }

    /**
     * Checks a Locator if it has a CSS class or classes. If more than one is listed then ALL must be found on the element.
     *
     * @param element the element to check
     * @param cssClasses the CSS class or classes to look for
     */
    protected void assertCss(Locator element, String... cssClasses) {
        String elementClass = element.getAttribute("class");
        if (elementClass == null) {
            Assertions.fail("Element did not have CSS 'class' attribute.");
            return;
        }

        String[] elementClasses = elementClass.split(" ");

        for (String expectedClass : cssClasses) {
            for (String expected : expectedClass.split(" ")) {
                boolean found = false;
                for (String actual : elementClasses) {
                    if (actual.equalsIgnoreCase(expected)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Assertions.fail("Element expected CSS class '" + expected + "' but was not found in '" + elementClass + "'.");
                    break;
                }
            }
        }
    }

    protected void assertCss(WrapsLocator element, String... cssClasses) {
        assertCss(element.getWrappedLocator(), cssClasses);
    }

    protected void noAjaxMinLoadAnimation() {
        setAjaxMinLoadAnimation(0);
    }

    protected void setAjaxMinLoadAnimation(int milliseconds) {
        if (milliseconds < 0) {
            throw new IllegalArgumentException("milliseconds cannot be negative");
        }
        PrimePlaywright.executeScript("PrimeFaces.ajax.minLoadAnimation = " + milliseconds + ";");
    }

    protected void waitAjaxMinLoadAnimation() {
        PrimePlaywright.wait(500);
    }

}
