package com.github.rorry;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.LightProjectDescriptor;
import com.siyeh.ig.LightInspectionTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CollectionOfOptionalsInspectionTest extends LightInspectionTestCase {

    public void testCollectionOfOptionals() {
        doTest();
    }

    @Nullable
    @Override
    protected InspectionProfileEntry getInspection() {
        return new CollectionOfOptionalsInspection();
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_8;
    }

    @Override
    protected String getTestDataPath() {
        return new File("src/test/resources").getAbsolutePath();
    }
}