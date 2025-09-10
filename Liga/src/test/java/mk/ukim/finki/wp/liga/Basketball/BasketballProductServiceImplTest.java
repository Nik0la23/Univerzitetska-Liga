package mk.ukim.finki.wp.liga.Basketball;

import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballProductException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballProductException;
import mk.ukim.finki.wp.liga.model.shop.BasketballProduct;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballProductRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.service.basketball.impl.BasketballProductServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketballProductServiceImplTest {

    // --- Mocks for the dependencies ---
    @Mock
    private BasketballProductRepository basketballProductRepository;
    @Mock
    private BasketballTeamRepository basketballTeamRepository;

    // --- The service under test ---
    @InjectMocks
    private BasketballProductServiceImpl basketballProductService;

    // --- Common test data ---
    private BasketballTeam team;
    private BasketballProduct product;

    @BeforeEach
    void setUp() {
        team = new BasketballTeam();
        team.setId(1L);
        team.setTeamName("LA Lakers");

        product = new BasketballProduct("Lakers Jersey", "Official team jersey", 99.99, "url/to/image.jpg", team);
        product.setId(1L);
    }

    @Test
    void testFindByTeam() {
        when(basketballProductRepository.findByBasketballMerch(team)).thenReturn(Collections.singletonList(product));
        List<BasketballProduct> result = basketballProductService.findByTeam(team);
        assertEquals(1, result.size());
        assertEquals("Lakers Jersey", result.get(0).getName());
    }

    @Test
    void testFindByTeamName() {
        when(basketballProductRepository.findByBasketballMerch_TeamName("LA Lakers")).thenReturn(Collections.singletonList(product));
        List<BasketballProduct> result = basketballProductService.findByTeamName("LA Lakers");
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getBasketballMerch().getId());
    }

    @Test
    void testFindAll() {
        when(basketballProductRepository.findAll()).thenReturn(Collections.singletonList(product));
        List<BasketballProduct> result = basketballProductService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByTeamId_ShouldReturnProductById() {
        // NOTE: The implementation of findByTeamId seems incorrect in the service.
        // It calls repository.findById(teamId), which finds a PRODUCT by its ID, not a team's products.
        // This test verifies the code AS WRITTEN.
        when(basketballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        List<BasketballProduct> result = basketballProductService.findByTeamId(1L);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void testFindById_WhenProductExists() {
        when(basketballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        BasketballProduct result = basketballProductService.findById(1L);
        assertNotNull(result);
        assertEquals("Lakers Jersey", result.getName());
    }

    @Test
    void testFindById_WhenProductDoesNotExist_ShouldThrowException() {
        when(basketballProductRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidBasketballProductException.class, () -> basketballProductService.findById(99L));
    }

    @Test
    void testDeleteById() {
        // Act: Call the delete method.
        basketballProductService.deleteById(1L);
        // Assert: Verify that the repository's deleteById method was called with the correct ID.
        verify(basketballProductRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreateNewBasketballProduct_WhenTeamExists() {
        // Arrange
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act
        basketballProductService.createNewBasketballProduct("New Hat", "A new team hat", 25.00, "url/hat.jpg", 1L);

        // Assert: Use an ArgumentCaptor to capture the object passed to the save method.
        ArgumentCaptor<BasketballProduct> productCaptor = ArgumentCaptor.forClass(BasketballProduct.class);
        verify(basketballProductRepository, times(1)).save(productCaptor.capture());

        BasketballProduct savedProduct = productCaptor.getValue();
        assertEquals("New Hat", savedProduct.getName());
        assertEquals(25.00, savedProduct.getPrice());
        assertEquals(team, savedProduct.getBasketballMerch());
    }

    @Test
    void testCreateNewBasketballProduct_WhenTeamDoesNotExist_ShouldThrowException() {
        // Arrange
        when(basketballTeamRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        // NOTE: The service throws InvalidFootballProductException here, which is likely a copy-paste error.
        // This test verifies the code AS WRITTEN.
        assertThrows(InvalidFootballProductException.class, () -> basketballProductService.createNewBasketballProduct("New Hat", "Desc", 25.00, "url", 99L));
    }

    @Test
    void testUpdate_WhenProductAndTeamExist() {
        // Arrange
        when(basketballProductRepository.findById(1L)).thenReturn(Optional.of(product));
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act
        basketballProductService.update(1L, "Updated Jersey", "New description", 119.99, "new/url.jpg", 1L);

        // Assert: Capture the product passed to the save method to check its properties.
        ArgumentCaptor<BasketballProduct> productCaptor = ArgumentCaptor.forClass(BasketballProduct.class);
        verify(basketballProductRepository, times(1)).save(productCaptor.capture());

        BasketballProduct updatedProduct = productCaptor.getValue();
        assertEquals("Updated Jersey", updatedProduct.getName());
        assertEquals(119.99, updatedProduct.getPrice());
        assertEquals("New description", updatedProduct.getDescription());
    }

    @Test
    void testUpdate_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(basketballProductRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidBasketballProductException.class, () -> basketballProductService.update(99L, "Name", "Desc", 10.0, "url", 1L));
    }
}
