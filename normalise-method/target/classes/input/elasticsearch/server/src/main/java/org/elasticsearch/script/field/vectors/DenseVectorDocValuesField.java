/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.script.field.vectors;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.mapper.vectors.DenseVectorScriptDocValues;
import org.elasticsearch.script.field.AbstractScriptFieldFactory;
import org.elasticsearch.script.field.DocValuesScriptFieldFactory;
import org.elasticsearch.script.field.Field;

import java.util.Iterator;

public abstract class DenseVectorDocValuesField extends AbstractScriptFieldFactory<DenseVector>
    implements
        Field<DenseVector>,
        DocValuesScriptFieldFactory,
        DenseVectorScriptDocValues.DenseVectorSupplier {
    protected final String name;

    public DenseVectorDocValuesField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return isEmpty() ? 0 : 1;
    }

    @Override
    public BytesRef getInternal(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the DenseVector for a document if one exists, DenseVector.EMPTY otherwise
     */
    public abstract DenseVector get();

    public abstract DenseVector get(DenseVector defaultValue);

    public abstract DenseVectorScriptDocValues toScriptDocValues();

    // DenseVector fields are single valued, so Iterable does not make sense.
    @Override
    public Iterator<DenseVector> iterator() {
        throw new UnsupportedOperationException("Cannot iterate over single valued dense_vector field, use get() instead");
    }
}
