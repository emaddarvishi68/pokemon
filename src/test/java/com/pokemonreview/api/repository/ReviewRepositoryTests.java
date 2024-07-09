package com.pokemonreview.api.repository;

import com.pokemonreview.api.models.Pokemon;

import static org.assertj.core.api.Assertions.*;

import com.pokemonreview.api.models.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void saveAll_validInput_returnSavedReview() {
        Review review1 = Review.builder()
                .title("title1")
                .content("content1")
                .stars(5)
                .build();
        Review review2 = Review.builder()
                .title("title2")
                .content("content2")
                .stars(5)
                .build();

        List<Review> savedReviews = reviewRepository.saveAll(Arrays.asList(review1, review2));

        assertThat(savedReviews).isNotNull();
        assertThat(savedReviews.size()).isEqualTo(2);
    }

    @Test
    public void findAll_validInput_returnAllReviews() {
        Review review1 = Review.builder()
                .title("title1")
                .content("content1")
                .stars(5)
                .build();
        Review review2 = Review.builder()
                .title("title2")
                .content("content2")
                .stars(5)
                .build();
        Review review3 = Review.builder()
                .title("title3")
                .content("content3")
                .stars(5)
                .build();

        reviewRepository.saveAll(Arrays.asList(review1, review2, review3));

        List<Review> reviewList = reviewRepository.findAll();

        assertThat(reviewList).isNotNull();
        assertThat(reviewList.size()).isEqualTo(3);
    }

    @Test
    public void findById_validInput_returnSavedReview() {
        Review review = Review.builder()
                .title("title")
                .content("content")
                .stars(5)
                .build();

        reviewRepository.save(review);

        assertThat(review).isNotNull();
        assertThat(review.getId()).isGreaterThan(0);

        Optional<Review> byId = reviewRepository.findById(review.getId());

        assertThat(byId).isPresent();
        assertThat(byId.get().getTitle()).isEqualTo(review.getTitle());
        assertThat(byId.get().getStars()).isEqualTo(review.getStars());
        assertThat(byId.get().getContent()).isEqualTo(review.getContent());
    }

    @Test
    public void updateReview_validInput_returnUpdatedReview() {
        String contentBeforeUpdate = "content1";
        String titleBeforeUpdate = "title1";
        int starsBeforeUpdate = 5;
        String contentAfterUpdate = "content2";
        String titleAfterUpdate = "title2";
        int starsAfterUpdate = 2;

        Review review = Review.builder()
                .stars(starsBeforeUpdate)
                .content(contentBeforeUpdate)
                .title(titleBeforeUpdate)
                .build();

        Review savedReview = reviewRepository.save(review);
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getContent()).isEqualTo(contentBeforeUpdate);
        assertThat(savedReview.getStars()).isEqualTo(starsBeforeUpdate);
        assertThat(savedReview.getTitle()).isEqualTo(titleBeforeUpdate);

        savedReview.setContent(contentAfterUpdate);
        savedReview.setStars(starsAfterUpdate);
        savedReview.setTitle(titleAfterUpdate);

        reviewRepository.save(savedReview);//update

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isEqualTo(review.getId());
        assertThat(savedReview.getStars()).isNotEqualTo(starsBeforeUpdate);
        assertThat(savedReview.getContent()).isNotEqualTo(contentBeforeUpdate);
        assertThat(savedReview.getTitle()).isNotEqualTo(titleBeforeUpdate);

    }

    @Test
    public void deleteById_validInput_returnEmptyReview(){
        Review review = Review.builder()
                .stars(5)
                .content("content")
                .title("title")
                .build();

        Review savedReview = reviewRepository.save(review);
        assertThat(savedReview).isNotNull();

        reviewRepository.deleteById(savedReview.getId());

        Optional<Review> byId = reviewRepository.findById(savedReview.getId());
        assertThat(byId).isNotPresent();
    }

}
