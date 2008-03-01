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

package com.intellij.struts2.dom.struts.action;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.struts2.dom.ParamsElement;
import com.intellij.struts2.dom.struts.strutspackage.InterceptorRef;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <code>action</code>
 *
 * @author Yann C�bron
 */
public interface Action extends ParamsElement {


  @Attribute(value = "name")
  @NameValue
  @Required(nonEmpty = true)
  @NotNull
  GenericAttributeValue<String> getName();

  @Attribute(value = "class")
  @Convert(ActionClassConverter.class)
  GenericAttributeValue<PsiClass> getActionClass();

  @Attribute(value = "method")
  @Convert(ActionMethodConverter.class)
  GenericAttributeValue<PsiMethod> getMethod();


  // --------------------
  @SubTagList("result")
  @NotNull
  List<Result> getResults();

  @SubTagList("interceptor-ref")
  @NotNull
  List<InterceptorRef> getInterceptorRefs();

  @SubTagList("exception-mapping")
  @NotNull
  List<ExceptionMapping> getExceptionMappings();

  // additional methods -----------------------------------

  /**
   * Does the given path match this action's path (including support for wildcards).
   *
   * @param path Path to check.
   * @return true if yes.
   */
  boolean matchesPath(@NotNull final String path);


  /**
   * Gets the enclosing package.
   *
   * @return Enclosing package.
   */
  @NotNull
  StrutsPackage getStrutsPackage();

  /**
   * Search the Action-class being used: <ol> <li>local "class" attribute</li> <li>default-class-ref of parent
   * package (search hierarchy upwards)</li> </ol>
   *
   * @return null if no matches.
   */
  @Nullable
  PsiClass searchActionClass();

  /**
   * Gets the defined method or the default method ("execute").
   *
   * @return null if nothing could be found.
   */
  @Nullable
  PsiMethod searchActionMethod();

  /**
   * Gets the namespace from enclosing {@link StrutsPackage}.
   *
   * @return Namespace identifier.
   */
  @NotNull
  String getNamespace();

  /**
   * Gets all methods suitable serving as action.
   *
   * @return List of methods (can be empty).
   */
  @NotNull
  List<PsiMethod> getActionMethods();

}