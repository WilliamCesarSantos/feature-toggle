package br.com.will.classes.featuretoggle.configserver.controller

data class ParameterUpdateRequest(
    val parameterName: String,
    val parameterValue: String,
    val parameterType: String = "String"
)

data class ParameterResponse(
    val success: Boolean,
    val message: String,
    val parameterName: String? = null
)
