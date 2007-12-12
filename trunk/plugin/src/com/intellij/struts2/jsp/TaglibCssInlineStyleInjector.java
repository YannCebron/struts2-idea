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

package com.intellij.struts2.jsp;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.css.CssSupportLoader;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.struts2.reference.ReferenceFilters;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Adds CSS inline support for UI tags attribute "cssStyle".
 *
 * @author Yann C�bron
 */
public class TaglibCssInlineStyleInjector implements MultiHostInjector {

  @NonNls
  private static final String CSS_STYLE_ATTRIBUTE_NAME = "cssStyle";

  public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement context) {
    // operate only in JSP(X) files
    final FileType fileType = context.getContainingFile().getFileType();
    if (fileType != StdFileTypes.JSP &&
        fileType != StdFileTypes.JSPX) {
      return;
    }

    if (ReferenceFilters.NAMESPACE_TAGLIB_STRUTS_UI.isAcceptable(context.getParent().getParent(), null)) {
      @NonNls final String name = ((XmlAttribute) context.getParent()).getName();
      if (name.equals(CSS_STYLE_ATTRIBUTE_NAME)) {
        final TextRange range = new TextRange(1, context.getTextLength() - 1);
        registrar.startInjecting(CssSupportLoader.CSS_FILE_TYPE.getLanguage())
          .addPlace("inline.style {", "}", (PsiLanguageInjectionHost) context, range)
          .doneInjecting();
      }
    }
  }

  @NotNull
  public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return Arrays.asList(XmlAttributeValue.class);
  }

}