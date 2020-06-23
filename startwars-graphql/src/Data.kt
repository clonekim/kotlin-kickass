package startwars

enum class Episode {
    NEWHOPE, EMPIRE, JEDI
}

interface Character {
    val id : String
    val name : String?
    val friends: List<Character>
    val appearsIn: Set<Episode>
}

data class Human (
    override val id: String,
    override val name: String?,
    override val friends: List<Character>,
    override val appearsIn: Set<Episode>,
    val homePlanet: String,
    val height: Double
) : Character

data class Droid (
    override val id: String,
    override val name: String?,
    override val friends: List<Character>,
    override val appearsIn: Set<Episode>,
    val primaryFunction : String
) : Character