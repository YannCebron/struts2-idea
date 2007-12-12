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

import com.intellij.util.xml.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exclude the current element from completion/resolving variants. The element must have a {@link NameValue} annotation.
 *
 * @author Yann CŽbron
 */
public abstract class FilterCurrentElementInVariantsResolvingConverter<T extends DomElement> extends ResolvingConverter<T> {

  protected Collection<? extends T> filterVariants(final ConvertContext context,
                                                   final List<? extends T> allVariants) {
    final DomElement invocationElement = DomUtil.getDomElement(context.getTag());

    //noinspection unchecked
    final T currentElement = (T) invocationElement;
    assert currentElement != null : "currentElement was null for " + invocationElement;
    final GenericDomValue currentNameElement = currentElement.getGenericInfo().getNameDomElement(currentElement);
    assert currentNameElement != null : "currentNameElement was null for " + invocationElement;
    final String currentName = currentNameElement.getStringValue();
    assert currentName != null : "currentName was null for " + invocationElement;

    final Map<String, T> toFilter = new HashMap<String, T>(allVariants.size() - 1);
    for (final T variant : allVariants) {
      final GenericDomValue nameElement = variant.getGenericInfo().getNameDomElement(variant);
      assert nameElement != null : "NameDomElement was null for " + variant;
      final String variantName = nameElement.getStringValue();
      if (!currentName.equals(variantName)) {
        toFilter.put(variantName, variant);
      }
    }
    return toFilter.values();
  }

}
