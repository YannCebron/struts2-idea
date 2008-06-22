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

package com.intellij.struts2.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.struts2.StrutsConstants;
import com.intellij.struts2.StrutsIcons;
import com.intellij.struts2.dom.struts.StrutsRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * Description-type for {@link StrutsFacet}.
 * Adds autodetection feature for struts.xml files found in project.
 *
 * @author Yann C&eacute;bron
 */
public class StrutsFacetType extends FacetType<StrutsFacet, StrutsFacetConfiguration> {

  public static final FacetType<StrutsFacet, StrutsFacetConfiguration> INSTANCE = new StrutsFacetType();

  private StrutsFacetType() {
    super(StrutsFacet.FACET_TYPE_ID, "Struts2", "Struts 2");
  }

  public StrutsFacetConfiguration createDefaultConfiguration() {
    return new StrutsFacetConfiguration();
  }

  public StrutsFacet createFacet(@NotNull final Module module,
                                 final String name,
                                 @NotNull final StrutsFacetConfiguration configuration,
                                 @Nullable final Facet underlyingFacet) {
    return new StrutsFacet(this, module, name, configuration, underlyingFacet);
  }

  public Icon getIcon() {
    return StrutsIcons.ACTION;
  }

  public void registerDetectors(final FacetDetectorRegistry<StrutsFacetConfiguration> facetDetectorRegistry) {
    final FacetDetectorRegistryEx<StrutsFacetConfiguration> registry =
        (FacetDetectorRegistryEx<StrutsFacetConfiguration>) facetDetectorRegistry;
    registry.registerUniversalDetectorByFileNameAndRootTag(StrutsConstants.STRUTS_DEFAULT_FILENAME, StrutsRoot.TAG_NAME,
        new StrutsFacetDetector(), null);
  }

  private static class StrutsFacetDetector extends FacetDetector<VirtualFile, StrutsFacetConfiguration> {

    public StrutsFacetConfiguration detectFacet(final VirtualFile source,
                                                final Collection<StrutsFacetConfiguration> existentFacetConfigurations) {
      final Iterator<StrutsFacetConfiguration> iterator = existentFacetConfigurations.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      }
      return new StrutsFacetConfiguration();
    }
  }

}