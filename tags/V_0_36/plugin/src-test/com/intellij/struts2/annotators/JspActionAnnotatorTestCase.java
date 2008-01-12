/*
 * Copyright 2008 The authors
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

package com.intellij.struts2.annotators;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.struts2.dom.struts.BasicStrutsHighlightingTestCase;
import com.intellij.testFramework.builders.WebModuleFixtureBuilder;

/**
 * Test for {@link JspActionAnnotator}
 *
 * @author Yann CŽbron
 */
public class JspActionAnnotatorTestCase extends BasicStrutsHighlightingTestCase<WebModuleFixtureBuilder> {

  protected String getTestDataLocation() {
    return "/gutterJsp/actionClass";
  }

  protected Class<WebModuleFixtureBuilder> getModuleFixtureBuilderClass() {
    return WebModuleFixtureBuilder.class;
  }

  protected void configureModule(final WebModuleFixtureBuilder moduleBuilder) throws Exception {
    super.configureModule(moduleBuilder);
    moduleBuilder.addWebRoot(myFixture.getTempDirPath() + "/jsp/", "/");
    addStrutsJars(moduleBuilder);
  }

  public void testGutterActionAttribute() throws Throwable {
    createStrutsFileSet("struts-actionClass.xml");
    final GutterIconRenderer iconRenderer = myFixture.findGutter("/jsp/test_gutter_action_attribute.jsp");
    assertNotNull(iconRenderer);
  }

  public void testGutterNameAttribute() throws Throwable {
    createStrutsFileSet("struts-actionClass.xml");
    final GutterIconRenderer iconRenderer = myFixture.findGutter("/jsp/test_gutter_name_attribute.jsp");
    assertNotNull(iconRenderer);
  }

}