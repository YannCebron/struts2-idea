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

package com.intellij.struts2;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.struts2.dom.struts.IncludeFileResolvingConverter;
import com.intellij.struts2.dom.struts.action.ResultTypeResolvingConverter;
import com.intellij.struts2.dom.struts.impl.IncludeFileResolvingConverterImpl;
import com.intellij.struts2.dom.struts.impl.InterceptorRefResolveConverterImpl;
import com.intellij.struts2.dom.struts.impl.ResultTypeResolvingConverterImpl;
import com.intellij.struts2.dom.struts.impl.StrutsPackageExtendsResolveConverterImpl;
import com.intellij.struts2.dom.struts.strutspackage.InterceptorRefResolveConverter;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackageExtendsResolveConverter;
import com.intellij.struts2.dom.validator.config.ValidatorConfigResolveConverter;
import com.intellij.struts2.dom.validator.impl.ValidatorConfigResolveConverterImpl;
import com.intellij.util.xml.ConverterManager;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;

/**
 * Project-level support.
 * <p/>
 * <ul>
 * <li>DOM-Converter implementations</li>
 * </ul>
 *
 * @author Yann CŽbron
 */
public class StrutsProjectComponent extends AbstractProjectComponent {

  private final DomManager domManager;

  public StrutsProjectComponent(final Project project, final DomManager domManager) {
    super(project);
    this.domManager = domManager;
  }

  public void initComponent() {
    registerDomConverters();
  }

  private void registerDomConverters() {
    final ConverterManager converterManager = domManager.getConverterManager();
    converterManager.registerConverterImplementation(StrutsPackageExtendsResolveConverter.class,
                                                     new StrutsPackageExtendsResolveConverterImpl());
    converterManager.registerConverterImplementation(IncludeFileResolvingConverter.class,
                                                     new IncludeFileResolvingConverterImpl());
    converterManager.registerConverterImplementation(ResultTypeResolvingConverter.class,
                                                     new ResultTypeResolvingConverterImpl());
    converterManager.registerConverterImplementation(InterceptorRefResolveConverter.class,
                                                     new InterceptorRefResolveConverterImpl());

    converterManager.registerConverterImplementation(ValidatorConfigResolveConverter.class,
                                                     new ValidatorConfigResolveConverterImpl());
  }

  @NotNull
  public String getComponentName() {
    return "StrutsProjectComponent";
  }

}