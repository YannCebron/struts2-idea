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
 */
package com.intellij.struts2.dom.struts;

import com.intellij.jpa.model.xml.impl.converters.ClassConverterBase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.spring.SpringManager;
import com.intellij.spring.SpringModel;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Resolve &lt;constant&gt; "value" dynamically based on underlying type.
 * <p/>
 * See <code>struts.properties</code> in struts.jar.
 *
 * @author Yann C&eacute;bron
 */
public class ConstantValueConverter extends WrappingConverter {

  private static final Map<String, Converter> CONVERTERS = new HashMap<String, Converter>();

  static {
    addClassWithShortcutProperty("struts.configuration", "", Collections.<String, String>emptyMap());
    addStringProperty("struts.i18n.encoding"); // TODO

    final Map<String, String> objectFactoryShortcutMap = new HashMap<String, String>();
    objectFactoryShortcutMap.put("struts", "org.apache.struts2.impl.StrutsObjectFactory");
    objectFactoryShortcutMap.put("spring", "org.apache.struts2.spring.StrutsSpringObjectFactory");
    addClassWithShortcutProperty("struts.objectFactory",
                                 "com.opensymphony.xwork2.ObjectFactory",
                                 objectFactoryShortcutMap);

    addStringValuesProperty("struts.objectFactory.spring.autoWire", "name", "type", "auto", "constructor");
    addBooleanProperty("struts.objectFactory.spring.useClassCache");

    final Map<String, String> objectDeterminerShortcutMap = new HashMap<String, String>();
    objectDeterminerShortcutMap.put("tiger", "com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer");
    objectDeterminerShortcutMap.put("notiger", "com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer");
    objectDeterminerShortcutMap.put("struts", "com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer");
    addClassWithShortcutProperty("struts.objectTypeDeterminer",
                                 "com.opensymphony.xwork2.util.ObjectTypeDeterminer",
                                 objectDeterminerShortcutMap);

    addStringValuesProperty("struts.multipart.parser", "cos", "pell", "jakarta");
    addStringProperty("struts.multipart.saveDir");
    addIntegerProperty("struts.multipart.maxSize");
    addStringProperty("struts.custom.properties");
    addClassWithShortcutProperty("struts.mapper.class", "", Collections.<String, String>emptyMap());
    addStringProperty("struts.action.extension");
    addBooleanProperty("struts.serve.static");
    addBooleanProperty("struts.serve.static.browserCache");
    addBooleanProperty("struts.enable.DynamicMethodInvocation");
    addBooleanProperty("struts.enable.SlashesInActionNames");
    addBooleanProperty("struts.tag.altSyntax");
    addBooleanProperty("struts.devMode");
    addBooleanProperty("struts.i18n.reload");
    addStringValuesProperty("struts.ui.theme", "simple", "xhtml", "ajax");
    addStringProperty("struts.ui.templateDir");
    addStringProperty("struts.ui.templateSuffix");
    addBooleanProperty("struts.configuration.xml.reload");
    addStringProperty("struts.velocity.configfile");
    addStringProperty("struts.velocity.contexts");
    addStringProperty("struts.velocity.toolboxlocation");
    addIntegerProperty("struts.url.http.port");
    addIntegerProperty("struts.url.https.port");
    addStringValuesProperty("struts.url.includeParams", "none", "get", "all");
    addStringProperty("struts.custom.i18n.resources");
    addBooleanProperty("struts.dispatcher.parametersWorkaround");
    addClassWithShortcutProperty("struts.freemarker.manager.classname",
                                 "org.apache.struts2.views.freemarker.FreemarkerManager",
                                 Collections.<String, String>emptyMap());
    addBooleanProperty("struts.freemarker.templatesCache");
    addBooleanProperty("struts.freemarker.beanwrapperCache");
    addBooleanProperty("struts.freemarker.wrapper.altMap");
    addBooleanProperty("struts.xslt.nocache");
    addStringProperty("struts.configuration.files");
    addBooleanProperty("struts.mapper.alwaysSelectFullNamespace");
  }

  public Converter<?> getConverter(@NotNull final GenericDomValue domElement) {
    final Constant constant = domElement.getParentOfType(Constant.class, true);
    assert constant != null;
    return CONVERTERS.get(constant.getName().getStringValue());
  }

  static Set<String> getConstantNames() {
    return CONVERTERS.keySet();
  }

  private static void addStringProperty(@NonNls final String propertyName) {
    CONVERTERS.put(propertyName, EMPTY_CONVERTER);
  }

  private static void addStringValuesProperty(@NonNls final String propertyName, @NonNls final String... values) {
    CONVERTERS.put(propertyName, new StringValuesConverter(values));
  }

  private static void addClassWithShortcutProperty(@NonNls final String propertyName,
                                                   @NonNls final String baseClass,
                                                   final Map<String, String> shortCutToPsiClassMap) {
    CONVERTERS.put(propertyName, new PsiClassSpringBeanShortcutConverter(baseClass, shortCutToPsiClassMap));
  }

  private static void addBooleanProperty(@NonNls final String propertyName) {
    CONVERTERS.put(propertyName, ResolvingConverter.BOOLEAN_CONVERTER);
  }

  private static void addIntegerProperty(@NonNls final String propertyName) {
    CONVERTERS.put(propertyName, INTEGER_CONVERTER);
  }


  /**
   * Resolves to JAVA-Class, Shortcut-name or Spring bean name.
   */
  private static class PsiClassSpringBeanShortcutConverter extends ClassConverterBase {

    private final String baseClass;
    private final Map<String, String> shortCutToPsiClassMap;

    private PsiClassSpringBeanShortcutConverter(final @NonNls String baseClass,
                                                final Map<String, String> shortCutToPsiClassMap) {

      this.baseClass = baseClass;
      this.shortCutToPsiClassMap = shortCutToPsiClassMap;
    }

    public PsiClass fromString(@Nullable @NonNls final String s, final ConvertContext convertContext) {
      if (s == null) {
        return null;
      }

      final String shortCutClassName = shortCutToPsiClassMap.get(s);

      // 1. via shortcut
      if (StringUtil.isNotEmpty(shortCutClassName)) {
        return super.fromString(shortCutClassName, convertContext);
      }

      // 2. via Spring bean name (if available)
      final Module module = convertContext.getModule();
      if (convertContext.findClass("org.apache.struts2.spring.StrutsSpringObjectFactory", null) != null) {
        final SpringModel springModel = SpringManager.getInstance(module.getProject()).getCombinedModel(module);
        if (springModel != null) {
          final SpringBeanPointer springBeanPointer = springModel.findBean(s);
          if (springBeanPointer != null) {
            return springBeanPointer.getSpringBean().getBeanClass();
          }
        }
      }

      // 3. via JAVA-class
      return super.fromString(s, convertContext);
    }

    @NotNull
    public Set<String> getAdditionalVariants(@NotNull final ConvertContext context) {
      return shortCutToPsiClassMap.keySet();
    }

    protected void setJavaClassReferenceProviderOptions(final JavaClassReferenceProvider javaClassReferenceProvider,
                                                        final ConvertContext convertContext) {
      super.setJavaClassReferenceProviderOptions(javaClassReferenceProvider, convertContext);
      javaClassReferenceProvider.setOption(JavaClassReferenceProvider.CONCRETE, Boolean.TRUE);
      javaClassReferenceProvider.setOption(JavaClassReferenceProvider.NOT_INTERFACE, Boolean.TRUE);
      javaClassReferenceProvider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, new String[]{baseClass});
    }
  }


  /**
   * Resolves to list of given names.
   */
  private static class StringValuesConverter extends ResolvingConverter.StringConverter {

    private final String[] values;

    private StringValuesConverter(@NonNls final String... values) {
      Arrays.sort(values);
      this.values = values;
    }

    public String fromString(final String s, final ConvertContext context) {
      return Arrays.binarySearch(values, s) > -1 ? s : null;
    }

    @NotNull
    public Collection<? extends String> getVariants(final ConvertContext context) {
      return Arrays.asList(values);
    }
  }

}