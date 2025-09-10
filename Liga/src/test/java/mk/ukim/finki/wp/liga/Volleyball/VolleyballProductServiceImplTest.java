package mk.ukim.finki.wp.liga.Volleyball;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballProductException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballProductException;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.shop.VolleyballProduct;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballProductRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.impl.VolleyballProductServiceImpl;
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
class VolleyballProductServiceImplTest {

    @Mock private VolleyballProductRepository volleyballProductRepository;
    @Mock private VolleyballTeamRepository volleyballTeamRepository;

    @InjectMocks
    private VolleyballProductServiceImpl volleyballProductService;

    private VolleyballTeam team;
    private VolleyballProduct product;

    @BeforeEach
    void setUp() {
        team = new VolleyballTeam();
        team.setVolleyball_team_id(1L);
        product = new VolleyballProduct("VakifBank Jersey", "Official team jersey", 85.00, "url/image.jpg", team);
        product.setId(1L);
    }

    @Test
    void testFindById_WhenProductExists() {
        when(volleyballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        VolleyballProduct result = volleyballProductService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_WhenProductDoesNotExist_ShouldThrowException() {
        when(volleyballProductRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidVolleyballProductException.class, () -> volleyballProductService.findById(99L));
    }

    @Test
    void testCreateNewFootballProduct_BuggyMethodNameButWorks() {
        // NOTE: This test covers a method named "createNewFootballProduct"
        // which incorrectly creates a VolleyballProduct. This test verifies the actual behavior.
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act: Call the misnamed method
        volleyballProductService.createNewFootballProduct("New Ball", "Official ball", 40.0, "url/ball.jpg", 1L);

        // Assert
        ArgumentCaptor<VolleyballProduct> productCaptor = ArgumentCaptor.forClass(VolleyballProduct.class);
        verify(volleyballProductRepository).save(productCaptor.capture());

        assertEquals("New Ball", productCaptor.getValue().getName());
        assertEquals(1L, productCaptor.getValue().getVolleyballMerch().getVolleyball_team_id());
    }

    @Test
    void testCreate_WhenTeamNotFound_ShouldThrowIncorrectException() {
        // NOTE: This test verifies that the service throws InvalidFootballProductException,
        // which is likely a copy-paste error. It should probably throw InvalidVolleyballProductException.
        when(volleyballTeamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InvalidFootballProductException.class, () -> volleyballProductService.createNewFootballProduct("New Ball", "desc", 40.0, "url", 99L));
    }

    @Test
    void testUpdate_Success() {
        when(volleyballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        volleyballProductService.update(1L, "Updated Jersey", "New Desc", 90.0, "new_url", 1L);

        ArgumentCaptor<VolleyballProduct> productCaptor = ArgumentCaptor.forClass(VolleyballProduct.class);
        verify(volleyballProductRepository).save(productCaptor.capture());

        assertEquals("Updated Jersey", productCaptor.getValue().getName());
        assertEquals(90.0, productCaptor.getValue().getPrice());
    }

    @Test
    void testFindByTeamId_UnusualBehavior() {
        // NOTE: The service implementation finds a product by its own ID, not products for a team ID.
        // This test verifies the code AS WRITTEN.
        when(volleyballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        List<VolleyballProduct> result = volleyballProductService.findByTeamId(1L);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }
}
