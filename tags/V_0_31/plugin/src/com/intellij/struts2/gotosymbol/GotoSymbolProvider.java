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

package com.intellij.struts2.gotosymbol;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.struts2.facet.StrutsFacet;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ElementPresentation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for "Go To Symbol" contributors.
 *
 * @author Dmitry Avdeev
 */
abstract class GoToSymbolProvider implements ChooseByNameContributor {

  protected abstract void getNames(@NotNull Module module, Set<String> result);

  protected abstract void getItems(@NotNull Module module, String name, List<NavigationItem> result);

  public String[] getNames(final Project project, final boolean includeNonProjectItems) {
    final Set<String> result = new HashSet<String>();
    final Module[] modules = ModuleManager.getInstance(project).getModules();
    for (final Module module : modules) {
      if (StrutsFacet.getInstance(module) != null) {
        getNames(module, result);
      }
    }

    return result.toArray(new String[result.size()]);
  }

  public NavigationItem[] getItemsByName(final String name,
                                         final Project project,
                                         final boolean includeNonProjectItems) {
    final List<NavigationItem> result = new ArrayList<NavigationItem>();
    final Module[] modules = ModuleManager.getInstance(project).getModules();
    for (final Module module : modules) {
      if (StrutsFacet.getInstance(module) != null) {
        getItems(module, name, result);
      }
    }

    return result.toArray(new NavigationItem[result.size()]);
  }

  @Nullable
  protected static NavigationItem createNavigationItem(@NotNull final DomElement domElement,
                                                       @NotNull @NonNls final String location) {
    final PsiElement psiElement = domElement.getXmlTag();
    if (psiElement == null) {
      return null;
    }

    final ElementPresentation presentation = domElement.getPresentation();
    return new BaseNavigationItem(psiElement, presentation.getElementName(), location, presentation.getIcon());
  }


  /**
   * Wraps one entry to display in "Go To Symbol" dialog.
   */
  private static class BaseNavigationItem extends ASTWrapperPsiElement {

    private final PsiElement psiElement;
    private final String text;
    private final String location;
    private final Icon icon;

    /**
     * Creates a new display item.
     *
     * @param psiElement The PsiElement to navigate to.
     * @param text       Text to show for this element.
     * @param location   Additional location information.
     * @param icon       Icon to show for this element.
     */
    private BaseNavigationItem(@NotNull final PsiElement psiElement,
                               @NotNull @NonNls final String text,
                               @NotNull @NonNls final String location,
                               @Nullable final Icon icon) {
      super(psiElement.getNode());
      this.psiElement = psiElement;
      this.text = text;
      this.location = location;
      this.icon = icon;
    }

    public Icon getIcon(final int flags) {
      return icon;
    }

    public ItemPresentation getPresentation() {
      return new ItemPresentation() {

        public String getPresentableText() {
          return text;
        }

        @Nullable
        public String getLocationString() {
          return location + " (" + psiElement.getContainingFile().getName() + ')';
        }

        @Nullable
        public Icon getIcon(final boolean open) {
          return icon;
        }

        @Nullable
        public TextAttributesKey getTextAttributesKey() {
          return null;
        }
      };
    }
  }

}