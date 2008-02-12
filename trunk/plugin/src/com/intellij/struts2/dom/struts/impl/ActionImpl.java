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

package com.intellij.struts2.dom.struts.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.struts2.dom.struts.action.Action;
import com.intellij.struts2.dom.struts.strutspackage.DefaultClassRef;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Adds utility methods.
 *
 * @author Yann CŽbron
 */
public abstract class ActionImpl implements Action {

  public boolean matchesPath(@NotNull final String path) {
    final String myPath = getName().getStringValue();
    if (myPath == null) {
      return false;
    }

    // do we have any wildcard-markers in our path? no --> exact compare
    if (myPath.indexOf("*") == -1 &&
        myPath.indexOf("**") == -1) {
      return path.equals(myPath);
    }

    final String matchString = myPath.replaceAll("\\*", "\\\\S*"); // TODO exclude "/"
    // TODO replace "**"
    final boolean match = Pattern.matches(matchString, path);

    return match;
  }

  @NotNull
  public StrutsPackage getStrutsPackage() {
    final StrutsPackage strutsPackage = DomUtil.getParentOfType(this, StrutsPackage.class, true);
    assert strutsPackage != null : "could not resolve enclosing <package> for " + this + " (" + this.getName()
        .getStringValue() + ")";
    return strutsPackage;
  }

  @Nullable
  public PsiClass searchActionClass() {
    final GenericAttributeValue<PsiClass> actionClassAttribute = getActionClass();
    if (actionClassAttribute.getXmlElement() != null) {
      return actionClassAttribute.getValue();
    }
    // resolve parent package <default-class-ref> (walk upwards)
    final DefaultClassRef ref = getStrutsPackage().searchDefaultClassRef();
    if (ref != null) {
      return ref.getDefaultClass().getValue();
    }

    // nothing found in parents --> error highlighting
    return null;
  }

  @Nullable
  public PsiMethod searchActionMethod() {
    final GenericAttributeValue<PsiMethod> methodValue = getMethod();
    if (methodValue.getXmlElement() != null) {
      return methodValue.getValue();
    }

    final List<PsiMethod> methods = getActionMethods();
    for (final PsiMethod method : methods) {
      if (method.getName().equals("execute")) {
        return method;
      }
    }

    return null;
  }

  @NotNull
  public String getNamespace() {
    return getStrutsPackage().searchNamespace();
  }

  @NotNull
  public List<PsiMethod> getActionMethods() {
    final PsiClass actionClass = getActionClass().getValue();
    if (actionClass == null) {
      return Collections.emptyList();
    }

    final Project project = actionClass.getProject();
    final PsiElementFactory psiElementFactory = PsiManager.getInstance(project).getElementFactory();
    final GlobalSearchScope projectScope = GlobalSearchScope.allScope(project);
    final PsiClassType stringType = psiElementFactory.createTypeByFQClassName(CommonClassNames.JAVA_LANG_STRING,
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

      // return type "java.lang.String"
      final PsiType type = psiMethod.getReturnType();
      if (type != null &&
          type instanceof PsiClassType &&
          type.equals(stringType)) {
        actionMethods.add(psiMethod);
      }

    }

    return actionMethods;
  }
}