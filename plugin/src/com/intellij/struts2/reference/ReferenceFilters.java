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

import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.xml.XmlTag;
import com.intellij.struts2.StrutsConstants;
import org.jetbrains.annotations.NonNls;

/**
 * Filters for use in {@link com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider}s.
 *
 * @author Yann C&eacute;bron
 */
public final class ReferenceFilters {

  private ReferenceFilters() {
  }

  /**
   * Struts UI taglib URI.
   */
  @NonNls
  public static final String TAGLIB_STRUTS_UI_URI = "/struts-tags";

  /**
   * Struts UI taglib namespace filter.
   */
  public static final NamespaceFilter NAMESPACE_TAGLIB_STRUTS_UI = new NamespaceFilter(TAGLIB_STRUTS_UI_URI);

  /**
   * struts.xml namespace filter.
   */
  public static final NamespaceFilter NAMESPACE_STRUTS_XML =
      new NamespaceFilter(StrutsConstants.STRUTS_2_0_DTD_ID, StrutsConstants.STRUTS_2_0_DTD_URI);

  public final static ClassFilter TAG_CLASS_FILTER = new ClassFilter(XmlTag.class);

  public static ScopeFilter andTagNames(final ElementFilter namespace, final String... tagNames) {
    return new ScopeFilter(new ParentElementFilter(new AndFilter(namespace, TAG_CLASS_FILTER, new TextFilter(tagNames)),
        2));
  }

}