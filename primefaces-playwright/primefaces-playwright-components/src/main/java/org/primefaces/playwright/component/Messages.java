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
package org.primefaces.playwright.component;

import org.primefaces.playwright.By;
import org.primefaces.playwright.component.base.AbstractComponent;
import org.primefaces.playwright.component.model.Msg;
import org.primefaces.playwright.component.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
/**
 * Component wrapper for the PrimeFaces {@code p:messages}.
 */
public abstract class Messages extends AbstractComponent {
    public Locator getCloseButton(String severity) {
        return findElement(By.cssSelector(".ui-messages-" + severity + " > .ui-messages-close"));
    }

    public boolean isEmpty() {
        if (!isDisplayed()) {
            return true;
        }
        return getAllMessages().isEmpty();
    }

    public List<Msg> getAllMessages() {
        List<Msg> result = new ArrayList<>();
        if (!isDisplayed()) {
            return result;
        }

        List<Locator> messagesSeverities = findElements(By.tagName("div"));
        for (Locator messageSeverity : messagesSeverities) {

            Severity severity = Severity.toSeverity(messageSeverity.getAttribute("class"));

            for (Locator message : messageSeverity.locator("li").all()) {
                Msg msg = new Msg();
                msg.setSeverity(severity);
                msg.setSummary(message.locator(".ui-messages-" + Severity.toName(severity) + "-summary").innerText());
                try {
                    msg.setDetail(message.locator(".ui-messages-" + Severity.toName(severity) + "-detail").innerText());
                }
                catch (NoSuchElementException e) {
                    // ignore, it's optional
                }

                result.add(msg);
            }
        }

        return result;
    }

    public Msg getMessage(int index) {
        List<Msg> allMessages = getAllMessages();

        if (allMessages.size() > index) {
            return allMessages.get(index);
        }

        return null;
    }

    public List<Msg> getMessagesBySeverity(Severity severity) {
        return getAllMessages().stream()
                    .filter(message -> severity.equals(message.getSeverity()))
                    .collect(Collectors.toList());
    }

    public List<String> getAllSummaries() {
        return getAllMessages().stream()
                    .map(Msg::getSummary)
                    .collect(Collectors.toList());
    }
}



