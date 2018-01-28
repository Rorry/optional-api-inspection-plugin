package com.github.rorry;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.psiutils.CollectionUtils;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CollectionOfOptionalsInspection extends AbstractBaseJavaLocalInspectionTool {
    @NonNls
    private static final String DESCRIPTION_TEMPLATE =
            InspectionsBundle.message("group.names.potentially.confusing.code.constructs");

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.CONFUSING_GROUP_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "It doesn't recommend to use collection of optionals";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "CollectionOfOptionals";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if (!PsiUtil.isLanguageLevel8OrHigher(holder.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }
        return new JavaElementVisitor() {
            @Override
            public void visitVariable(PsiVariable variable) {
                super.visitVariable(variable);
                final PsiTypeElement variableTypeElement = variable.getTypeElement();
                if (variableTypeElement == null) {
                    return;
                }
                final PsiType variableType = variable.getType();
                if (!CollectionUtils.isCollectionClassOrInterface(variableType)) {
                    return;
                }
                final PsiType[] parameters = ((PsiClassReferenceType) variableType).getParameters();
                if (parameters.length != 1) {
                    return;
                }
                final PsiType parameterType = parameters[0];
                if (!TypeUtils.isOptional(parameterType)) {
                    return;
                }
                holder.registerProblem(variableTypeElement, DESCRIPTION_TEMPLATE);
            }
        };
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        return new JPanel(new FlowLayout(FlowLayout.LEFT));
    }
}
