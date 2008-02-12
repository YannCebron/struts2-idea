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

package com.intellij.struts2.dom;

import com.intellij.codeInsight.daemon.quickFix.CreateFieldOrPropertyFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyMemberType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.struts2.dom.struts.action.Action;
import com.intellij.struts2.dom.struts.action.Result;
import com.intellij.struts2.dom.struts.strutspackage.*;
import com.intellij.struts2.dom.validator.FieldValidator;
import com.intellij.struts2.dom.validator.Validator;
import com.intellij.struts2.dom.validator.config.ValidatorConfig;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves to all writable properties.
 *
 * @author Yann CŽbron
 */
public class ParamsNameConverter extends ResolvingConverter<PsiMethod> {

  @NotNull
  public Collection<? extends PsiMethod> getVariants(final ConvertContext context) {
    final DomElement parent = getEnclosingElement(context);
    if (parent != null) {
      final PsiClass enclosingClass = getEnclosingClass(parent);

      if (enclosingClass != null) {
        return getAllWritableFields(enclosingClass);
      }
    }

    return Collections.emptySet();
  }

  /**
   * Try to resolve to underlying {@link PsiField} if possible via setter-name, else to setter-method.
   *
   * @param resolvedValue Resolved value.
   * @return PsiField or PsiMethod.
   */
  public PsiElement getPsiElement(@Nullable final PsiMethod resolvedValue) {
    if (resolvedValue == null) {
      return null;
    }

    final PsiField field = PropertyUtil.findPropertyField(resolvedValue.getProject(),
        resolvedValue.getContainingClass(),
        PropertyUtil.getPropertyName(resolvedValue),
        false);
    return field != null ? field : resolvedValue;
  }

  public PsiMethod fromString(@Nullable @NonNls final String name, final ConvertContext context) {
    if (name == null) {
      return null;
    }

    final Collection<? extends PsiMethod> variants = getVariants(context);
    for (final PsiMethod variant : variants) {
      if (name.equals(PropertyUtil.getPropertyName(variant))) {
        return variant;
      }
    }
    return null;
  }

  public String toString(@Nullable final PsiMethod field, final ConvertContext context) {
    return field != null ? PropertyUtil.getPropertyName(field) : null;
  }

  public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
    return "Cannot resolve param-property ''" + s + "''";
  }

  public LocalQuickFix[] getQuickFixes(final ConvertContext context) {
    final DomElement enclosingElement = getEnclosingElement(context);

    if (enclosingElement == null) {
      return LocalQuickFix.EMPTY_ARRAY;
    }

    final PsiClass psiClass = getEnclosingClass(enclosingElement);
    if (psiClass == null || !psiClass.isWritable()) {
      return LocalQuickFix.EMPTY_ARRAY;
    }

    final GenericAttributeValue element = (GenericAttributeValue) context.getInvocationElement();

    return new LocalQuickFix[]{new CreateFieldOrPropertyFix(psiClass,
        element.getStringValue(),
        PsiType.getJavaLangString(context.getPsiManager(), GlobalSearchScope.moduleScope(context.getModule())),
        PropertyMemberType.FIELD, PsiAnnotation.EMPTY_ARRAY)};
  }

  @Nullable
  private static PsiClass getEnclosingClass(@NotNull final DomElement parent) {

    // struts.xml ----------------------------------------

    if (parent instanceof Action) {
      // resolve <action> "class" (locally or via default-class-ref)
      return ((Action) parent).searchActionClass();
    }

    if (parent instanceof DefaultActionRef) {
      final Action action = ((DefaultActionRef) parent).getName().getValue();
      if (action == null) {
        return null;
      }
      return action.searchActionClass();
    }

    if (parent instanceof DefaultClassRef) {
      return ((DefaultClassRef) parent).getDefaultClass().getValue();
    }

    if (parent instanceof Result) {
      final ResultType resultType = ((Result) parent).getType().getValue();
      if (resultType != null) {
        return resultType.getResultTypeClass().getValue();
      }

      // find default result-type in enclosing package or its parents
      final StrutsPackage strutsPackage = parent.getParentOfType(StrutsPackage.class, true);
      if (strutsPackage == null) {
        return null;
      }

      final ResultType defaultResultType = strutsPackage.searchDefaultResultType();
      return defaultResultType != null ? defaultResultType.getResultTypeClass().getValue() : null;
    }

    if (parent instanceof GlobalResult) {
      final ResultType resultType = ((GlobalResult) parent).getType().getValue();
      return resultType != null ? resultType.getResultTypeClass().getValue() : null;
    }


    if (parent instanceof InterceptorRef) {
      final InterceptorRef interceptorRef = (InterceptorRef) parent;
      final InterceptorOrStackBase interceptorOrStack = interceptorRef.getName().getValue();
      if (interceptorOrStack instanceof Interceptor) {
        return ((Interceptor) interceptorOrStack).getInterceptorClass().getValue();
      }

      // TODO [interceptor].[property]
      /*if (interceptorOrStack instanceof InterceptorStack) {
        final GenericAttributeValue element = (GenericAttributeValue) context.getInvocationElement();
        System.out.println("resolving stack:  " + element.getStringValue());
        final String path = element.getStringValue();
        System.out.println("path = " + path);
        if (path != null) {
          //noinspection ConstantConditions
          final String[] strings = path.split("\\.");

          System.out.println("strings.length = " + strings.length);
          if (strings.length == 2) {
            final String validator = strings[0];
            final String property = strings[1];
            System.out.println("validator = " + validator);
            System.out.println("property = " + property);
            final List<InterceptorRef> refs = ((InterceptorStack) interceptorOrStack).getInterceptorRefs();
            final InterceptorRef byName = DomUtil.findByName(refs, validator);
            System.out.println("interceptorRef = " + byName.getName().getValue());
            if (byName != null) {
              return getEnclosingClass(byName.getName().getValue(), context);
            }

          }
        }
      }*/

    }

    // validation.xml ----------------------------------------

    if (parent instanceof FieldValidator) {
      final ValidatorConfig validatorConfig = ((FieldValidator) parent).getType().getValue();
      return validatorConfig != null ? validatorConfig.getValidatorClass().getValue() : null;
    }

    if (parent instanceof Validator) {
      final ValidatorConfig validatorConfig = ((Validator) parent).getType().getValue();
      return validatorConfig != null ? validatorConfig.getValidatorClass().getValue() : null;
    }

    return null;
  }

  @NotNull
  private static Collection<? extends PsiMethod> getAllWritableFields(@NotNull final PsiClass psiClass) {
    final PsiMethod[] allMethods = psiClass.getAllMethods();

    final Set<PsiMethod> propertySetters = new HashSet<PsiMethod>();
    for (final PsiMethod psiMethod : allMethods) {
      if (!psiMethod.hasModifierProperty(PsiModifier.STATIC) &&
          PropertyUtil.isSimplePropertySetter(psiMethod)) {
        propertySetters.add(psiMethod);
      }
    }
    return propertySetters;
  }

  /**
   * Gets the enclosing parent element.
   *
   * @param context Current context.
   * @return Parent element or <code>null</code> if none found (should not happen in valid XML).
   */
  @Nullable
  private static DomElement getEnclosingElement(final ConvertContext context) {
    final DomElement current = context.getInvocationElement();
    final DomElement parent = current.getParent();
    return parent != null ? parent.getParent() : null;
  }

}