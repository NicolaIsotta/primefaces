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

import org.primefaces.playwright.internal.ConfigProvider;
import org.primefaces.playwright.internal.Guard;
import org.primefaces.playwright.spi.DeploymentAdapter;
import org.primefaces.playwright.spi.PlaywrightProvider;
import org.primefaces.playwright.spi.PrimePageFactory;
import org.primefaces.playwright.spi.PrimePageFragmentFactory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public final class PrimePlaywright {

    private PrimePlaywright() {
        super();
    }

    public static Page getPage() {
        return PlaywrightProvider.get();
    }

    public static WaitGui waitGui() {
        return new WaitGui(10000);
    }

    public static WaitGui waitGui(int timeoutMs) {
        return new WaitGui(timeoutMs);
    }

    public static <T> T createFragment(Class<T> fragmentClass, String selector) {
        return PrimePageFragmentFactory.create(fragmentClass, getPage(), getPage().locator(selector));
    }

    public static <T> T createFragment(Class<T> fragmentClass, Locator locator) {
        return PrimePageFragmentFactory.create(fragmentClass, getPage(), locator);
    }

    public static <T extends AbstractPrimePage> T goTo(Class<T> pageClass) {
        Page page = PlaywrightProvider.get();
        T primePage = PrimePageFactory.create(pageClass, page);
        page.navigate(getUrl(primePage));
        return primePage;
    }

    public static void goTo(AbstractPrimePage page) {
        Page playwrightPage = PlaywrightProvider.get();
        playwrightPage.navigate(getUrl(page));
    }

    public static void goTo(String partialUrl) {
        Page page = PlaywrightProvider.get();
        page.navigate(getUrl(partialUrl));
    }

    public static String getUrl(AbstractPrimePage page) {
        String baseLocation = page.getBaseLocation();
        if (baseLocation == null) {
            baseLocation = getBaseUrl();
        }
        if (baseLocation == null) {
            DeploymentAdapter deploymentAdapter = ConfigProvider.getInstance().getDeploymentAdapter();
            String message = "Cannot determine base url. Please either configure " + ConfigProvider.DEPLOYMENT_BASEURL + " or " +
                    (deploymentAdapter != null ?
                            ("implement " + deploymentAdapter.getClass().getCanonicalName() + "#getBaseUrl") :
                            ("define " + ConfigProvider.DEPLOYMENT_ADAPTER + " with implemented DeploymentAdapter#getBaseUrl")) +
                    " or implement " + page.getClass().getCanonicalName() + "#getBaseLocation";
            throw new RuntimeException(message);
        }
        return baseLocation + page.getLocation();
    }

    public static String getUrl(String url) {
        String baseUrl = getBaseUrl();
        if (baseUrl == null) {
            DeploymentAdapter deploymentAdapter = ConfigProvider.getInstance().getDeploymentAdapter();
            String message = "Cannot determine base url. Please either configure " + ConfigProvider.DEPLOYMENT_BASEURL + " or " +
                    (deploymentAdapter != null ?
                            ("implement " + deploymentAdapter.getClass().getCanonicalName() + "#getBaseUrl") :
                            ("define " + ConfigProvider.DEPLOYMENT_ADAPTER + " with implemented DeploymentAdapter#getBaseUrl"));
            throw new RuntimeException(message);
        }
        return baseUrl + url;
    }

    public static String getBaseUrl() {
        DeploymentAdapter deploymentAdapter = ConfigProvider.getInstance().getDeploymentAdapter();
        if (deploymentAdapter != null) {
            return deploymentAdapter.getBaseUrl();
        }
        return ConfigProvider.getInstance().getDeploymentBaseUrl();
    }

    public static boolean hasCssClass(Locator element, String... cssClass) {
        String elementClass = element.getAttribute("class");
        if (elementClass == null) {
            return false;
        }

        String[] elementClasses = elementClass.split(" ");

        boolean result = true;
        for (String expected : cssClass) {
            boolean found = false;
            for (String actual : elementClasses) {
                if (actual.equalsIgnoreCase(expected)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                result = false;
                break;
            }
        }

        return result;
    }

    public static boolean hasCssClass(WrapsLocator element, String... cssClass) {
        return hasCssClass(element.getWrappedLocator(), cssClass);
    }

    public static boolean isElementPresent(String selector) {
        return getPage().locator(selector).count() > 0;
    }

    public static boolean isElementPresent(Locator element) {
        return element.count() > 0;
    }

    public static boolean isElementPresent(WrapsLocator element) {
        return isElementPresent(element.getWrappedLocator());
    }

    public static boolean isElementDisplayed(String selector) {
        return getPage().locator(selector).isVisible();
    }

    public static boolean isElementDisplayed(Locator element) {
        return element.isVisible();
    }

    public static boolean isElementDisplayed(WrapsLocator element) {
        return isElementDisplayed(element.getWrappedLocator());
    }

    public static boolean isVisibleInViewport(Locator element) {
        if (!isElementDisplayed(element)) {
            return false;
        }
        return (Boolean) element.evaluate("elem => {"
                + "  var box = elem.getBoundingClientRect(),"
                + "      cx = box.left + box.width / 2,"
                + "      cy = box.top + box.height / 2,"
                + "      e = document.elementFromPoint(cx, cy);"
                + "  for (; e; e = e.parentElement) {"
                + "      if (e === elem) { return true; }"
                + "  }"
                + "  return false;"
                + "}");
    }

    public static boolean isVisibleInViewport(WrapsLocator element) {
        return isVisibleInViewport(element.getWrappedLocator());
    }

    public static boolean isElementEnabled(String selector) {
        return getPage().locator(selector).isEnabled();
    }

    public static boolean isElementEnabled(Locator element) {
        return element.isEnabled() && !hasCssClass(element, "ui-state-disabled");
    }

    public static boolean isElementEnabled(WrapsLocator element) {
        return isElementEnabled(element.getWrappedLocator());
    }

    public static boolean isElementClickable(Locator element) {
        return isElementDisplayed(element) &&
                    isElementEnabled(element) &&
                    !hasCssClass(element, "ui-state-disabled") &&
                    !Boolean.parseBoolean(element.getAttribute("aria-busy"));
    }

    public static boolean isElementClickable(WrapsLocator element) {
        return isElementClickable(element.getWrappedLocator());
    }

    public static <T> T guardHttp(T target) {
        return Guard.http(target);
    }

    public static <T> T guardAjax(T target) {
        return Guard.ajax(target);
    }

    public static <T> T guardAjax(T target, int delayInMilliseconds) {
        return Guard.ajax(target, delayInMilliseconds);
    }

    public static <T> T guardAjax(String script, Object... args) {
        return Guard.ajax(script, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeScript(String script, Object... args) {
        return (T) getPage().evaluate(script, args);
    }

    public static <T> T executeScript(boolean isAjaxified, String script, Object... args) {
        if (isAjaxified) {
            return guardAjax(script, args);
        }
        else {
            return executeScript(script, args);
        }
    }

    public static void disableAnimations() {
        executeScript("if (window.PrimeFaces) { $(function() { PrimeFaces.utils.disableAnimations(); }); }");
    }

    public static void enableAnimations() {
        executeScript("if (window.PrimeFaces) { $(function() { PrimeFaces.utils.enableAnimations(); }); }");
    }

    public static void setHiddenInput(Locator input, String value) {
        executeScript("document.getElementById('" + input.getAttribute("id") + "').value='" + value + "'");
    }

    public static void setHiddenInput(WrapsLocator input, String value) {
        setHiddenInput(input.getWrappedLocator(), value);
    }

    public static void clearInput(Locator input, boolean isAjaxified) {
        if (isAjaxified) {
            guardAjax(input).clear();
        }
        else {
            input.clear();
        }
    }

    public static void clearInput(WrapsLocator input, boolean isAjaxified) {
        clearInput(input.getWrappedLocator(), isAjaxified);
    }

    public static boolean isChrome() {
        return "chromium".equalsIgnoreCase(getPage().context().browser().browserType().name());
    }

    public static boolean isFirefox() {
        return "firefox".equalsIgnoreCase(getPage().context().browser().browserType().name());
    }

    public static boolean isSafari() {
        return "webkit".equalsIgnoreCase(getPage().context().browser().browserType().name());
    }

    public static boolean isMacOs() {
        String os = System.getProperty("os.name").toUpperCase();
        return (os.contains("DARWIN")) || (os.contains("MAC"));
    }

    public static boolean isHeadless() {
        return ConfigProvider.getInstance().isWebdriverHeadless();
    }

    public static void wait(int milliseconds) {
        if (milliseconds > 0) {
            try {
                Thread.sleep(milliseconds);
            }
            catch (InterruptedException ex) {
                System.err.println("Wait was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
    }
}
