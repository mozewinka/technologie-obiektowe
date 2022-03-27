package com.github.mozewinka.technologieobiektowe.services

import com.intellij.openapi.project.Project
import com.github.mozewinka.technologieobiektowe.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
