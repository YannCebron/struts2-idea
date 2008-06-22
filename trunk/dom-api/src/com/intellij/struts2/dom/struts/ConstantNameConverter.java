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

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Provides the predefined <code>constant</code> names as completion variants.
 * <p/>
 * See <code>struts.properties</code> in struts.jar.
 *
 * @author Yann C&eacute;bron
 */
public class ConstantNameConverter extends ResolvingConverter.StringConverter {

  private static final List<String> PREDEFINED_VARIANTS =
    Arrays.asList("struts.i18n.encoding",
                  "struts.objectFactory",
                  "struts.objectFactory.spring.autoWire",
                  "struts.objectFactory.spring.useClassCache",
                  "struts.objectTypeDeterminer",
                  "struts.multipart.parser",
                  "struts.multipart.saveDir",
                  "struts.multipart.maxSize",
                  "struts.custom.properties",
                  "struts.mapper.class",
                  "struts.action.extension",
                  "struts.serve.static",
                  "struts.serve.static.browserCache",
                  "struts.enable.DynamicMethodInvocation",
                  "struts.enable.SlashesInActionNames",
                  "struts.tag.altSyntax",
                  "struts.devMode",
                  "struts.i18n.reload",
                  "struts.ui.theme",
                  "struts.ui.templateDir",
                  "struts.ui.templateSuffix",
                  "struts.configuration.xml.reload",
                  "struts.velocity.configfile",
                  "struts.velocity.contexts",
                  "struts.velocity.toolboxlocation",
                  "struts.url.http.port",
                  "struts.url.https.port",
                  "struts.url.includeParams",
                  "struts.custom.i18n.resources",
                  "struts.dispatcher.parametersWorkaround",
                  "struts.freemarker.manager.classname",
                  "struts.freemarker.templatesCache",
                  "struts.freemarker.beanwrapperCache",
                  "struts.freemarker.wrapper.altMap",
                  "struts.xslt.nocache",
                  "struts.configuration.files",
                  "struts.mapper.alwaysSelectFullNamespace");

  @NotNull
  public Collection<? extends String> getVariants(final ConvertContext context) {
    return PREDEFINED_VARIANTS;
  }

}