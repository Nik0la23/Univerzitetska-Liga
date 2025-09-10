package mk.ukim.finki.wp.liga.Shop;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidOrderException;
import mk.ukim.finki.wp.liga.model.shop.BasketballProduct;
import mk.ukim.finki.wp.liga.model.shop.FootballProduct;
import mk.ukim.finki.wp.liga.model.shop.Order;
import mk.ukim.finki.wp.liga.model.shop.ShoppingCart;
import mk.ukim.finki.wp.liga.repository.football.FootballProductRepository;
import mk.ukim.finki.wp.liga.repository.shop.OrderRepository;
import mk.ukim.finki.wp.liga.repository.shop.ShoppingCartRepository;
import mk.ukim.finki.wp.liga.service.shop.impl.OrderServiceImpl;
import mk.ukim.finki.wp.liga.service.shop.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTests {

    @Nested
    class ShoppingCartServiceTests {

        @Mock private ShoppingCartRepository shoppingCartRepository;
        @Mock private FootballProductRepository footballProductRepository;


        @InjectMocks
        private ShoppingCartServiceImpl shoppingCartService;

        private ShoppingCart cart;
        private FootballProduct footballProduct;

        @BeforeEach
        void setUp() {
            cart = new ShoppingCart();
            cart.setId(1L);
            cart.setItemsFootball(new ArrayList<>());
            cart.setItemsBasketball(new ArrayList<>());
            cart.setItemsVolleyball(new ArrayList<>());

            footballProduct = new FootballProduct();
            footballProduct.setId(1L);
            footballProduct.setPrice(50.0);
        }

        @Test
        void testGetShoppingCart_WhenCartExists() {
            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));
            ShoppingCart result = shoppingCartService.getShoppingCart();
            assertEquals(1L, result.getId());
            verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
        }

        @Test
        void testGetShoppingCart_WhenCartDoesNotExist_ShouldCreateNew() {
            when(shoppingCartRepository.findAll()).thenReturn(Collections.emptyList());
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(new ShoppingCart());

            shoppingCartService.getShoppingCart();

            verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        }

        @Test
        void testAddFootballProductToCart_AddNewProduct() {
            when(footballProductRepository.findById(1L)).thenReturn(Optional.of(footballProduct));
            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));

            shoppingCartService.addFootballProductToCart(1L, 2);

            assertEquals(1, cart.getItemsFootball().size());
            assertEquals(2, cart.getItemsFootball().get(0).getQuantity());
            verify(shoppingCartRepository).save(cart);
        }

        @Test
        void testAddFootballProductToCart_IncreaseQuantityOfExistingProduct() {
            footballProduct.setQuantity(1);
            cart.getItemsFootball().add(footballProduct);

            when(footballProductRepository.findById(1L)).thenReturn(Optional.of(footballProduct));
            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));

            shoppingCartService.addFootballProductToCart(1L, 2);

            assertEquals(1, cart.getItemsFootball().size()); // List size should not change
            assertEquals(3, cart.getItemsFootball().get(0).getQuantity()); // 1 + 2
            verify(shoppingCartRepository).save(cart);
        }

        // --- NEW TEST ---
        @Test
        void testRemoveFootballProductFromCart_DecreaseQuantity() {
            footballProduct.setQuantity(3);
            cart.getItemsFootball().add(footballProduct);

            when(footballProductRepository.findById(1L)).thenReturn(Optional.of(footballProduct));
            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));

            shoppingCartService.removeFootballProductFromCart(1L);

            assertEquals(1, cart.getItemsFootball().size()); // Still in cart
            assertEquals(2, cart.getItemsFootball().get(0).getQuantity()); // Quantity decreased
            verify(shoppingCartRepository).save(cart);
        }

        @Test
        void testRemoveFootballProductFromCart_WhenQuantityIsOne() {
            footballProduct.setQuantity(1);
            cart.getItemsFootball().add(footballProduct);

            when(footballProductRepository.findById(1L)).thenReturn(Optional.of(footballProduct));
            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));

            shoppingCartService.removeFootballProductFromCart(1L);

            assertTrue(cart.getItemsFootball().isEmpty());
            verify(shoppingCartRepository).save(cart);
        }

        // --- NEW TEST ---
        @Test
        void testClearCart() {
            cart.getItemsFootball().add(new FootballProduct());
            cart.getItemsBasketball().add(new BasketballProduct());

            when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(cart));

            shoppingCartService.clearCart();

            ArgumentCaptor<ShoppingCart> cartCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
            verify(shoppingCartRepository).save(cartCaptor.capture());

            assertTrue(cartCaptor.getValue().getItemsFootball().isEmpty());
            assertTrue(cartCaptor.getValue().getItemsBasketball().isEmpty());
        }
    }

    @Nested
    class OrderServiceTests {

        @Mock private OrderRepository orderRepository;

        @InjectMocks
        private OrderServiceImpl orderService;

        private ShoppingCart cart;
        private Order order;

        @BeforeEach
        void setUp() {
            cart = new ShoppingCart();
            order = new Order();
            order.setOrderId(1L);

            FootballProduct fp = new FootballProduct();
            fp.setPrice(50.0);
            fp.setQuantity(2); // Total: 100.0

            BasketballProduct bp = new BasketballProduct();
            bp.setPrice(60.0);
            bp.setQuantity(1); // Total: 60.0

            cart.setItemsFootball(List.of(fp));
            cart.setItemsBasketball(List.of(bp));
            cart.setItemsVolleyball(new ArrayList<>());
        }

        // --- NEW TEST ---
        @Test
        void testFindAllOrders() {
            when(orderRepository.findAll()).thenReturn(List.of(new Order(), new Order()));
            List<Order> orders = orderService.findAll();
            assertEquals(2, orders.size());
        }

        // --- NEW TEST ---
        @Test
        void testFindOrderById() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            Order result = orderService.findOrderById(1L);
            assertNotNull(result);
            assertEquals(1L, result.getOrderId());
        }

        // --- NEW TEST ---
        @Test
        void testFindOrderById_WhenNotFound_ShouldThrowException() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidOrderException.class, () -> orderService.findOrderById(99L));
        }

        @Test
        void testCalculateTotalPrice() {
            double totalPrice = orderService.calculateTotalPrice(cart);
            assertEquals(160.0, totalPrice);
        }

        @Test
        void testCreateOrderFromCart() {
            orderService.createOrderFromCart(cart);

            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());

            Order savedOrder = orderCaptor.getValue();
            assertNotNull(savedOrder);
            assertEquals(1, savedOrder.getOrderedFootballProducts().size());
            assertEquals(1, savedOrder.getOrderedBasketballProducts().size());
            assertEquals(160.0, savedOrder.getTotalPrice());
        }

        @Test
        void testMarkAsPaid() {
            order.setPaid(false);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.markAsPaid(1L);

            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());

            assertTrue(orderCaptor.getValue().isPaid());
        }
    }
}
