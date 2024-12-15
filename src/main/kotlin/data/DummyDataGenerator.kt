package data

import com.github.javafaker.Faker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class DummyDataGenerator(
	private val faker: Faker
) {

	fun generateUser(): User {
		return User(
			id = faker.number().numberBetween(1, 100000),
			nickname = faker.name().fullName(),
			level = faker.number().numberBetween(1, 100),
			experience = faker.number().numberBetween(0, 10000),
			gender = faker.options().option("Male", "Female", "Other"),
			nationality = faker.country().name(),
			loginHistory = List(faker.number().numberBetween(1, 10)) {
				LocalDateTime.ofInstant(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant(), java.time.ZoneOffset.UTC)
			},
			playTime = faker.number().numberBetween(0, 10000),
			friends = List(faker.number().numberBetween(0, 50)) { faker.name().fullName() },
			preferredGenres = List(faker.number().numberBetween(1, 5)) { faker.lorem().word() },
			virtualCurrencyBalance = faker.number().randomDouble(2, 0, 1000),
			deviceType = faker.options().option("PC", "Console", "Mobile"),
			os = faker.options().option("Windows", "macOS", "Android", "iOS"),
			network = faker.options().option("WiFi", "4G", "5G", "Ethernet"),
			socialMediaAccounts = List(faker.number().numberBetween(0, 5)) { faker.name().username() }
		)
	}

	fun generateGameSession(userId: Int): GameSession {
		return GameSession(
			sessionId = faker.idNumber().valid(),
			userId = userId,
			startTime = System.currentTimeMillis(),
			endTime = System.currentTimeMillis() + faker.number().numberBetween(60000, 3600000),
			score = faker.number().numberBetween(0, 1000)
		)
	}

	fun generateAchievement(): Achievement {
		return Achievement(
			id = faker.number().numberBetween(1, 500),
			nickname = faker.lorem().words(3).joinToString(" "),
			description = faker.lorem().sentence(),
			points = faker.number().numberBetween(10, 100)
		)
	}

	fun generateGameActivities(users: List<User>, activityCount: Int): List<GameActivity> {
		val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
		val activityTypes =
			listOf("create_room", "play_game", "send_chat", "end_game", "enter_shop", "exit_shop", "watch_replay", "in_app_purchase")

		return (1..activityCount).map {
			val randomUser = users.random()
			GameActivity(
				userId = randomUser.id,
				activityType = activityTypes.random(),
				timestamp = LocalDateTime.now().minusMinutes(Random.nextLong(1, 1440)).format(formatter),
				details = when (activityTypes.random()) {
					"create_room" -> "Created room with ID: ${Random.nextInt(1000, 9999)}"
					"play_game" -> "Played game ID: ${Random.nextInt(1000, 9999)}"
					"send_chat" -> "Sent message: '${faker.lorem().sentence()}'"
					"end_game" -> "Ended game session ID: ${Random.nextInt(1000, 9999)}"
					"enter_shop" -> "Entered shop section: ${faker.commerce().department()}"
					"exit_shop" -> "Exited shop after browsing ${Random.nextInt(1, 10)} items"
					"watch_replay" -> "Watched replay of game ID: ${Random.nextInt(1000, 9999)}"
					"in_app_purchase" -> "Purchased item: ${faker.commerce().productName()} for \$${faker.commerce().price()}"
					else -> ""
				}
			)
		}
	}
}


