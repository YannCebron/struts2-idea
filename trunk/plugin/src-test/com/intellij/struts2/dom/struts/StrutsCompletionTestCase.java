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

package com.intellij.struts2.dom.struts;

import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;

/**
 * @author Yann CŽbron
 */
public class StrutsCompletionTestCase extends BasicStrutsHighlightingTestCase {

  protected String getTestDataLocation() {
    return "strutsXmlCompletion";
  }

  protected void configureModule(final JavaModuleFixtureBuilder moduleBuilder) throws Exception {
    super.configureModule(moduleBuilder);
    addStrutsJars(moduleBuilder);
  }

  public void testCompletionVariantsResult() throws Throwable {
    performCompletionVariantTest("struts-completionvariants-result.xml",
                                 "chain",
                                 "dispatcher",
                                 "freemarker",
                                 "httpheader");
  }

  public void testCompletionVariantsResultExtendingPackage() throws Throwable {
    performCompletionVariantTest("struts-completionvariants-result-extending.xml",
                                 "chain",
                                 "dispatcher",
                                 "freemarker",
                                 "httpheader",
                                 "velocity");

  }

}