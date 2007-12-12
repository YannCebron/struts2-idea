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

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.Nullable;

/**
 * Converter for &lt;include&gt; "file"-attribute (struts.xml files).
 *
 * @author Yann C�bron
 * @see com.intellij.struts2.dom.struts.Include#getFile()
 */
public abstract class IncludeFileResolvingConverter extends ResolvingConverter<XmlFile> {

  public String toString(@Nullable final XmlFile xmlFile, final ConvertContext context) {
    return xmlFile != null ? xmlFile.getName() : null;
  }

}