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
package org.primefaces.component.api;

import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.ELUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MessageFactory;

import java.time.Instant;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public abstract class UICalendar extends AbstractPrimeHtmlInputText implements InputHolder, TouchAware {

    public static final String CONTAINER_CLASS = "ui-calendar";
    public static final String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default";
    public static final String DATE_OUT_OF_RANGE_MESSAGE_ID = "primefaces.calendar.OUT_OF_RANGE";
    public static final String DATE_MIN_DATE_ID = "primefaces.calendar.MIN_DATE";
    public static final String DATE_MAX_DATE_ID = "primefaces.calendar.MAX_DATE";
    public static final String DATE_INVALID_MESSAGE_ID = "primefaces.calendar.INVALID";
    public static final String DATE_INVALID_RANGE_MESSAGE_ID = "primefaces.calendar.DATE_INVALID_RANGE_MESSAGE_ID";

    protected static final List<String> UNOBSTRUSIVE_EVENT_NAMES = LangUtils.unmodifiableList("dateSelect", "viewChange", "close");
    protected static final Collection<String> CALENDAR_EVENT_NAMES =  LangUtils.concat(AbstractPrimeHtmlInputText.EVENT_NAMES, UNOBSTRUSIVE_EVENT_NAMES);

    protected String timeOnlyPattern;

    private boolean conversionFailed;

    public enum PropertyKeys {
        defaultHour,
        defaultMillisecond,
        defaultMinute,
        defaultSecond,
        inputStyle,
        inputStyleClass,
        locale,
        mask,
        maskAutoClear,
        maskSlotChar,
        maxdate,
        mindate,
        pattern,
        rangeSeparator,
        readonlyInput,
        resolverStyle,
        timeOnly,
        timeZone,
        touchable
    }

    public Object getLocale() {
        return getStateHelper().eval(PropertyKeys.locale, null);
    }

    public void setLocale(Object locale) {
        getStateHelper().put(PropertyKeys.locale, locale);
    }

    public Object getTimeZone() {
        return getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(Object timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, timeZone);
    }

    public String getPattern() {
        return (String) getStateHelper().eval(PropertyKeys.pattern, null);
    }

    public void setPattern(String pattern) {
        getStateHelper().put(PropertyKeys.pattern, pattern);
    }

    public Object getMindate() {
        return getStateHelper().eval(PropertyKeys.mindate, null);
    }

    public void setMindate(Object mindate) {
        getStateHelper().put(PropertyKeys.mindate, mindate);
    }

    public Object getMaxdate() {
        return getStateHelper().eval(PropertyKeys.maxdate, null);
    }

    public void setMaxdate(Object maxdate) {
        getStateHelper().put(PropertyKeys.maxdate, maxdate);
    }

    public boolean isTimeOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeOnly, false);
    }

    public Boolean isTimeOnlyWithoutDefault() {
        return (Boolean) getStateHelper().eval(PropertyKeys.timeOnly);
    }

    public void setTimeOnly(boolean timeOnly) {
        getStateHelper().put(PropertyKeys.timeOnly, timeOnly);
    }

    public boolean isReadonlyInput() {
        return (Boolean) getStateHelper().eval(PropertyKeys.readonlyInput, false);
    }

    public void setReadonlyInput(boolean readonlyInput) {
        getStateHelper().put(PropertyKeys.readonlyInput, readonlyInput);
    }

    public String getInputStyle() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyle, null);
    }

    public void setInputStyle(String inputStyle) {
        getStateHelper().put(PropertyKeys.inputStyle, inputStyle);
    }

    public String getInputStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
    }

    public void setInputStyleClass(String inputStyleClass) {
        getStateHelper().put(PropertyKeys.inputStyleClass, inputStyleClass);
    }

    public int getDefaultHour() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultHour, 0);
    }

    public void setDefaultHour(int defaultHour) {
        getStateHelper().put(PropertyKeys.defaultHour, defaultHour);
    }

    public int getDefaultMinute() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultMinute, 0);
    }

    public void setDefaultMinute(int defaultMinute) {
        getStateHelper().put(PropertyKeys.defaultMinute, defaultMinute);
    }

    public int getDefaultSecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultSecond, 0);
    }

    public void setDefaultSecond(int defaultSecond) {
        getStateHelper().put(PropertyKeys.defaultSecond, defaultSecond);
    }

    public int getDefaultMillisecond() {
        return (Integer) getStateHelper().eval(PropertyKeys.defaultMillisecond, 0);
    }

    public void setDefaultMillisecond(int defaultMillisecond) {
        getStateHelper().put(PropertyKeys.defaultMillisecond, defaultMillisecond);
    }

    public String getSelectionMode() {
        return null;
    }

    public Locale calculateLocale(FacesContext facesContext) {
        return LocaleUtils.resolveLocale(facesContext, getLocale(), getClientId(facesContext));
    }

    public boolean hasTime() {
        String pattern = getPattern();

        return (pattern != null && (pattern.contains("HH") || pattern.contains("mm") || pattern.contains("ss")));
    }

    public String calculatePattern() {
        String pattern = getPattern();

        if (pattern == null) {
            return calculateLocalizedPattern();
        }
        else return pattern;
    }

    public String calculateTimeOnlyPattern() {
        if (timeOnlyPattern == null) {
            String localePattern = calculateLocalizedPattern();
            String userTimePattern = getPattern();
            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    public String calculateLocalizedPattern() {
        Locale locale = calculateLocale(getFacesContext());
        String localePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);

        // #6170 default to 4 digit year
        if (LangUtils.countMatches(localePattern, 'y') == 2) {
            localePattern = localePattern.replace("yy", "yyyy");
        }
        return localePattern;
    }

    public abstract String calculateWidgetPattern();

    /**
     * @see <a href="https://github.com/RobinHerbots/Inputmask/blob/5.x/README_date.md">Inputmask README_date</a>
     * @param patternTemplate the date pattern
     * @return the value converted for InputMask plugin
     */
    public String convertPattern(String patternTemplate) {
        // switch capital and lower M's for InputMask
        char[] chars = patternTemplate.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == 'm' || c == 'M') {
                if (Character.isUpperCase(c)) {
                    chars[i] = Character.toLowerCase(c);
                }
                else if (Character.isLowerCase(c)) {
                    chars[i] = Character.toUpperCase(c);
                }
            }
        }
        String pattern = new String(chars);
        int countY = LangUtils.countMatches(pattern, 'y');
        int countM = LangUtils.countMatches(pattern, 'm');
        int countD = LangUtils.countMatches(pattern, 'd');
        if (countD == 1) {
            pattern = pattern.replace("d", "dd");
        }
        if (countM == 1) {
            pattern = pattern.replace("m", "mm");
        }
        if (countY == 1) {
            pattern = pattern.replace("y", "yy");
        }
        return pattern;
    }

    public boolean isConversionFailed() {
        return conversionFailed;
    }

    public void setConversionFailed(boolean value) {
        conversionFailed = value;
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    public String getRangeSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.rangeSeparator, "-");
    }

    public void setRangeSeparator(java.lang.String _rangeSeparator) {
        getStateHelper().put(PropertyKeys.rangeSeparator, _rangeSeparator);
    }

    public String getResolverStyle() {
        return (String) getStateHelper().eval(PropertyKeys.resolverStyle, ResolverStyle.SMART.name());
    }

    public void setResolverStyle(String resolverStyle) {
        getStateHelper().put(PropertyKeys.resolverStyle, resolverStyle);
    }

    @Override
    public Boolean isTouchable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.touchable);
    }

    @Override
    public void setTouchable(Boolean touchable) {
        getStateHelper().put(PropertyKeys.touchable, touchable);
    }

    public String getMask() {
        return (String) getStateHelper().eval(PropertyKeys.mask, "false");
    }

    public void setMask(String mask) {
        getStateHelper().put(PropertyKeys.mask, mask);
    }

    public String getMaskSlotChar() {
        return (String) getStateHelper().eval(PropertyKeys.maskSlotChar, "_");
    }

    public void setMaskSlotChar(String maskSlotChar) {
        getStateHelper().put(PropertyKeys.maskSlotChar, maskSlotChar);
    }

    public boolean isMaskAutoClear() {
        return (Boolean) getStateHelper().eval(PropertyKeys.maskAutoClear, true);
    }

    public void setMaskAutoClear(boolean maskAutoClear) {
        getStateHelper().put(PropertyKeys.maskAutoClear, maskAutoClear);
    }

    public enum ValidationResult {
        OK, INVALID_DISABLED_DATE, INVALID_RANGE_DATES_SEQUENTIAL, INVALID_MIN_DATE, INVALID_MAX_DATE, INVALID_OUT_OF_RANGE
    }

    protected void createFacesMessageFromValidationResult(FacesContext context, ValidationResult validationResult) {
        FacesMessage msg = null;
        String validatorMessage = getValidatorMessage();
        Object[] params = new Object[] {ComponentUtils.getLabel(context, this),
                CalendarUtils.getValueAsString(context, this, getMindate()),
                CalendarUtils.getValueAsString(context, this, getMaxdate())};
        if (validatorMessage != null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
        }
        else {
            switch (validationResult) {
                case OK:
                    break;
                case INVALID_DISABLED_DATE:
                    msg = MessageFactory.getFacesMessage(context, DATE_INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_RANGE_DATES_SEQUENTIAL:
                    msg = MessageFactory.getFacesMessage(context, DATE_INVALID_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MIN_DATE:
                    msg = MessageFactory.getFacesMessage(context, DATE_MIN_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_MAX_DATE:
                    msg = MessageFactory.getFacesMessage(context, DATE_MAX_DATE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
                case INVALID_OUT_OF_RANGE:
                    msg = MessageFactory.getFacesMessage(context, DATE_OUT_OF_RANGE_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                    break;
            }
        }
        context.addMessage(getClientId(context), msg);
    }

    public Class<?> getValueType() {
        return ELUtils.getType(getFacesContext(), getValueExpression("value"), this::getValue);
    }

    public void validateMinMax(FacesContext context) {
        Instant minDate = CalendarUtils.getObjectAsInstant(context, this, getMindate(), PropertyKeys.mindate.name());
        Instant maxDate = CalendarUtils.getObjectAsInstant(context, this, getMaxdate(), PropertyKeys.maxdate.name());
        if (minDate != null && maxDate != null && maxDate.compareTo(minDate) < 0) {
            String id = getClientId(context);
            String component = this.getClass().getSimpleName();
            throw new FacesException(component + " : \"" + id + "\" minimum date must be less than maximum date.");
        }
    }
}
