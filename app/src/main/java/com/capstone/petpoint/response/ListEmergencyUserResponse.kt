package com.capstone.petpoint.response

import com.google.gson.annotations.SerializedName

data class ListEmergencyUserResponse(

	@field:SerializedName("data")
	val data: List<DataItem> = listOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("em_id")
	val emId: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("pet_status")
	val petStatus: String? = null,

	@field:SerializedName("date_created")
	val dateCreated: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("time_created")
	val timeCreated: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null,

	@field:SerializedName("pic_pet")
	val picPet: String? = null,

	@field:SerializedName("pet_category")
	val petCategory: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,


)
