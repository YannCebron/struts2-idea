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

import com.intellij.facet.FacetManager;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.struts2.BasicStrutsTestCase;
import com.intellij.struts2.dom.inspection.Struts2ModelInspection;
import com.intellij.struts2.facet.StrutsFacet;
import com.intellij.struts2.facet.StrutsFacetType;
import com.intellij.struts2.facet.configuration.StrutsFileSet;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.*;

import java.io.File;
import java.util.Set;

/**
 * Base class for highlighting tests.
 *
 * @author Yann C�bron
 * @param <T> ModuleFixtureBuilder class to use.
 */
public abstract class StrutsHighlightingTestCase<T extends JavaModuleFixtureBuilder> extends BasicStrutsTestCase {

  protected CodeInsightTestFixture myFixture;
  protected ModuleFixture myModuleTestFixture;
  protected Project myProject;
  protected Module myModule;
  protected StrutsFacet myFacet;

  protected Class<T> getModuleFixtureBuilderClass() {
    return (Class<T>) JavaModuleFixtureBuilder.class;
  }

  protected void setUp() throws Exception {
    super.setUp();

    final TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder = IdeaTestFixtureFactory.getFixtureFactory()
        .createFixtureBuilder();
    final T moduleBuilder = projectBuilder.addModule(getModuleFixtureBuilderClass());
    myFixture = IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.getFixture());

    myFixture.setTestDataPath(getTestDataPath());

    configureModule(moduleBuilder);
    myFixture.enableInspections(new Struts2ModelInspection());

    myFixture.setUp();

    myProject = myFixture.getProject();
    myModuleTestFixture = moduleBuilder.getFixture();
    myModule = myModuleTestFixture.getModule();
    myFacet = createFacet();
  }

  protected void configureModule(final T moduleBuilder) throws Exception {
    moduleBuilder.addContentRoot(myFixture.getTempDirPath());
    moduleBuilder.addSourceRoot("src");
  }

  /**
   * Adds the S2 jars.
   *
   * @param moduleBuilder Current module builder.
   * @throws Exception On internal errors.
   */
  protected void addStrutsJars(final T moduleBuilder) throws Exception {
    final File testDataBasePathFile = new File(getTestDataBasePath()); // little hack to get absolute path..
    moduleBuilder.addLibraryJars("struts-2",
                                 testDataBasePathFile.getAbsolutePath(),
                                 "struts2-core-2.1.0.jar",
                                 "freemarker-2.3.10.jar",
                                 "ognl-2.6.11.jar",
                                 "xwork-2.1.0.jar");
  }

  protected StrutsFacet createFacet() {
    final RunResult<StrutsFacet> runResult = new WriteCommandAction<StrutsFacet>(myProject) {
      protected void run(final Result<StrutsFacet> result) throws Throwable {
        final ModifiableFacetModel model = FacetManager.getInstance(myModule).createModifiableModel();
        final StrutsFacet facet = StrutsFacetType.INSTANCE
            .createFacet(myModule, StrutsFacetType.INSTANCE.getPresentableName(),
                         StrutsFacetType.INSTANCE.createDefaultConfiguration(), null);
        result.setResult(facet);
        model.addFacet(facet);
        model.commit();
      }
    }.execute();
    final Throwable throwable = runResult.getThrowable();
    if (throwable != null) {
      throw new RuntimeException("error setting up StrutsFacet", throwable);
    }

    return runResult.getResultObject();
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

  protected void tearDown() throws Exception {
    myFixture.tearDown();
    myFixture = null;
    myModuleTestFixture = null;
    myProject = null;
    myModule = null;
    myFacet = null;
    super.tearDown();
  }

}