/*
 * Copyright 2007 The authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.struts2.dom.struts;

import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;

/**
 * Various basic and complex struts.xml highlighting tests.
 *
 * @author Yann CŽbron
 */
public class StrutsHighlightingTestCase extends BasicStrutsHighlightingTestCase {

  protected void configureModule(final JavaModuleFixtureBuilder moduleBuilder) throws Exception {
    super.configureModule(moduleBuilder);
    addStrutsJars(moduleBuilder);
  }

  protected String getTestDataLocation() {
    return "strutsXmlHighlighting";
  }

  public void testSimpleStruts() throws Throwable {
    createStrutsFileSet("struts-simple.xml", "include-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts-simple.xml");
    System.out.println("struts-simple.xml = " + duration);
  }

  public void testParam() throws Throwable {
    createStrutsFileSet("param-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "param-struts.xml");
    System.out.println("param-struts.xml = " + duration);
  }

  public void testExceptionMapping() throws Throwable {
    createStrutsFileSet("exceptionmapping-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "exceptionmapping-struts.xml");
    System.out.println("exceptionmapping-struts.xml = " + duration);
  }

  public void testStrutsDefault() throws Throwable {
    createStrutsFileSet("struts-default.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts-default.xml");
    System.out.println("struts-default.xml = " + duration);
  }

}