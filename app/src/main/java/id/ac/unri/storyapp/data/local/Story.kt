package id.ac.unri.storyapp.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val name: String?,
    val description: String?,
    val photoUrl: String?
):Parcelable
