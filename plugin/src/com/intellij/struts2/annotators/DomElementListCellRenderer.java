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

package com.intellij.struts2.annotators;

import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Renderer for "GoTo"-gutter-popups.
 *
 * @author Dmitry Avdeev
 * @author Yann Cebron
 * @param <T> Navigation target DOM element type.
 */
abstract class DomElementListCellRenderer<T extends DomElement> extends PsiElementListCellRenderer<XmlTag> {

  private final String unknownElementText;

  protected DomElementListCellRenderer(@Nls final String unknownElementText) {
    this.unknownElementText = unknownElementText;
  }

  /**
   * Gets an additional location string.
   *
   * @param domElement Target navigation element.
   *
   * @return Empty String if not suitable.
   */
  @NotNull
  @NonNls
  public abstract String getAdditionalLocation(final T domElement);

  public String getElementText(final XmlTag element) {
    final DomElement domElement = getDomElement(element);
    if (domElement == null) {
      return element.getName();
    }

    final String elementName = domElement.getPresentation().getElementName();
    return elementName == null ? unknownElementText : elementName;
  }

  protected String getContainerText(final PsiElement element, final String name) {
    return getAdditionalLocation(getDomElement((XmlTag) element)) +
           " (" + element.getContainingFile().getName() + ')';
  }

  protected int getIconFlags() {
    return 0;
  }

  protected Icon getIcon(final PsiElement element) {
    final DomElement domElement = getDomElement((XmlTag) element);
    if (domElement != null) {
      final Icon icon = domElement.getPresentation().getIcon();
      if (icon != null) {
        return icon;
      }
    }

    return super.getIcon(element);
  }

  @Nullable
  protected T getDomElement(@NotNull final XmlTag tag) {
    return (T) DomManager.getDomManager(tag.getProject()).getDomElement(tag);
  }

}