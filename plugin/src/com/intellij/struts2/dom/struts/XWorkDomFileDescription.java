package com.intellij.struts2.dom.struts;

import com.intellij.psi.xml.XmlTag;
import com.intellij.struts2.StrutsConstants;
import com.intellij.struts2.dom.StrutsDomConstants;
import com.intellij.util.NotNullFunction;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * <code>xwork.xml</code> DOM-Model files.
 *
 * @author Yann CŽbron
 */
public class XWorkDomFileDescription extends DomFileDescription<StrutsRoot> {

  private static final List<String> XWORK_NAMESPACES = Arrays.asList(StrutsConstants.XWORK_DTD_URI,
                                                                     StrutsConstants.XWORK_DTD_ID);

  public XWorkDomFileDescription() {
    super(StrutsRoot.class, StrutsRoot.XWORK_TAG_NAME);
  }

  protected void initializeFileDescription() {
    registerNamespacePolicy(StrutsDomConstants.STRUTS_NAMESPACE_KEY, new NotNullFunction<XmlTag, List<String>>() {
      @NotNull
      public List<String> fun(final XmlTag tag) {
        return XWORK_NAMESPACES;
      }
    });
  }

}