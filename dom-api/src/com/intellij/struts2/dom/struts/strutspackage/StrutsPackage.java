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

package com.intellij.struts2.dom.struts.strutspackage;

import com.intellij.struts2.dom.StrutsDomConstants;
import com.intellij.struts2.dom.struts.action.Action;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <code>package</code>
 *
 * @author Yann C�bron
 */
@Namespace(StrutsDomConstants.STRUTS_NAMESPACE_KEY)
public interface StrutsPackage extends DomElement {

  @NameValue
  @Required
  GenericAttributeValue<String> getName();

  @Convert(StrutsPackageExtendsResolveConverter.class)
  GenericAttributeValue<StrutsPackage> getExtends();

  GenericAttributeValue<Boolean> getAbstract();

  @Required(value = false, nonEmpty = true)
  GenericAttributeValue<String> getNamespace();


  @NotNull
  String searchNamespace();

  // TODO externalReferenceResolver ?!

  // default-XXX tags ------------

  DefaultActionRef getDefaultActionRef();

  DefaultInterceptorRef getDefaultInterceptorRef();

  DefaultClassRef getDefaultClassRef();

  // global-XXX tags -------------

  @SubTag("global-results")
  GlobalResults getGlobalResults();

  @SubTag("global-exception-mappings")
  GlobalExceptionMappings getGlobalExceptionMappings();

  // --------------

  /**
   * not used directly.
   *
   * @return result-types element
   */
  @SubTag("result-types")
  ResultTypes getResultTypesElement();

  @PropertyAccessor({"resultTypesElement", "resultTypes"})
  List<ResultType> getResultTypes();

  /**
   * not used directly.
   *
   * @return interceptors element
   */
  @SubTag("interceptors")
  Interceptors getInterceptorsElement();

  @PropertyAccessor({"interceptorsElement", "interceptors"})
  List<Interceptor> getInterceptors();

  @PropertyAccessor({"interceptorsElement", "interceptorStacks"})
  List<InterceptorStack> getInterceptorStacks();

  @SubTagList(value = "action")
  List<Action> getActions();

  /**
   * Searches the <code>default-class-ref</code> element for this package, walking up the hierarchy until one is found.
   *
   * @return null if none was found.
   */
  @Nullable
  DefaultClassRef searchDefaultClassRef();

  // TODO same for the other default-xx stuff

}