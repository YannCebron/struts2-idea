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

package com.intellij.struts2.dom.validator.impl;

import com.intellij.openapi.module.Module;
import com.intellij.struts2.dom.validator.ValidatorManager;
import com.intellij.struts2.dom.validator.config.ValidatorConfig;
import com.intellij.struts2.dom.validator.config.ValidatorConfigResolveConverter;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Yann C�bron
 */
public class ValidatorConfigResolveConverterImpl extends ValidatorConfigResolveConverter {

  @NotNull
  public Collection<? extends ValidatorConfig> getVariants(final ConvertContext context) {
    final Module module = context.getModule();
    final ValidatorManager validatorManager = ValidatorManager.getInstance(module.getProject());
    return validatorManager.getValidators(module);
  }

  public ValidatorConfig fromString(@Nullable @NonNls final String name, final ConvertContext context) {
    if (name == null) {
      return null;
    }

    final Collection<? extends ValidatorConfig> validatorConfigs = getVariants(context);
    for (final ValidatorConfig validatorConfig : validatorConfigs) {
      if (name.equals(validatorConfig.getName().getStringValue())) {
        return validatorConfig;
      }
    }

    return null;
  }

}