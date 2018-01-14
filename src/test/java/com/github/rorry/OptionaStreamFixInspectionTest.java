package com.github.rorry;

import com.intellij.codeInsight.daemon.quickFix.LightQuickFixParameterizedTestCase;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.testFramework.IdeaTestUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OptionaStreamFixInspectionTest extends LightQuickFixParameterizedTestCase {
    @Override
    protected Sdk getProjectJDK() {
        return IdeaTestUtil.getMockJdk9();
    }

    @NotNull
    @Override
    protected LocalInspectionTool[] configureLocalInspectionTools() {
        return new LocalInspectionTool[]{new OptionalStreamInspection()};
    }

    public void test() throws Exception {
        doAllTests();
    }

    @Override
    protected String getBasePath() {
        return "optionalStreams";
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return new File("src/test/resources").getAbsolutePath();
    }
}
