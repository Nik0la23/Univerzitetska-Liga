package mk.ukim.finki.wp.liga.News;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidNewsException;
import mk.ukim.finki.wp.liga.model.News;
import mk.ukim.finki.wp.liga.model.Sport;
import mk.ukim.finki.wp.liga.repository.NewsRepository;
import mk.ukim.finki.wp.liga.service.news.Impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    // --- Mock the repository dependency ---
    @Mock
    private NewsRepository newsRepository;

    // --- The service under test ---
    @InjectMocks
    private NewsServiceImpl newsService;

    // --- Common test data ---
    private News news;

    @BeforeEach
    void setUp() {
        news = new News("Test Title", Sport.FOOTBALL, "Test content here.");
        news.setNews_id(1L);
    }

    @Test
    void testListAllNews() {
        // Arrange
        when(newsRepository.findAll()).thenReturn(Collections.singletonList(news));

        // Act
        List<News> result = newsService.listAllNews();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    void testFindById_WhenNewsExists() {
        // Arrange
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));

        // Act
        News result = newsService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getNews_id());
    }

    @Test
    void testFindById_WhenNewsDoesNotExist_ShouldThrowException() {
        // Arrange
        when(newsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidNewsException.class, () -> newsService.findById(99L));
    }

    @Test
    void testFindBySport() {
        // Arrange
        when(newsRepository.findBySport(Sport.FOOTBALL)).thenReturn(Collections.singletonList(news));

        // Act
        List<News> result = newsService.findBySport("FOOTBALL");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Sport.FOOTBALL, result.get(0).getSport());
    }

    @Test
    void testCreate() {
        // Arrange
        when(newsRepository.save(any(News.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        News result = newsService.create("New Title", "BASKETBALL", "New content");

        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals(Sport.BASKETBALL, result.getSport());

        // Use ArgumentCaptor to verify the exact object passed to the save method
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).save(newsCaptor.capture());
        assertEquals("New content", newsCaptor.getValue().getContent());
    }

    @Test
    void testUpdate() {
        // Arrange
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));
        when(newsRepository.save(any(News.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        News result = newsService.update(1L, "Updated Title", "VOLLEYBALL", "Updated content");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getNews_id());
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Sport.VOLLEYBALL, result.getSport());
        assertEquals("Updated content", result.getContent());
        verify(newsRepository, times(1)).save(news);
    }

    @Test
    void testDelete() {
        // Arrange
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));
        // doNothing() is used to mock void methods
        doNothing().when(newsRepository).delete(news);

        // Act
        News result = newsService.delete(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getNews_id());
        // Verify that the repository's delete method was called exactly once with our news object
        verify(newsRepository, times(1)).delete(news);
    }
}
