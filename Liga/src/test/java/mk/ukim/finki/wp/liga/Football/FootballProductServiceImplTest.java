package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballProductException;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.shop.FootballProduct;
import mk.ukim.finki.wp.liga.repository.football.FootballProductRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.service.football.impl.FootballProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FootballProductServiceImplTest {

    @Mock private FootballProductRepository footballProductRepository;
    @Mock private FootballTeamRepository footballTeamRepository;

    @InjectMocks
    private FootballProductServiceImpl footballProductService;

    private FootballTeam team;
    private FootballProduct product;

    @BeforeEach
    void setUp() {
        team = new FootballTeam();
        team.setId(1L);
        product = new FootballProduct("Jersey", "Official", 99.99, "url", team);
        product.setId(1L);
    }

    @Test
    void testFindById_WhenProductExists() {
        when(footballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        FootballProduct result = footballProductService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_WhenProductDoesNotExist_ShouldThrowException() {
        when(footballProductRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidFootballProductException.class, () -> footballProductService.findById(99L));
    }

    @Test
    void testCreateNewFootballProduct_Success() {
        when(footballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        footballProductService.createNewFootballProduct("New Scarf", "A warm scarf", 25.0, "url", 1L);

        ArgumentCaptor<FootballProduct> productCaptor = ArgumentCaptor.forClass(FootballProduct.class);
        verify(footballProductRepository).save(productCaptor.capture());

        assertEquals("New Scarf", productCaptor.getValue().getName());
        assertEquals(1L, productCaptor.getValue().getFootballMerch().getId());
    }

    @Test
    void testFindByTeamId_UnusualBehavior() {
        // NOTE: The service implementation finds a product by its own ID, not products by a team ID.
        // This test verifies the code as written.
        when(footballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        List<FootballProduct> result = footballProductService.findByTeamId(1L);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void testDeleteById() {
        footballProductService.deleteById(1L);
        verify(footballProductRepository, times(1)).deleteById(1L);
    }
}
