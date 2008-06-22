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
 *
 */

package com.intellij.struts2.dom.struts.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper methods for {@link ActionImpl}.
 *
 * @author Yann C&eacute;bron
 */
final class ActionUtil {

  private ActionUtil() {
  }

  /**
   * Does the given path match the Action's path (including support for wildcards).
   * <p/>
   * TODO handle '**' wildcards, handle backslash escape
   *
   * @param actionPath Path of Action.
   * @param checkPath  Path to check.
   * @return true if matched.
   */
  public static boolean matchesPath(@NotNull final String actionPath,
                                    @NotNull final String checkPath) {

    // do we have any wildcard-markers in our path? no --> exact compare
    if (actionPath.indexOf("*") == -1) {
      return checkPath.equals(actionPath);
    }

    final String matchString = actionPath.replaceAll("\\*", "[^/]*");
    return Pattern.matches(matchString, checkPath);
  }

  /**
   * Returns all suitable action methods for the given Action class.
   *
   * @param actionClass Action class to search for action methods.
   * @return Methods suitable for action execution.
   */
  public static List<PsiMethod> findActionMethods(@NotNull final PsiClass actionClass) {
    final Project project = actionClass.getProject();
    final PsiElementFactory psiElementFactory = PsiManager.getInstance(project).getElementFactory();
    final GlobalSearchScope projectScope = GlobalSearchScope.allScope(project);
    final PsiClassType stringType = psiElementFactory.createTypeByFQClassName(CommonClassNames.JAVA_LANG_STRING,
                                                                              projectScope);
    final PsiClassType resultType = psiElementFactory.createTypeByFQClassName("com.opensymphony.xwork2.Result",
                                                                              projectScope);
    final PsiClassType exceptionType = psiElementFactory.createTypeByFQClassName("java.lang.Exception", projectScope);

    final List<PsiMethod> actionMethods = new ArrayList<PsiMethod>();
    for (final PsiMethod psiMethod : actionClass.getAllMethods()) {

      if (psiMethod.isConstructor()) {
        continue;
      }

      // only public non-static concrete methods
      final PsiModifierList modifiers = psiMethod.getModifierList();
      if (!modifiers.hasModifierProperty(PsiModifier.PUBLIC) ||
          modifiers.hasModifierProperty(PsiModifier.STATIC) ||
          modifiers.hasModifierProperty(PsiModifier.ABSTRACT)) {
        continue;
      }

      // no parameters
      if (psiMethod.getParameterList().getParametersCount() != 0) {
        continue;
      }

      // skip "toString()"
      if (psiMethod.getName().equals("toString")) {
        continue;
      }

      // do not include simple getters (with underlying field)
      if (PropertyUtil.isSimplePropertyGetter(psiMethod) &&
          actionClass.findFieldByName(PropertyUtil.getPropertyName(psiMethod), true) != null) {
        continue;
      }

      // only "throws java.lang.Exception" or no throws at all
      final PsiClassType[] exceptionTypes = psiMethod.getThrowsList().getReferencedTypes();
      if (exceptionTypes.length > 1) {
        continue;
      }

      if (exceptionTypes.length == 1) {
        if (!exceptionTypes[0].equals(exceptionType)) {
          continue;
        }
      }

      // return type "java.lang.String" or "com.opensymphony.xwork2.Result"
      final PsiType type = psiMethod.getReturnType();
      if (type != null &&
          type instanceof PsiClassType &&
          (type.equals(stringType) || type.equals(resultType))) {
        actionMethods.add(psiMethod);
      }

    }

    return actionMethods;
  }

}