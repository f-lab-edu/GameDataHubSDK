import com.github.javafaker.Faker
import data.DummyDataGenerator


fun main() {
	val faker = Faker()
	val generator = DummyDataGenerator(faker)

	val users = List(10) { generator.generateUser() }

	println("Generated Users:")
	users.forEach { println(it) }

	// 게임 세션 데이터 생성 및 출력
	println("\nGenerated Game Sessions:")
	users.forEach { user ->
		println(generator.generateGameSession(user.id))
	}

	// 업적 데이터 생성 및 출력
	println("\nGenerated Achievements:")
	repeat(10) {
		println(generator.generateAchievement())
	}

	// 게임 활동 데이터 생성 및 출력
	println("\nGenerated Game Activities:")
	generator.generateGameActivities(users, 20).forEach { println(it) }
}

