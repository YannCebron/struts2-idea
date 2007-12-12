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

import com.intellij.util.xml.NamedEnum;

/**
 * <code>bean</code> "scope" possible values.
 *
 * @author Yann C�bron
 */
public enum BeanScope implements NamedEnum {

  _default("default"),
  singleton("singleton"),
  request("request"),
  session("session"),
  thread("thread");

  private final String name;

  BeanScope(final String name) {
    this.name = name;
  }

  public String getValue() {
    return name;
  }

}