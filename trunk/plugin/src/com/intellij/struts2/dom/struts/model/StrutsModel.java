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

package com.intellij.struts2.dom.struts.model;

import com.intellij.psi.PsiClass;
import com.intellij.struts2.dom.struts.StrutsRoot;
import com.intellij.struts2.dom.struts.action.Action;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.util.xml.model.DomModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Service for accessing <code>struts.xml</code> files.
 *
 * @author Yann C&eacute;bron
 */
public interface StrutsModel extends DomModel<StrutsRoot> {

  StrutsModel[] EMPTY_ARRAY = new StrutsModel[0];

  /**
   * Get all {@link StrutsRoot} elements of the files belonging to this model.
   *
   * @return List.
   */
  @NotNull
  List<StrutsRoot> getMergedStrutsRoots();

  /**
   * Get all {@link StrutsPackage} elements of this model.
   *
   * @return List.
   */
  @NotNull
  List<StrutsPackage> getStrutsPackages();

  /**
   * Gets all Actions for the given name and optionally namespace.
   *
   * @param name      Name of the action.
   * @param namespace Namespace to search within, <code>null</code> for any.
   *
   * @return List of all Actions.
   */
  @NotNull
  List<Action> findActionsByName(@NotNull @NonNls String name, @Nullable @NonNls String namespace);

  /**
   * Gets all Actions for the given class.
   *
   * @param clazz Class to search usages for.
   *
   * @return List of all Actions.
   */
  @NotNull
  List<Action> findActionsByClass(final PsiClass clazz);

  /**
   * Gets all available actions for the given namespace.
   *
   * @param namespace Namespace identifier.
   *
   * @return List of all Actions.
   */
  List<Action> getActionsForNamespace(@Nullable @NonNls final String namespace);
}