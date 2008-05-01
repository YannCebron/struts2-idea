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

package com.intellij.struts2.dom.struts.impl;

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.spring.SpringManager;
import com.intellij.spring.SpringModel;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;
import com.intellij.struts2.dom.struts.action.ActionClassConverter;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends &lt;action&gt; "class" resolving to Spring beans.
 *
 * @author Yann C�bron
 */
public class ActionClassConverterSpringContributor extends PsiReferenceProviderBase implements ActionClassConverter.ActionClassConverterContributor {

  /**
   * Checks if struts2-spring-plugin is present in current module.
   *
   * @param convertContext Current context.
   * @return true if yes.
   */
  public boolean isSuitable(@NotNull final ConvertContext convertContext) {
    return convertContext.findClass("org.apache.struts2.spring.StrutsSpringObjectFactory", null) != null;
  }

  public String getContributorType() {
    return "Spring bean";
  }

  @NotNull
  public PsiReference[] getReferencesByElement(final PsiElement psiElement) {
    return new PsiReference[]{new SpringBeanReference((XmlAttributeValue) psiElement)};
  }


  private static class SpringBeanReference extends PsiReferenceBase<XmlAttributeValue> {

    private SpringBeanReference(final XmlAttributeValue element) {
      super(element);
    }

    public boolean isSoft() {
      return true;
    }

    public PsiElement resolve() {
      final SpringModel model = getSpringModel();
      if (model == null) {
        return null;
      }

      final String beanName = myElement.getValue();
      final SpringBeanPointer springBean = model.findBean(beanName);

      return springBean == null ? null : springBean.getBeanClass();
    }

    @Nullable
    private SpringModel getSpringModel() {
      final Module module = ModuleUtil.findModuleForPsiElement(myElement);
      if (module == null) {
        return null;
      }

      return SpringManager.getInstance(module.getProject()).getCombinedModel(module);
    }

    @SuppressWarnings({"unchecked"})
    public Object[] getVariants() {
      final SpringModel model = getSpringModel();
      if (model == null) {
        return new Object[0];
      }

      final List lookups = new ArrayList();
      final Collection<? extends SpringBeanPointer> list = model.getAllCommonBeans(true);

      for (final SpringBeanPointer bean : list) {
        final String beanName = bean.getName();
        final PsiFile psiFile = bean.getContainingFile();

        if (psiFile != null && StringUtil.isNotEmpty(beanName)) {
          //noinspection ConstantConditions
          lookups.add(LookupValueFactory.createLookupValueWithHint(beanName, bean.getBeanIcon(), psiFile.getName()));
        }
      }

      return lookups.toArray(new Object[lookups.size()]);
    }
  }

}