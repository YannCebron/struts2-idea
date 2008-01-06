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

import com.intellij.testFramework.builders.WebModuleFixtureBuilder;

/**
 * Tests for {@link com.intellij.struts2.dom.struts.action.StrutsPathReferenceConverter}.
 *
 * @author Yann CŽbron
 */
public class PathResolvingTestCase extends BasicStrutsHighlightingTestCase<WebModuleFixtureBuilder> {

  protected String getTestDataLocation() {
    return "path";
  }

  protected Class<WebModuleFixtureBuilder> getModuleFixtureBuilderClass() {
    return WebModuleFixtureBuilder.class;
  }

  protected void configureModule(final WebModuleFixtureBuilder moduleBuilder) throws Exception {
    super.configureModule(moduleBuilder);
    moduleBuilder.addWebRoot(myFixture.getTempDirPath() + "/jsp/", "/");
    moduleBuilder.addWebRoot(myFixture.getTempDirPath() + "/jsp2/", "/2ndWebRoot/");
    addStrutsJars(moduleBuilder);
  }

  /**
   * {@link com.intellij.struts2.dom.struts.impl.path.DispatchPathReferenceProvider}
   *
   * @throws Throwable On errors.
   */
  public void testPathDispatcher() throws Throwable {
    createStrutsFileSet("struts-path-dispatcher.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts-path-dispatcher.xml");
    System.out.println("struts-path-dispatcher.xml = " + duration);
  }

  /**
   * {@link com.intellij.struts2.dom.struts.impl.path.ActionPathReferenceProvider}
   *
   * @throws Throwable On errors.
   */
  public void testActionPath() throws Throwable {
    createStrutsFileSet("struts-actionpath.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts-actionpath.xml");
    System.out.println("struts-actionpath.xml = " + duration);
  }

  /**
   * {@link com.intellij.struts2.dom.struts.impl.path.ActionChainReferenceProvider}
   *
   * @throws Throwable On errors.
   */
  public void testActionChain() throws Throwable {
    createStrutsFileSet("struts-actionchain.xml");
    final long duration = myFixture.testHighlighting(true, false, true, "struts-actionchain.xml");
    System.out.println("struts-actionchain.xml = " + duration);
  }

}