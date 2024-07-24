# RiotAuth

RiotAuth is a Kotlin-based project designed to bypass Riot's Cloudflare block during authentication.<br>
Inspired by existing projects, this library provides Kotlin developers with the opportunity to easily authenticate Riot accounts.

#### Inspiration

* [python-riot-auth by floxay](https://github.com/floxay/python-riot-auth)
* [RiotCloudflareAuthFix by GhostRider584](https://github.com/GhostRider584/RiotCloudflareAuthFix)

#### Usage
```kotlin
val account: RiotAccount = RiotAuthenticator.authorize("username", "password")
println("Access Token: ${account.accessToken}")
```
