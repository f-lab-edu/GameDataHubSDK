package data

import java.time.LocalDateTime


data class User(
	val id: Int,
	val nickname: String,
	val level: Int,
	val experience: Int,
	val gender: String,
	val nationality: String,
	val loginHistory: List<LocalDateTime>,
	val playTime: Int,
	val friends: List<String>,
	val preferredGenres: List<String>,
	val virtualCurrencyBalance: Double,
	val deviceType: String,
	val os: String,
	val network: String,
	val socialMediaAccounts: List<String>
)

data class GameSession(
	val sessionId: String,
	val userId: Int,
	val startTime: Long,
	val endTime: Long,
	val score: Int
)

data class Achievement(
	val id: Int,
	val nickname: String,
	val description: String,
	val points: Int
)

data class GameActivity(
	val userId: Int,
	val activityType: String,
	val timestamp: String,
	val details: String
)
