package com.sidgowda.pawcalc.doginput.model

import androidx.annotation.StringRes
import com.sidgowda.pawcalc.doginput.R

enum class DogInputRequirements(
    @StringRes val label: Int
) {
    ONE_PICTURE(R.string.profile_picture_requirements),
    NAME_ONE_CHARACTER(R.string.name_input_requirements),
    WEIGHT_OVER_ZERO(R.string.weight_input_requirements),
    DATE_OVER_TODAY(R.string.date_input_requirements)
}
