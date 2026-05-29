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
package org.primefaces.playwright.internal;

import org.primefaces.playwright.WrapsLocator;
import org.primefaces.playwright.spi.PlaywrightProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class Guard {

    private Guard() {
    }

    public static <T> T http(T target) {
        return proxy(target, (Object p, Method method, Object[] args) -> {
            Page page = PlaywrightProvider.get();
            try {
                page.evaluate("pfselenium.submitting = true;");

                Object result = method.invoke(target, args);

                waitUntilHttpCompletes(page);

                return result;
            }
            catch (PlaywrightException e) {
                throw new RuntimeException("Timeout/Error while waiting for document ready!", e);
            }
        });
    }

    public static <T> T ajax(String script, Object... args) {
        Page page = PlaywrightProvider.get();
        try {
            page.evaluate("pfselenium.xhr = 'somethingJustNotNull';");

            T result = (T) page.evaluate(script, args);

            waitUntilAjaxCompletes(page);

            return result;
        }
        catch (PlaywrightException e) {
            throw new RuntimeException("Timeout/Error while waiting for AJAX complete!", e);
        }
    }

    public static <T> T ajax(T target) {
        return ajax(target, 0);
    }

    public static <T> T ajax(T target, int delay) {
        return proxy(target, (Object p, Method method, Object[] args) -> {
            Page page = PlaywrightProvider.get();
            try {
                page.evaluate("pfselenium.xhr = 'somethingJustNotNull';");

                Object result = method.invoke(target, args);

                if (delay > 0) {
                    Thread.sleep(delay);
                }

                waitUntilAjaxCompletes(page);

                return result;
            }
            catch (PlaywrightException e) {
                throw new RuntimeException("Timeout/Error while waiting for AJAX complete!", e);
            }
            catch (InterruptedException e) {
                throw new RuntimeException("AJAX Guard delay was interrupted!", e);
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    public static Runnable custom(Runnable runnable, int delay, int timeout, java.util.function.BooleanSupplier condition) {
        return () -> {
            runnable.run();
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            waitUntilCondition(condition, timeout);
        };
    }

    public static <T> T custom(T target, int delay, int timeout, java.util.function.BooleanSupplier condition) {
        return proxy(target, (Object p, Method method, Object[] args) -> {
            try {
                Object result = method.invoke(target, args);
                if (delay > 0) {
                    Thread.sleep(delay);
                }
                waitUntilCondition(condition, timeout);
                return result;
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Wait for custom condition was interrupted!", e);
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    private static void waitUntilCondition(java.util.function.BooleanSupplier condition, int timeout) {
        long end = System.currentTimeMillis() + (timeout * 1000L);
        while (System.currentTimeMillis() < end) {
            if (condition.getAsBoolean()) {
                return;
            }
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait for custom condition was interrupted!", e);
            }
        }
        if (!condition.getAsBoolean()) {
            throw new RuntimeException("Timeout while waiting for custom condition!");
        }
    }

    private static void waitUntilAjaxCompletes(Page page) {
        String script = "(window.pfselenium && pfselenium.validationFailed === true) || (document.readyState === 'complete'"
                    + " && (!window.jQuery || jQuery.active == 0)"
                    + " && (!window.PrimeFaces || (PrimeFaces.ajax.Queue.isEmpty() && PrimeFaces.animationActive === false))"
                    + " && (!window.pfselenium || (pfselenium.xhr == null && pfselenium.navigating === false)));";
        page.waitForFunction(script, null, new Page.WaitForFunctionOptions()
                .setTimeout(ConfigProvider.getInstance().getTimeoutAjax() * 1000.0)
                .setPollingInterval(50.0));
    }

    private static void waitUntilHttpCompletes(Page page) {
        String script = "(window.pfselenium && pfselenium.validationFailed === true) || "
                    + "(document.readyState === 'complete' && "
                    + "(!window.pfselenium || (pfselenium.navigating === false && pfselenium.submitting === false)))";
        page.waitForFunction(script, null, new Page.WaitForFunctionOptions()
                .setTimeout(ConfigProvider.getInstance().getTimeoutHttp() * 1000.0)
                .setPollingInterval(100.0));
    }

    private static <T> T proxy(T target, InvocationHandler handler) {
        Class<?> classToProxy = target.getClass();
        List<Class<?>> interfacesToImplement = new ArrayList<>();
        ElementMatcher.Junction<MethodDescription> methods = ElementMatchers.isPublic();

        if (Modifier.isPrivate(classToProxy.getModifiers()) || Modifier.isFinal(classToProxy.getModifiers())) {
            interfacesToImplement = Arrays.asList(classToProxy.getInterfaces());
            classToProxy = Object.class;
            methods = null;

            for (Class<?> c : interfacesToImplement) {
                if (methods == null) {
                    methods = ElementMatchers.isDeclaredBy(c);
                }
                else {
                    methods = methods.or(ElementMatchers.isDeclaredBy(c));
                }
            }
        }

        @SuppressWarnings("unchecked")
        Class<T> proxyClass = (Class<T>) new ByteBuddy()
                .subclass(classToProxy)
                .implement(interfacesToImplement)
                .method(methods)
                .intercept(InvocationHandlerAdapter.of(handler))
                .make()
                .load(target.getClass().getClassLoader())
                .getLoaded();

        try {
            try {
                Constructor<T> defaultCtor = proxyClass.getDeclaredConstructor();
                return defaultCtor.newInstance();
            }
            catch (NoSuchMethodException ex) {
                // ignore
            }

            try {
                if (target instanceof WrapsLocator) {
                    Constructor<T> ctor = proxyClass.getDeclaredConstructor(Locator.class);
                    return ctor.newInstance(((WrapsLocator) target).getWrappedLocator());
                }
            }
            catch (NoSuchMethodException ex) {
                // ignore
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Could not proxy class "
                + classToProxy.getName()
                + " because of missing constructor (default or Locator)");
    }
}
