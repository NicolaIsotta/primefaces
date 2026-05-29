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

import org.primefaces.playwright.AbstractPrimePageFragment;
import org.primefaces.playwright.FindBy;
import org.primefaces.playwright.WrapsLocator;
import org.primefaces.playwright.findby.FindByParentPartialId;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PrimePageFragmentFactory {

    private PrimePageFragmentFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> fragment, Page page, Locator locator) {
        try {
            if (fragment.equals(Locator.class)) {
                return (T) locator;
            }

            if (AbstractPrimePageFragment.class.isAssignableFrom(fragment)) {
                T instance = fragment.getDeclaredConstructor().newInstance();
                AbstractPrimePageFragment f = (AbstractPrimePageFragment) instance;
                f.setPage(page);
                f.setLocator(locator);
                setMembers(page, instance, instance);
                return instance;
            }

            throw new IllegalArgumentException("Unsupported fragment class: " + fragment.getName());
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create page fragment: " + fragment.getName(), e);
        }
    }

    public static void setMembers(Page page, Object parent, Object obj) {
        for (Field field : collectFields(obj)) {
            FindBy findBy = field.getAnnotation(FindBy.class);
            if (findBy != null) {
                String selector = getSelector(findBy);
                if (selector != null) {
                    Locator locator = findLocator(page, parent, selector);
                    setMember(page, locator, field, obj);
                }
            }

            FindByParentPartialId findByParentPartialId = field.getAnnotation(FindByParentPartialId.class);
            if (findByParentPartialId != null) {
                Locator parentLocator = (parent instanceof WrapsLocator) ? ((WrapsLocator) parent).getWrappedLocator()
                        : (parent instanceof Locator ? (Locator) parent : null);

                if (parentLocator == null) {
                    throw new RuntimeException("Parent must be a Locator/Fragment for FindByParentPartialId!");
                }

                // Lazy resolution of partial ID at call-time using standard JDK Proxy to handle dynamic rendering
                Locator locator = (Locator) java.lang.reflect.Proxy.newProxyInstance(
                        PrimePageFragmentFactory.class.getClassLoader(),
                        new Class<?>[]{Locator.class},
                        (proxyInstance, method, args) -> {
                            String parentId = parentLocator.getAttribute("id");
                            if (parentId == null || parentId.trim().isEmpty()) {
                                throw new RuntimeException("Id of parent element is null or empty!");
                            }

                            String selector;
                            if (!findByParentPartialId.name().isEmpty()) {
                                selector = "[name=\"" + parentId + findByParentPartialId.name() + "\"]";
                            }
                            else {
                                selector = "[id=\"" + parentId + findByParentPartialId.value() + "\"]";
                            }

                            Locator target;
                            if (findByParentPartialId.searchFromRoot()) {
                                target = page.locator(selector);
                            }
                            else {
                                target = parentLocator.locator(selector);
                            }

                            return method.invoke(target, args);
                        }
                );

                setMember(page, locator, field, obj);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void setMember(Page page, Locator locator, Field field, Object obj) {
        Object value = null;

        if (Locator.class.isAssignableFrom(field.getType())) {
            value = locator;
        }
        else if (AbstractPrimePageFragment.class.isAssignableFrom(field.getType())) {
            value = create((Class<? extends AbstractPrimePageFragment>) field.getType(), page, locator);
        }
        else if (List.class.isAssignableFrom(field.getType())) {
            Class<?> genericClass = extractGenericListType(field);
            if (genericClass != null) {
                java.lang.reflect.InvocationHandler handler = (Object p, Method method, Object[] args) -> {
                    List<Locator> allLocators = locator.all();
                    List<Object> mappedList = new ArrayList<>();
                    for (Locator loc : allLocators) {
                        if (genericClass.equals(Locator.class)) {
                            mappedList.add(loc);
                        }
                        else if (AbstractPrimePageFragment.class.isAssignableFrom(genericClass)) {
                            mappedList.add(create((Class<? extends AbstractPrimePageFragment>) genericClass, page, loc));
                        }
                    }
                    return method.invoke(mappedList, args);
                };

                value = java.lang.reflect.Proxy.newProxyInstance(
                        PrimePageFragmentFactory.class.getClassLoader(), new Class[] {List.class}, handler);
            }
        }

        try {
            field.setAccessible(true);
            field.set(obj, value);
        }
        catch (Exception e) {
            throw new RuntimeException("Can not set field in PageFragment!", e);
        }
    }

    private static String getSelector(FindBy findBy) {
        if (!findBy.id().isEmpty()) {
            return "[id=\"" + findBy.id() + "\"]";
        }
        if (!findBy.name().isEmpty()) {
            return "[name=\"" + findBy.name() + "\"]";
        }
        if (!findBy.className().isEmpty()) {
            return "." + findBy.className();
        }
        if (!findBy.css().isEmpty()) {
            return findBy.css();
        }
        if (!findBy.xpath().isEmpty()) {
            return "xpath=" + findBy.xpath();
        }
        if (!findBy.value().isEmpty()) {
            return findBy.value();
        }
        return null;
    }

    private static Locator findLocator(Page page, Object parent, String selector) {
        if (parent instanceof Page) {
            return page.locator(selector);
        }
        else if (parent instanceof WrapsLocator) {
            return ((WrapsLocator) parent).getWrappedLocator().locator(selector);
        }
        else if (parent instanceof Locator) {
            return ((Locator) parent).locator(selector);
        }
        throw new IllegalArgumentException("Unsupported parent type: " + parent.getClass());
    }

    private static Class<?> extractGenericListType(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }

        Type listGenericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        try {
            return Class.forName(listGenericType.getTypeName());
        }
        catch (ClassNotFoundException ex) {
            // do nothing
        }

        return null;
    }

    private static List<Field> collectFields(Object obj) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
