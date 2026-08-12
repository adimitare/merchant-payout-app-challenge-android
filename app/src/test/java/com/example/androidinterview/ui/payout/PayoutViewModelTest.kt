package com.example.androidinterview.ui.payout

import com.example.androidinterview.R
import com.example.androidinterview.domain.biometric.BiometricResult
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutException
import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.model.PayoutStatus
import com.example.androidinterview.domain.usecase.SubmitPayoutUseCase
import com.example.androidinterview.domain.usecase.ValidateIbanUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PayoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val validateIbanUseCase = mockk<ValidateIbanUseCase>()
    private val submitPayoutUseCase = mockk<SubmitPayoutUseCase>()

    private lateinit var viewModel: PayoutViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every {
            validateIbanUseCase(any())
        } returns null

        viewModel = PayoutViewModel(
            validateIbanUseCase = validateIbanUseCase,
            submitPayoutUseCase = submitPayoutUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ------------------------------------------------------------------------
    // Initial state
    // ------------------------------------------------------------------------

    @Test
    fun initialState_isForm() {
        val state = viewModel.uiState.value

        assertTrue(state is PayoutUiState.Form)
    }

    // ------------------------------------------------------------------------
    // Amount
    // ------------------------------------------------------------------------

    @Test
    fun onAmountChanged_updatesAmount() {
        viewModel.onAmountChanged("123.45")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("123.45", state.data.amount)
        assertEquals(null, state.data.amountError)
    }

    @Test
    fun onAmountChanged_acceptsEmptyValue() {
        viewModel.onAmountChanged("")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("", state.data.amount)
    }

    @Test
    fun onAmountChanged_acceptsTwoDecimalPlaces() {
        viewModel.onAmountChanged("123.45")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("123.45", state.data.amount)
    }

    @Test
    fun onAmountChanged_rejectsMoreThanTwoDecimalPlaces() {
        viewModel.onAmountChanged("123.456")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("", state.data.amount)
    }

    @Test
    fun onAmountChanged_rejectsNonNumericValue() {
        viewModel.onAmountChanged("abc")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("", state.data.amount)
    }

    @Test
    fun onAmountChanged_clearsAmountError() {
        viewModel.onConfirmClicked()

        val before = viewModel.uiState.value as PayoutUiState.Form
        assertEquals(R.string.enter_an_amount, before.data.amountError)

        viewModel.onAmountChanged("100")

        val after = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("100", after.data.amount)
        assertEquals(null, after.data.amountError)
    }

    // ------------------------------------------------------------------------
    // Currency
    // ------------------------------------------------------------------------

    @Test
    fun onCurrencyChanged_updatesCurrency() {
        viewModel.onCurrencyChanged(Currency.EUR)

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(Currency.EUR, state.data.currency)
    }

    // ------------------------------------------------------------------------
    // IBAN
    // ------------------------------------------------------------------------

    @Test
    fun onIbanChanged_updatesIban() {
        viewModel.onIbanChanged("DE89370400440532013000")

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(
            "DE89370400440532013000",
            state.data.iban
        )
    }

    @Test
    fun onIbanChanged_clearsIbanError() {
        every {
            validateIbanUseCase("")
        } returns R.string.enter_a_valid_iban

        viewModel.onConfirmClicked()

        val before = viewModel.uiState.value as PayoutUiState.Form
        assertEquals(
            R.string.enter_a_valid_iban,
            before.data.ibanError
        )

        viewModel.onIbanChanged("DE89370400440532013000")

        val after = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(
            "DE89370400440532013000",
            after.data.iban
        )
        assertEquals(null, after.data.ibanError)
    }

    // ------------------------------------------------------------------------
    // Confirm
    // ------------------------------------------------------------------------

    @Test
    fun onConfirmClicked_setsAmountError_whenAmountIsBlank() {
        viewModel.onConfirmClicked()

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(
            R.string.enter_an_amount,
            state.data.amountError
        )
    }

    @Test
    fun onConfirmClicked_setsAmountError_whenAmountIsZero() {
        viewModel.onAmountChanged("0")
        viewModel.onIbanChanged("DE89370400440532013000")

        viewModel.onConfirmClicked()

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(
            R.string.amount_must_be_greater_than_zero,
            state.data.amountError
        )
    }

    @Test
    fun onConfirmClicked_setsIbanError_whenIbanIsInvalid() {
        every {
            validateIbanUseCase("INVALID")
        } returns R.string.enter_a_valid_iban

        viewModel.onAmountChanged("100")
        viewModel.onIbanChanged("INVALID")

        viewModel.onConfirmClicked()

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals(
            R.string.enter_a_valid_iban,
            state.data.ibanError
        )
    }

    @Test
    fun onConfirmClicked_doesNotConfirm_whenAmountIsInvalid() {
        every {
            validateIbanUseCase("DE89370400440532013000")
        } returns null

        viewModel.onAmountChanged("0")
        viewModel.onIbanChanged("DE89370400440532013000")

        viewModel.onConfirmClicked()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Form
        )
    }

    @Test
    fun onConfirmClicked_doesNotConfirm_whenIbanIsInvalid() {
        every {
            validateIbanUseCase("INVALID")
        } returns R.string.enter_a_valid_iban

        viewModel.onAmountChanged("100")
        viewModel.onIbanChanged("INVALID")

        viewModel.onConfirmClicked()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Form
        )
    }

    @Test
    fun onConfirmClicked_changesStateToConfirming_whenDataIsValid() {
        val iban = "DE89370400440532013000"

        every {
            validateIbanUseCase(iban)
        } returns null

        viewModel.onAmountChanged("100")
        viewModel.onCurrencyChanged(Currency.EUR)
        viewModel.onIbanChanged(iban)

        viewModel.onConfirmClicked()

        val state = viewModel.uiState.value

        assertTrue(state is PayoutUiState.Confirming)

        val confirming = state as PayoutUiState.Confirming

        assertEquals("100", confirming.data.amount)
        assertEquals(Currency.EUR, confirming.data.currency)
        assertEquals(iban, confirming.data.iban)
    }

    @Test
    fun onConfirmClicked_callsValidateIban() {
        val iban = "DE89370400440532013000"

        every {
            validateIbanUseCase(iban)
        } returns null

        viewModel.onAmountChanged("100")
        viewModel.onIbanChanged(iban)

        viewModel.onConfirmClicked()

        verify(exactly = 1) {
            validateIbanUseCase(iban)
        }
    }

    // ------------------------------------------------------------------------
    // Cancel confirmation
    // ------------------------------------------------------------------------

    @Test
    fun cancelConfirmation_returnsToForm() {
        makeValidForm()
        viewModel.onConfirmClicked()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Confirming
        )

        viewModel.cancelConfirmation()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Form
        )
    }

    @Test
    fun cancelConfirmation_preservesFormData() {
        val iban = "DE89370400440532013000"

        makeValidForm(
            amount = "250.50",
            currency = Currency.EUR,
            iban = iban
        )

        viewModel.onConfirmClicked()
        viewModel.cancelConfirmation()

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("250.50", state.data.amount)
        assertEquals(Currency.EUR, state.data.currency)
        assertEquals(iban, state.data.iban)
    }

    // ------------------------------------------------------------------------
    // Create another payout
    // ------------------------------------------------------------------------

    @Test
    fun createAnotherPayout_resetsStateToEmptyForm() {
        makeValidForm()
        viewModel.onConfirmClicked()

        viewModel.createAnotherPayout()

        val state = viewModel.uiState.value as PayoutUiState.Form

        assertEquals("", state.data.amount)
        assertEquals("", state.data.iban)
    }

    // ------------------------------------------------------------------------
    // Submit payout
    // ------------------------------------------------------------------------
    @Test
    fun submitPayout_changesStateToSubmitting() = runTest {
        makeValidForm(
            amount = "50.00"
        )

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 5000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } coAnswers {
            // Suspend here so we can inspect the intermediate state.
            kotlinx.coroutines.awaitCancellation()
        }

        viewModel.submitPayout()

        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Submitting
        )
    }

    @Test
    fun submitPayout_callsBackend_forAmountBelowBiometricThreshold() = runTest {
        makeValidForm(
            amount = "50.00"
        )

        viewModel.onConfirmClicked()

        val result = payoutResult(
            amount = 5000
        )

        coEvery {
            submitPayoutUseCase(
                amount = 5000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } returns result

        viewModel.submitPayout()

        advanceUntilIdle()

        coVerify(exactly = 1) {
            submitPayoutUseCase(
                amount = 5000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        }

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Success
        )
    }

    @Test
    fun submitPayout_doesNotRequireBiometric_belowThreshold() = runTest {
        makeValidForm(
            amount = "999.99"
        )

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 99999,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } returns payoutResult(amount = 99999)

        viewModel.submitPayout()

        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Success
        )

        assertFalse(
            viewModel.uiState.value is PayoutUiState.AwaitingBiometric
        )
    }

    /*@Test
    fun submitPayout_requiresBiometric_whenAmountReachesThreshold() {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()

        viewModel.submitPayout()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.AwaitingBiometric
        )

        assertTrue(
            viewModel.effects.retry {  }.getOrNull()
                    is PayoutEffect.AuthenticateBiometric
        )

        coVerify(exactly = 0) {
            submitPayoutUseCase(
                any(),
                any(),
                any()
            )
        }
    }*/

    // ------------------------------------------------------------------------
    // Biometric
    // ------------------------------------------------------------------------

    @Test
    fun onBiometricResult_success_submitsPayout() = runTest {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()
        viewModel.submitPayout()

        assertTrue(
            viewModel.uiState.value is PayoutUiState.AwaitingBiometric
        )

        coEvery {
            submitPayoutUseCase(
                amount = 100000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } returns payoutResult(amount = 100000)

        viewModel.onBiometricResult(
            BiometricResult.Success
        )

        advanceUntilIdle()

        coVerify(exactly = 1) {
            submitPayoutUseCase(
                amount = 100000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        }

        assertTrue(
            viewModel.uiState.value is PayoutUiState.Success
        )
    }

    @Test
    fun onBiometricResult_cancelled_setsBiometricCancelledError() {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()
        viewModel.submitPayout()

        viewModel.onBiometricResult(
            BiometricResult.Cancelled
        )

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.BiometricCancelled,
            state.error
        )
    }

    @Test
    fun onBiometricResult_notEnrolled_setsBiometricNotEnrolledError() {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()
        viewModel.submitPayout()

        viewModel.onBiometricResult(
            BiometricResult.NotEnrolled
        )

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.BiometricNotEnrolled,
            state.error
        )
    }

    @Test
    fun onBiometricResult_unavailable_setsBiometricUnavailableError() {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()
        viewModel.submitPayout()

        viewModel.onBiometricResult(
            BiometricResult.Unavailable
        )

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.BiometricUnavailable,
            state.error
        )
    }

    @Test
    fun onBiometricResult_failed_setsBiometricFailedError() {
        makeValidForm(
            amount = "1000.00"
        )

        viewModel.onConfirmClicked()
        viewModel.submitPayout()

        viewModel.onBiometricResult(
            BiometricResult.Failed
        )

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertTrue(
            state.error is PayoutError.BiometricFailed
        )
    }

    // ------------------------------------------------------------------------
    // Backend errors
    // ------------------------------------------------------------------------

    @Test
    fun submitPayout_insufficientFunds_setsCorrectError() = runTest {
        makeValidForm()

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 10000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } throws PayoutException.InsufficientFunds

        viewModel.submitPayout()

        advanceUntilIdle()

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.InsufficientFunds,
            state.error
        )
    }

    @Test
    fun submitPayout_serviceUnavailable_setsCorrectError() = runTest {
        makeValidForm()

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 10000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } throws PayoutException.ServiceUnavailable

        viewModel.submitPayout()

        advanceUntilIdle()

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.ServiceUnavailable,
            state.error
        )
    }

    @Test
    fun submitPayout_apiError_setsCorrectError() = runTest {
        makeValidForm()

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 10000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } throws PayoutException.ApiError(
            errorMessage = "Something went wrong"
        )

        viewModel.submitPayout()

        advanceUntilIdle()

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.ApiError,
            state.error
        )
    }

    @Test
    fun submitPayout_unknownException_setsUnknownError() = runTest {
        makeValidForm()

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 10000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } throws IllegalStateException("Unexpected error")

        viewModel.submitPayout()

        advanceUntilIdle()

        val state = viewModel.uiState.value as PayoutUiState.Error

        assertEquals(
            PayoutError.Unknown,
            state.error
        )
    }

    // ------------------------------------------------------------------------
    // Successful payout
    // ------------------------------------------------------------------------

    @Test
    fun submitPayout_success_setsSuccessState() = runTest {
        makeValidForm()

        val result = payoutResult(
            id = "123",
            amount = 10000,
            currency = Currency.EUR,
            iban = "DE89370400440532013000"
        )

        viewModel.onConfirmClicked()

        coEvery {
            submitPayoutUseCase(
                amount = 10000,
                currency = Currency.EUR.name,
                iban = "DE89370400440532013000"
            )
        } returns result

        viewModel.submitPayout()

        advanceUntilIdle()

        val state = viewModel.uiState.value as PayoutUiState.Success

        assertEquals(result, state.payout)
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    private fun makeValidForm(
        amount: String = "100.00",
        currency: Currency = Currency.EUR,
        iban: String = "DE89370400440532013000"
    ) {
        every {
            validateIbanUseCase(iban)
        } returns null

        viewModel.onAmountChanged(amount)
        viewModel.onCurrencyChanged(currency)
        viewModel.onIbanChanged(iban)
    }

    private fun payoutResult(
        id: String = "1",
        amount: Int = 10000,
        currency: Currency = Currency.EUR,
        iban: String = "DE89370400440532013000",
        status: PayoutStatus = PayoutStatus.COMPLETED,
        createdAt: String = "2026-08-12T10:00:00Z"
    ): PayoutResult {
        return PayoutResult(
            id = id,
            amount = amount,
            currency = currency,
            iban = iban,
            status = status,
            createdAt = createdAt
        )
    }
}