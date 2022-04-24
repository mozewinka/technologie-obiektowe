package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchScopeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClassListAction extends AnAction {
    private ProjectOnlySearchScope searchScope;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        searchScope = new ProjectOnlySearchScope(project);

        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile[] directories = projectRootManager.getContentSourceRoots();
        List<PsiClass> psiClasses = getClasses(directories, psiManager);
        PsiClass[] classes = psiClasses.toArray(new PsiClass[0]);

        ClassListDialog dialog = new ClassListDialog(classes);
        dialog.pack();
        dialog.setVisible(true);
    }

    private List<PsiClass> getClasses(VirtualFile[] directories, PsiManager psiManager) {
        List<PsiClass> result = new ArrayList<>();
        for (VirtualFile d : directories) {
            PsiDirectory psiDirectory = psiManager.findDirectory(d);
            if (psiDirectory != null)
                addClasses(psiDirectory, result);
        }
        return result;
    }

    private void addClasses (PsiDirectory psiDirectory, List<PsiClass> classes) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        if (psiPackage != null) {
            PsiClass[] packageClasses = psiPackage.getClasses();
            for (PsiClass psiClass : packageClasses) {
                if (PsiSearchScopeUtil.isInScope(searchScope, psiClass))
                    classes.add(psiClass);
            }
            PsiDirectory[] psiSubDirectories = psiDirectory.getSubdirectories();
            for (PsiDirectory sub : psiSubDirectories)
                addClasses(sub, classes);
        }
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
