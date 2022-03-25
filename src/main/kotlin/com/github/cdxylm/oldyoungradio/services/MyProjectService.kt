package com.github.cdxylm.oldyoungradio.services

import com.intellij.openapi.project.Project
import com.github.cdxylm.oldyoungradio.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
