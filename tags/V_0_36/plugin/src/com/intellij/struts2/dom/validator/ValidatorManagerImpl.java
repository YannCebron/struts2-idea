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

package com.intellij.struts2.dom.validator;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.struts2.dom.validator.config.ValidatorConfig;
import com.intellij.struts2.dom.validator.config.ValidatorsConfig;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Yann C�bron
 */
public class ValidatorManagerImpl extends ValidatorManager {

  @NonNls
  private static final String VALIDATORS_XML = "validators.xml";

  @NonNls
  private static final String VALIDATORS_DEFAULT_XML = "default.xml";

  public boolean isValidationConfigFile(@NotNull final XmlFile xmlFile) {
    return DomManager.getDomManager(xmlFile.getProject()).getFileElement(xmlFile, Validators.class) != null;
  }

  @Nullable
  private static DomFileElement<ValidatorsConfig> getValidatorsConfigFileElement(@NotNull final XmlFile xmlFile) {
    return DomManager.getDomManager(xmlFile.getProject()).getFileElement(xmlFile, ValidatorsConfig.class);
  }

  public boolean isCustomValidatorsConfigFile(@NotNull final PsiFile psiFile) {
    return !psiFile.getName().equals(VALIDATORS_DEFAULT_XML);
  }

  public List<ValidatorConfig> getValidators(@NotNull final Module module) {
    final PsiFile validatorsFile = getValidatorConfigFile(module);
    if (validatorsFile == null) {
      return Collections.emptyList();
    }

    final DomFileElement<ValidatorsConfig> validatorsConfigElement = getValidatorsConfigFileElement((XmlFile) validatorsFile);

    if (validatorsConfigElement == null) {
      return Collections.emptyList();
    }

    return validatorsConfigElement.getRootElement().getValidatorConfigs();
  }

  @Nullable
  public PsiFile getValidatorConfigFile(@NotNull final Module module) {
    final Project project = module.getProject();
    final PsiManager psiManager = PsiManager.getInstance(project);

    final VirtualFile validatorsVirtualFile = ModuleUtil.findResourceFileInScope(VALIDATORS_XML, project,
                                                                                 GlobalSearchScope.moduleRuntimeScope(
                                                                                     module,
                                                                                     false));

    if (validatorsVirtualFile != null) {
      final PsiFile file = psiManager.findFile(validatorsVirtualFile);
      if (file != null &&
          getValidatorsConfigFileElement((XmlFile) file) != null) {
        return file;
      }
    }

    // find com/opensymphony/xwork2/validator/validators/default.xml from xwork.jar
    final PsiClass emailValidatorClass = psiManager.findClass(
        "com.opensymphony.xwork2.validator.validators.EmailValidator",
        GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));
    if (emailValidatorClass == null) {
      return null;
    }

    final VirtualFile file = PsiUtil.getVirtualFile(emailValidatorClass);
    if (file == null ||
        file.getFileSystem() != JarFileSystem.getInstance()) {
      return null;
    }

    // go up one level to ../validators/
    final VirtualFile parent = file.getParent();
    assert parent != null : "error walking up to parent from EmailValidator.class, xwork JAR file=" + file;

    final VirtualFile vfDefaultXml = parent.findChild(VALIDATORS_DEFAULT_XML);
    assert vfDefaultXml != null : "VF for default.xml null, parent=" + parent;

    return psiManager.findFile(vfDefaultXml);
  }

}