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

package com.intellij.struts2.reference;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.css.impl.util.CssInHtmlClassOrIdReferenceProvider;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.struts2.dom.struts.impl.path.StrutsPathReferenceConverterImpl;
import static com.intellij.struts2.reference.ReferenceFilters.NAMESPACE_STRUTS_XML;
import static com.intellij.struts2.reference.ReferenceFilters.NAMESPACE_TAGLIB_STRUTS_UI;
import com.intellij.struts2.reference.jsp.ActionReferenceProvider;
import com.intellij.struts2.reference.jsp.NamespaceReferenceProvider;
import com.intellij.struts2.reference.jsp.ThemeReferenceProvider;
import org.jetbrains.annotations.NonNls;

/**
 * Registers all {@link com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider}s.
 *
 * @author Yann C�bron
 */
public class StrutsReferenceProviderComponent extends AbstractProjectComponent {

  private final ReferenceProvidersRegistry registry;

  private static final String[] TAGLIB_UI_FORM_TAGS = new String[]{
      "autocompleter",
      "checkbox",
      "checkboxlist",
      "combobox",
      "doubleselect",
      "head",
      "file",
      "form",
      "hidden",
      "label",
      "optiontransferselect",
      "optgroup",
      "password",
      "radio",
      "reset",
      "select",
      "submit",
      "textarea",
      "textfield",
      "token",
      "updownselect"
  };

  private static final StaticStringValuesReferenceProvider BOOLEAN_VALUE_REFERENCE_PROVIDER =
      new StaticStringValuesReferenceProvider(false, "false", "true");

  private static final ActionReferenceProvider ACTION_REFERENCE_PROVIDER = new ActionReferenceProvider();

  protected StrutsReferenceProviderComponent(final Project project) {
    super(project);
    registry = ReferenceProvidersRegistry.getInstance(project);
  }

  public void initComponent() {
    registerUITags();

    registerStrutsXmlTags();
  }

  private void registerStrutsXmlTags() {
    // <result> body content (location)
    registry.registerXmlTagReferenceProvider(
        new String[]{"result"},
        NAMESPACE_STRUTS_XML, true,
        new PathReferenceProviderWrapper(new StrutsPathReferenceConverterImpl()));

    // <result> "name" common values
    registerTags(new StaticStringValuesReferenceProvider("error", "input", "login", "success"),
                 "name", NAMESPACE_STRUTS_XML,
                 "result");
  }

  private void registerUITags() {

    // common attributes --------------------------------------

    registerTags(new ThemeReferenceProvider(),
                 "theme", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS);

    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "disabled", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS);
//    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER, // TODO ?!
//                 "jsTooltipEnabled", ReferenceFilters.NAMESPACE_TAGLIB_STRUTS_UI,
//                 TAGLIB_UI_FORM_TAGS);
    registerTags(new StaticStringValuesReferenceProvider(false, "left", "top"),
                 "labelposition", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS);
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "required", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS);
    registerTags(new StaticStringValuesReferenceProvider(false, "left", "right"),
                 "requiredposition", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS); // TODO all tags included?

    // elements with "readonly"
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "readonly", NAMESPACE_TAGLIB_STRUTS_UI,
                 "autocompleter", "combobox", "password", "textarea", "textfield");

    // elements with "action"
    registerTags(ACTION_REFERENCE_PROVIDER,
                 "action", NAMESPACE_TAGLIB_STRUTS_UI,
                 "form", "url");

    registerTags(ACTION_REFERENCE_PROVIDER,
                 "name", NAMESPACE_TAGLIB_STRUTS_UI,
                 "action");

    // elements with "namespace"
    registerTags(new NamespaceReferenceProvider(),
                 "namespace", NAMESPACE_TAGLIB_STRUTS_UI,
                 "action", "form", "url");

    // elements with "cssClass"
    registerTags(new CssInHtmlClassOrIdReferenceProvider(),
                 "cssClass", NAMESPACE_TAGLIB_STRUTS_UI,
                 TAGLIB_UI_FORM_TAGS);

    // specific tags ---------------------------------------------------------------------------------------------------

    // <action>
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "flush", NAMESPACE_TAGLIB_STRUTS_UI,
                 "action");
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "executeResult", NAMESPACE_TAGLIB_STRUTS_UI,
                 "action");
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "ignoreContextParams", NAMESPACE_TAGLIB_STRUTS_UI,
                 "action");

    // <form>
    registerTags(new StaticStringValuesReferenceProvider(false,
                                                         "application/x-www-form-urlencoded",
                                                         "multipart/form-data"),
                 "enctype", NAMESPACE_TAGLIB_STRUTS_UI,
                 "form");
    registerTags(new StaticStringValuesReferenceProvider("GET", "POST"),
                 "method", NAMESPACE_TAGLIB_STRUTS_UI,
                 "form");
    // TODO portletMode
    registerTags(new StaticStringValuesReferenceProvider("_blank", "_parent", "_self", "_top"),
                 "target", NAMESPACE_TAGLIB_STRUTS_UI,
                 "form");
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "validate", NAMESPACE_TAGLIB_STRUTS_UI,
                 "form");
    // TODO windowState

    // <submit>
    registerTags(new StaticStringValuesReferenceProvider(false, "input", "button", "image"),
                 "type", NAMESPACE_TAGLIB_STRUTS_UI,
                 "submit");

    // <table>
    registerTags(new StaticStringValuesReferenceProvider(false, "ASC", "DESC", "NONE"),
                 "sortOrder", NAMESPACE_TAGLIB_STRUTS_UI,
                 "table");
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "sortable", NAMESPACE_TAGLIB_STRUTS_UI,
                 "table");

    // <url>
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "encode", NAMESPACE_TAGLIB_STRUTS_UI,
                 "url");
    registerTags(BOOLEAN_VALUE_REFERENCE_PROVIDER,
                 "escapeAmp", NAMESPACE_TAGLIB_STRUTS_UI,
                 "url");
  }

  /**
   * Register the given provider on the given XmlAttribute/Namespace/XmlTag(s) combination.
   *
   * @param provider        Provider to install.
   * @param attributeName   Attribute name.
   * @param namespaceFilter Namespace for tag(s).
   * @param tagNames        Tag name(s).
   */
  private void registerTags(final PsiReferenceProvider provider,
                            final @NonNls String attributeName,
                            final NamespaceFilter namespaceFilter,
                            final @NonNls String... tagNames) {
    registry.registerXmlAttributeValueReferenceProvider(new String[]{attributeName},
                                                        ReferenceFilters.andTagNames(namespaceFilter, tagNames),
                                                        provider);
  }
}
