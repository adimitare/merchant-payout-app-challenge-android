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





# Table of Contents
- [Overview](#overview)
- [Challenge Requirements](#challenge-requirements)
- [Implementation](#implementation)
  - [Merchant Home Screen](#merchant-home-screen)
  - [Transaction List](#transaction-list)
  - [Payout Form and Confirmation](#payout-form-and-confirmation)
  - [Device Identity](#device-identity)
  - [Biometric Authentication](#biometric-authentication)
  - [Screenshot Protection](#screenshot-protection)
- [Architecture](#architecture)
  - [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [State Management](#state-management)
- [Data Layer](#data-layer)
  - [Remote API](#remote-api)
  - [Room](#room)
  - [Paging](#paging)
- [Payout Flow](#payout-flow)
- [Validation and Error Handling](#validation-and-error-handling)
- [Testing](#testing)
- [Completed Screenshots](#completed-screenshots)
- [Running the Project](#running-the-project)
- [Testing the Main Flows](#testing-the-main-flows)
- [Implementation Notes](#implementation-notes)
- [Conclusion](#conclusion)

## Overview
The Merchant Payout App implements the functionality described in the Merchant Payout Challenge.
The main application areas are:
- Merchant Home
- Transaction History
- Payout Form
- Payout Confirmation
- Payout Execution
- Payout Success and Failure States
- Device Identity
- Biometric Authentication
- Screenshot Protection

The implementation uses a layered architecture with clear separation between:
- UI
- Domain
- Data

The application is built using Kotlin, Jetpack Compose, Coroutines, Flow, Paging, Room, Retrofit/OkHttp, Hilt, and unit testing with JUnit, MockK, and kotlinx-coroutines-test.

## Challenge Requirements
The application implements the following challenge requirements.

### Merchant Home
The Home screen displays:
- Available merchant balance.
- Pending balance.
- Merchant currency.
- Recent activity.
- Loading state.
- Error state.

### Transactions
The Transactions screen provides:
- Complete transaction history.
- Transaction date.
- Transaction description.
- Transaction amount.
- Date grouping.
- Cursor-based pagination.
- Loading state while additional pages are loaded.
- Pagination error state.
- Retry functionality.

### Payout
The payout flow provides:
- Amount input.
- Currency selection.
- IBAN input.
- Input validation.
- Payout confirmation.
- Payout execution.
- Successful payout state.
- Failed payout state.
- Insufficient funds handling.

### Device Identity
Payout requests include the required device identity retrieved from the provided device endpoint.

### Biometric Authentication
Payouts above £1,000.00 require biometric authentication before the payout request can be submitted.

### Screenshot Protection
The payout screen is protected from screenshots while sensitive payout information is displayed.

## Implementation
### Merchant Home Screen
The Home screen retrieves merchant information through the domain layer and displays the current financial state.

The screen displays:
- Available balance.
- Pending balance.
- Currency.
- Recent activity.

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
The HomeViewModel starts loading the merchant when it is created and exposes the state through a StateFlow.
The UI therefore reacts to:

text
Loading
    ↓
Success
or:

text
Loading
    ↓
Error
The actual data retrieval is delegated to GetMerchantUseCase.

Transaction List
The transaction screen displays the merchant's complete activity history.
Transactions are grouped by date and displayed in a LazyColumn.
The list supports cursor-based pagination through AndroidX Paging and a RemoteMediator.

The overall data flow is:

text
Remote API
    ↓
RemoteMediator
    ↓
Room Database
    ↓
PagingSource
    ↓
ViewModel
    ↓
Compose UI
The API uses forward cursor pagination.
The initial request is made without a cursor:

text
cursor = null
The API then returns the next cursor.
Subsequent requests use the cursor from the previous response.

For example:

text
Request 1
cursor = null
limit = 15

Response
items = act_001 ... act_015
nextCursor = act_015
hasMore = true
Then:

text
Request 2
cursor = act_015
limit = 15

Response
items = act_016 ... act_030
nextCursor = null
hasMore = false
The application treats the API as forward-only pagination, so APPEND is supported while PREPEND reaches the end of pagination.

Payout Form and Confirmation
The payout flow starts with a form containing:

Amount.

Currency.

IBAN.

The confirmation action is disabled until the required values are valid.
The form validates:

Amount.

IBAN.

Required fields.

The flow is:

text
Payout Form
     ↓
Validation
     ↓
Confirmation
     ↓
Security Checks
     ↓
Payout API Request
     ↓
Success / Failure
The user is not sent directly from the form to the API request.
A confirmation state is displayed first so that the user can review the payout details before the transaction is submitted.

Device Identity
The payout API supports a device identifier.
The application retrieves the device identity using the provided device endpoint and includes it when creating a payout.

The general flow is:

text
GET /api/devices
        ↓
Device ID
        ↓
POST /api/payouts
The UI does not need to know how the device identifier is retrieved.
This responsibility remains within the data/domain layers.

Biometric Authentication
Biometric authentication is required for payouts above £1,000.00.
The API represents monetary values using the smallest monetary unit.

For GBP:

text
£1.00     = 100 pence
£1,000.00 = 100,000 pence
Therefore, the biometric requirement is applied when:

text
amount > 100000
The flow is:

text
Amount <= £1,000
        ↓
Payout request


Amount > £1,000
        ↓
Biometric authentication
        ↓
    ┌───┴───┐
    ↓       ↓
 Success   Failure
    ↓       ↓
 Continue  Abort
A failed biometric authentication prevents the payout request from being submitted.
If biometric authentication is unavailable or cannot be used, the payout is not executed.

Screenshot Protection
The payout screen contains sensitive financial information.
Screenshot protection is enabled while the user is within the payout flow.
The protection is scoped to the sensitive payout screen rather than being applied globally to the application.

The intended behaviour is:

text
Home
    ↓
Screenshots allowed

Payout
    ↓
Screenshots blocked

Back to Home
    ↓
Screenshots allowed
Architecture
The project follows a layered architecture:

text
┌────────────────────────────────────┐
│                 UI                 │
│                                    │
│  Compose Screens                   │
│  ViewModels                        │
│  UI State                          │
└──────────────────┬─────────────────┘
                   │
                   ▼
┌────────────────────────────────────┐
│               DOMAIN               │
│                                    │
│  Use Cases                         │
│  Domain Models                     │
│  Business Rules                    │
└──────────────────┬─────────────────┘
                   │
                   ▼
┌────────────────────────────────────┐
│                DATA                │
│                                    │
│  Repositories                      │
│  Remote API                        │
│  Room                              │
│  Paging RemoteMediator             │
│  Device Identity                   │
└────────────────────────────────────┘
UI Layer
The UI layer is responsible for:

Rendering UI state.

Handling user interaction.

Displaying loading states.

Displaying error states.

Navigation.

Compose-specific behaviour.

Examples include:

ui/home/

ui/transactions/

ui/payout/

ViewModels expose state and delegate business operations to use cases.

Domain Layer
The domain layer contains business operations and domain models.
Examples include:

GetMerchantUseCase

GetTransactionsUseCase

CreatePayoutUseCase

The domain layer is kept independent of Compose-specific implementation details.

Data Layer
The data layer is responsible for:

Network communication.

Persistence.

Paging.

Remote synchronization.

Device identity.

Mapping remote models to domain models.

Repositories provide the abstraction used by the domain layer.

Project Structure
The project is organised around the three main layers:

text
app/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com.example.androidinterview/
    │   │       ├── data/
    │   │       │   ├── local/
    │   │       │   ├── remote/
    │   │       │   └── repository/
    │   │       │
    │   │       ├── domain/
    │   │       │   ├── model/
    │   │       │   └── usecase/
    │   │       │
    │   │       └── ui/
    │   │           ├── home/
    │   │           ├── transactions/
    │   │           └── payout/
    │   │
    │   └── res/
    │
    └── test/
        └── java/
            └── com.example.androidinterview/
                ├── ui/
                │   ├── home/
                │   ├── transactions/
                │   └── payout/
                │
                └── domain/
Completed screenshots are stored in:

text
docs/
└── completed_screenshots/
    ├── home.png
    ├── transactions.png
    ├── transactions_loading.png
    ├── payout.png
    ├── payout_confirm.png
    ├── payout_confirmed.png
    ├── payout_failed.png
    ├── payout_insufficient_funds.png
    └── payout_biometric.png
Tech Stack
The application uses the following technologies.

Language
Kotlin

UI
Jetpack Compose

Material 3

Architecture
MVVM

Layered architecture

Repository pattern

Use case pattern

Android Architecture Components

ViewModel

StateFlow

Coroutines

Paging

Room

Networking
Retrofit

OkHttp

MockWebServer

Dependency Injection
Hilt

Testing
JUnit

MockK

kotlinx-coroutines-test

State Management
The UI uses explicit state representations instead of relying on multiple independent Boolean values.

For example, the Home screen uses:

kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val merchant: Merchant
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}
This makes the possible UI states explicit.
The general pattern is:

text
Loading
Success
Error
The same principle is applied to the payout flow and transaction loading states.

Data Layer
Remote API
The application communicates with the provided MockWebServer API.
The available endpoints include:

Endpoint	Method	Purpose
/api/merchant	GET	Retrieve merchant information
/api/merchant/activity	GET	Retrieve paginated activity
/api/payouts	POST	Create a payout
/api/payouts/:id	GET	Retrieve payout status
/api/devices	GET	Retrieve device identity
The MockWebServer is used as the backend during development and testing.

Room
Room is used as the local data source for the transaction list.
The transaction flow is:

text
API
 ↓
RemoteMediator
 ↓
Room
 ↓
PagingSource
 ↓
UI
This allows the Paging UI to consume database-backed data while the RemoteMediator synchronises remote pages.

Paging
The transaction API uses cursor-based pagination rather than page-number pagination.
The pagination request contains:

cursor

limit

The response contains:

items

next_cursor

has_more

The RemoteMediator stores the relevant cursor information and uses the last loaded item to determine the cursor for the next request.

For the forward-only API:

text
REFRESH
   ↓
APPEND
   ↓
APPEND
   ↓
endOfPaginationReached
PREPEND is not required because the API does not provide backwards pagination.

Payout Flow
The payout flow is implemented as a state-driven sequence.

text
┌───────────────┐
│ Payout Form   │
└───────┬───────┘
        │
        ▼
┌───────────────┐
│ Validation    │
└───────┬───────┘
        │
        ▼
┌───────────────┐
│ Confirmation  │
└───────┬───────┘
        │
        ▼
┌───────────────────┐
│ Security Checks   │
│                   │
│ Device ID         │
│ Biometric if      │
│ required          │
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│ POST /api/payouts │
└────────┬──────────┘
         │
      ┌──┴───┐
      ▼      ▼
   Success  Failure
This ensures that validation and security checks happen before the payout request is submitted.

Validation and Error Handling
The application handles validation and backend errors separately.

Form Validation
The payout form validates:

Amount is present.

Amount is valid.

IBAN is present.

IBAN is valid.

The confirm button is enabled only when:

text
amount is not blank
AND
IBAN is not blank
AND
amountError == null
AND
ibanError == null
Network Errors
Network failures are represented as an error state and are not allowed to crash the application.
The UI displays an appropriate failure state to the user.

HTTP Errors
The payout flow handles server responses such as:

400 Bad Request

503 Service Unavailable

The challenge's MockWebServer provides deterministic values for testing these scenarios.

Insufficient Funds
The insufficient funds scenario is represented separately from a generic server error.
The challenge provides a deterministic payout amount that triggers this response.

text
88888
The application handles this as an insufficient funds state.

Service Unavailable
The challenge provides another deterministic payout amount for testing the service unavailable response.

text
99999
This produces:

text
503 Service Unavailable
and the application displays the appropriate failure state.

Testing
Unit tests are located under:

text
app/src/test/
The tests focus on ViewModel and business behaviour and do not require an Android emulator.
The test stack includes:

JUnit

MockK

kotlinx-coroutines-test

HomeViewModel Tests
The HomeViewModelTest covers the following behaviours:

Initial State
The ViewModel starts in:

text
Loading
Successful Loading
When GetMerchantUseCase succeeds:

text
Loading
    ↓
Success(merchant)
Error Handling
When the use case throws an exception:

text
Loading
    ↓
Error(message)
Unknown Error
When an exception does not contain a message:

text
Error("Unknown error")
Reloading
The ViewModel can load merchant data again.
The test verifies that the use case is invoked again and that the resulting state is updated.

Example ViewModel Test
kotlin
@Test
fun `loadMerchant emits Success when use case succeeds`() = runTest {
    val merchant = merchant()

    coEvery {
        getMerchantUseCase()
    } returns merchant

    val viewModel = HomeViewModel(
        getMerchantUseCase = getMerchantUseCase
    )

    advanceUntilIdle()

    assertEquals(
        HomeUiState.Success(merchant),
        viewModel.uiState.value
    )

    coVerify(exactly = 1) {
        getMerchantUseCase()
    }
}
The test uses advanceUntilIdle() to allow the coroutine launched in viewModelScope to complete before asserting the final state.

Payout Tests
The payout logic can be tested independently from the Compose UI.
Important scenarios include:

Empty amount.

Invalid amount.

Valid amount.

Empty IBAN.

Invalid IBAN.

Valid IBAN.

Currency selection.

Payout confirmation.

Successful payout.

Insufficient funds.

Server unavailable.

Network failure.

Biometric requirement for amounts above £1,000.

Failed biometric authentication.

The focus is on verifying state transitions and business rules rather than testing Compose implementation details in JVM unit tests.

Test Location
All JVM unit tests are kept under:

text
src/test/
rather than src/androidTest/.
This keeps the business and ViewModel tests fast and independent from the Android emulator.

Completed Screenshots
The completed implementation screenshots are stored in:

text
docs/completed_screenshots/
The folder contains:

text
docs/
└── completed_screenshots/
    ├── home.png
    ├── transactions.png
    ├── transactions_loading.png
    ├── payout.png
    ├── payout_confirm.png
    ├── payout_confirmed.png
    ├── payout_failed.png
    ├── payout_insufficient_funds.png
    └── payout_biometric.png
These screenshots document the implemented application states.

Home
The Home screen displays the merchant balance, pending balance, currency, and recent activity.

Transactions
The Transactions screen displays the paginated transaction history grouped by date.

Transactions Loading
This screenshot demonstrates the loading state while additional transaction data is being requested.

Payout Form
The payout form allows the user to enter the payout amount, choose a currency, and provide the destination IBAN.

Payout Confirmation
The confirmation screen allows the user to review the payout information before the payout is submitted.

Payout Confirmed
This screen represents a successfully completed payout.

Payout Failed
This screen represents a payout failure caused by a server or network error.

Insufficient Funds
This screen represents the insufficient funds scenario.

Biometric Authentication
This screen demonstrates the biometric authentication step required for high-value payouts.

Running the Project
Requirements
The project requires:

Android Studio.

Android SDK.

A configured Android emulator or physical Android device.

A compatible JDK.

Build
Open the project in Android Studio and allow Gradle to synchronise.
The application can then be run using the standard Android Studio Run configuration.

Run
Open the project in Android Studio.

Sync the Gradle project.

Start an Android emulator or connect a physical device.

Run the application.

The provided MockWebServer starts with the application.
Navigate through the Home, Transactions, and Payout flows.

Testing the Main Flows
Home
Verify that:

The merchant balance is displayed.

The pending balance is displayed.

The currency is displayed.

Recent activity is displayed.

Loading state is displayed while data is loading.

Error state is displayed if loading fails.

Transactions
Verify that:

The first page of transactions loads.

Transactions are displayed in the correct order.

Transactions are grouped by date.

Scrolling triggers additional page loading.

A loading indicator appears while the next page is loading.

Pagination errors expose a retry action.

No additional pages are requested after the API reports that there is no more data.

Payout
Verify that:

An empty amount cannot be confirmed.

An invalid amount displays an error.

An empty IBAN cannot be confirmed.

An invalid IBAN displays an error.

A valid payout proceeds to confirmation.

A confirmed payout is submitted to the API.

A successful request displays the completed state.

A failed request displays the failed state.

The insufficient funds scenario is handled.

The service unavailable scenario is handled.

High-value payouts require biometric authentication.

Failed biometric authentication prevents the payout request.

Implementation Notes
Explicit UI State
The application uses explicit state objects rather than exposing implementation details directly to Compose.
This makes state transitions easier to reason about and easier to test.

ViewModel Responsibility
ViewModels coordinate UI actions with the domain layer.
For example:

text
Compose UI
    ↓
ViewModel
    ↓
Use Case
The ViewModel does not directly perform network calls.

Repository Responsibility
Repositories abstract data sources from the domain layer.
The repository determines whether data comes from:

Remote API.

Room.

Other data providers.

This keeps the domain layer independent from implementation details.

Paging Responsibility
The transaction list uses Paging with Room and a RemoteMediator.
The RemoteMediator is responsible for:

Loading remote pages.

Handling cursors.

Updating remote keys.

Inserting entities into Room.

Determining when pagination has completed.

The Compose UI only consumes the resulting PagingData.

Monetary Representation
Monetary amounts are represented using the smallest currency denomination expected by the API.
For example:

text
100000 = £1,000.00
This representation is used consistently when evaluating the biometric threshold and submitting payout requests.

Security
Security-related behaviour is kept within the payout flow.
The implementation includes:

Device identity.

Biometric authentication for high-value payouts.

Screenshot protection.

Validation before payout submission.

Conclusion
The Merchant Payout App implements the requested merchant dashboard and payout functionality using a modern Android architecture.

The implementation provides:

A Compose-based merchant dashboard.

Merchant balance and recent activity.

Cursor-based transaction pagination.

Room-backed Paging.

Payout form validation.

Payout confirmation.

Payout success and failure states.

Insufficient funds handling.

Device identity support.

Biometric authentication for payouts above £1,000.

Screenshot protection for sensitive payout screens.

Unit tests for ViewModel and business behaviour.

Completed screenshots documenting the implemented UI states.
