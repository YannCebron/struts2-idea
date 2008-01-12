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

package com.intellij.struts2;

import org.jetbrains.annotations.NonNls;

/**
 * @author Yann C�bron
 */
public final class StrutsConstants {

  private StrutsConstants() {
  }

  /**
   * struts.xml URI.
   */
  @NonNls
  public static final String STRUTS_2_0_DTD_URI = "http://struts.apache.org/dtds/struts-2.0.dtd";

  /**
   * struts.xml ID.
   */
  @NonNls
  public static final String STRUTS_2_0_DTD_ID = "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN";

  public static final String XWORK_DTD_URI = "http://www.opensymphony.com/xwork/xwork-2.0.dtd";

  public static final String XWORK_DTD_ID = "-//OpenSymphony Group//XWork 2.0//EN";


  @NonNls
  public static final String STRUTS_DEFAULT_FILENAME = "struts.xml";

  @NonNls
  public static final String VALIDATOR_1_00_DTD_URI = "http://www.opensymphony.com/xwork/xwork-validator-1.0.dtd";
  @NonNls
  public static final String VALIDATOR_1_00_DTD_ID = "-//OpenSymphony Group//XWork Validator 1.0//EN";

  @NonNls
  public static final String VALIDATOR_1_02_DTD_URI = "http://www.opensymphony.com/xwork/xwork-validator-1.0.2.dtd";
  @NonNls
  public static final String VALIDATOR_1_02_DTD_ID = "-//OpenSymphony Group//XWork Validator 1.0.2//EN";

  @NonNls
  public static final String VALIDATOR_CONFIG_DTD_URI = "http://www.opensymphony.com/xwork/xwork-validator-config-1.0.dtd";
  @NonNls
  public static final String VALIDATOR_CONFIG_DTD_ID = "-//OpenSymphony Group//XWork Validator Config 1.0//EN";

}