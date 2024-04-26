package model

import model.component.Components
import model.info.Info
import model.path.Path
import model.server.Server

data class ApiDocumentation(
    var openapi: String = "",
    var info: Info = Info(),
    var servers: List<Server> = emptyList(),
    var paths: Map<String, Path> = emptyMap(),
    var components: Components = Components()
)
