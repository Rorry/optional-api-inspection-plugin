package com.github.rorry;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.LambdaUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiMethodReferenceExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import com.intellij.refactoring.util.LambdaRefactoringUtil;
import com.intellij.util.ObjectUtils;
import com.siyeh.ig.callMatcher.CallMatcher;
import com.siyeh.ig.psiutils.CommentTracker;
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
    private static final CallMatcher OPTIONAL_GET =
            CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "get").parameterCount(0);
    private static final CallMatcher OPTIONAL_IS_PRESENT =
            CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "isPresent").parameterCount(0);

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
                if (!checkForMethodCallAndExactLambdaCall(expression, STREAM_MAP, OPTIONAL_GET)) return;

                final PsiMethodCallExpression psiMethodCallExpression = MethodCallUtils.getQualifierMethodCall(expression);
                if (psiMethodCallExpression == null) {
                    return;
                }

                /* .filter(Optional::isPresent)
                *  .map(Optional::get) */
                if (!checkForMethodCallAndExactLambdaCall(psiMethodCallExpression, STREAM_FILTER, OPTIONAL_IS_PRESENT))
                    return;

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

    private static boolean checkForMethodCallAndExactLambdaCall(PsiMethodCallExpression expression, CallMatcher streamCall, CallMatcher optionalCall) {
        if (!streamCall.test(expression)) {
            return false;
        }

        final PsiExpression psiOptExpression = expression.getArgumentList().getExpressions()[0];
        if (!(psiOptExpression instanceof PsiMethodReferenceExpression) &&
                !(psiOptExpression instanceof PsiLambdaExpression)) {
            return false;
        }

        final PsiLambdaExpression lambdaOpt = getLambda(psiOptExpression);
        if (lambdaOpt == null) {
            return false;
        }

        final PsiExpression lambdaOptBody = LambdaUtil.extractSingleExpressionFromBody(lambdaOpt.getBody());
        if (!(lambdaOptBody instanceof PsiMethodCallExpression)) {
            return false;
        }

        final PsiMethodCallExpression optBody = (PsiMethodCallExpression) lambdaOptBody;
        if (!optionalCall.test(optBody)) {
            return false;
        }

        return true;
    }

    @Nullable
    private static PsiLambdaExpression getLambda(PsiExpression initializer) {
        final PsiExpression expression = PsiUtil.skipParenthesizedExprDown(initializer);
        if (expression instanceof PsiLambdaExpression) {
            return (PsiLambdaExpression)expression;
        }
        if (expression instanceof PsiMethodReferenceExpression) {
            return LambdaRefactoringUtil.createLambda((PsiMethodReferenceExpression)expression, false);
        }

        return null;
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
