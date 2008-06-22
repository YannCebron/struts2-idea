package com.intellij.struts2.reference.jsp;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.struts2.BasicHighlightingTestCase;
import com.intellij.testFramework.builders.WebModuleFixtureBuilder;

/**
 * @author Yann C&eacute;bron
 */
public class UITagsAttributesReferenceProviderTest extends BasicHighlightingTestCase<WebModuleFixtureBuilder> {

  protected LocalInspectionTool[] getHighlightingInspections() {
    return new LocalInspectionTool[0];
  }

  protected String getTestDataLocation() {
    return "reference/jsp/uitags/";
  }

  protected Class<WebModuleFixtureBuilder> getModuleFixtureBuilderClass() {
    return WebModuleFixtureBuilder.class;
  }

  protected void configureModule(final WebModuleFixtureBuilder moduleBuilder) throws Exception {
    super.configureModule(moduleBuilder);
    moduleBuilder.addWebRoot(myFixture.getTempDirPath() + "/jsp", "/");
  }

  public void testPathAttributes() throws Throwable {
    myFixture.testHighlighting(true, false, true, "/jsp/paths.jsp");
  }

}