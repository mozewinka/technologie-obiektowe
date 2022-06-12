package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GenerateDiagramPngAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        ClassHelper classHelper = new ClassHelper(project);
        DiagramHelper diagramHelper = new DiagramHelper();

        try {
            diagramHelper.generatePng(classHelper.classesMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
