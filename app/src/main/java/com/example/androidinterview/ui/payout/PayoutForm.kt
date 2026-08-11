package com.example.androidinterview.ui.payout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutForm(
    state: PayoutUiState.Form,
    onAmountChanged: (String) -> Unit,
    onCurrencyChanged: (Currency) -> Unit,
    onIbanChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var currencyExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(
                        R.string.send_payout
                    )
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.data.amount,
                    onValueChange = onAmountChanged,
                    modifier = Modifier.weight(0.65f),
                    label = {
                        Text(
                            stringResource(
                                R.string.amount_text_field
                            )
                        )
                    },
                    singleLine = true,
                    isError = state.data.amountError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    supportingText = {
                        state.data.amountError?.let {
                            Text(
                                text = stringResource(it)
                            )
                        }
                    }
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = {
                        currencyExpanded = it
                    },
                    modifier = Modifier.weight(0.35f)
                ) {
                    OutlinedTextField(
                        value = state.data.currency.name,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                stringResource(
                                    R.string.currency_text_field
                                )
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = currencyExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                ExposedDropdownMenuAnchorType
                                    .PrimaryNotEditable
                            )
                    )
                    ExposedDropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = {
                            currencyExpanded = false
                        }
                    ) {
                        Currency.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option.name)
                                },
                                onClick = {
                                    onCurrencyChanged(option)
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            OutlinedTextField(
                value = state.data.iban,
                onValueChange = onIbanChanged,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        stringResource(
                            R.string.iban_text_field
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    capitalization = KeyboardCapitalization.Characters
                ),
                singleLine = true,
                isError = state.data.ibanError != null,
                supportingText = {
                    state.data.ibanError?.let {
                        Text(
                            text = stringResource(it)
                        )
                    }
                }
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text(
                text = stringResource(
                    R.string.enter_the_destination_bank_account_iban
                )
            )
            Spacer(
                modifier = Modifier.height(64.dp)
            )
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.data.amount.isNotBlank() &&
                        state.data.iban.isNotBlank() &&
                        state.data.amountError == null &&
                        state.data.ibanError == null
            ) {
                Text(
                    stringResource(
                        R.string.confirm_text_button_payout_form
                    )
                )
            }
        }
    }
}