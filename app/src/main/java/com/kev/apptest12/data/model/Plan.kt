package com.kev.apptest12.data.model


data class Plan(
    val name: String,
    val price: String,
    val features: List<Feature>,
    val isSelected: Boolean = false
)

data class Feature(
    val description: String,
    val isPositive: Boolean // true para características positivas (✔️), false para negativas (❌)
)