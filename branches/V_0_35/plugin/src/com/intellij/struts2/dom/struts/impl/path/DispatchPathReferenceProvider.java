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

package com.intellij.struts2.dom.struts.impl.path;

import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.WebUtil;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.paths.PathReference;
import com.intellij.openapi.paths.PathReferenceProvider;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.jsp.WebDirectoryUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.jsp.WebDirectoryElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides paths to static webresources.
 *
 * @author Yann C�bron
 */
class DispatchPathReferenceProvider implements PathReferenceProvider {

  public boolean createReferences(@NotNull final PsiElement psiElement,
                                  @NotNull final List<PsiReference> references,
                                  final boolean soft) {
    final FileReferenceSet set = FileReferenceSet.createSet(psiElement, soft, false, true);
    if (set == null) {
      return true;
    }

    // get parent <package> element
    final DomElement domElement = DomManager.getDomManager(psiElement.getProject())
        .getDomElement((XmlTag) psiElement);
    if (domElement == null) {
      return true; // XML syntax error
    }

    final StrutsPackage strutsPackage = domElement.getParentOfType(StrutsPackage.class, true);
    if (strutsPackage == null) {
      return true; // XML syntax error
    }

    final String packageNamespace = strutsPackage.searchNamespace();

    final WebFacet webFacet = WebUtil.getWebFacet(psiElement);
    if (webFacet == null) {
      return true; // setup error, web-facet must be present in current or dependent module
    }

    final WebDirectoryUtil directoryUtil = WebDirectoryUtil.getWebDirectoryUtil(psiElement.getProject());

    set.addCustomization(
        FileReferenceSet.DEFAULT_PATH_EVALUATOR_OPTION,
        new Function<PsiFile, Collection<PsiFileSystemItem>>() {
          public Collection<PsiFileSystemItem> fun(final PsiFile file) {
            final List<PsiFileSystemItem> basePathRoots = new ArrayList<PsiFileSystemItem>();

            // 1. add all configured web root mappings
            final List<WebRoot> webRoots = webFacet.getWebRoots(true);
            for (final WebRoot webRoot : webRoots) {
              final String webRootPath = webRoot.getRelativePath();
              final WebDirectoryElement webRootBase = directoryUtil.findWebDirectoryElementByPath(
                  webRootPath,
                  webFacet);
              ContainerUtil.addIfNotNull(webRootBase, basePathRoots);
            }

            // 2. add parent <package> "namespace" as result prefix directory path if not ROOT
            if (!packageNamespace.equals("/")) {
              final WebDirectoryElement packageBase = directoryUtil.findWebDirectoryElementByPath(
                  packageNamespace,
                  webFacet);
              ContainerUtil.addIfNotNull(packageBase, basePathRoots);
            }

            return basePathRoots;
          }
        });
    Collections.addAll(references, set.getAllReferences());
    return false;
  }

  @Nullable
  public PathReference getPathReference(@NotNull final String path, @NotNull final PsiElement element) {
    final WebFacet webFacet = WebUtil.getWebFacet(element);
    if (webFacet == null) {
      return null;
    }

    final WebDirectoryUtil webDirectoryUtil = WebDirectoryUtil.getWebDirectoryUtil(element.getProject());
    final PsiElement psiElement = webDirectoryUtil.findFileByPath(path, webFacet);
    final Function<PathReference, Icon> iconFunction = new Function<PathReference, Icon>() {
      public Icon fun(final PathReference webPath) {
        return psiElement.getIcon(Iconable.ICON_FLAG_READ_STATUS);
      }
    };

    return psiElement != null ? new PathReference(path, iconFunction) {
      public PsiElement resolve() {
        return webDirectoryUtil.findFileByPath(path, webFacet);
      }
    } : null;
  }

}