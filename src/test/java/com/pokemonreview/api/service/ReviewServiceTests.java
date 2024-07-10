package com.pokemonreview.api.service;

import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.exceptions.PokemonNotFoundException;
import com.pokemonreview.api.exceptions.ReviewNotFoundException;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.repository.PokemonRepository;
import com.pokemonreview.api.repository.ReviewRepository;
import com.pokemonreview.api.service.impl.ReviewServiceImpl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;


    private Pokemon pokemon;
    private PokemonDto pokemonDto;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    public void init() {
        pokemon = Pokemon.builder()
                .id(1)
                .name("pikachu")
                .type("electric")
                .build();

        pokemonDto = PokemonDto.builder()
                .name("pikachu")
                .type("electric")
                .build();

        review = Review.builder()
                .id(1)
                .title("title")
                .content("content")
                .stars(5)
                .pokemon(pokemon)
                .build();

        reviewDto = ReviewDto.builder()
                .title("tets title")
                .content("test content")
                .stars(5)
                .pokemonId(pokemon.getId())
                .build();
    }


    @Test
    public void createReview_validInput_returnReviewDto() {
        when(pokemonRepository.findById(pokemon.getId())).thenReturn(Optional.of(pokemon));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto savedReview = reviewService.createReview(pokemon.getId(), reviewDto);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getContent()).isEqualTo(review.getContent());
        assertThat(savedReview.getTitle()).isEqualTo(review.getTitle());
        assertThat(savedReview.getStars()).isEqualTo(review.getStars());
        assertThat(savedReview.getPokemonId()).isEqualTo(pokemon.getId());
    }

    @Test
    public void getReviewsByPokemonId_validInput_returnReviewDtoList() {
        when(reviewRepository.findByPokemonId(pokemon.getId())).thenReturn(Arrays.asList(review));

        List<ReviewDto> reviewList = reviewService.getReviewsByPokemonId(pokemon.getId());

        assertThat(reviewList).isNotNull();
        assertThat(reviewList.size()).isEqualTo(1);
    }

    @Test
    public void getReviewById_validInput_returnReviewDto() {
        when(pokemonRepository.findById(pokemon.getId())).thenReturn(Optional.of(pokemon));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        ReviewDto reviewById = reviewService.getReviewById(review.getId(), pokemon.getId());

        assertThat(reviewById).isNotNull();
        assertThat(reviewById.getContent()).isEqualTo(review.getContent());
        assertThat(reviewById.getStars()).isEqualTo(review.getStars());
        assertThat(reviewById.getTitle()).isEqualTo(review.getTitle());
    }

    @Test
    public void getReviewById_pokemonIdInvalidInput_throwsPokemonNotFoundException() {
        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(PokemonNotFoundException.class, () -> {
            reviewService.getReviewById(1, 2);
        });
        verify(pokemonRepository, times(1)).findById(anyInt());
        verify(reviewRepository, never()).findById(anyInt());
    }

    @Test
    public void getReviewById_reviewIdInvalidInput_throwsPokemonNotFoundException() {
        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.of(pokemon));
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getReviewById(2, 1);
        });
        verify(pokemonRepository, times(1)).findById(anyInt());
        verify(reviewRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getReviewById_invalidCheck_throwsPokemonNotFoundException() {
        Pokemon pokemon1 = Pokemon.builder()
                .id(2)
                .name("pikachu")
                .type("electric")
                .build();
        Review review1 = Review.builder()
                .id(1)
                .pokemon(pokemon)
                .stars(5)
                .content("new Content")
                .title("new Title")
                .build();
        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.of(pokemon1));
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.of(review1));

        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getReviewById(1, 1);
        });
        verify(pokemonRepository, times(1)).findById(anyInt());
        verify(reviewRepository, times(1)).findById(anyInt());
    }

    @Test
    public void updateReview_validInput_returnReviewDto() {
        pokemon.setReviews(Collections.singletonList(review));
        review.setPokemon(pokemon);

        when(pokemonRepository.findById(pokemon.getId())).thenReturn(Optional.of(pokemon));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewDto updatedReview = reviewService.updateReview(pokemon.getId(), review.getId(), reviewDto);

        assertThat(updatedReview).isNotNull();
        assertThat(updatedReview.getPokemonId()).isNotNull();
    }

    @Test
    public void deleteReview_validInput_returnVoid() {
        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.ofNullable(pokemon));
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.ofNullable(review));
        doNothing().when(reviewRepository).delete(review);

        reviewService.deleteReview(pokemon.getId(), review.getId());

        verify(pokemonRepository, times(1)).findById(anyInt());
        verify(reviewRepository, times(1)).findById(anyInt());
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void deleteReview_invalidInput_throwsException() {
        Pokemon pokemon1 = Pokemon.builder()
                .id(2)
                .name("pikachu")
                .type("electric")
                .build();
        Review review1 = Review.builder()
                .id(1)
                .pokemon(pokemon)
                .stars(5)
                .content("new Content")
                .title("new Title")
                .build();
        when(pokemonRepository.findById(anyInt())).thenReturn(Optional.ofNullable(pokemon1));
        when(reviewRepository.findById(anyInt())).thenReturn(Optional.ofNullable(review1));

        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(pokemon1.getId(), review1.getId());
        });

        verify(pokemonRepository, times(1)).findById(anyInt());
        verify(reviewRepository, times(1)).findById(anyInt());
        verify(reviewRepository, never()).delete(review);
    }

}
