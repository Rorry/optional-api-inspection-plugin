package com.github.rorry;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CollectionOfOptionalsInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final String DESCRIPTION = "Collection contains 'Optional'";

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.STYLE_GROUP_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return DESCRIPTION;
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
            public void visitTypeElement(PsiTypeElement typeElement) {
                super.visitTypeElement(typeElement);
                final PsiType type = typeElement.getType();
                final PsiClass resolvedClass = PsiUtil.resolveClassInClassTypeOnly(type);
                if (resolvedClass == null || !InheritanceUtil.isInheritor(resolvedClass, CommonClassNames.JAVA_UTIL_COLLECTION)) {
                    return;
                }

                final PsiType parameterType = PsiUtil.extractIterableTypeParameter(type, false);
                if (parameterType == null || !TypeUtils.isOptional(parameterType)) {
                    return;
                }

                holder.registerProblem(typeElement, DESCRIPTION);
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
