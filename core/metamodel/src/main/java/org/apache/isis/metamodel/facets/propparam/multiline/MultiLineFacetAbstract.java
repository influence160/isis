/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.metamodel.facets.propparam.multiline;

import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.facets.FacetHolder;
import org.apache.isis.metamodel.facets.MultipleValueFacetAbstract;


public abstract class MultiLineFacetAbstract extends MultipleValueFacetAbstract implements MultiLineFacet {

    public static Class<? extends Facet> type() {
        return MultiLineFacet.class;
    }

    private final int numberOfLines;
    private final boolean preventWrapping;

    public MultiLineFacetAbstract(final int numberOfLines, final boolean preventWrapping, final FacetHolder holder) {
        super(type(), holder);
        this.numberOfLines = numberOfLines;
        this.preventWrapping = preventWrapping;
    }

    public int numberOfLines() {
        return numberOfLines;
    }

    public boolean preventWrapping() {
        return preventWrapping;
    }

    @Override
    protected String toStringValues() {
        return "lines=" + numberOfLines + "," + (preventWrapping ? "prevent-wrapping" : "allow-wrapping");
    }

}