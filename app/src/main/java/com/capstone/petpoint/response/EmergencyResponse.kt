package com.capstone.petpoint.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostEmergencyResponse(

    @field:SerializedName("data")
    val data: EmergencyItem? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class EmergencyItem(

    @field:SerializedName("idUser")
    val idUser: String? = null,

    @field:SerializedName("Status")
    val status: String? = null,

    @field:SerializedName("Category")
    val category: String? = null,

    @field:SerializedName("notes")
    val notes: String? = null,

    @field:SerializedName("Picture")
    val picture: String? = null,

    @field:SerializedName("EmergencyId")
    val emergencyId: String? = null,

    @field:SerializedName("Location")
    val location: String? = null,

    @field:SerializedName("Created_at")
    val createdAt: String? = null
) : Parcelable
