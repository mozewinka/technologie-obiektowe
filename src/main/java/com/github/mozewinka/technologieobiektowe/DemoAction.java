package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchScopeUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoAction extends AnAction {
    private ProjectOnlySearchScope searchScope;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        searchScope = new ProjectOnlySearchScope(project);
//        List<String> classes = new ArrayList<>();
//        PsiShortNamesCache.getInstance(Objects.requireNonNull(project)).processAllClassNames(name -> {
//            classes.add(name);
//            return true;
//        }, searchScope, IdFilter.getProjectIdFilter(project, false));

//        Messages.showMessageDialog(e.getProject(), String.valueOf(classes),
//                "Lista Klas Projektu", Messages.getInformationIcon());

        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile[] directories = projectRootManager.getContentSourceRoots();
        List<PsiClass> psiClasses = getClasses(directories, psiManager);
//        JBList<PsiClass> classes = new JBList<>(psiClasses);
        PsiClass[] classes = psiClasses.toArray(new PsiClass[0]);
//        ListModel<PsiClass> model = classes.getModel();

        ClassListDialog dialog = new ClassListDialog(classes);
        dialog.pack();
        dialog.setVisible(true);

//        createDialog(project, classes);
    }

    private void createDialog(Project project, JBList<PsiClass> classes) {
        JBScrollPane scrollPane = new JBScrollPane(classes);
        scrollPane.setPreferredSize(new Dimension(200, 500));

        DialogBuilder dialogBuilder = new DialogBuilder(project);
        dialogBuilder.setTitle("Lista Klas Projektu");
        dialogBuilder.setCenterPanel(scrollPane);
        dialogBuilder.removeAllActions();
        dialogBuilder.addOkAction();
        boolean isOk = dialogBuilder.show() == DialogWrapper.OK_EXIT_CODE;
        if (isOk) {
            System.out.println(classes.getSelectedValue().getAllFields().getClass());
            System.out.println(classes.getSelectedValue().getAllMethods().getClass());
            System.out.println(Arrays.toString(classes.getSelectedValue().getAllFields()));
        }


        dialogBuilder.getDialogWrapper();
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