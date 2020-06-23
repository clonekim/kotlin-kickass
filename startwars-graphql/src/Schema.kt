package startwars

import com.apurebase.kgraphql.KGraphQL

val luke = Human("2000", "Luke Skywalker", emptyList(), Episode.values().toSet(), "Tatooine", 1.72)
val r2d2 = Droid("2001", "R2-D2", emptyList(), Episode.values().toSet(), "Astromech")

val schema = KGraphQL.schema {

    configure {
        useDefaultPrettyPrinter = true
    }

    // create query "hero" which returns instance of Character
    query("hero") {
        resolver { episode: Episode ->
            when (episode) {
                Episode.NEWHOPE, Episode.JEDI -> r2d2
                Episode.EMPIRE -> luke
            }
        }
    }

    // create query "heroes" which returns list of luke and r2d2
    query("heroes") {
        resolver{ -> listOf(luke, r2d2) }
    }

    mutation("createHero"){
        description = "Creates hero with specified name"
        resolver { name : String -> name }
    }

    // 1kotlin classes need to be registered with "type" method
    // to be included in created schema type system
    // class Character is automatically included,
    // as it is return type of both created queries
    type<Droid>()
    type<Human>()
    enum<Episode>()

}