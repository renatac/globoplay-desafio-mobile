package com.example.globechallenge.data.model.models.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: String,
    val name: String,
    val genre: List<Int>,
    val image: String
): Parcelable