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
package org.apache.isis.objectstore.jdo.metamodel.facets.prop.notpersistent;

import java.lang.reflect.Method;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PrimaryKey;

import junit.framework.Assert;

import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactory;
import org.apache.isis.core.metamodel.facets.mandatory.MandatoryFacet;
import org.apache.isis.core.metamodel.facets.notpersisted.NotPersistedFacet;
import org.apache.isis.core.progmodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.progmodel.facets.members.disabled.DisabledFacet;
import org.apache.isis.objectstore.jdo.metamodel.facets.prop.primarykey.DisabledFacetDerivedFromJdoPrimaryKeyAnnotation;
import org.apache.isis.objectstore.jdo.metamodel.facets.prop.primarykey.JdoPrimaryKeyFacet;
import org.apache.isis.objectstore.jdo.metamodel.facets.prop.primarykey.OptionalFacetDerivedFromJdoPrimaryKeyAnnotation;

public class GivenJdoNotPersistentAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private JdoNotPersistentAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new JdoNotPersistentAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testFeatureTypes() {
        final List<FeatureType> featureTypes = facetFactory.getFeatureTypes();
        assertFalse(contains(featureTypes, FeatureType.OBJECT));
        assertTrue(contains(featureTypes, FeatureType.PROPERTY));
        assertFalse(contains(featureTypes, FeatureType.COLLECTION));
        assertFalse(contains(featureTypes, FeatureType.ACTION));
        assertFalse(contains(featureTypes, FeatureType.ACTION_PARAMETER));
    }

    public void testNotPersistentAnnotationPickedUpOnProperty() throws Exception {
        final Class<?> cls = SimpleObjectWithNotPersistentColumn.class;
        final Method method = cls.getMethod("getSomeColumn");
        facetFactory.process(new FacetFactory.ProcessMethodContext(cls, null, null, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(JdoNotPersistentFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof JdoNotPersistentFacet);
    }

    public void testNotPersistedDerived() throws Exception {
        final Class<?> cls = SimpleObjectWithNotPersistentColumn.class;
        final Method method = cls.getMethod("getSomeColumn");
        facetFactory.process(new FacetFactory.ProcessMethodContext(cls, null, null, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NotPersistedFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistedFacetDerivedFromJdoNotPersistentAnnotation);
    }

    public void testIfNoIdAnnotationThenNoFacet() throws Exception {

        class Customer {
            private Long someColumn;

            // @NotPersistent missing
            @SuppressWarnings("unused")
            public Long getSomeColumn() {
                return someColumn;
            }

            @SuppressWarnings("unused")
            public void setSomeColumn(final Long someColumn) {
                this.someColumn = someColumn;
            }
        }

        final Class<?> cls = Customer.class;
        final Method method = cls.getMethod("getSomeColumn");
        facetFactory.process(new FacetFactory.ProcessMethodContext(cls, null, null, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(JdoNotPersistentFacet.class);
        assertNull(facet);
    }

    public void testNoMethodsRemoved() throws Exception {
        class Customer {
            private Long someColumn;

            @NotPersistent
            public Long getSomeColumn() {
                return someColumn;
            }

            @SuppressWarnings("unused")
            public void setSomeColumn(final Long someColumn) {
                this.someColumn = someColumn;
            }
        }

        final Class<?> cls = Customer.class;
        final Method method = cls.getMethod("getSomeColumn");
        facetFactory.process(new FacetFactory.ProcessMethodContext(cls, null, null, method, methodRemover, facetedMethod));

        assertNoMethodsRemoved();
    }
}
