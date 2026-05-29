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
package org.primefaces.playwright.spi;

import org.primefaces.playwright.internal.ConfigProvider;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightProvider {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightProvider() {
    }

    public static Page get() {
        return get(false);
    }

    public static void set(Page page) {
        PAGE.set(page);
    }

    public static Page get(boolean create) {
        Page page = PAGE.get();
        if (page == null && create) {
            ConfigProvider config = ConfigProvider.getInstance();
            PlaywrightAdapter adapter = config.getPlaywrightAdapter();

            Playwright playwright = Playwright.create();
            PLAYWRIGHT.set(playwright);

            Browser browser = adapter.createBrowser(playwright);
            BROWSER.set(browser);

            int width = config.isWebdriverHeadless() ? 1920 : 1280;
            int height = config.isWebdriverHeadless() ? 1080 : 1000;

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(width, height));
            CONTEXT.set(context);

            page = context.newPage();
            PAGE.set(page);

            // Register onload tracking scripts (pfselenium context)
            for (String script : config.getOnloadScripts()) {
                page.addInitScript(script);
            }
        }
        return page;
    }

    public static void quit() {
        Page page = PAGE.get();
        if (page != null) {
            try {
                page.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
        BrowserContext context = CONTEXT.get();
        if (context != null) {
            try {
                context.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
        Browser browser = BROWSER.get();
        if (browser != null) {
            try {
                browser.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
        Playwright playwright = PLAYWRIGHT.get();
        if (playwright != null) {
            try {
                playwright.close();
            }
            catch (Exception e) {
                // ignore
            }
        }

        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}
