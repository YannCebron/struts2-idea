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

package com.intellij.struts2.dom.struts;

import com.intellij.lang.properties.PropertiesUtil;
import com.intellij.lang.properties.psi.Property;
import com.intellij.lang.properties.psi.ResourceBundleManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * TODO use referenceprovider!
 *
 * @author Yann CŽbron
 */
public class PropertyKeyResolveConverter extends ResolvingConverter<Property> {
  @NotNull
  public Collection<? extends Property> getVariants(final ConvertContext context) {
    try {
      final List<String> stringList = ResourceBundleManager.getManager(context.getFile()).suggestPropertiesFiles();
      for (String s : stringList) {
        System.out.println("s = " + s);
      }
    } catch (ResourceBundleManager.ResourceBundleNotFoundException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return Collections.emptyList();
  }

  public Property fromString(@Nullable @NonNls final String s, final ConvertContext context) {
    if (StringUtil.isEmpty(s)) {
      return null;
    }

    System.out.println("PropertyKeyResolveConverter.fromString " + s);
    final List<Property> list = PropertiesUtil.findPropertiesByKey(context.getPsiManager().getProject(), s);
//    System.out.println("list.size() = " + list.size());
    /*  for (Property property : list) {
      System.out.println("property = " + property);
      System.out.println("property.getContainingFile() = " + property.getContainingFile());
    }*/
    if (list.size() < 1) {
      System.out.println("list <1");
      return null;
    }

    final Property property = list.get(0);
    System.out.println("returning property = " + property);
    return property;
  }

  public String toString(@Nullable final Property property, final ConvertContext context) {
    if (property == null) {
      return null;
    }

    System.out.println("PropertyKeyResolveConverter.toString " + property);
    return property.getKey();
  }

}