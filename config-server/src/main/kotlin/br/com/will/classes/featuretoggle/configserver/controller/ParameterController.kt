package br.com.will.classes.featuretoggle.configserver.controller

import br.com.will.classes.featuretoggle.configserver.service.ParameterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/parameters")
class ParameterController(
    private val parameterService: ParameterService
) {

    @PostMapping("/update")
    fun updateParameter(
        @RequestBody request: ParameterUpdateRequest
    ): ResponseEntity<ParameterResponse> {
        val message = parameterService.updateParameter(
            parameterName = request.parameterName,
            parameterValue = request.parameterValue,
            parameterType = request.parameterType
        )

        return ResponseEntity.ok(
            ParameterResponse(
                success = true,
                message = message,
                parameterName = request.parameterName
            )
        )
    }

    @PostMapping("/refresh")
    fun refreshAllClients(
        @RequestParam("destination", defaultValue = "*") destination: String
    ): ResponseEntity<ParameterResponse> {
        parameterService.publishRefreshEvent(destination)
        return ResponseEntity.ok(
            ParameterResponse(
                success = true,
                message = "Refresh event published to all connected clients"
            )
        )
    }

}

