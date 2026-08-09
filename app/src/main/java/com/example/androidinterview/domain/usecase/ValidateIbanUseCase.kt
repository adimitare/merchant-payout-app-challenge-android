package com.example.androidinterview.domain.usecase

import com.example.androidinterview.R
import javax.inject.Inject

class ValidateIbanUseCase @Inject constructor() {
    operator fun invoke(iban: String): Int? {
        return validateIban(iban)
    }

    private val IBAN_BASIC_REGEX = Regex("^[A-Z]{2}[0-9]{2}[A-Z0-9]+$")

    private val IBAN_LENGTHS = mapOf(
        "AL" to 28,
        "AD" to 24,
        "AT" to 20,
        "AZ" to 28,
        "BH" to 22,
        "BE" to 16,
        "BA" to 20,
        "BR" to 29,
        "BG" to 22,
        "CR" to 22,
        "HR" to 21,
        "CY" to 28,
        "CZ" to 24,
        "DK" to 18,
        "DO" to 28,
        "EE" to 20,
        "FO" to 18,
        "FI" to 18,
        "FR" to 27,
        "GE" to 22,
        "DE" to 22,
        "GI" to 23,
        "GR" to 27,
        "GL" to 18,
        "GT" to 28,
        "HU" to 28,
        "IS" to 26,
        "IE" to 22,
        "IL" to 23,
        "IT" to 27,
        "JO" to 30,
        "KZ" to 20,
        "XK" to 20,
        "KW" to 30,
        "LV" to 21,
        "LB" to 28,
        "LI" to 21,
        "LT" to 20,
        "LU" to 20,
        "MT" to 31,
        "MR" to 27,
        "MU" to 30,
        "MC" to 27,
        "MD" to 24,
        "ME" to 22,
        "NL" to 18,
        "MK" to 19,
        "NO" to 15,
        "PK" to 24,
        "PS" to 29,
        "PL" to 28,
        "PT" to 25,
        "QA" to 29,
        "RO" to 24,
        "SM" to 27,
        "SA" to 24,
        "RS" to 22,
        "SK" to 24,
        "SI" to 19,
        "ES" to 24,
        "SE" to 24,
        "CH" to 21,
        "TN" to 24,
        "TR" to 26,
        "UA" to 29,
        "AE" to 23,
        "GB" to 22,
        "VA" to 22
    )

    fun validateIban(iban: String): Int? {
        val normalized = iban
            .replace(" ", "")
            .uppercase()

        return when {
            normalized.isBlank() ->
                R.string.enter_an_iban

            normalized.length < 15 ->
                R.string.enter_a_valid_iban

            !normalized.matches(IBAN_BASIC_REGEX) ->
                R.string.enter_a_valid_iban

            normalized.substring(0, 2) !in IBAN_LENGTHS ->
                R.string.enter_a_valid_iban

            normalized.length != IBAN_LENGTHS[normalized.substring(0, 2)] ->
                R.string.enter_a_valid_iban

            !hasValidIbanChecksum(normalized) ->
                R.string.enter_a_valid_iban

            else -> null
        }
    }

    private fun hasValidIbanChecksum(iban: String): Boolean {
        val rearranged = iban.substring(4) + iban.substring(0, 4)

        var remainder = 0

        for (char in rearranged) {
            val value = when {
                char.isDigit() -> char - '0'
                char in 'A'..'Z' -> char.code - 'A'.code + 10
                else -> return false
            }

            if (value >= 10) {
                remainder = (remainder * 100 + value) % 97
            } else {
                remainder = (remainder * 10 + value) % 97
            }
        }

        return remainder == 1
    }
}