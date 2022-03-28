package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DemoAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<String> classes = new ArrayList<>();
        PsiShortNamesCache.getInstance(Objects.requireNonNull(e.getProject())).processAllClassNames(name -> {
            classes.add(name);
            return true;
        }, new ProjectOnlySearchScope(e.getProject()),
                IdFilter.getProjectIdFilter(e.getProject(), false));
        Messages.showMessageDialog(e.getProject(), String.valueOf(classes),
                "Lista Klas Projektu", Messages.getInformationIcon());
    }
}

class ProjectOnlySearchScope extends GlobalSearchScope {
    private final ProjectFileIndex index;

    public ProjectOnlySearchScope(Project project) {
        super(project);
        this.index = ProjectRootManager.getInstance(project).getFileIndex();
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return false;
    }

    @Override
    public boolean isSearchInLibraries() {
        return false;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return index.isInSourceContent(file);
    }
}