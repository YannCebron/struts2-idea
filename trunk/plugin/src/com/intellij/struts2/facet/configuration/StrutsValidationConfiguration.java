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

package com.intellij.struts2.facet.configuration;

/**
 * Container for persisting {@link com.intellij.struts2.facet.ui.ValidationConfigurationTab} options.
 *
 * @author Yann CŽbron
 */
public class StrutsValidationConfiguration {

  private boolean reportErrorsAsWarning;

  private boolean validateStruts = true;

  private boolean validateValidation = true;

  public boolean isReportErrorsAsWarning() {
    return reportErrorsAsWarning;
  }

  public void setReportErrorsAsWarning(final boolean reportErrorsAsWarning) {
    this.reportErrorsAsWarning = reportErrorsAsWarning;
  }

  public boolean isValidateStruts() {
    return validateStruts;
  }

  public void setValidateStruts(final boolean validateStruts) {
    this.validateStruts = validateStruts;
  }

  public boolean isValidateValidation() {
    return validateValidation;
  }

  public void setValidateValidation(final boolean validateValidation) {
    this.validateValidation = validateValidation;
  }

}