package marcelo.breguenait.breedingassistant.logic

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon

class Couple(var related: InternalPokemon) {

    var compatible: InternalPokemon? = null

    constructor(related: InternalPokemon, compatible : InternalPokemon) : this(related) {
        this.compatible = compatible
    }

}
