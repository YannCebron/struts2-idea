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

/**
 * @author Yann CŽbron
 */
public class TestStrutsHighlightingTestCase extends StrutsHighlightingTestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected String getTestDataLocation() {
    return "strutsXmlHighlighting";
  }

  public void testHighlightSimpleStruts() throws Throwable {
    createStrutsFileSet("struts.xml", "include-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts.xml");
    System.out.println("struts.xml = " + duration);
  }

  public void testParams() throws Throwable {
    createStrutsFileSet("param-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "param-struts.xml");
    System.out.println("param-struts.xml = " + duration);
  }

  public void testExceptionMapping() throws Throwable {
    createStrutsFileSet("exceptionmapping-struts.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "exceptionmapping-struts.xml");
    System.out.println("exceptionmapping-struts.xml = " + duration);
  }

}