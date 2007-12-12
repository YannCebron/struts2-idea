package com.intellij.struts2.structure;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Function;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementsNavigationManager;
import com.intellij.util.xml.DomService;
import com.intellij.util.xml.highlighting.DomElementAnnotationsManager;
import com.intellij.util.xml.highlighting.DomElementProblemDescriptor;
import com.intellij.util.xml.highlighting.DomElementsProblemsHolder;
import com.intellij.util.xml.structure.DomStructureTreeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one node for the structure view.
 *
 * @author Yann Cebron
 */
public class StructureViewTreeElement extends DomStructureTreeElement {

  private static final Function<DomElement, DomService.StructureViewMode> MY_STRUCTURE_VIEW_MODE_FUNCTION =
      new Function<DomElement, DomService.StructureViewMode>() {
        public DomService.StructureViewMode fun(final DomElement domElement) {
          return DomService.StructureViewMode.SHOW;
        }
      };

  public StructureViewTreeElement(@NotNull final DomElement domElement) {
    super(domElement,
        MY_STRUCTURE_VIEW_MODE_FUNCTION,
        DomElementsNavigationManager.getManager(domElement.getRoot().getFile().getProject()).
            getDomElementsNavigateProvider(DomElementsNavigationManager.DEFAULT_PROVIDER_NAME));
  }

  /**
   * Highlight invalid elements with red underwave.
   *
   * @return null if no errors.
   */
  @Nullable
  public TextAttributesKey getTextAttributesKey() {
    final DomElement element = getElement();
    final XmlTag tag = element.getXmlTag();
    if (tag == null) {
      return null;
    }

    final DomElementsProblemsHolder holder = DomElementAnnotationsManager.getInstance(tag.getProject())
        .getCachedProblemHolder(element);

    final List<DomElementProblemDescriptor> problems = holder.getProblems(element, true, HighlightSeverity.ERROR);
    if (!problems.isEmpty()) {
      return CodeInsightColors.ERRORS_ATTRIBUTES;
    }

    return null;
  }

  public TreeElement[] getChildren() {
    final TreeElement[] elements = super.getChildren();
    final List<StructureViewTreeElement> myList = new ArrayList<StructureViewTreeElement>(elements.length);
    for (final TreeElement treeElement : elements) {
      myList.add(new StructureViewTreeElement(((DomStructureTreeElement) treeElement).getElement()));
    }
    return myList.toArray(new StructureViewTreeElement[myList.size()]);
  }

  /**
   * Add some extra text behind element presentation.
   *
   * @return null if no extra text is provided for the current element.
   */
  @Nullable
  public String getLocationString() {
    final DomElement element = getElement();

    if (element instanceof LocationPresentation) {
      return ((LocationPresentation) element).getLocation();
    }


/*
    if (element instanceof StrutsPackage) {
      return ((StrutsPackage) element).getNamespace().getStringValue();
    }

    if (element instanceof Param) {
      return ((Param) element).getValue();
    }

    if (element instanceof Result) {
      return ((Result) element).getPath();
    }

    if (element instanceof GlobalResult) {
      return ((GlobalResult) element).getPath();
    }

    if (element instanceof ExceptionMapping) {
      final Result result = ((ExceptionMapping) element).getResult().getValue();
      return result != null ? result.getName().getStringValue() : null;
    }

    if (element instanceof GlobalExceptionMapping) {
      final GlobalResult result = ((GlobalExceptionMapping) element).getResult().getValue();
      return result != null ? result.getName().getStringValue() : null;
    }
*/

    return super.getLocationString();
  }

}