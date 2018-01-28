package com.github.rorry;

import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class CollectionOfOptionalsInspectionProvider implements InspectionToolProvider {
    @NotNull
    @Override
    public Class[] getInspectionClasses() {
        return new Class[] {CollectionOfOptionalsInspection.class};
    }
}
