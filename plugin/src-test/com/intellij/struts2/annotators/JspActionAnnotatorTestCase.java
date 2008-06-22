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

import com.intellij.codeInsight.navigation.NavigationGutterIconRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.struts2.dom.struts.BasicStrutsHighlightingTestCase;
import com.intellij.testFramework.builders.WebModuleFixtureBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Test for {@link JspActionAnnotator}
 *
 * @author Yann C&eacute;bron
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

  /**
   * Checks whether the gutter target elements resolve to the given action method names.
   *
   * @param gutterIconRenderer  Gutter icon renderer to check.
   * @param expectedActionNames Names of the actions.
   */
  private static void checkGutterActionMethodTargetElements(final GutterIconRenderer gutterIconRenderer,
                                                            final String... expectedActionNames) {
    assertNotNull(gutterIconRenderer);
    assertEquals(gutterIconRenderer.getIcon(), com.intellij.struts2.annotators.JspActionAnnotator.ACTION_CLASS_ICON);

    assertTrue(gutterIconRenderer instanceof NavigationGutterIconRenderer);
    final NavigationGutterIconRenderer gutter = (NavigationGutterIconRenderer) gutterIconRenderer;

    final Set<String> foundActionNames = new HashSet<String>();
    for (final PsiElement psiElement : gutter.getTargetElements()) {
      assertTrue(psiElement + " != XmlTag", psiElement instanceof PsiMethod);
      final String actionName = ((PsiMethod) psiElement).getName();
      foundActionNames.add(actionName);
    }

    assertSameElements(foundActionNames, expectedActionNames);
  }

  public void testGutterActionAttribute() throws Throwable {
    createStrutsFileSet("struts-actionClass.xml");
    final GutterIconRenderer iconRenderer = myFixture.findGutter("/jsp/test_gutter_action_attribute.jsp");
    checkGutterActionMethodTargetElements(iconRenderer, "validActionMethod");
  }

  public void testGutterNameAttribute() throws Throwable {
    createStrutsFileSet("struts-actionClass.xml");
    final GutterIconRenderer iconRenderer = myFixture.findGutter("/jsp/test_gutter_name_attribute.jsp");
    checkGutterActionMethodTargetElements(iconRenderer, "validActionMethod");
  }

}