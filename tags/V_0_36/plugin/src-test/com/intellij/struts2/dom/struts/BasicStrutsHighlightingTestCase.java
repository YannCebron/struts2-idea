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

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.struts2.BasicHighlightingTestCase;
import com.intellij.struts2.dom.inspection.Struts2ModelInspection;
import com.intellij.struts2.facet.configuration.StrutsFileSet;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;

import java.util.Set;

/**
 * Base class for struts.xml highlighting tests.
 *
 * @author Yann CŽbron
 */
public abstract class BasicStrutsHighlightingTestCase<T extends JavaModuleFixtureBuilder> extends BasicHighlightingTestCase<T> {

  protected LocalInspectionTool[] getHighlightingInspections() {
    return new LocalInspectionTool[]{new Struts2ModelInspection()};
  }

  protected VirtualFile addFileToSet(final StrutsFileSet fileSet, final String path) {
    final VirtualFile file = myFixture.getTempDirFixture().getFile(path);
    assertTrue("cannot find file: " + path, file != null);
    fileSet.addFile(file);
    return file;
  }

  protected void createStrutsFileSet(final String... fileNames) {
    final StrutsFileSet fileSet = new StrutsFileSet("test", "test");
    for (final String fileName : fileNames) {
      addFileToSet(fileSet, fileName);
    }
    final Set<StrutsFileSet> strutsFileSetSet = myFacet.getConfiguration().getFileSets();
    strutsFileSetSet.clear();
    strutsFileSetSet.add(fileSet);
  }

  /**
   * Performs highlighting test for the given struts.xml file.
   *
   * @param strutsXmlFileName Filename of struts.xml to check.
   * @throws Throwable On any errors.
   */
  protected void performHighlightingTest(final String strutsXmlFileName) throws Throwable {
    createStrutsFileSet(strutsXmlFileName);
    final long duration = myFixture.testHighlighting(true, false, true, strutsXmlFileName);
    System.out.println(strutsXmlFileName + " = " + duration);
  }

  /**
   * Performs completion variants test for the given struts.xml file.
   *
   * @param strutsXmlFileName Filename of struts.xml to check.
   * @param expectedItems     Expected completion variants.
   * @throws Throwable On any errors.
   */
  protected void performCompletionVariantTest(final String strutsXmlFileName, final String... expectedItems)
      throws Throwable {
    createStrutsFileSet(strutsXmlFileName);
    myFixture.testCompletionVariants(strutsXmlFileName, expectedItems);
  }

}