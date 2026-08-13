# 💳 Merchant Payout App Challenge (Android)

Welcome to the Merchant Payout Challenge! This is a mobile frontend coding challenge designed to assess your ability to implement a financial payout experience using Kotlin and Jetpack Compose.

Your task is to build a merchant dashboard and payout flow that allows users to:

* Review account balances and recent activity with pagination
* Initiate and validate a payout to a bank account with confirmation
* Integrate native device identity for payout requests
* Require biometric authentication for payouts over £1,000.00
* Protect the payout screen from screenshots
* Handle various edge cases, including network errors and insufficient funds

## 📑 Table of Contents

- [💳 Merchant Payout App Challenge (Android)](#-merchant-payout-app-challenge-android)
  - [📑 Table of Contents](#-table-of-contents)
  - [🛠️ Tech Stack](#️-tech-stack)
  - [📡 API Documentation](#-api-documentation)
    - [Available Endpoints](#available-endpoints)
    - [Testing Error States](#testing-error-states)
  - [📝 Evaluation Criteria](#-evaluation-criteria)
  - [💡 Tips](#-tips)
  - [📋 Implementation Steps](#-implementation-steps)
    - [Step 1: Merchant Home Screen](#step-1-merchant-home-screen)
    - [Step 2: Transaction List](#step-2-transaction-list)
    - [Step 3: Payout Initiation Form \& Confirmation](#step-3-payout-initiation-form--confirmation)
    - [Step 4: Device Identity](#step-4-device-identity)
    - [Step 5: Biometric Authentication for Payouts over £1,000.00](#step-5-biometric-authentication-for-payouts-over-100000)
    - [Step 6: Screenshot Prevention](#step-6-screenshot-prevention)


## 🛠️ Tech Stack

The project comes with the following pre-configured:

* **Kotlin** — primary language
* **Jetpack Compose** — UI toolkit (required)
* **Android Studio** — recommended IDE
* **OkHttp MockWebServer** — local mock API server, starts automatically at launch

Additional libraries are available in `gradle/libs.versions.toml`. You are free to install other packages if they help you solve the problem more efficiently.


## 📡 API Documentation

The project uses **OkHttp MockWebServer** to serve a local banking API. The server starts automatically when the app launches via `InterviewApplication`. Use `MockServerManager.baseUrl` as the base URL for your HTTP client.

> **Note**: The mock server runs on a random available port at `localhost`. All requests and responses are logged to Logcat with the tag `MockWebServer`. This is expected behaviour — use Logcat to debug API calls.

### Available Endpoints

| Endpoint | Method | Description |
| --- | --- | --- |
| `/api/merchant` | `GET` | Returns `available_balance`, `pending_balance`, `currency`, and `activity` (3 most recent transactions). |
| `/api/merchant/activity` | `GET` | Returns paginated activity items using cursor-based pagination. Query parameters: `cursor` (optional, activity ID from previous page) and `limit` (optional, default: 15). Returns `{ items, next_cursor, has_more }`. |
| `/api/payouts` | `POST` | Initiates a payout. Request body: `{ amount, currency, iban, device_id? }` |
| `/api/payouts/:id` | `GET` | Returns the status of a previously created payout. |
| `/api/devices` | `GET` | Returns a stable `{ device_id }` for this device. |

> **Note**: The API returns and expects all monetary amounts in the **lowest denomination** (pence for GBP, cents for EUR). For example, `500000` represents `£5,000.00`. The `£1,000` biometric threshold is `100000` pence.

The data contracts for API responses are:

```kotlin
enum class Currency { GBP, EUR }
enum class ActivityType { payout, deposit, refund, fee }
enum class ActivityStatus { completed, pending, processing, failed }
enum class PayoutStatus { pending, processing, completed, failed }

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val amount: Int,           // pence; negative for outflows
    val currency: Currency,
    val date: String,          // ISO 8601
    val description: String,
    val status: ActivityStatus,
)

data class MerchantDataResponse(
    val available_balance: Int,
    val pending_balance: Int,
    val currency: Currency,
    val activity: List<ActivityItem>,
)

data class PaginatedActivityResponse(
    val items: List<ActivityItem>,
    val next_cursor: String?,
    val has_more: Boolean,
)

data class CreatePayoutRequest(
    val amount: Int,
    val currency: Currency,
    val iban: String,
    val device_id: String? = null,
)

data class PayoutResponse(
    val id: String,
    val status: PayoutStatus,
    val amount: Int,
    val currency: Currency,
    val iban: String,
    val created_at: String,
)
```

### Testing Error States

The mock server supports specific triggers to test error handling:

* **Service Unavailable**: `POST /api/payouts` with `amount` of `99999` (999.99 pence) returns `503 Service Unavailable`.
* **Insufficient Funds**: `POST /api/payouts` with `amount` of `88888` (888.88 pence) returns `400 Bad Request`.


## 📝 Evaluation Criteria

Your solution will be evaluated based on:

- 🏗️ **Layered architecture** — clear separation of data / domain / UI layers
- 🧹 **Clean, readable code** — well-named types, no unnecessary complexity
- 🎯 **State management** — distinct loading / error / empty / success states
- ✅ **Testing** — at least one unit test covering a business rule

## 💡 Tips

- **Start with Step 1 and work through each step incrementally**
- Use `kotlinx.serialization`, `Moshi`, or `Gson` for JSON parsing — your choice
- Keep accessibility in mind throughout development
- Test with the provided invalid input values to verify error handling
- Don't hesitate to install additional packages if they help you solve the problem more efficiently

## 📋 Implementation Steps

### Step 1: Merchant Home Screen

**Goal**: Fetch and display the merchant's financial overview.

**Requirements**:

* Fetch balance data using the provided API client.
* Display an account balance section showing the merchant's available balance and pending balance with the currency symbol from the API response.
* Display a list of the 3 most recent activity items in a single-row layout showing only the description and amount.
* Display a "show more" button that opens a modal with a full list of activity items.
* Handle loading and error states gracefully.

<details>
<summary>📱 Reference Screenshots</summary>

<table>
<thead>
<tr>
<th>Android</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_home.png" alt="Android Home Screen" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

</details>


### Step 2: Transaction List

**Goal**: Display recent activity with enhanced functionality.

**Requirements**:

* Display the list of all activity items with type, description, amount, and date (formatted as `DD MM YYYY`).
* Implement "Infinite Scroll" functionality on the transaction list modal. Load more items automatically as the user scrolls to the bottom.
* Use cursor-based pagination to fetch additional activity items.
* Handle loading and error states gracefully.
* Group transactions by local date (Today / Yesterday / 18 May 2025).

<details>
<summary>📱 Reference Screenshots</summary>

<table>
<thead>
<tr>
<th>Loading</th>
<th>Loaded</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_transaction_loading.png" alt="Android Transaction Loading" style="width: 300px;" /></td>
<td><img src="docs/screenshots/and_transaction.png" alt="Android Transaction List" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

</details>


### Step 3: Payout Initiation Form & Confirmation

**Goal**: Create a screen for users to send a payout to a bank account with confirmation modal.

**Requirements**:

* Use a numeric input field for the payout amount.
* Use a dropdown to select the currency (`GBP` or `EUR`). The currency can be different from the merchant's account currency.
* Capture the destination IBAN 
  * Valid IBAN formats (e.g., `GB29NWBK60161331926819`).
  * Invalid IBAN format (e.g., `FR1212345123451234567A12310131231231231`).
* Ensure the "Confirm" button is disabled if the input is empty, zero, or negative.
* Display a confirmation screen summarizing the transaction before execution (as shown in the reference images).
* Handle success response by showing Payout confirmation with amount and currency.
* Handle failures (e.g., `4xx`, `5xx` errors, insufficient funds) and network errors.

<details>
<summary>📱 Reference Screenshots</summary>

<table>
<thead>
<tr>
<th>Form</th>
<th>Confirm</th>
<th>Confirmed</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_payout.png" alt="Android Payout Form" style="width: 300px;" /></td>
<td><img src="docs/screenshots/and_payout_confirm.png" alt="Android Payout Confirm" style="width: 300px;" /></td>
<td><img src="docs/screenshots/and_payout_confirmed.png" alt="Android Payout Confirmed" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

<table>
<thead>
<tr>
<th>Failed</th>
<th>Insufficient Funds</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_payout_failed.png" alt="Android Payout Failed" style="width: 300px;" /></td>
<td><img src="docs/screenshots/and_payout_insufficient_funds.png" alt="Android Payout Insufficient Funds" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

</details>


### Step 4: Device Identity

**Goal**: Identify the Merchant's device identifier and send as part of the Payout API request.

**Requirements**:

* Send a stable ID with the Payout request as `device_id`.

### Step 5: Biometric Authentication for Payouts over £1,000.00

**Goal**: Secure payouts over **£1,000.00** (100,000 pence) with biometric authentication.

**Requirements**:

* Before the `/api/payouts` call, check if the payout amount exceeds the threshold (`1,000.00` in the selected currency). If it does, await the native bridge. If the promise resolves `false`, abort the payout.
* If biometrics are not setup, inform the user to setup biometrics in the settings and abort the payout.

**Emulator setup:**
* Go to emulator **Settings → Security → Fingerprint** (or search "fingerprint") and enrol a fingerprint.
* Then use **Extended Controls (…) → Fingerprint** to simulate a touch during the biometric prompt.

<details>
<summary>📱 Reference Screenshots</summary>

<table>
<thead>
<tr>
<th>Prompt</th>
<th>Failed</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_payout_biometric.png" alt="Android Biometric Prompt" style="width: 300px;" /></td>
<td><img src="docs/screenshots/and_payout_biometric_failed.png" alt="Android Biometric Failed" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

</details>


### Step 6: Screenshot Prevention

**Goal**: Make sure the Merchant is aware of the risk of screenshots on the Payout screen.

**Requirements**:

* **UI Reaction**: On the **Payout** screen, listen for screenshot event and show a non-intrusive warning (like a Toast or an Alert) reminding the user to keep their financial data private.

**Emulator testing:**
* Navigate to the Payout Form screen, then try taking a screenshot using the emulator toolbar's camera button or via `adb`:
  ```bash
  adb exec-out screencap -p > /tmp/payout_screenshot.png
  ```
* The captured image should be entirely black — this confirms `FLAG_SECURE` is active.
* Navigate back to the Home screen and repeat — the screenshot should capture normally, confirming the flag is scoped correctly to the Payout screens.

<details>
<summary>📱 Reference Screenshots</summary>

<table>
<thead>
<tr>
<th>Screenshot Warning (payout screen)</th>
</tr>
</thead>
<tbody>
<tr>
<td><img src="docs/screenshots/and_payout_screenshot_warning.png" alt="Android Screenshot Warning" style="width: 300px;" /></td>
</tr>
</tbody>
</table>

</details>



-------------------------------------------------------------------------------------------------------------------------------------------------------------

# Merchant Payout App

A modern Android merchant dashboard and payout application built with **Kotlin**, **Jetpack Compose**, **Coroutines**, **Flow**, **Paging**, **Room**, **Retrofit/OkHttp**, **Hilt**, and **JUnit-based testing**.

---

## Table of Contents

- [Overview](#overview)
- [Challenge Requirements](#challenge-requirements)
  - [Merchant Home](#merchant-home)
  - [Transactions](#transactions)
  - [Payout](#payout)
  - [Device Identity](#device-identity)
  - [Biometric Authentication](#biometric-authentication)
  - [Screenshot Protection](#screenshot-protection)
- [Implementation](#implementation)
  - [Merchant Home Screen](#merchant-home-screen)
  - [Transaction List](#transaction-list)
  - [Payout Form and Confirmation](#payout-form-and-confirmation)
  - [Device Identity](#device-identity-1)
  - [Biometric Authentication](#biometric-authentication-1)
  - [Screenshot Protection](#screenshot-protection-1)
- [Architecture](#architecture)
  - [UI Layer](#ui-layer)
  - [Domain Layer](#domain-layer)
  - [Data Layer](#data-layer)
  - [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [State Management](#state-management)
- [Data Layer](#data-layer-1)
  - [Remote API](#remote-api)
  - [Room](#room)
  - [Paging](#paging)
- [Payout Flow](#payout-flow)
- [Validation and Error Handling](#validation-and-error-handling)
- [Testing](#testing)
  - [HomeViewModel Tests](#homeviewmodel-tests)
  - [Payout Tests](#payout-tests)
  - [Test Location](#test-location)
- [Completed Screenshots](#completed-screenshots)
- [Running the Project](#running-the-project)
- [Testing the Main Flows](#testing-the-main-flows)
- [Implementation Notes](#implementation-notes)
- [Conclusion](#conclusion)

---

# Overview

The **Merchant Payout App** implements the functionality described in the Merchant Payout Challenge.

## Main Application Areas

- Merchant Home
- Transaction History
- Payout Form
- Payout Confirmation
- Payout Execution
- Payout Success and Failure States
- Device Identity
- Biometric Authentication
- Screenshot Protection

The application follows a layered architecture with a clear separation between:

- **UI**
- **Domain**
- **Data**

The application is built using:

- Kotlin
- Jetpack Compose
- Material 3
- Coroutines
- Flow
- Paging
- Room
- Retrofit
- OkHttp
- Hilt
- JUnit
- MockK
- `kotlinx-coroutines-test`

---

# Challenge Requirements

The application implements the following challenge requirements.

## Merchant Home

The Home screen displays:

- Available merchant balance
- Pending balance
- Merchant currency
- Recent activity
- Loading state
- Error state

[View Home screenshot](docs/completed_screenshots/home.png)

## Transactions

The Transactions screen provides:

- Complete transaction history
- Transaction date
- Transaction description
- Transaction amount
- Date grouping
- Cursor-based pagination
- Loading state while additional pages are loaded
- Pagination error state
- Retry functionality

[View Transactions screenshot](docs/completed_screenshots/transactions.png)

[View Transactions Loading screenshot](docs/completed_screenshots/transactions_loading.png)

## Payout

The payout flow provides:

- Amount input
- Currency selection
- IBAN input
- Input validation
- Payout confirmation
- Payout execution
- Successful payout state
- Failed payout state
- Insufficient funds handling

[View Payout Form screenshot](docs/completed_screenshots/payout.png)

[View Payout Confirmation screenshot](docs/completed_screenshots/payout_confirm.png)

[View Payout Success screenshot](docs/completed_screenshots/payout_confirmed.png)

[View Payout Failure screenshot](docs/completed_screenshots/payout_failed.png)

[View Insufficient Funds screenshot](docs/completed_screenshots/payout_insufficient_funds.png)

## Device Identity

Payout requests include the required device identity retrieved from the provided device endpoint.

## Biometric Authentication

Payouts above **£1,000.00** require biometric authentication before the payout request can be submitted.

[View Biometric Authentication screenshot](docs/completed_screenshots/payout_biometric.png)

## Screenshot Protection

The payout screen is protected from screenshots while sensitive payout information is displayed.

---

# Implementation

## Merchant Home Screen

The Home screen retrieves merchant information through the domain layer and displays the current financial state.

### Displayed Information

- Available balance
- Pending balance
- Currency
- Recent activity

The ViewModel exposes an explicit UI state:

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val merchant: Merchant
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}
