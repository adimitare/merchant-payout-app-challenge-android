package com.example.androidinterview.domain.usecase

import com.example.androidinterview.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ValidateIbanUseCaseTest {
    private lateinit var useCase: ValidateIbanUseCase

    @Before
    fun setUp() {
        useCase = ValidateIbanUseCase()
    }

    @Test
    fun `invoke returns enter an iban error when iban is blank`() {
        // When
        val result = useCase("")
        // Then
        assertEquals(
            R.string.enter_an_iban,
            result
        )
    }

    @Test
    fun `invoke returns enter an iban error when iban contains only spaces`() {
        // When
        val result = useCase("   ")
        // Then
        assertEquals(
            R.string.enter_an_iban,
            result
        )
    }

    @Test
    fun `invoke returns invalid iban error when iban is shorter than minimum length`() {
        // When
        val result = useCase("BG80")
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }

    @Test
    fun `invoke returns invalid iban error when iban has invalid format`() {
        // Given
        val iban = "BG80-123456789012345678"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }

    @Test
    fun `invoke returns invalid iban error when country is not supported`() {
        // Given
        val iban = "XX12345678901234567890"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }

    @Test
    fun `invoke returns invalid iban error when country specific length is incorrect`() {
        // Given
        val iban = "BG801234567890123456"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }

    @Test
    fun `invoke returns invalid iban error when checksum is invalid`() {
        // Given
        val iban = "BG80BNBG96611020345679"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }

    @Test
    fun `invoke returns null for valid Bulgarian iban`() {
        // Given
        val iban = "BG80BNBG96611020345678"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke accepts lowercase iban`() {
        // Given
        val iban = "bg80bnbg96611020345678"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke accepts iban containing spaces`() {
        // Given
        val iban = "BG80 BNBG 9661 1020 3456 78"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke accepts lowercase iban containing spaces`() {
        // Given
        val iban = "bg80 bnbg 9661 1020 3456 78"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke accepts valid German iban`() {
        // Given
        val iban = "DE89370400440532013000"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke accepts valid British iban`() {
        // Given
        val iban = "GB82WEST12345698765432"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            null,
            result
        )
    }

    @Test
    fun `invoke returns null for valid IBAN format`() {
        // Given
        val iban = "GB29NWBK60161331926819"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(null, result)
    }

    @Test
    fun `invoke returns invalid iban error for invalid IBAN format`() {
        // Given
        val iban = "FR1212345123451234567A12310131231231231"
        // When
        val result = useCase(iban)
        // Then
        assertEquals(
            R.string.enter_a_valid_iban,
            result
        )
    }
}