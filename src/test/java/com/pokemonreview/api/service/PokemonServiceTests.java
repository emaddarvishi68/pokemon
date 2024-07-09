package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.PokemonResponse;
import com.pokemonreview.api.exceptions.PokemonNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.service.impl.PokemonServiceImpl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonServiceTests {

    @Mock
    private PokemonRepository pokemonRepository;

    @InjectMocks
    private PokemonServiceImpl pokemonService;

    @Test
    public void createPokemon_validInput_returnPokemonDto() {
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();
        PokemonDto pokemonDto = PokemonDto.builder()
                .name("pikachu")
                .type("electric")
                .build();

        when(pokemonRepository.save(any(Pokemon.class))).thenReturn(pokemon);

        PokemonDto savedPokemon = pokemonService.createPokemon(pokemonDto);

        assertThat(savedPokemon).isNotNull();
        assertThat(savedPokemon.getName()).isEqualTo(pokemon.getName());
        assertThat(savedPokemon.getType()).isEqualTo(pokemon.getType());
    }

    @Test
    public void getAllPokemon_validInput_returnsPokemonResponse() {
        Page<Pokemon> pokemonsReturn = mock(Page.class);

        when(pokemonRepository.findAll(any(Pageable.class))).thenReturn(pokemonsReturn);

        PokemonResponse savePokemon = pokemonService.getAllPokemon(1, 10);

        assertThat(savePokemon).isNotNull();
    }

    @Test
    public void getPokemonById_validInput_returnsPokemonDto() {
        Pokemon pokemon = Pokemon.builder()
                .name("pikachu")
                .type("electric")
                .build();

        when(pokemonRepository.findById(1)).thenReturn(Optional.ofNullable(pokemon));
        PokemonDto pokemonById = pokemonService.getPokemonById(1);

        assertThat(pokemonById).isNotNull();
        assertThat(pokemonById.getType()).isEqualTo(pokemon.getType());
        assertThat(pokemonById.getName()).isEqualTo(pokemon.getName());
        assertThat(pokemonById.getId()).isEqualTo(pokemon.getId());
    }

    @Test
    public void updatePokemon_validInput_returnPokemonDto() {
        Pokemon pokemon = Pokemon.builder()
                .id(1)
                .name("pikachu")
                .type("electric")
                .build();
        PokemonDto pokemonDto = PokemonDto.builder()
                .name("pikachu2")
                .type("electric2")
                .build();

        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.ofNullable(pokemon));
        when(pokemonRepository.save(any(Pokemon.class))).thenReturn(pokemon);

        PokemonDto updatedPokemon = pokemonService.updatePokemon(pokemonDto, 1);

        assertThat(updatedPokemon).isNotNull();
        assertThat(updatedPokemon.getId()).isEqualTo(1);
        assertThat(updatedPokemon.getType()).isNotEqualTo(pokemon.getName());
        assertThat(updatedPokemon.getName()).isNotEqualTo(pokemon.getType());

    }

    @Test
    public void deletePokemonId_validInput_returnEmpty() {
        Pokemon pokemon = Pokemon.builder()
                .id(1)
                .name("pikachu")
                .type("electric")
                .build();

        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.ofNullable(pokemon));
        doNothing().when(pokemonRepository).delete(pokemon);

        pokemonService.deletePokemonId(1);

        verify(pokemonRepository, times(1)).findById(pokemon.getId());
        verify(pokemonRepository, times(1)).delete(pokemon);
    }

    @Test
    public void deletePokemonId_validInput_returnNotFound() {
        Pokemon pokemon = Pokemon.builder()
                .id(1)
                .name("pikachu")
                .type("electric")
                .build();
        when(pokemonRepository.findById(pokemon.getId())).thenReturn(Optional.empty());

        assertThrows(PokemonNotFoundException.class, () -> pokemonService.deletePokemonId(pokemon.getId()));
        verify(pokemonRepository, times(1)).findById(pokemon.getId());
        verify(pokemonRepository, never()).delete(any());
    }

}
