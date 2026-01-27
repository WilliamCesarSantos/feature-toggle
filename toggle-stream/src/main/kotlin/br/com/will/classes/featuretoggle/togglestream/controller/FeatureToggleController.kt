package br.com.will.classes.featuretoggle.togglestream.controller

import br.com.will.classes.featuretoggle.togglestream.event.FeatureToggleSnsPublisher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/toggles")
class FeatureToggleController(
    private val snsPublisher: FeatureToggleSnsPublisher
) {

    @PostMapping
    fun toggle(
        @RequestParam feature: String,
        @RequestParam enabled: Boolean
    ) {
        snsPublisher.publishToggle(feature, enabled)
    }
}

