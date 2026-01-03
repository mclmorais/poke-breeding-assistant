# Pokemon Breeding Assistant

An Android application designed to help Pokemon breeders select optimal breeding pairs to maximize their chances of obtaining Pokemon with higher Individual Values (IVs) and desired characteristics.

## Overview

This application assists Pokemon breeders by analyzing their Pokemon collection and suggesting the best breeding pairs based on their breeding goals. The app calculates the probability of obtaining offspring with specific IVs, natures, abilities, and other attributes, helping users make informed decisions about which Pokemon to breed together.

## Features

- **Pokemon Management**: Create and store Pokemon profiles with detailed information including IVs, natures, abilities, egg groups, and gender
- **Breeding Goals**: Set specific breeding goals for desired Pokemon characteristics
- **Breeding Assistant**: AI-powered assistant that analyzes your Pokemon collection and suggests optimal breeding pairs
- **Chance Calculation**: Calculates the probability of obtaining offspring with desired traits from specific breeding combinations
- **Direct Combinations**: Identifies breeding pairs that can directly produce the goal Pokemon
- **Improvement Paths**: Suggests intermediate breeding steps to improve Pokemon stats before attempting the final goal
- **Compatibility Checking**: Validates breeding compatibility based on egg groups and other breeding mechanics

## Technical Details

This is an Android application built with:
- **Language**: Kotlin (with some Java components)
- **Architecture**: MVP (Model-View-Presenter) pattern with dependency injection using Dagger 2
- **Minimum SDK**: Android API 21 (Lollipop)
- **Target SDK**: Android API 27 (Oreo)

### Key Components

- **Data Layer**: Manages both external Pokemon data (from JSON assets) and internal user-created Pokemon profiles
- **Logic Layer**: Contains breeding algorithms, chance calculators, and compatibility checkers
- **UI Layer**: Three main screens:
  - Goals screen for managing breeding objectives
  - Creation screen for adding new Pokemon to the collection
  - Assistant screen for viewing breeding recommendations

## Project Status

This is the first iteration of the project. The application provides core breeding assistance functionality, but may be subject to future improvements and feature additions.

## Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Build and run on an Android device or emulator with API level 21 or higher

## Data Sources

The application includes Pokemon data stored in JSON format:
- Pokedex information
- Pokemon stats
- Egg groups
- Abilities
- Natures
- Type information
- Learnsets
