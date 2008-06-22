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

package com.intellij.struts2.facet.ui;

import com.intellij.facet.Facet;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.libraries.FacetLibrariesValidator;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.struts2.facet.configuration.StrutsValidationConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Struts2 facet tab "Validation".
 *
 * @author Yann C&eacute;bron
 */
public class ValidationConfigurationTab extends FacetEditorTab {
  private JPanel myPanel;
  private JCheckBox reportErrorsAsWarningCheckBox;
  private JCheckBox strutsXmlCheckBox;
  private JCheckBox validatorXmlCheckBox;

  private final FacetLibrariesValidator validator;
  private final StrutsValidationConfiguration validationConfiguration;

  public ValidationConfigurationTab(final FacetLibrariesValidator validator,
                                    final StrutsValidationConfiguration validationConfiguration) {
    this.validator = validator;
    this.validationConfiguration = validationConfiguration;


    strutsXmlCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        updateReportErrorsAsWarningsCheckboxState();
      }
    });
    validatorXmlCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        updateReportErrorsAsWarningsCheckboxState();
      }
    });

    updateReportErrorsAsWarningsCheckboxState();
  }

  private void updateReportErrorsAsWarningsCheckboxState() {
    final boolean enabled = strutsXmlCheckBox.isSelected() || validatorXmlCheckBox.isSelected();
    reportErrorsAsWarningCheckBox.setEnabled(enabled);
  }

  public void onFacetInitialized(@NotNull final Facet facet) {
    validator.onFacetInitialized(facet);
  }

  @Nullable
  public Icon getIcon() {
    return IconLoader.getIcon("/objectBrowser/showGlobalInspections.png");
  }

  @Nls
  public String getDisplayName() {
    return "Validation";
  }

  public JComponent createComponent() {
    return myPanel;
  }

  public boolean isModified() {
    if (validationConfiguration.isReportErrorsAsWarning() != reportErrorsAsWarningCheckBox.isSelected()) {
      return true;
    }

    if (validationConfiguration.isValidateStruts() != strutsXmlCheckBox.isSelected()) {
      return true;
    }

    if (validationConfiguration.isValidateValidation() != validatorXmlCheckBox.isSelected()) {
      return true;
    }

    return false;
  }

  public void apply() throws ConfigurationException {
    validationConfiguration.setReportErrorsAsWarning(reportErrorsAsWarningCheckBox.isSelected());
    validationConfiguration.setValidateStruts(strutsXmlCheckBox.isSelected());
    validationConfiguration.setValidateValidation(validatorXmlCheckBox.isSelected());
  }

  public void reset() {
    reportErrorsAsWarningCheckBox.setSelected(validationConfiguration.isReportErrorsAsWarning());
    strutsXmlCheckBox.setSelected(validationConfiguration.isValidateStruts());
    validatorXmlCheckBox.setSelected(validationConfiguration.isValidateValidation());
    updateReportErrorsAsWarningsCheckboxState();
  }

  public void disposeUIResources() {
  }

}