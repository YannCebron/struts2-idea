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

package com.intellij.struts2.reference.jsp;

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.struts2.dom.struts.model.StrutsManager;
import com.intellij.struts2.dom.struts.model.StrutsModel;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom tags attribute "namespace".
 *
 * @author Yann C�bron
 */
public class NamespaceReferenceProvider extends PsiReferenceProviderBase {

  @NotNull
  public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
    final StrutsManager strutsManager = StrutsManager.getInstance(psiElement.getProject());
    final StrutsModel strutsModel = strutsManager.getCombinedModel(ModuleUtil.findModuleForPsiElement(psiElement));

    if (strutsModel == null) {
      return PsiReference.EMPTY_ARRAY;
    }

    return new PsiReference[]{new NamespaceReference((XmlAttributeValue) psiElement, strutsModel)
    };
  }

  private static class NamespaceReference extends PsiReferenceBase.Poly<XmlAttributeValue> implements EmptyResolveMessageProvider {
    private final StrutsModel strutsModel;

    private NamespaceReference(final XmlAttributeValue psiElement, final StrutsModel strutsModel) {
      super(psiElement);
      this.strutsModel = strutsModel;
    }

    @NotNull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
      final List<ResolveResult> resolveResults = new ArrayList<ResolveResult>();
      final String namespace = myElement.getValue();
      for (final StrutsPackage strutsPackage : strutsModel.getStrutsPackages()) {
        if (namespace.equals(strutsPackage.searchNamespace())) {
          final XmlTag packageTag = strutsPackage.getXmlTag();
          assert packageTag != null;
          resolveResults.add(new PsiElementResolveResult(packageTag));
        }
      }
      return resolveResults.toArray(new ResolveResult[resolveResults.size()]);
    }

    public Object[] getVariants() {
      final List<String> variants = new ArrayList<String>();
      for (final StrutsPackage strutsPackage : strutsModel.getStrutsPackages()) {
        final String namespace = strutsPackage.searchNamespace();
        variants.add(namespace);
      }
      return variants.toArray(new Object[variants.size()]);
    }

    public String getUnresolvedMessagePattern() {
      return "Cannot resolve namespace ''" + getCanonicalText() + "''";
    }
  }

}