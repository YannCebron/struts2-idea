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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.converters.PathReferenceConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for using existing PathReferenceProvider in PsiReferenceProvider (-> autocompletion, highlighting).
 * <p/>
 * TODO inline?
 *
 * @author Yann CŽbron
 */
class PathReferenceProviderWrapper extends PsiReferenceProviderBase {

  private PathReferenceConverter converter;

  public PathReferenceProviderWrapper(final PathReferenceConverter converter) {
    this.converter = converter;
  }

  @NotNull
  public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
    // TODO get rid of nested <param> tags
    final XmlTag tag = (XmlTag) psiElement;
    return tag.getSubTags().length != 0 ? PsiReference.EMPTY_ARRAY : converter.createReferences(psiElement, false);
  }

}