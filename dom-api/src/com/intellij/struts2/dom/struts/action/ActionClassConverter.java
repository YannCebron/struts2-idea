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

package com.intellij.struts2.dom.struts.action;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Converter for &lt;action&gt; "class" attribute.
 *
 * @author Yann C�bron
 */
public abstract class ActionClassConverter extends Converter<PsiClass> implements CustomReferenceConverter<PsiClass> {

  /**
   * {@link #createReferences(com.intellij.util.xml.GenericDomValue, com.intellij.psi.PsiElement, com.intellij.util.xml.ConvertContext)} stores
   * its results under this key for error highlighting in Struts2ModelInspection.
   */
  public static final Key<PsiReference[]> REFERENCES_KEY = Key.create("STRUTS2_ACTIONCLASS_REFERENCES");

  /**
   * Stores the reference type display names.
   */
  public static final Key<String[]> REFERENCES_TYPES = Key.create("STRUTS2_REFERENCE_TYPES");

  /**
   * References from this EP will be added automatically.
   */
  public static final ExtensionPointName<ActionClassConverterContributor> EP_NAME =
      new ExtensionPointName<ActionClassConverterContributor>("com.intellij.struts2.actionClassContributor");


  /**
   * Contributes results to {@link ActionClassConverter}.
   *
   * @author Yann C�bron
   */
  public static interface ActionClassConverterContributor extends PsiReferenceProvider {

    /**
     * Is this contributor suitable in the current resolving context.
     *
     * @param convertContext Current context.
     * @return true if yes, false otherwise.
     */
    boolean isSuitable(@NotNull final ConvertContext convertContext);

    /**
     * Returns this contributor's type name for display in messages.
     *
     * @return Display name.
     */
    String getContributorType();

  }

}