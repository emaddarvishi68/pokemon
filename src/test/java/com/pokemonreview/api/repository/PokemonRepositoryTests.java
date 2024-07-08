package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Pokemon;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PokemonRepositoryTests {

    @Autowired
    private PokemonRepository pokemonRepository;

    @Test
    public void save_validInput_returnCorrectResult() {
        // arrange
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();

        // act
        Pokemon savedPokemon = pokemonRepository.save(pokemon);

        //assert
        assertThat(savedPokemon).isNotNull();
        assertThat(savedPokemon.getId()).isGreaterThan(0);
    }

    @Test
    public void findAll_validInputAndSavePokemons_returnMoreThenOnePokemon() {
        Pokemon pokemon1 = Pokemon.builder()
                .name("pikachu1")
                .type("electric")
                .build();
        Pokemon pokemon2 = Pokemon.builder()
                .name("pikachu2")
                .type("electric")
                .build();

        pokemonRepository.save(pokemon1);
        pokemonRepository.save(pokemon2);

        List<Pokemon> pokemonList = pokemonRepository.findAll();
        assertThat(pokemonList).isNotNull();
        assertThat(pokemonList.size()).isEqualTo(2);
    }

    @Test
    public void findById_validInputAndSavePokemon_returnSavedPokemon() {
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();

        pokemonRepository.save(pokemon);
        Optional<Pokemon> byId = pokemonRepository.findById(pokemon.getId());

        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo(pokemon.getName());
        assertThat(byId.get().getType()).isEqualTo(pokemon.getType());
    }

    @Test
    public void findByType_validInputAndSavePokemon_returnSavedPokemonNotNull() {
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();

        pokemonRepository.save(pokemon);
        Optional<Pokemon> byId = pokemonRepository.findByType("electric");

        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo(pokemon.getName());
        assertThat(byId.get().getType()).isEqualTo(pokemon.getType());

        byId = pokemonRepository.findByType("hybrid");

        assertThat(byId).isNotPresent();
    }

    @Test
    public void update_validInputAndSavePokemon_returnUpdatedPokemonNotNull() {
        String nameBeforeUpdate = "pikachu";
        String typeBeforeUpdate = "electric";
        String nameAfterUpdate = "Raichu";
        String typeAfterUpdate = "Electric";
        Pokemon pokemon = Pokemon.builder()
                .name(nameBeforeUpdate)
                .type(typeBeforeUpdate)
                .build();

        pokemonRepository.save(pokemon);
        Optional<Pokemon> byId = pokemonRepository.findById(pokemon.getId());
        assertThat(byId).isPresent();

        byId.get().setName(nameAfterUpdate);
        byId.get().setType(typeAfterUpdate);

        Pokemon updatedPokemon = pokemonRepository.save(byId.get());
        assertThat(updatedPokemon).isNotNull();
        assertThat(updatedPokemon.getType()).isNotEqualTo(typeBeforeUpdate);
        assertThat(updatedPokemon.getName()).isNotEqualTo(nameBeforeUpdate);
    }

    @Test
    public void deleteById_validInputAndSavePokemon_returnIsNotPresent() {
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();

        pokemonRepository.save(pokemon);
        Optional<Pokemon> byId = pokemonRepository.findById(pokemon.getId());
        assertThat(byId).isPresent();

        pokemonRepository.deleteById(byId.get().getId());
        Optional<Pokemon> afterDelete = pokemonRepository.findById(pokemon.getId());
        assertThat(afterDelete).isNotPresent();
    }

}
