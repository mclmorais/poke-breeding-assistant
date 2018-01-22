package marcelo.breguenait.breedingassistant.data.internal

class InternalPokemon {

    val internalId: String

    val dateCreated: Long

    var valid = false

    var externalId: Int = 0

    var gender: Int = -1

    var IVs = IntArray(6)

    var natureId: Int = 0

    var abilitySlot: Int = 0

    constructor(externalId: Int, dateCreated: Long, internalId: String) {
        this.externalId = externalId
        this.dateCreated = dateCreated
        this.internalId = internalId
    }

    constructor(externalId: Int) {
        this.externalId = externalId
        this.dateCreated = 0
        this.internalId = ""
    }

    constructor() {
        this.externalId = -10000
        this.dateCreated = 0
        this.internalId = ""
    }

}
