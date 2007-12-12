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

package com.intellij.struts2.facet.ui;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.struts2.StrutsIcons;
import com.intellij.struts2.dom.struts.model.StrutsManager;
import com.intellij.struts2.facet.StrutsFacetConfiguration;
import com.intellij.struts2.facet.configuration.StrutsFileSet;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.*;
import com.intellij.util.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Struts2 facet tab "File Sets".
 *
 * @author Yann C�bron
 */
public class FileSetConfigurationTab extends FacetEditorTab {

  // GUI components -----------------------
  private JPanel myPanel;
  private SimpleTree myTree;
  private JButton myAddSetButton;
  private JButton myRemoveButton;
  private JButton myEditButton;

  // GUI helpers
  private final SimpleTreeBuilder myBuilder;
  private final SimpleNode myRootNode = new SimpleNode() {
    public SimpleNode[] getChildren() {
      // TODO shorten paths for display
//      final VirtualFile baseDir = facetEditorContext.getProject().getBaseDir();
//      final String s = baseDir.getPresentableUrl();
//      System.out.println("baseDir.getPath() = " + s);

      final List<SimpleNode> nodes = new ArrayList<SimpleNode>(myBuffer.size());
      for (final StrutsFileSet entry : myBuffer) {
        if (!entry.isRemoved()) {
          final FileSetNode setNode = new FileSetNode(entry);
          nodes.add(setNode);
        }
      }
      return nodes.toArray(new SimpleNode[nodes.size()]);
    }

    public boolean isAutoExpandNode() {
      return true;
    }

  };

  private final StrutsConfigsSearcher myConfigsSearcher;

  // original config
  private final StrutsFacetConfiguration originalConfiguration;
  private final FacetEditorContext facetEditorContext;

  // local config
  private final Set<StrutsFileSet> myBuffer = new LinkedHashSet<StrutsFileSet>();
  private boolean myModified;

  public FileSetConfigurationTab(@NotNull final StrutsFacetConfiguration strutsFacetConfiguration,
                                 @NotNull final FacetEditorContext facetEditorContext) {
    originalConfiguration = strutsFacetConfiguration;
    this.facetEditorContext = facetEditorContext;
    this.myConfigsSearcher = new StrutsConfigsSearcher(facetEditorContext);

    // init tree
    final SimpleTreeStructure structure = new SimpleTreeStructure() {
      public Object getRootElement() {
        return myRootNode;
      }
    };
    myTree.setRootVisible(false);
    myBuilder = new SimpleTreeBuilder(myTree, (DefaultTreeModel) myTree.getModel(), structure, null);
    myBuilder.initRoot();

    myTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(final TreeSelectionEvent e) {
        final StrutsFileSet fileSet = getCurrentFileSet();
        myEditButton.setEnabled(fileSet != null);
        myRemoveButton.setEnabled(fileSet != null);
      }
    });

    myAddSetButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        final StrutsFileSet fileSet = new StrutsFileSet(StrutsFileSet.getUniqueId(myBuffer),
                                                        StrutsFileSet.getUniqueName("My Fileset", myBuffer)) {
          public boolean isNew() {
            return true;
          }
        };

        final FileSetEditor editor = new FileSetEditor(myPanel,
                                                       fileSet,
                                                       myBuffer,
                                                       facetEditorContext,
                                                       myConfigsSearcher);
        editor.show();
        if (editor.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
          myBuffer.add(editor.getEditedFileSet());
          myModified = true;
          myBuilder.updateFromRoot();
          selectFileSet(fileSet);
        }
        myTree.requestFocus();
      }
    });

    myEditButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        final StrutsFileSet fileSet = getCurrentFileSet();
        if (fileSet != null) {
          final FileSetEditor editor = new FileSetEditor(myPanel,
                                                         fileSet,
                                                         myBuffer,
                                                         facetEditorContext,
                                                         myConfigsSearcher);
          editor.show();
          if (editor.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            myModified = true;
            myBuffer.remove(fileSet);
            final StrutsFileSet edited = editor.getEditedFileSet();
            myBuffer.add(edited);
            edited.setAutodetected(false);
            myBuilder.updateFromRoot();
            selectFileSet(edited);
          }
          myTree.requestFocus();
        }
      }
    });

    myRemoveButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        remove();
        myModified = true;
        myBuilder.updateFromRoot();
        myTree.requestFocus();
      }
    });

  }

  @Nullable
  private StrutsFileSet getCurrentFileSet() {
    final FileSetNode currentFileSetNode = getCurrentFileSetNode();
    return currentFileSetNode == null ? null : currentFileSetNode.mySet;
  }

  @Nullable
  private FileSetNode getCurrentFileSetNode() {
    final SimpleNode selectedNode = myTree.getSelectedNode();
    if (selectedNode == null) {
      return null;
    }
    if (selectedNode instanceof FileSetNode) {
      return (FileSetNode) selectedNode;
    } else if (selectedNode.getParent() instanceof FileSetNode) {
      return (FileSetNode) selectedNode.getParent();
    } else {
      final SimpleNode parent = selectedNode.getParent();
      if (parent != null && parent.getParent() instanceof FileSetNode) {
        return (FileSetNode) selectedNode.getParent().getParent();
      }
    }
    return null;
  }

  private void selectFileSet(final StrutsFileSet fileSet) {
    myTree.select(myBuilder, new SimpleNodeVisitor() {
      public boolean accept(final SimpleNode simpleNode) {
        if (simpleNode instanceof FileSetNode) {
          if (((FileSetNode) simpleNode).mySet.equals(fileSet)) {
            return true;
          }
        }
        return false;
      }
    }, false);
  }

  void remove() {
    final SimpleNode[] nodes = myTree.getSelectedNodesIfUniform();
    for (final SimpleNode node : nodes) {

      if (node instanceof FileSetNode) {
        final StrutsFileSet fileSet = ((FileSetNode) node).mySet;
        final int result = Messages.showYesNoDialog(myPanel,
                                                    "Remove File Set '" + fileSet.getName() + "' ?",
                                                    "Confirm removal",
                                                    Messages.getQuestionIcon());
        if (result == DialogWrapper.OK_EXIT_CODE) {
          if (fileSet.isAutodetected()) {
            fileSet.setRemoved(true);
            myBuffer.add(fileSet);
          } else {
            myBuffer.remove(fileSet);
          }
        }
      } else if (node instanceof ConfigFileNode) {
        final VirtualFilePointer filePointer = ((ConfigFileNode) node).myFilePointer;
        final StrutsFileSet fileSet = ((FileSetNode) node.getParent()).mySet;
        fileSet.removeFile(filePointer);
      }
    }

  }

  @Nullable
  public Icon getIcon() {
    return Icons.PACKAGE_ICON;
  }

  @Nls
  public String getDisplayName() {
    return "File Sets";
  }

  public JComponent createComponent() {
    return myPanel;
  }

  public boolean isModified() {
    return myModified;
  }

  public void apply() throws ConfigurationException {
    final Set<StrutsFileSet> fileSets = originalConfiguration.getFileSets();
    fileSets.clear();
    for (final StrutsFileSet fileSet : myBuffer) {
      if (!fileSet.isAutodetected() || fileSet.isRemoved()) {
        fileSets.add(fileSet);
      }
    }
    originalConfiguration.setModified();
  }

  public void reset() {
    myBuffer.clear();
    final Module module = facetEditorContext.getModule();
    if (module != null) {
      final Set<StrutsFileSet> sets = StrutsManager.getInstance(module.getProject()).getAllConfigFileSets(module);
      for (final StrutsFileSet fileSet : sets) {
        myBuffer.add(new StrutsFileSet(fileSet));
      }
    } else {
      final Set<StrutsFileSet> list = originalConfiguration.getFileSets();
      for (final StrutsFileSet fileSet : list) {
        myBuffer.add(new StrutsFileSet(fileSet));
      }
    }

    myBuilder.updateFromRoot();
    myTree.setSelectionRow(0);
  }

  public void disposeUIResources() {
    Disposer.dispose(myBuilder);
  }


  private class FileSetNode extends SimpleNode {

    protected final StrutsFileSet mySet;

    FileSetNode(final StrutsFileSet fileSet) {
      mySet = fileSet;
      final String name = mySet.getName();
      if (fileSet.getFiles().isEmpty()) {
        addErrorText(name, "No files attached");
      } else {
        addPlainText(name);
      }

      if (fileSet.isAutodetected()) {
        addColoredFragment(" [autodetected]", SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
      }
      setIcons(fileSet.getIcon(), Icons.PACKAGE_OPEN_ICON);
    }

    public SimpleNode[] getChildren() {
      final List<SimpleNode> nodes = new ArrayList<SimpleNode>();

      for (final VirtualFilePointer file : mySet.getFiles()) {
        nodes.add(new ConfigFileNode(file, this));
      }
      return nodes.toArray(new SimpleNode[nodes.size()]);
    }

    public boolean isAutoExpandNode() {
      return true;
    }

    public Object[] getEqualityObjects() {
      return new Object[]{mySet, mySet.getName(), mySet.getFiles()};
    }
  }


  private static final class ConfigFileNode extends SimpleNode {
    private final VirtualFilePointer myFilePointer;

    ConfigFileNode(final VirtualFilePointer name, final SimpleNode parent) {
      super(parent);
      myFilePointer = name;
      setIcons(StrutsIcons.STRUTS_CONFIG_FILE_ICON, StrutsIcons.STRUTS_CONFIG_FILE_ICON);
    }

    protected void doUpdate() {
      final VirtualFile file = myFilePointer.getFile();
      if (file != null) {
        renderFile(SimpleTextAttributes.REGULAR_ATTRIBUTES,
                   SimpleTextAttributes.GRAYED_ATTRIBUTES,
                   null);
      } else {
        renderFile(SimpleTextAttributes.ERROR_ATTRIBUTES,
                   SimpleTextAttributes.ERROR_ATTRIBUTES,
                   "File not found!");
      }
    }

    private void renderFile(final SimpleTextAttributes main,
                            final SimpleTextAttributes full,
                            @Nullable final String toolTip) {
      addColoredFragment(myFilePointer.getFileName(), toolTip, main);

      final VirtualFile file = myFilePointer.getFile();
      if (file != null) {
        addColoredFragment(" (" + file.getPath() + ")", toolTip, full);
      }
    }

    public SimpleNode[] getChildren() {
      return NO_CHILDREN;
    }

  }

}
