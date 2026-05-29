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
package org.primefaces.playwright.internal.junit;

import org.primefaces.playwright.internal.ConfigProvider;
import org.primefaces.playwright.spi.PlaywrightProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.microsoft.playwright.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class ScreenshotOnFailureExtension implements TestWatcher {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String screenshotDirectory = ConfigProvider.getInstance().getScreenshotDirectory();

        if (StringUtils.isNotBlank(screenshotDirectory)) {
            Page page = PlaywrightProvider.get();
            String filename = screenshotDirectory + LocalDateTime.now().format(DATE_TIME_FORMATTER) + "_" + UUID.randomUUID().toString();

            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filename + ".png")));
            // write additional textfile with context-information
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(filename + ".txt"))) {
                if (context.getTestMethod().isPresent()) {
                    printWriter.println("Test-method: " + context.getTestMethod().get());
                }
                printWriter.println("Test-description: " + context.getDisplayName());
                printWriter.println("");
                printWriter.println("Stacktrace: " + ExceptionUtils.getStackTrace(cause));
            }
            catch (IOException ex) {
                System.err.println("Failed to save screenshot of failed test: " + ex.getMessage());
            }
        }

        TestWatcher.super.testFailed(context, cause);
    }
}
