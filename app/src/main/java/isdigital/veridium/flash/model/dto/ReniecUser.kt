package isdigital.veridium.flash.model.dto

data class ReniecUser(
    val dni: String,
    val nombres: String,
    val appaterno: String,
    val apmaterno: String,
    val codresultado: String,
    val restriccion: String,
    val feccaducidaddni: String,
    val descerror: String
)