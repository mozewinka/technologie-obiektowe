package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ClassListAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        ClassListDialog dialog = new ClassListDialog(project);
        dialog.pack();
        dialog.setVisible(true);
    }
}