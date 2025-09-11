# 🏆 Универзитетска Лига - Спортска Лига Менаџмент Систем

## 📋 Опис на Проектот

**Универзитетската Лига** е целосна веб-апликација за менаџмент на спортски лиги кој опфаќа три спортови: **Фудбал**, **Кошарка** и **Одбојка**. Апликацијата е изградена со Spring Boot и нуди комплетна функционалност за администрирање на тимови, играчи, натпревари, вести, продавница и фантази лиги.

## 🎯 Клучни Функционалности

### ⚽ Фудбал Лига
- **Тимови**: 8 факултетски тимови со логоа и детални информации
- **Играчи**: 11 играчи по тим со позиции (GK, DEF, MID, FWD, SUB)
- **Натпревари**: Лигашки натпревари со полувременски резултати
- **Статистики**: Автоматско пресметување на табелата, постигнати/примени голови, поени
- **Playoffs**: Турнирски систем со четвртфинале, полуфинале и финале

### 🏀 Кошарка Лига
- **Тимови**: 8 факултетски тимови со логоа
- **Играчи**: 11 играчи по тим со позиции (PG, SG, SF, PF, C, SUB)
- **Натпревари**: Лигашки натпревари со четвртински резултати
- **Статистики**: Поени, асистенции, скокови по играч

### 🏐 Одбојка Лига
- **Тимови**: 8 факултетски тимови со логоа
- **Играчи**: 11 играчи по тим со позиции (S, OH, MB, OPP, L, DS)
- **Натпревари**: Лигашки натпревари со сетови (3-5 сета)
- **Статистики**: Постигнати поени, асистенции, сервирања, блокови

### 📰 Вести Систем
- Спортски вести за секој спорт
- Админ функционалност за додавање и уредување на вести
- Категоризирани вести по спорт

### 🛒 Онлајн Продавница
- **Фудбал**: Дресови, шалови, капи, шолји
- **Кошарка**: Дресови, шорцеви, топки, нараквици
- **Одбојка**: Дресови, шорцеви, топки, коленки
- Shopping cart функционалност
- Order менаџмент систем

### 🎮 Фантази Лиги
- **Фудбал Фантази**: 4-4-2 формација со буџет од $100M
- **Кошарка Фантази**: 1-2-2 формација
- **Одбојка Фантази**: 6-2 формација
- Динамички цени на играчите базирани на статистики
- Тим менаџмент со купување/продавање играчи

## 🏗️ Технолошка Архитектура

### Backend Технологии
- **Spring Boot 3.2.5** - Главен фрејмворк
- **Spring Data JPA** - ORM за база на податоци
- **H2 Database** - In-memory база за развој
- **PostgreSQL** - Production база (конфигурирана)
- **Thymeleaf** - Server-side template engine
- **Lombok** - Code generation
- **Spring Security Crypto** - Password hashing

### Frontend Технологии
- **Bootstrap 4.0.0** - CSS фрејмворк
- **Custom Premium Design System** - Сопствен CSS систем
- **jQuery 3.2.1** - JavaScript библиотека
- **Thymeleaf** - Template engine за динамички садржај

### База на Податоци
- **H2 In-Memory Database** (развој)
- **PostgreSQL** (production)
- **JPA/Hibernate** за ORM мапирање

## 📁 Проектна Структура

```
Liga/
├── src/main/java/mk/ukim/finki/wp/liga/
│   ├── config/                    # Конфигурациски класи
│   │   ├── DatabaseSeeder.java    # Автоматско полнење на базата
│   │   └── StaticResourceConfig.java
│   ├── model/                     # JPA ентитети
│   │   ├── football/              # Фудбал модели
│   │   ├── basketball/            # Кошарка модели
│   │   ├── volleyball/            # Одбојка модели
│   │   ├── shop/                  # Продавница модели
│   │   ├── fantasy/               # Фантази модели
│   │   └── Exceptions/            # Custom исклучоци
│   ├── repository/                # JPA репозиториуми
│   │   ├── football/
│   │   ├── basketball/
│   │   ├── volleyball/
│   │   ├── shop/
│   │   └── fantasy/
│   ├── service/                   # Бизнис логика
│   │   ├── football/
│   │   ├── basketball/
│   │   ├── volleyball/
│   │   ├── shop/
│   │   └── fantasy/
│   └── web/                       # Web контролери
│       ├── football/
│       ├── basketball/
│       ├── volleyball/
│       ├── shop/
│       └── fantasy/
├── src/main/resources/
│   ├── templates/                 # Thymeleaf шаблони
│   │   ├── football/              # Фудбал страници
│   │   ├── basketball/            # Кошарка страници
│   │   ├── volleyball/            # Одбојка страници
│   │   ├── shop/                  # Продавница страници
│   │   ├── fantasy/               # Фантази страници
│   │   └── fragments/             # Заеднички фрагменти
│   ├── static/css/
│   │   └── premium-design-system.css
│   └── application.properties
└── src/test/java/                 # Unit тестови
    └── mk/ukim/finki/wp/liga/
        ├── Football/
        ├── Basketball/
        ├── Volleyball/
        ├── Shop/
        └── Fantasy/
```

## 🚀 Инсталација и Покретување

### Предуслови
- Java 17 или повисоко
- Maven 3.6 или повисоко
- (Опционално) PostgreSQL за production

### Локално Покретување

1. **Клонирај го репозиториумот**
```bash
git clone <repository-url>
cd Univerzitetska-Liga/Liga
```

2. **Покрени ја апликацијата**
```bash
./mvnw spring-boot:run
```

3. **Пристапи до апликацијата**
- Главна страница: http://localhost:9090
- H2 Console: http://localhost:9090/h2-console
  - JDBC URL: `jdbc:h2:mem:liga_db`
  - Username: `sa`
  - Password: (празно)

### Docker Покретување

```bash
cd Liga
docker-compose up -d
```

## 👥 Кориснички Акаунти

Апликацијата се иницијализира со следниве тест корисници:

| Корисничко Име | Email | Лозинка | Улога |
|----------------|-------|---------|-------|
| admin | admin@liga.com | admin123 | Администратор |
| user1 | user1@liga.com | password123 | Корисник |
| user2 | user2@liga.com | password123 | Корисник |
| coach1 | coach1@liga.com | password123 | Корисник |
| player1 | player1@liga.com | password123 | Корисник |

## 🎮 Функционални Детали

### Фудбал Лига
- **8 тимови**: Пумбарски, Медицински, Технички, Економски, Филолошки, Правен, Педагошки, Факултет за информатички науки
- **11 играчи по тим** со реалистични статистики
- **Лигашки систем** со 3 поени за победа, 1 за нерешено
- **Playoff турнир** со 8 тимови
- **Детални статистики** по играч и тим

### Кошарка Лига
- **8 тимови** со истите факултети
- **11 играчи по тим** со позиции: PG, SG, SF, PF, C
- **Четвртински резултати** за секој натпревар
- **Статистики**: поени, асистенции, скокови

### Одбојка Лига
- **8 тимови** со истите факултети
- **11 играчи по тим** со позиции: S, OH, MB, OPP, L, DS
- **Сетови систем** (3-5 сета по натпревар)
- **Статистики**: поени, асистенции, сервирања, блокови

### Онлајн Продавница
- **Продукти по спорт**: Фудбал, Кошарка, Одбојка
- **Типови производи**: Дресови, опрема, аксесоари
- **Shopping Cart**: Додавање/отстранување производи
- **Order менаџмент**: Создавање и следење на нарачки

### Фантази Лиги
- **Фудбал**: 4-4-2 формација, $100M буџет
- **Кошарка**: 1-2-2 формација
- **Одбојка**: 6-2 формација
- **Динамички цени**: Цените се менуваат според статистиките
- **Тим менаџмент**: Купување, продавање, доделување позиции

## 🎨 UI/UX Дизајн

### Premium Design System
- **Модерен дизајн** со премиум изглед
- **Responsive дизајн** за сите уреди
- **Консистентна палета** на бои
- **Интуитивна навигација** со sidebar и header
- **Интерактивни елементи** со hover ефекти

### Клучни Страници
- **Главна страница**: Избор на спорт
- **Тимови**: Листа и детали за тимови
- **Играчи**: Листа и детали за играчи
- **Натпревари**: Fixtures, Results, Live
- **Табела**: Standings со статистики
- **Playoffs**: Турнирски bracket
- **Вести**: Спортски вести
- **Продавница**: Онлајн shopping
- **Фантази**: Fantasy team менаџмент

## 🧪 Тестирање

Проектот содржи комплетни unit тестови за сите сервиси:

- **Football тестови**: 6 тест класи
- **Basketball тестови**: 4 тест класи  
- **Volleyball тестови**: 5 тест класи
- **Shop тестови**: 1 тест класа
- **Fantasy тестови**: 1 тест класа
- **News тестови**: 1 тест класа
- **User тестови**: 1 тест класа

### Покретување на тестовите
```bash
./mvnw test
```

## 📊 База на Податоци

### Автоматско Полнење (DatabaseSeeder)
Апликацијата автоматски ги создава следниве податоци при стартување:

- **8 тимови** по спорт (32 вкупно)
- **88 играчи** по спорт (352 вкупно)
- **Натпревари** со реалистични резултати
- **Вести** за секој спорт
- **Продукти** во продавницата
- **Фантази тимови** за тест корисници

### Клучни Табели
- `users` - Корисници и администратори
- `football_team`, `basketball_team`, `volleyball_team` - Тимови
- `football_player`, `basketball_player`, `volleyball_player` - Играчи
- `football_match`, `basketball_match`, `volleyball_match` - Натпревари
- `news` - Вести
- `football_product`, `basketball_product`, `volleyball_product` - Продукти
- `fantasy_team`, `fantasy_player` - Фантази тимови
- `shopping_cart`, `order` - Shopping функционалност

## 🔧 Конфигурација

### application.properties
```properties
# H2 Database (развој)
spring.datasource.url=jdbc:h2:mem:liga_db
spring.datasource.username=sa
spring.datasource.password=

# Server
server.port=9090

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# File Upload
spring.servlet.multipart.max-file-size=10MB
```

### PostgreSQL (production)
```properties
# PostgreSQL (за production)
spring.datasource.url=jdbc:postgresql://localhost:5432/Liga
spring.datasource.username=liga_user
spring.datasource.password=admin




### Production
1. Конфигурирај PostgreSQL база
2. Промени `application.properties` за production
3. Build на JAR фајл: `./mvnw clean package`
4. Покрени: `java -jar target/Liga-0.0.1-SNAPSHOT.jar`

---

## 📸 Скриншоти од Апликацијата

![Matches](https://github.com/user-attachments/assets/0b9da299-6073-4ef3-b7f1-4c3ea05d3fdb)
![Players](https://github.com/user-attachments/assets/ad7a8ce8-0df6-445f-a5a2-0775f8693404)
![Teams](https://github.com/user-attachments/assets/dd1a68da-1a1e-4c1b-a760-e3593c307a61)
![Add_Player](https://github.com/user-attachments/assets/c700c14a-1644-40b9-af97-752e555f8919)
![Edit_Player](https://github.com/user-attachments/assets/b2cca764-923f-4c7f-945b-4f4fd0a0f15e)
![Player_Details](https://github.com/user-attachments/assets/8fb4aa40-033d-4b03-ae97-c580efbdad97)
![Add_Team](https://github.com/user-attachments/assets/a6f8c906-df48-40a9-84f6-9b292ecfa69f)
![Team_Details_Part1](https://github.com/user-attachments/assets/3de37aa0-3b1e-43a0-8ae1-7406f1093855)
![Team_Details_Part2](https://github.com/user-attachments/assets/f82e8916-142e-49b2-ac11-80716fbc56e7)
![Add_Match](https://github.com/user-attachments/assets/3b200f13-3fa0-469c-b1c2-ab06d29901be)
![Live_Matches](https://github.com/user-attachments/assets/1a8bd36c-6cd4-458d-a080-9456803e08c6)
![Edit_Live_Match](https://github.com/user-attachments/assets/caf91561-f556-4d73-8667-4f54e5f30d18)
![Upcoming_Matches](https://github.com/user-attachments/assets/dc82861b-9bbd-4690-a7a7-085eeb7bed43)
![Match_Results](https://github.com/user-attachments/assets/c77b8831-1ee7-48a5-8db6-47aded29a523)
![Match_Results_Details](https://github.com/user-attachments/assets/d270f4b8-37fa-4170-ae5a-ef4b9b51c7b0)
![Standings](https://github.com/user-attachments/assets/30d11415-fa72-4d65-92de-537787154b5b)
![Playoff_Quarters](https://github.com/user-attachments/assets/31f06675-77ec-4378-b5ac-9203e3f4ca18)
![Edit_Playoff_Match](https://github.com/user-attachments/assets/a1a0b91b-08a3-4be0-af9b-d0f9345b1e4a)
![Playoff_Semis](https://github.com/user-attachments/assets/e4064cd1-4a54-4d77-81a9-ebf1885eb464)
![Playoff_Finals](https://github.com/user-attachments/assets/e9e2d86e-e4f8-494b-bd29-095b4b2ff4a8)
![Playoff_Winner](https://github.com/user-attachments/assets/a72a8191-90e1-4ff9-98a3-33c47764a64c)
![News](https://github.com/user-attachments/assets/8adc643a-d3c3-484b-a905-9d5839d815e0)
![News_View](https://github.com/user-attachments/assets/2916559f-48dd-41c3-bea3-a74da2779661)
![News_Edit](https://github.com/user-attachments/assets/bf8009f5-1e2e-4cf6-8f9d-e4f4118c7b79)
![Shop_Home](https://github.com/user-attachments/assets/1185d7a6-18da-4170-8420-481654d4d19a)
![Shop](https://github.com/user-attachments/assets/851663b4-ce66-477d-9417-adc13c60cee8)
![Shop_Filter](https://github.com/user-attachments/assets/b148f0ba-882e-41c8-a2e0-4e399ff5cc73)
![Shop_Edit](https://github.com/user-attachments/assets/c3082856-eea3-480b-85ef-ca116856195f)
![Shop_Product_Detail](https://github.com/user-attachments/assets/72fdc5a2-8100-43dd-9840-33bcbe11feab)
![Shop_Cart](https://github.com/user-attachments/assets/c765d216-56d1-4ac2-b515-92c93a2900c6)
![Shop_Created_Order](https://github.com/user-attachments/assets/423bc8ec-4af1-4a0c-b269-e030569f3b07)
![Shop_Paid_Order](https://github.com/user-attachments/assets/ccebce48-798c-478e-bf33-bdc8669eab93)