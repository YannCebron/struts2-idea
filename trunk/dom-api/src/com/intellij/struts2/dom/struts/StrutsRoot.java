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

import com.intellij.struts2.dom.StrutsDomConstants;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <code>struts</code> root-element for struts.xml-files.
 *
 * @author Yann C�bron
 */
@Namespace(StrutsDomConstants.STRUTS_NAMESPACE_KEY)
public interface StrutsRoot extends DomElement {

  @NonNls
  String TAG_NAME = "struts";

  @NonNls
  String XWORK_TAG_NAME = "xwork";

  @SubTagList(value = "package")
  @NotNull
  List<StrutsPackage> getPackages();

  @SubTagList(value ="include")
  List<Include> getIncludes();

  @SubTagList(value = "bean")
  @NotNull
  List<Bean> getBeans();

  @SubTagList(value = "constant")
  @NotNull
  List<Constant> getConstants();

}