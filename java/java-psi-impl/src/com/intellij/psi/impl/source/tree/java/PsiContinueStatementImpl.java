/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
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
package com.intellij.psi.impl.source.tree.java;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiLabelReference;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.Constants;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ChildRoleBase;
import org.jetbrains.annotations.NotNull;

public class PsiContinueStatementImpl extends CompositePsiElement implements PsiContinueStatement, Constants {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.impl.source.tree.java.PsiContinueStatementImpl");

  public PsiContinueStatementImpl() {
    super(CONTINUE_STATEMENT);
  }

  @Override
  public PsiIdentifier getLabelIdentifier() {
    return (PsiIdentifier)findChildByRoleAsPsiElement(ChildRole.LABEL);
  }

  @Override
  public PsiStatement findContinuedStatement() {
    PsiIdentifier label = getLabelIdentifier();
    if (label == null){
      for(ASTNode parent = getTreeParent(); parent != null; parent = parent.getTreeParent()){
        IElementType i = parent.getElementType();
        if (i == FOR_STATEMENT || i == FOREACH_STATEMENT || i == WHILE_STATEMENT || i == DO_WHILE_STATEMENT) {
          return (PsiStatement)SourceTreeToPsiMap.treeElementToPsi(parent);
        }
        if (i == METHOD || i == CLASS_INITIALIZER) {
          return null;
        }
      }
    }
    else{
      String labelName = label.getText();
      for(CompositeElement parent = getTreeParent(); parent != null; parent = parent.getTreeParent()){
        if (parent.getElementType() == LABELED_STATEMENT){
          TreeElement statementLabel = (TreeElement)parent.findChildByRole(ChildRole.LABEL_NAME);
          if (statementLabel.textMatches(labelName)){
            return ((PsiLabeledStatement)SourceTreeToPsiMap.treeElementToPsi(parent)).getStatement();
          }
        }
        if (parent.getElementType() == METHOD || parent.getElementType() == CLASS_INITIALIZER || parent.getElementType() == LAMBDA_EXPRESSION) return null; // do not pass through anonymous/local class
      }
    }
    return null;
  }

  @Override
  public ASTNode findChildByRole(int role) {
    LOG.assertTrue(ChildRole.isUnique(role));
    switch(role){
      default:
        return null;

      case ChildRole.CONTINUE_KEYWORD:
        return findChildByType(CONTINUE_KEYWORD);

      case ChildRole.LABEL:
        return findChildByType(IDENTIFIER);

      case ChildRole.CLOSING_SEMICOLON:
        return TreeUtil.findChildBackward(this, SEMICOLON);
    }
  }

  @Override
  public int getChildRole(@NotNull ASTNode child) {
    LOG.assertTrue(child.getTreeParent() == this);
    IElementType i = child.getElementType();
    if (i == CONTINUE_KEYWORD) {
      return ChildRole.CONTINUE_KEYWORD;
    }
    else if (i == IDENTIFIER) {
      return ChildRole.LABEL;
    }
    else if (i == SEMICOLON) {
      return ChildRole.CLOSING_SEMICOLON;
    }
    else {
      return ChildRoleBase.NONE;
    }
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JavaElementVisitor) {
      ((JavaElementVisitor)visitor).visitContinueStatement(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  public String toString() {
    return "PsiContinueStatement";
  }

  @Override
  public PsiReference getReference() {
    final PsiReference[] references = getReferences();
    if (references != null && references.length > 0)
      return references[0];
    return null;
  }

  @Override
  @NotNull
  public PsiReference[] getReferences() {
    if (getLabelIdentifier() == null)
      return PsiReference.EMPTY_ARRAY;
    return new PsiReference[]{new PsiLabelReference(this, getLabelIdentifier())};
  }
}
