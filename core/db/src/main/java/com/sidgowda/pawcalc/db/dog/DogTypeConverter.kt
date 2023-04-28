package com.sidgowda.pawcalc.db.dog

import android.net.Uri
import androidx.room.TypeConverter

class DogTypeConverter {

    @TypeConverter
    fun uriFromString(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun uriToString(uri: Uri): String {
        return uri.toString()
    }
}
