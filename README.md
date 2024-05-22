# Simple Daraja API implementation in Kotlin

A simple implementation of Daraja API in Kotlin

## Getting Started

Create an Mpesa Daraja Dev account [here](https://developer.safaricom.co.ke/)

Head to [my apps](https://developer.safaricom.co.ke/MyApps) and create a new app

Make sure you've clicked on both Lipa na M-Pesa Sandbox and M-Pesa Sandbox, write your App Name and click on CREATE APP.

Head over to [APIS](https://developer.safaricom.co.ke/APIs) and click on Simulate under M-Pesa Express

Select your app and all the keys and configurations needed will be there

## Input Credentials

Head over to build.gradle (app level) and head over to default config code part, and input your credentials to the corresponding name

### Example

> The following credentials are fake

buildConfigField("String", "MPESA_APP_KEY", "\"B9JoffzGqmjoofROxs2Irjr4caV95eOKWP9SF37A8iWwG\"")
buildConfigField("String", "MPESA_APP_SECRET", "\"LI36UvIdqSoLpRxUfQnYynlw0Zo75RqAgysCGb0lKpObBxISlcAk7TzLexGEG\"")
buildConfigField("String", "MPESA_SHORTCODE", "\"17379\"")
buildConfigField("String", "MPESA_PASSKEY", "\"bfb279f9aa9bdbcf158e97dd71a467cd2e0059b10f78e6b72ada1ed2c919\"")
buildConfigField("String", "MPESA_CALLBACK_URL", "\"https://mydomain.com/pat\"")
buildConfigField("String", "MPESA_ACCOUNT_REFERENCE", "\"Test\"")

### Sync Now

Remember to click on sync now after editing build.gradle (app level) file