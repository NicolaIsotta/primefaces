/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

/**
 * Custom lazy loading DataModel to deal with huge datasets
 */
public abstract class LazyDataModel<T> extends DataModel<T> implements SelectableDataModel<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private int rowIndex = -1;

    private int rowCount;

    private int pageSize;

    private List<T> data;

    public LazyDataModel() {
        super();
    }

    @Override
    public boolean isRowAvailable() {
        if (data == null) {
            return false;
        }

        return rowIndex >= 0 && rowIndex < data.size();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public T getRowData() {
        return data.get(rowIndex);
    }

    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        int oldIndex = this.rowIndex;

        if (rowIndex == -1 || pageSize == 0) {
            this.rowIndex = -1;
        }
        else {
            this.rowIndex = (rowIndex % pageSize);
        }

        if (data == null) {
            return;
        }

        DataModelListener[] listeners = getDataModelListeners();
        if (listeners != null && oldIndex != this.rowIndex) {
            Object rowData = null;
            if (isRowAvailable()) {
                rowData = getRowData();
            }

            DataModelEvent dataModelEvent = new DataModelEvent(this, rowIndex, rowData);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].rowSelected(dataModelEvent);
            }
        }
    }

    @Override
    public List<T> getWrappedData() {
        return data;
    }

    @Override
    public void setWrappedData(Object list) {
        this.data = (List) list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, List<FilterMeta> filterMeta) {

        List<SortMeta> sortMeta;
        if (sortField == null) {
            sortMeta = Collections.emptyList();
        }
        else {
            sortMeta = new ArrayList<>(1);
            sortMeta.add(new SortMeta(null, sortField, sortOrder == null ? SortOrder.UNSORTED : sortOrder, null));
        }

        return load(first, pageSize, sortMeta, filterMeta);
    }

    /**
     * Loads the data for the given parameters.
     *
     * NOTE: Thats the only method that should be implemented by the user.
     *
     * @param first the first entry
     * @param pageSize the page size
     * @param sortMeta a list with all sort informations
     * @param filterMeta a list with all filter informations
     * @return the data
     */
    public abstract List<T> load(int first, int pageSize, List<SortMeta> sortMeta, List<FilterMeta> filterMeta);

    @Override
    public T getRowData(String rowKey) {
        throw new UnsupportedOperationException(
                getMessage("getRowData(String rowKey) must be implemented by %s when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
    public Object getRowKey(T object) {
        throw new UnsupportedOperationException(
                getMessage("getRowKey(T object) must be implemented by %s when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    private String getMessage(String format) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();
        UIComponent component = UIComponent.getCurrentComponent(facesContext);
        String clientId = component == null ? "<unknown>" : component.getClientId(facesContext);
        return String.format(format, getClass().getName(), clientId, viewId);
    }

    @Override
    public Iterator<T> iterator() {
        return new LazyDataModelIterator<>(this);
    }

    @Deprecated
    public Iterator<T> iterator(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<SortMeta> sortMeta;
        if (sortField == null) {
            sortMeta = Collections.emptyList();
        }
        else {
            sortMeta = new ArrayList<>(1);
            sortMeta.add(new SortMeta(null, sortField, sortOrder == null ? SortOrder.UNSORTED : sortOrder, null));
        }

        List<FilterMeta> filterMeta;
        if (filters == null) {
            filterMeta = Collections.emptyList();
        }
        else {
            filterMeta = new ArrayList<>();
            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                filterMeta.add(
                    new FilterMeta(filter.getKey(), null, null, null, filter.getValue()));
            }
        }

        return iterator(sortMeta, filterMeta);
    }

    @Deprecated
    public Iterator<T> iterator(List<SortMeta> sortMeta, Map<String, Object> filters) {
        List<FilterMeta> filterMeta;
        if (filters == null) {
            filterMeta = Collections.emptyList();
        }
        else {
            filterMeta = new ArrayList<>();
            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                filterMeta.add(
                    new FilterMeta(filter.getKey(), null, null, null, filter.getValue()));
            }
        }

        return iterator(sortMeta, filterMeta);
    }

    public Iterator<T> iterator(List<SortMeta> sortMeta, List<FilterMeta> filterMeta) {
        return new LazyDataModelIterator<>(this, sortMeta, filterMeta);
    }
}
