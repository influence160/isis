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
package org.apache.isis.extensions.jpa.metamodel.facets.object.entity;

import java.util.List;

import javax.persistence.Entity;

import junit.framework.Assert;

import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactory;
import org.apache.isis.core.progmodel.facets.AbstractFacetFactoryTest;


public class GivenJpaEntityAnnotationFacetFactoryTest extends
        AbstractFacetFactoryTest {

    private JpaEntityAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new JpaEntityAnnotationFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testFeatureTypes() {
        final List<FeatureType> featureTypes = facetFactory
                .getFeatureTypes();
        Assert
                .assertTrue(contains(featureTypes,
                FeatureType.OBJECT));
        assertFalse(contains(featureTypes,
                FeatureType.PROPERTY));
        assertFalse(contains(featureTypes,
                FeatureType.COLLECTION));
        Assert
                .assertFalse(contains(featureTypes,
                FeatureType.ACTION));
        assertFalse(contains(featureTypes,
                FeatureType.ACTION_PARAMETER));
    }

    public void testEntityAnnotationPickedUpOnClass() {
        @Entity
        class Customer {
        }

        facetFactory.process(new FacetFactory.ProcessClassContext(Customer.class, methodRemover, facetHolder));

        final Facet facet = facetHolder.getFacet(JpaEntityFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof JpaEntityFacetAnnotation);
    }

    public void testIfNoEntityAnnotationThenNoFacet() {

        class Customer {
        }

        facetFactory.process(new FacetFactory.ProcessClassContext(Customer.class, methodRemover, facetHolder));

        final Facet facet = facetHolder.getFacet(JpaEntityFacet.class);
        assertNull(facet);
    }

    public void testEntityAnnotationWithNoExplicitNameDefaultsToClassName() {
        @Entity()
        class Customer {
        }
        facetFactory.process(new FacetFactory.ProcessClassContext(Customer.class, methodRemover, facetHolder));

        final JpaEntityFacet entityFacet = facetHolder
                .getFacet(JpaEntityFacet.class);
        assertEquals("Customer", entityFacet.getName());
    }

    public void testEntityAnnotationWithExplicitNameAttributeProvided() {
        @Entity(name = "CUS_CUSTOMER")
        class Customer {
        }

        facetFactory.process(new FacetFactory.ProcessClassContext(Customer.class, methodRemover, facetHolder));

        final JpaEntityFacet entityFacet = facetHolder
                .getFacet(JpaEntityFacet.class);
        assertEquals("CUS_CUSTOMER", entityFacet.getName());
    }

    public void testNoMethodsRemoved() {
        @Entity
        class Customer {
        }

        facetFactory.process(new FacetFactory.ProcessClassContext(Customer.class, methodRemover, facetHolder));

        assertNoMethodsRemoved();
    }
}