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

package com.intellij.struts2.dom.inspection;

import com.intellij.struts2.dom.struts.StrutsRoot;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Default DOM-Model inspection for struts.xml files.
 *
 * @author Yann CŽbron
 */
public class Struts2ModelInspection extends BasicDomElementsInspection<StrutsRoot> {

  public Struts2ModelInspection() {
    super(StrutsRoot.class);
  }

  // TODO hack for suppresing wildcard-resolving
  protected boolean shouldCheckResolveProblems(final GenericDomValue value) {
    final String stringValue = value.getStringValue();
    return stringValue == null || stringValue.indexOf('{') < 0;
  }

  @NotNull
  public String getGroupDisplayName() {
    return "Struts 2";
  }

  @NotNull
  public String getDisplayName() {
    return "Struts 2 Model Inspection";
  }

  @NotNull
  @NonNls
  public String getShortName() {
    return "Struts2ModelInspection";
  }

}