/*  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.pluto.container.om.portlet.impl.jsr362;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.container.om.portlet.PortletDefinition;
import org.apache.pluto.container.om.portlet.Preference;
import org.apache.pluto.container.om.portlet.Preferences;
import org.apache.pluto.container.om.portlet.impl.ConfigurationHolder;
import org.apache.pluto.container.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.pluto.container.om.portlet.impl.fixtures.TestAnnotatedPrefs2;
import org.apache.pluto.container.om.portlet.impl.fixtures.TestPreferencesValidator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Junit test cases for JSR 362 portlet application definition.
 * @author Scott Nicklous
 *
 */
public class MergePrefs2Test {
   
   // defines both some portlets and a Listener
   private static final Class<?> TEST_ANNOTATED_CLASS1 = TestAnnotatedPrefs2.class;
   private static final String XML_FILE = 
         "org/apache/pluto/container/om/portlet/portlet362AlterValidator2.xml";
   
   private static PortletApplicationDefinition pad;
   private static ConfigurationHolder cfp;
   
   // class under test; cloned new for each test
   private  PortletApplicationDefinition cut;

   /**
    * @throws java.lang.Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      
      InputStream in = MergePortletAppTest.class
            .getClassLoader().getResourceAsStream(XML_FILE);

      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(TEST_ANNOTATED_CLASS1);
      cfp = new ConfigurationHolder();
      try {
         cfp.processConfigAnnotations(classes);
         cfp.processPortletDD(in);     // process portlet xml after annotations
         try {
            cfp.validate();         // validate and ignore any validation problems.
         } catch (Exception e) {}   
         pad = cfp.getPad();
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }
   
   @Before
   public void setUpBefore() throws Exception {
      cut = new PortletApplicationDefinitionImpl(pad);
   }

   /**
    * Tests that the validator is properly applied to portlets 1 & 5 only
    */
   @Test
   public void testPrefs() {
      List<PortletDefinition> list = cut.getPortlets();
      assertNotNull(list);
      assertEquals(5, list.size());
   }
   
   @Test
   public void testGetPreferences1() {
      String name = "bPref";
      String[] testVals = {"dValue", "eValue", "fValue"};
      PortletDefinition pd = cut.getPortlet("portlet1");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      List<String> vals = pref.getValues();
      assertEquals(testVals.length, vals.size());
      assertArrayEquals(testVals, vals.toArray());
      
      name = "aPref";
      testVals = new String[] {"aValue"}; 
      pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      vals = pref.getValues();
      assertEquals(testVals.length, vals.size());
      assertArrayEquals(testVals, vals.toArray());
   }
   
   @Test
   public void testGetPreferences2() {
      String name = "aPref";
      String val = "aValue";
      PortletDefinition pd = cut.getPortlet("portlet2");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      List<String> vals = pref.getValues();
      assertEquals(1, vals.size());
      assertEquals(val, vals.get(0));
      
      name = "bPref";
      String[] testVals = {"bValue", "cValue"};
      pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      vals = pref.getValues();
      assertEquals(2, vals.size());
      assertArrayEquals(testVals, vals.toArray());
   }
   
   @Test
   public void testGetPreferences3() {
      String name = "cPref";
      String val = "cValue";
      PortletDefinition pd = cut.getPortlet("portlet3");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      List<String> vals = pref.getValues();
      assertEquals(1, vals.size());
      assertEquals(val, vals.get(0));
      
      name = "aPref";
      val = "aValue";
      pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      vals = pref.getValues();
      assertEquals(1, vals.size());
      assertEquals(val, vals.get(0));
   }
   
   @Test
   public void testGetPreferences4() {
      String name = "dPref";
      String val = "dValue";
      PortletDefinition pd = cut.getPortlet("portlet4");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(1, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference(name);
      assertNotNull(pref);
      List<String> vals = pref.getValues();
      assertEquals(1, vals.size());
      assertEquals(val, vals.get(0));
   }
   
   @Test
   public void testGetPreferenceList() {
      PortletDefinition pd = cut.getPortlet("portlet1");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      for (Preference p : prefs.getPortletPreferences()) {
         assertTrue(p.isReadOnly());
      }
   }
   
   @Test
   public void testReadOnly1() {
      PortletDefinition pd = cut.getPortlet("portlet2");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference("aPref");
      assertNotNull(pref);
      assertTrue(pref.isReadOnly());
      
      pref = prefs.getPortletPreference("bPref");
      assertNotNull(pref);
      assertFalse(pref.isReadOnly());
   }
   
   @Test
   public void testReadOnly2() {
      PortletDefinition pd = cut.getPortlet("portlet3");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(2, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference("aPref");
      assertNotNull(pref);
      assertTrue(pref.isReadOnly());
      
      pref = prefs.getPortletPreference("cPref");
      assertNotNull(pref);
      assertFalse(pref.isReadOnly());
   }
   
   @Test
   public void testReadOnly4() {
      PortletDefinition pd = cut.getPortlet("portlet4");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertEquals(1, prefs.getPortletPreferences().size());
      
      Preference pref = prefs.getPortletPreference("dPref");
      assertNotNull(pref);
      assertTrue(pref.isReadOnly());
   }

   /**
    * Annotated Validator is applied to portlets 3 & 4. Portlet 1 gets
    * a validator specified in the portlet DD. 
    */
   @Test
   public void testValidatorApplied() {
      PortletDefinition pd1 = cut.getPortlet("portlet1");
      PortletDefinition pd3 = cut.getPortlet("portlet3");
      PortletDefinition pd4 = cut.getPortlet("portlet4");
      
      assertNotNull(pd1);
      assertNotNull(pd3);
      assertNotNull(pd4);
      
      Preferences prefs1 = pd1.getPortletPreferences();
      Preferences prefs3 = pd3.getPortletPreferences();
      Preferences prefs4 = pd4.getPortletPreferences();
      
      assertNotNull(prefs1);
      assertNotNull(prefs3);
      assertNotNull(prefs4);
      
      assertNotNull(prefs1.getPreferencesValidator());
      assertNotNull(prefs3.getPreferencesValidator());
      assertNotNull(prefs4.getPreferencesValidator());
      
      assertEquals(TEST_ANNOTATED_CLASS1.getCanonicalName(), prefs1.getPreferencesValidator());
      assertEquals(TestPreferencesValidator.class.getCanonicalName(), prefs3.getPreferencesValidator());
      assertEquals(TestPreferencesValidator.class.getCanonicalName(), prefs4.getPreferencesValidator());
   }

   /**
    * The annotated validator is not applied to portlet 2. The validator would normally
    * be applied to portlet 5, but is explicitly set to null in the portlet DD, so
    * portlet 5 doesn't get a validator.
    */
   @Test
   public void testValidatorNotApplied() {
      PortletDefinition pd = cut.getPortlet("portlet2");
      assertNotNull(pd);
      Preferences prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertNull(prefs.getPreferencesValidator());
      
      pd = cut.getPortlet("portlet5");
      assertNotNull(pd);
      prefs = pd.getPortletPreferences();
      assertNotNull(prefs);
      assertNull(prefs.getPreferencesValidator());
   }
}
