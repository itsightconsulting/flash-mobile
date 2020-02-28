package isdigital.veridium.flash.model.dto

import isdigital.veridium.flash.model.pojo.ActivationPOJO

data class PendingActivations(
    val pendingActivations: List<ActivationPOJO>,
    val totalRequestActivations: Int,
    val maximumActivationsCompletedStateReached: Boolean
)