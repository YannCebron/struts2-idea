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

package com.intellij.struts2.annotators;

import com.intellij.codeInsight.daemon.impl.quickfix.ShowModulePropertiesFix;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.ide.DataManager;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.struts2.dom.struts.model.StrutsManager;
import com.intellij.struts2.facet.StrutsFacet;
import com.intellij.struts2.facet.configuration.StrutsFileSet;
import com.intellij.util.Icons;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Checks if <code>struts.xml</code> is registered in any of the file sets in the current module.
 *
 * @author Yann C&eacute;bron
 */
public class StrutsFileSetCheckingAnnotator implements Annotator {

  public void annotate(final PsiElement psiElement, final AnnotationHolder holder) {
    if (psiElement instanceof JspFile) {
      return;
    }

    if (!(psiElement instanceof XmlFile)) {
      return;
    }

    // do not run when facet not enabled
    if (StrutsFacet.getInstance(psiElement) == null) {
      return;
    }

    final XmlFile xmlFile = (XmlFile) psiElement;
    final Project project = psiElement.getProject();

    final StrutsManager strutsManager = StrutsManager.getInstance(project);
    if (!strutsManager.isStruts2ConfigFile(xmlFile)) {
      return;
    }

    final Module module = ModuleUtil.findModuleForPsiElement(xmlFile);
    if (module == null) {
      return;
    }

    final VirtualFile currentVirtualFile = xmlFile.getVirtualFile();
    assert currentVirtualFile != null;

    final Set<StrutsFileSet> allConfigFileSets = strutsManager.getAllConfigFileSets(module);
    for (final StrutsFileSet configFileSet : allConfigFileSets) {
      if (configFileSet.hasFile(currentVirtualFile)) {
        return;
      }
    }

    final boolean fileSetAvailable = allConfigFileSets.size() != 0;
    final Annotation annotation = holder.createWarningAnnotation(xmlFile,
                                                                 fileSetAvailable ?
                                                                 "File not registered in file set" :
                                                                 "No file sets configured for Struts 2 facet");
    annotation.setFileLevelAnnotation(true);

    if (fileSetAvailable) {
      final AddToFileSetFix addToFileSetFix = new AddToFileSetFix(xmlFile.getName());
      annotation.registerFix(addToFileSetFix);
    } else {
      annotation.registerFix(new ShowModulePropertiesFix(xmlFile) {
        @NotNull
        public String getText() {
          return "Edit Struts 2 facet settings";
        }
      });
    }
  }


  /**
   * Adds the current struts.xml file to an existing file set.
   */
  private static class AddToFileSetFix extends BaseIntentionAction {

    private AddToFileSetFix(final String filename) {
      setText("Add " + filename + " to file set");
    }

    @NotNull
    public String getFamilyName() {
      return "Struts 2 Intentions";
    }

    public boolean isAvailable(@NotNull final Project project, final Editor editor, final PsiFile file) {
      return true;
    }

    public void invoke(@NotNull final Project project,
                       final Editor editor,
                       final PsiFile file) throws IncorrectOperationException {
      final StrutsFacet strutsFacet = StrutsFacet.getInstance(file);
      assert strutsFacet != null;

      final Set<StrutsFileSet> strutsFileSets = strutsFacet.getConfiguration().getFileSets();
      final BaseListPopupStep<StrutsFileSet> step =
        new BaseListPopupStep<StrutsFileSet>("Choose file set", new ArrayList<StrutsFileSet>(strutsFileSets)) {

          public Icon getIconFor(final StrutsFileSet aValue) {
            return Icons.PACKAGE_ICON;
          }

          public PopupStep onChosen(final StrutsFileSet selectedValue, final boolean finalChoice) {
            selectedValue.addFile(file.getVirtualFile());
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
              public void run() {
                ProjectRootManagerEx.getInstanceEx(project).beforeRootsChange(false);
                ProjectRootManagerEx.getInstanceEx(project).rootsChanged(false);
              }
            });
            return super.onChosen(selectedValue, finalChoice);
          }
        };
      JBPopupFactory.getInstance().createListPopup(step).showInBestPositionFor(DataManager.getInstance().getDataContext());
    }
  }

}