package com.github.rorry;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ObjectUtils;
import com.siyeh.ig.callMatcher.CallMatcher;
import com.siyeh.ig.psiutils.CommentTracker;
import com.siyeh.ig.psiutils.FunctionalExpressionUtils;
import com.siyeh.ig.psiutils.MethodCallUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OptionalStreamInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final CallMatcher STREAM_MAP =
            CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_STREAM_STREAM, "map").parameterCount(1);
    private static final CallMatcher STREAM_FILTER =
            CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_STREAM_STREAM, "filter").parameterCount(1);
    private static final String OPTIONAL_GET_METHOD = "get";
    private static final String OPTIONAL_IS_PRESENT_METHOD = "isPresent";
    private static final String DESCRIPTION = "Stream chain of optionals can be simplified";

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.LANGUAGE_LEVEL_SPECIFIC_GROUP_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return DESCRIPTION;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if (!PsiUtil.isLanguageLevel9OrHigher(holder.getFile())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);

                /* .map(Optional::get) */
                if (!STREAM_MAP.test(expression)) {
                    return;
                }
                final PsiExpression psiGetOptExpression = expression.getArgumentList().getExpressions()[0];
                if (!FunctionalExpressionUtils.isFunctionalReferenceTo(psiGetOptExpression,
                                CommonClassNames.JAVA_UTIL_OPTIONAL, null, OPTIONAL_GET_METHOD)) {
                    return;
                }
                final PsiMethodCallExpression psiMethodCallExpression = MethodCallUtils.getQualifierMethodCall(expression);
                if (psiMethodCallExpression == null) {
                    return;
                }

                /* .filter(Optional::isPresent)
                *  .map(Optional::get) */
                if (!STREAM_FILTER.test(psiMethodCallExpression)) {
                    return;
                }
                final PsiExpression psiIsPresentOptExpression = psiMethodCallExpression.getArgumentList().getExpressions()[0];
                if (!FunctionalExpressionUtils.isFunctionalReferenceTo(psiIsPresentOptExpression,
                        CommonClassNames.JAVA_UTIL_OPTIONAL, null, OPTIONAL_IS_PRESENT_METHOD)) {
                    return;
                }
                final PsiExpression beforeQualifierExpression = psiMethodCallExpression.getMethodExpression().getQualifierExpression();
                if (beforeQualifierExpression == null) {
                    return;
                }

                holder.registerProblem(expression, DESCRIPTION, new OptionalStreamFix(beforeQualifierExpression.getText()));
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

    private static class OptionalStreamFix implements LocalQuickFix {
        private final String beforeReplacePart;

        public OptionalStreamFix(String beforeReplacePart) {
            this.beforeReplacePart = beforeReplacePart;
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            return DESCRIPTION;
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return "Replace with flatMap.(Optional::stream)";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            final PsiElement element = descriptor.getStartElement();
            final PsiMethodCallExpression callExpression = ObjectUtils.tryCast(element, PsiMethodCallExpression.class);

            if (callExpression == null) {
                return;
            }

            final String replacementResultText = beforeReplacePart + ".flatMap(Optional::stream)";
            final CommentTracker ct = new CommentTracker();
            final PsiElement result = ct.replaceAndRestoreComments(callExpression, replacementResultText);
            CodeStyleManager.getInstance(project).reformat(result);
        }
    }
}
