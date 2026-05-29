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

import org.primefaces.playwright.AbstractPrimePage;
import org.primefaces.playwright.PrimePlaywright;
import org.primefaces.playwright.spi.PlaywrightProvider;
import org.primefaces.playwright.spi.PrimePageFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class PageInjectionExtension implements ParameterResolver, TestInstancePostProcessor {

    private final List<Class<? extends Annotation>> injectMarkerAnnotations;

    public PageInjectionExtension() {
        injectMarkerAnnotations = new ArrayList<>();

        try {
            injectMarkerAnnotations.add((Class<? extends Annotation>) Class.forName("javax.inject.Inject"));
        }
        catch (ClassNotFoundException e) {
            // Ignore
        }

        try {
            injectMarkerAnnotations.add((Class<? extends Annotation>) Class.forName("jakarta.inject.Inject"));
        }
        catch (ClassNotFoundException e) {
            // Ignore
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {

        return AbstractPrimePage.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {

        Page page = PlaywrightProvider.get(true);

        AbstractPrimePage primePage = PrimePageFactory.create((Class<? extends AbstractPrimePage>) parameterContext.getParameter().getType(), page);

        PrimePlaywright.goTo(primePage);

        return primePage;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if ((injectMarkerAnnotations.stream().anyMatch(it -> field.getAnnotation(it) != null))
                        && AbstractPrimePage.class.isAssignableFrom(field.getType())
                        && field.get(testInstance) == null) {

                Page page = PlaywrightProvider.get(true);

                AbstractPrimePage primePage = PrimePageFactory.create((Class<? extends AbstractPrimePage>) field.getType(), page);

                field.set(testInstance, primePage);
            }
        }
    }
}
