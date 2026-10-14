package com.app.restaurante.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as anotações de validação do DTO chamando o Validator direto
 * (sem Spring, sem Mockito).
 */
class PratoCreateRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ---------- helpers ----------

    private PratoCreateRequestDTO dtoValido() {
        PratoCreateRequestDTO dto = new PratoCreateRequestDTO();
        dto.setName("Pizza Margherita");
        dto.setDescription("Massa fina, molho de tomate, mussarela e manjericão");
        dto.setPrice(new BigDecimal("45.90"));
        dto.setCategory("Pizzas");
        dto.setCategoryId(1);
        dto.setAvailable(true);
        dto.setImageUrl("http://example.com/pizza.png");
        return dto;
    }

    private Set<ConstraintViolation<PratoCreateRequestDTO>> validar(PratoCreateRequestDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolacaoUnica(PratoCreateRequestDTO dto, String campo, String mensagem) {
        Set<ConstraintViolation<PratoCreateRequestDTO>> violacoes = validar(dto);

        assertEquals(1, violacoes.size(), "esperava exatamente 1 violação, vieram: " + violacoes);
        ConstraintViolation<PratoCreateRequestDTO> v = violacoes.iterator().next();
        assertEquals(campo, v.getPropertyPath().toString());
        assertEquals(mensagem, v.getMessage());
    }

    // =====================================================
    // DTO como um todo
    // =====================================================

    @Test
    @DisplayName("DTO válido não deve ter violações")
    void dtoValido_semViolacoes() {
        assertTrue(validar(dtoValido()).isEmpty());
    }

    @Test
    @DisplayName("DTO vazio (como o Jackson cria antes de preencher) deve violar os 4 campos obrigatórios")
    void dtoVazio_violaTodosOsCampos() {
        Set<String> camposComErro = validar(new PratoCreateRequestDTO()).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("name", "price", "category", "categoryId"), camposComErro);
    }

    @Test
    @DisplayName("deve reportar vários campos inválidos ao mesmo tempo")
    void variosCamposInvalidos() {
        PratoCreateRequestDTO dto = dtoValido();
        dto.setName(null);
        dto.setPrice(BigDecimal.ZERO);
        dto.setCategoryId(null);

        assertEquals(3, validar(dto).size());
    }

    // =====================================================
    // name
    // =====================================================
    @Nested
    @DisplayName("name")
    class Name {

        @Test
        @DisplayName("name nulo deve gerar violação")
        void nameNulo() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setName(null);

            assertViolacaoUnica(dto, "name", "O nome do prato é obrigatório");
        }

        @ParameterizedTest(name = "name = [{0}]")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("name vazio ou só com espaços deve ser inválido")
        void nameEmBranco_invalido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setName(valor);

            assertFalse(validar(dto).isEmpty());
        }

        @ParameterizedTest(name = "name = [{0}]")
        @ValueSource(strings = {"A", "Pizza", "Pizza Margherita", "Açaí & Cia"})
        @DisplayName("name com conteúdo é válido")
        void nameComConteudo_valido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setName(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // price
    // =====================================================
    @Nested
    @DisplayName("price")
    class Price {

        @Test
        @DisplayName("price nulo deve gerar uma única violação (@NotNull), sem duplicar com @Positive")
        void priceNulo() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setPrice(null);

            assertViolacaoUnica(dto, "price", "O preço é obrigatório");
        }

        @ParameterizedTest(name = "price = {0}")
        @ValueSource(strings = {"0", "0.00", "-0.01", "-1", "-100.50"})
        @DisplayName("price zero ou negativo deve gerar violação")
        void priceInvalido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setPrice(new BigDecimal(valor));

            assertViolacaoUnica(dto, "price", "O preço deve ser maior que zero");
        }

        @ParameterizedTest(name = "price = {0}")
        @ValueSource(strings = {"0.01", "1", "45.90", "999999.99"})
        @DisplayName("price positivo deve ser válido")
        void priceValido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setPrice(new BigDecimal(valor));

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // category
    // =====================================================
    @Nested
    @DisplayName("category")
    class Category {

        @Test
        @DisplayName("category nulo deve gerar violação")
        void categoryNulo() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setCategory(null);

            assertViolacaoUnica(dto, "category", "A categoria é obrigatória");
        }

        @ParameterizedTest(name = "category = [{0}]")
        @ValueSource(strings = {"", " ", "   ", "\t"})
        @DisplayName("category vazio ou só com espaços deve ser inválido")
        void categoryEmBranco_invalido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setCategory(valor);

            assertFalse(validar(dto).isEmpty());
        }

        @ParameterizedTest(name = "category = [{0}]")
        @ValueSource(strings = {"Pizzas", "Bebidas", "Sobremesas e doces"})
        @DisplayName("category com conteúdo é válido")
        void categoryComConteudo_valido(String valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setCategory(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // categoryId
    // =====================================================
    @Nested
    @DisplayName("categoryId")
    class CategoryId {

        @Test
        @DisplayName("categoryId nulo deve gerar violação")
        void categoryIdNulo() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setCategoryId(null);

            assertViolacaoUnica(dto, "categoryId", "O ID da categoria é obrigatório");
        }

        /*
         * COMPORTAMENTO ATUAL: categoryId só tem @NotNull, sem @Positive.
         * Zero e negativos passam na validação do DTO e só falham no service
         * ao buscar a categoria no banco.
         */
        @ParameterizedTest(name = "categoryId = {0}")
        @ValueSource(ints = {0, -1, -999})
        @DisplayName("hoje categoryId zero ou negativo passa na validação do DTO (só há @NotNull)")
        void categoryIdNaoPositivo_hojePassa(int valor) {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setCategoryId(valor);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // description e imageUrl (opcionais)
    // =====================================================
    @Nested
    @DisplayName("campos opcionais")
    class CamposOpcionais {

        @Test
        @DisplayName("description nula deve ser válida")
        void descriptionNula_valida() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setDescription(null);

            assertTrue(validar(dto).isEmpty());
        }

        @Test
        @DisplayName("imageUrl nula deve ser válida")
        void imageUrlNula_valida() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setImageUrl(null);

            assertTrue(validar(dto).isEmpty());
        }
    }

    // =====================================================
    // available
    // =====================================================
    @Nested
    @DisplayName("available")
    class Available {

        @Test
        @DisplayName("available padrão deve ser true")
        void availablePadraoTrue() {
            PratoCreateRequestDTO dto = new PratoCreateRequestDTO();

            assertTrue(dto.isAvailable());
        }

        @Test
        @DisplayName("available false deve ser válido")
        void availableFalse_valido() {
            PratoCreateRequestDTO dto = dtoValido();
            dto.setAvailable(false);

            assertTrue(validar(dto).isEmpty());
        }
    }
}
