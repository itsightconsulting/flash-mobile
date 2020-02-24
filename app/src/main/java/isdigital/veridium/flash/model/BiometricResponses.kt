package isdigital.veridium.flash.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author Luis Alonso Paulino Flores <alonsopf1@gmail.com>
 *
 */

data class BestBiometricPrintResponse(@SerializedName("ResultCode") val resultCode: Int,
                                      @SerializedName("ResultCodeDescription") val resultCodeDescription: String,
                                      @SerializedName("SubTypes") val subTypes: List<BestBiometricPrintSubTypes>): Serializable

data class BestBiometricPrintSubTypes(@SerializedName("Descripcion") val description: String,
                                      @SerializedName("Id") val id: Int): Serializable

data class ValidateBiometricPrintResponse(@SerializedName("BiometricSubType") val subType: Int,
                                          @SerializedName("BiometricType") val type: Int,
                                          @SerializedName("DNINumber") val dni: String,
                                          @SerializedName("ExpirationDate") val expiration: String,
                                          @SerializedName("IsForeign") val foreign: Boolean,
                                          @SerializedName("IsValidateOnDevice") val validateOnDeice: Boolean,
                                          @SerializedName("IsWhiteList") val whiteList: Boolean,
                                          @SerializedName("LastName") val lastName: String,
                                          @SerializedName("MothersLastName") val motherLastName: String,
                                          @SerializedName("Name") val name: String,
                                          @SerializedName("ResultCode") val resultCode: Int,
                                          @SerializedName("ResultCodeDescription") val resultCodeDescription: String,
                                          @SerializedName("ResultCodeRENIEC") val resultCodeReniec: String,
                                          @SerializedName("ResultCodeRENIECDescription") val resultCodeDescriptionReniec: String,
                                          @SerializedName("TransactionId") val transactionId: Int)

data class ReniecBiometricResponse(@SerializedName("min") val info: ReniecUserInfoResponse)

data class ReniecUserInfoResponse(@SerializedName("nombres") val names: String,
                                  @SerializedName("apmaterno") val lastMotherName: String,
                                  @SerializedName("appaterno") val lastFatherName: String,
                                  @SerializedName("codresultado") val codResult: String)

